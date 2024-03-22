import java.io.*;
import java.net.*;
import java.util.*;

public class WordleClient {
   public static void main(String argv[]) throws Exception {
      Socket client = null;
      Scanner scanner = null;
      // int attempts = 1;
      // int maxAttempts = 3;
      try {
         client = new Socket("localhost", 2865);

         OutputStream output = client.getOutputStream();
         InputStream input = client.getInputStream();
         BufferedReader reader = new BufferedReader(new InputStreamReader(input));
         PrintWriter writer = new PrintWriter(output, true);

         // Read and print the welcome message from the server.
         System.out.println(" == Welcome to the Wordle Game! == ");

         // printng the menu
         String[] menu = { "1) Cheat", "2) Propose a word", "3) Quit" };
         for (String menuItem : menu) {
            System.out.println(menuItem);
         }

         scanner = new Scanner(System.in);
         while (true) {
            System.out.print("Your choice: ");
            String choice = scanner.next();

            if (choice.equals("1")) {
               // Send the user's choice to the server.
               writer.print("CHEAT\r\n");
               writer.flush();

               String rep = reader.readLine();
               System.out.println("The secret word is: " + rep);
            } else if (choice.equals("2")) {
               // if (attempts > maxAttempts) {
               //    System.out.println("Your attempts can not extends more than 6 try");
               //    break;
               // } else {
                  // Send the user's choice to the server.
                  //System.out.println("Attempt : [" + attempts + "]");
                  System.out.print("Enter your word guess: ");
                  String userGuess = scanner.next();
                  writer.print("TRY " + userGuess + "\r\n");
                  writer.flush();
                  String guessResult = reader.readLine();
                  System.out.println("Result: " + guessResult);

                  if (guessResult.trim().equals("GGGGG")) {
                     break;
                  }
                  //attempts++;
               }

             else if (choice.equals("3")) {
               // Send the user's choice to the server.
               writer.print("QUIT\r\n");
               writer.flush();
               System.out.println("Goodbye!");
               client.close();
               break; // Exit the loop.
            } else {
               System.out.println("Invalid choice. Please select a valid option (1-3)");
            }
         }

         // Close the resources.
      } catch (ConnectException e) {
         System.out.println("Server is not running");
      } catch (SocketException e) {
         System.out.println("Server connection closed.");
      } catch (Exception e) {
         e.printStackTrace();
      } finally {
         try {
            if (client != null) {
               client.close();
            }
            scanner.close();
         } catch (IOException e) {
            System.out.println("Error while closing the socket, input and output streams: " + e.getMessage());
         }
      }
   }
}
