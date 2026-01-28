import java.io.*;
import java.net.*;
import java.util.*;

public class ChatApplication {

    static Set<ClientHandler> clients = new HashSet<>();

    public static void main(String[] args) throws Exception {

        Scanner sc = new Scanner(System.in);
        System.out.print("Enter mode (server/client): ");
        String mode = sc.nextLine();

        if (mode.equalsIgnoreCase("server")) {
            startServer();
        } else if (mode.equalsIgnoreCase("client")) {
            startClient();
        } else {
            System.out.println("Invalid mode.");
        }
    }

    public static void startServer() throws IOException {
        ServerSocket serverSocket = new ServerSocket(1234);
        System.out.println("Chat Server started...");

        while (true) {
            Socket socket = serverSocket.accept();
            ClientHandler client = new ClientHandler(socket);
            clients.add(client);
            client.start();
        }
    }

    public static void startClient() throws IOException {
        Socket socket = new Socket("localhost", 1234);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(
                socket.getOutputStream(), true);

        Scanner sc = new Scanner(System.in);

        
        new Thread(() -> {
            try {
                String msg;
                while ((msg = in.readLine()) != null) {
                    System.out.println(msg);
                }
            } catch (IOException e) {
                System.out.println("Disconnected from server.");
            }
        }).start();

        // Send messages
        while (true) {
            out.println(sc.nextLine());
        }
    }

  
    static class ClientHandler extends Thread {
        Socket socket;
        BufferedReader in;
        PrintWriter out;

        ClientHandler(Socket socket) throws IOException {
            this.socket = socket;
            in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(
                    socket.getOutputStream(), true);
        }

        public void run() {
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    for (ClientHandler client : clients) {
                        client.out.println(message);
                    }
                }
            } catch (IOException e) {
                System.out.println("Client disconnected.");
            }
        }
    }
}
