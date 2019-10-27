package Server;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    private static ServerSocket serverSocket = null;
    private static Socket socket = null;
    private static DataInputStream dataInputStream = null;
    private static int clientCount = 0;
    private static Map<String, Socket> userNameMap = new ConcurrentHashMap<>();

    public Server()
    {
        try {
            serverSocket = new ServerSocket(12345);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        Server server = new Server();
        System.out.println("Server started");
        while (true) {
            try {
                socket = serverSocket.accept();
                clientCount++;
                System.out.println("Client Accepted");
                ClientHandler clientHandler = new ClientHandler(socket, userNameMap);
                Thread newClient = new Thread(clientHandler);
                newClient.start();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
