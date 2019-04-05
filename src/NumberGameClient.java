import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;


public class NumberGameClient {
    static int lowerLimit;
    static int upperLimit;

    public static void main(String[] args) {
        String hostName = "localhost"; // default host name
        int hostPort = 4444; // default host port


        // assign host machine name and port to connect to
        if (args.length != 0) {
            if (args[0] != null) {
                hostName = args[0]; // user specified machine
            }
            if (args[1] != null) {
                hostPort = Integer.parseInt(args[1]); // user specified port
            }
        }

        System.out.println("Trying to Connect to Number Game Server");
        // connect to server and extract input and output streams
        try (Socket serverSocket = new Socket(hostName, hostPort);
             PrintWriter os = new PrintWriter(new BufferedWriter(new OutputStreamWriter(serverSocket.getOutputStream())));
             ObjectInputStream is = new ObjectInputStream(new BufferedInputStream(serverSocket.getInputStream()))) {

            // read and display opening message from server
            System.out.println("Server: " + is.readObject());

            // create client input stream
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));

            // user enters lower limit number
            System.out.print("Enter Lower Limit Number ");
            lowerLimit = Integer.parseInt(userInput.readLine());
           while(!checkNumberIsPositive(lowerLimit)){ // method checks the number is positive
               System.out.print("Please enter a positive number ");
               lowerLimit = Integer.parseInt(userInput.readLine());
           }
           sendMessageToServer(os, Integer.toString(lowerLimit)); // number sent to server
            System.out.print("Enter Upper Limit Number ");
           upperLimit = Integer.parseInt(userInput.readLine());
           while (!checkNumberIsPositive(upperLimit) || !higherThanLowerNum(upperLimit)){ // method checks the number is positive
               System.out.print("Please enter a positive number higher than the lower limit " + lowerLimit);
               upperLimit = Integer.parseInt(userInput.readLine());
           }
            sendMessageToServer(os, Integer.toString(upperLimit));

           // user inputs guess number
           System.out.println("Enter Guess Number");
           int clientNumberGuess = Integer.parseInt(userInput.readLine());
           while(!checkUserGuess(clientNumberGuess)){
               System.out.println("Please enter a number between the limits " + lowerLimit + " and " + upperLimit);
               clientNumberGuess = Integer.parseInt(userInput.readLine());
           }
           sendMessageToServer(os, Integer.toString(clientNumberGuess));

            do{
                String response = is.readObject().toString();
                if(response.equals("true")){
                    break;
                } else{
                    System.out.println(response);
                    int clientGuess = Integer.parseInt(userInput.readLine());
                    if(!checkUserGuess(clientGuess)){ // method checks the number is between lower and upper limit
                        System.out.println("Please guess a number between " + lowerLimit + " and " + upperLimit);
                    }
                    else {
                        sendMessageToServer(os, Integer.toString(clientGuess));
                    }
                }
            } while (true);
            System.out.println(is.readObject());
        } catch (Exception e) {
            System.err.println("Exception:  " + e.getMessage());
        }
    }

    public static void sendMessageToServer(PrintWriter os, String msg)
    {
        os.println(msg);
        os.flush();
    }

    public static boolean checkNumberIsPositive(int inputNumber){
        boolean response;
        if(inputNumber > 0){
            response = true;
        } else {
            response = false;
        }
        return response;
    }

    public static boolean checkUserGuess(int number){
        boolean response;
        if(number < lowerLimit || number > upperLimit ){
            response = false;
        }
        else{
            response = true;
        }
        return response;
    }

    public static boolean higherThanLowerNum(int number){
        boolean response;
        if(number < lowerLimit){
            response = false;
        } else {
            response = true;
        }
        return response;
    }

}
