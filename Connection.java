import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;

public class Connection extends Thread {
   private final Socket connectionSocket;
   int attempts = 1;
   int maxAttempts = 6;

   public Connection(Socket socket) {
      this.connectionSocket = socket;
      try {
         this.connectionSocket.setSoTimeout(120000);
         connectionSocket.setTcpNoDelay(true);
      } catch (SocketException e) {
         System.err.println("Connection closed: " + e.getMessage());
         e.printStackTrace();
      }
   }

   public static boolean isIn(String request) {
      if (WordleWordSet.WORD_SET.contains(request)) {
         return true;
      }
      return false;
   }

   public int checkClientRequest(String request, String secretWord, Set<String> wordSet) {
      if (request.length() != 5) {
         return -2;
      }
      boolean checkContains = isIn(request);
      if (!checkContains) {
         return -1;
      } else if (request.length() < secretWord.length() || request.length() > secretWord.length()) {
         System.out.println("WRONG");
         return -2;
      } else {
         return 1;
      }
   }

   private void handleWordGuessing(String secretWord, String clientWord, PrintWriter writer,
         WordleAlgorithm wordleAlgorithm) {

      String result = wordleAlgorithm.checkWord(secretWord, clientWord);

      if (result.trim().equals("GGGGG") || attempts == maxAttempts) {
         writer.print(result + " GAMEOVER" + "\r\n");
         writer.flush();
      } else {
         writer.print(result + "\r\n");
         writer.flush();
         attempts++;
      }

      System.out.println("result: " + result);
   }

   public String extractWord(String input) {
      String word = null;
      Pattern pattern = Pattern.compile("TRY\\s(\\w+)");
      Matcher matcher = pattern.matcher(input);
      if (matcher.find()) {
         word = matcher.group(1);
         return word;
      } else {
         System.out.println("No match found.");
         return null;
      }
   }

   public int clientChoice(String str) {
      if (str.equals("CHEAT")) {
         return 1;
      } else if (str.equals("QUIT")) {
         return 3;
      } else if (str.matches("^TRY\\s\\w+.*")) {
         return 2;
      } else {
         System.out.println("server : client entered wrong input");
         return 0;
      }
   }

   @Override
   public void run() {

      OutputStream output = null;
      InputStream input = null;
      // int clientChoice = 0;
      String clientInput = null;
      String clientWord = null;

      Set<String> wordSet = WordleWordSet.WORD_SET;
      WordleAlgorithm wordleAlgorithm = new WordleAlgorithm(wordSet);

      try {
         // send responses to the client
         output = connectionSocket.getOutputStream();
         PrintWriter writer = new PrintWriter(output, true);
         // read from the client
         input = connectionSocket.getInputStream();
         BufferedReader reader = new BufferedReader(new InputStreamReader(input));

         // Select a random as a secret word
         String secretWord = wordleAlgorithm.selectRandomword();
         System.out.println("Secret word is: " + secretWord);
         while (attempts <= maxAttempts) {
            // Reading client word
            clientInput = reader.readLine();
            switch (clientChoice(clientInput)) {
               case 1:
                  writer.print(secretWord.toUpperCase() + "\r\n");
                  writer.flush();
                  break;
               case 3:
                  connectionSocket.close();
                  break;
               case 2:
                  clientWord = extractWord(clientInput).toLowerCase();
                  System.out.println("Client word is: " + clientWord);
                  if (checkClientRequest(clientWord, secretWord, wordSet) == -1) {
                     writer.print("NONEXISTENT" + "\r\n");
                     writer.flush();
                  } else if (checkClientRequest(clientWord, secretWord, wordSet) == 1) {
                     handleWordGuessing(secretWord, clientWord, writer, wordleAlgorithm);
                  } else if (checkClientRequest(clientWord, secretWord, wordSet) == -2) {
                     writer.print("WRONG" + "\r\n");
                     writer.flush();
                  } else {
                     writer.print("Invalid word. Please try again." + "\r\n");
                     writer.flush();
                  }

                  break;
               default:
                  System.out.println("Client made an Invalid choice");
                  break;
            }
         }
      } catch (IOException e) {
         System.out.println("Error: " + e.getMessage());
      } finally {
         try {
            connectionSocket.close();
         } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
         }
      }
   }
}
