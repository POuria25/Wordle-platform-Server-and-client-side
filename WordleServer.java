import java.io.*;
import java.net.*;

public class WordleServer {
   private int PORT;
   private int clientCounter;

   public WordleServer(int port) {
      this.PORT = port;
      this.clientCounter = 1;
   }

   public void start() throws InterruptedException {
      try (ServerSocket server = new ServerSocket(this.PORT)) {
         System.out.println("Wordle Server is running on port " + this.PORT);
         while (true) {
            Socket incoming = server.accept();
            System.out.println("\n================================================================\n");
            System.out.println("Client[" + clientCounter + "] New connection accepted from " + incoming.getInetAddress());
            System.out.println("Connection is established");
            Connection connection = new Connection(incoming);
            connection.start();
            clientCounter++;
         }
      } catch (IOException e) {
         System.err.println("Server Error: " + e.getMessage());
         System.exit(1);
      }
   }

   public static void main(String[] args) throws InterruptedException {
      int port = 2865;
      WordleServer wordleServer = new WordleServer(port);
      wordleServer.start();
   }

}
