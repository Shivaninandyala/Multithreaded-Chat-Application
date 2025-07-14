import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {

    private static final int PORT = 12345;
    private static Set<ClientHandler> clients = new HashSet<>();

    public static void main(String[] args) {
        System.out.println("Server started... Listening on port " + PORT);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected: " + socket);

                ClientHandler clientHandler = new ClientHandler(socket);
                clients.add(clientHandler);

                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            System.err.println("Error in server: " + e.getMessage());
        }
    }

    // Inner class for handling each client
    static class ClientHandler implements Runnable {
        private Socket socket;
        private PrintWriter out;
        private String name;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try (
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            ) {
                out = new PrintWriter(socket.getOutputStream(), true);

                out.println("Enter your name: ");
                name = in.readLine();
                broadcast(">> " + name + " has joined the chat.");

                String msg;
                while ((msg = in.readLine()) != null) {
                    if (msg.equalsIgnoreCase("exit")) break;
                    broadcast(name + ": " + msg);
                }

            } catch (IOException e) {
                System.out.println("Client disconnected: " + name);
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {}
                clients.remove(this);
                broadcast(">> " + name + " has left the chat.");
            }
        }

        private void broadcast(String message) {
            for (ClientHandler client : clients) {
                if (client != this) {
                    client.out.println(message);
                }
            }
        }
    }
}
