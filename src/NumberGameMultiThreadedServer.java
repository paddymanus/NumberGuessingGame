import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ThreadLocalRandom;

public class NumberGameMultiThreadedServer extends Thread {

    private final Socket clientSocket;

    NumberGameMultiThreadedServer(Socket socket){
        this.clientSocket= socket;
    }

    @Override
    public void run(){
        try (BufferedReader is = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             ObjectOutputStream os = new ObjectOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()))) {

            System.out.println("Client Accepted");
            // send initial prompt to client
            sendMessageToClient(os, "Server Saying Hello"); // send initial message to client
            int lowerLimit = parseToInt(is.readLine());
            int upperLimit = parseToInt(is.readLine());
            int clientGuess = parseToInt(is.readLine());
            int numberOfTries = checkNumber(os,is, clientGuess, lowerLimit, upperLimit);
            sendMessageToClient(os, "true");
            sendMessageToClient(os, "Congrats, you guessed correctly, it took you " + numberOfTries + " attempts");
        } catch (Exception e) {
            System.out.println("IOException:" + e.getMessage());
        }
    }


    public static void main(String[] args){
        int portNumber = 4444;
        if(args.length == 1){
            portNumber = Integer.parseInt(args[0]);
        }
        System.out.println("Number Game Server started");
        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            while (true) {
                System.out.println("Multi Threaded Server Waiting");
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client Accepted from " + clientSocket.getInetAddress());
                // spawn a new thread to handle client
                NumberGameMultiThreadedServer studentAdminServerThread = new NumberGameMultiThreadedServer(clientSocket);
                System.out.println("About to start new thread");
                studentAdminServerThread.start();
            } // end while true
        } catch (Exception e) {
            System.out.println("Exception:" + e.getMessage());
        } // end catch
    } // end main

    public static void sendMessageToClient(ObjectOutputStream os, Object msg) throws Exception {
        os.writeObject(msg);
        os.flush();
    }

    public static int parseToInt(String s) {
        int clientNumber = 0;
        try {
            clientNumber = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            System.out.println("Unable to read client command");
        }
        return clientNumber;
    }


    public static int checkNumber(ObjectOutputStream os,BufferedReader is, int number, int lowerLimit, int upperLimit){
        int randomNum = ThreadLocalRandom.current().nextInt(lowerLimit, upperLimit);
        int guessCount = 1;
        try{
            while(randomNum != number){
                if(number > randomNum){
                    sendMessageToClient(os, "Lower than " + number);
                    guessCount++;
                } else{
                    sendMessageToClient(os, "Higher than " + number);
                    guessCount++;
                }
                number = Integer.parseInt(is.readLine());
            }
        }catch (Exception e){
            System.out.println(e);
        }
        return guessCount;
    }
}
