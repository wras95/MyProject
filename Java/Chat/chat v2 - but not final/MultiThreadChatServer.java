package Chat;


import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;

// A chat server that delivers messages.
 
public class MultiThreadChatServer {

  // server socket.
  private static ServerSocket serverSocket = null;
  // client socket.
  private static Socket clientSocket = null;

  // This chat server can accept up to maxClientsCount clients' connections. (10 clients)
  private static final int maxClientsCount = 10;
  private static final clientThread[] threads = new clientThread[maxClientsCount];

  public static void main(String args[]) {

    //--------------------------------Port_Number-----------------------------------------
    int portNumber = 2222;
    if (args.length < 1) {
      System.out
          .println("Usage: java MultiThreadChatServer <portNumber>\n"
              + "Now using port number=" + portNumber);
    } else {
      portNumber = Integer.valueOf(args[0]).intValue();
    }
//------------------------------Server_Socket------------------------------------------------
    try {
      serverSocket = new ServerSocket(portNumber);
    } catch (IOException e) {
      System.out.println(e);
    }
//-------------------------------Client_Socket--------------------------------------------
    while (true) {
      try {
        clientSocket = serverSocket.accept();
        int i = 0;

for (i = 0; i < maxClientsCount; i++) {
          if (threads[i] == null) {
            (threads[i] = new clientThread(clientSocket, threads)).start();
            break;
          }
        }

if (i == maxClientsCount) {
          PrintStream os = new PrintStream(clientSocket.getOutputStream());
          os.println("Server too busy. Try later.");
          os.close();
          clientSocket.close();
        }
      } catch (IOException e) {
        System.out.println(e);
      }
    }
  }
}

class clientThread extends Thread {

  private DataInputStream is = null;
  private PrintStream os = null;
  private Socket clientSocket = null;
  private final clientThread[] threads;
  private int maxClientsCount;

  public clientThread(Socket clientSocket, clientThread[] threads) {
    this.clientSocket = clientSocket;
    this.threads = threads;
    maxClientsCount = threads.length;
  }

  public void run() {
    int maxClientsCount = this.maxClientsCount;
    clientThread[] threads = this.threads;

    try {
//--------------------Input/Output_stream--------------------------------------------------------
      is = new DataInputStream(clientSocket.getInputStream());
      os = new PrintStream(clientSocket.getOutputStream());
      os.println("Enter your name.");
      String name = is.readLine().trim();
      os.println("Hello " + name
          + " to our chat room.\nTo leave enter /quit in a new line");

      for (int i = 0; i < maxClientsCount; i++) {
              if (threads[i] != null && threads[i] != this) {
                threads[i].os.println("*** A new user " + name
                    + " entered the chat room !!! ***");
              }
            }

      while (true) {
              String line = is.readLine();
              if (line.startsWith("/quit")) {
                break;
              }

      for (int i = 0; i < maxClientsCount; i++) {
                if (threads[i] != null) {
                  threads[i].os.println("<" + name + "> " + line);
                }
              }

      }

      for (int i = 0; i < maxClientsCount; i++) {
              if (threads[i] != null && threads[i] != this) {
                threads[i].os.println("*** The user " + name
                    + " is leaving the chat room !!! ***");
              }
            }

      os.println("*** Bye " + name + " ***");
    
      for (int i = 0; i < maxClientsCount; i++) {
              if (threads[i] == this) {
                threads[i] = null;
              }
            }

  //------------------Close_Input/Output_Stream---------------------------------------------------------
      is.close();
      os.close();
      clientSocket.close();
    } catch (IOException e) {
    }
  }
}
