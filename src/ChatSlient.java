import java.io.*;
import java.net.*;

public class ChatClient {

    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        try (
            Socket socket = new Socket(SERVER_IP, SERVER_PORT);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
        ) {
            System.out.println("Connected to chat server.");
          
            new Thread(() -> {
                String response;
                try {
                    while ((response = in.readLine()) != null) {
                        System.out.println(response);
                    }
                } catch (IOException e) {
                    System.out.println("Disconnected from server.");
                }
            }).start();

            String input;
            while ((input = userInput.readLine()) != null) {
                out.println(input);
                if (input.equalsIgnoreCase("exit")) break;
            }

        } catch (IOException e) {
            System.err.println("Could not connect to server: " + e.getMessage());
        }
    }
}
