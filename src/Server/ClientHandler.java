package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

public class ClientHandler implements Runnable {

    private String userName;
    private Socket client;
    private static Map<String, Socket> userNameMap;
    private DataInputStream dataInputStream = null;
    private DataOutputStream dataOutputStream = null;

    public ClientHandler(Socket client, Map<String, Socket>userNameMap) {
        this.client = client;
        this.userNameMap = userNameMap;

    }
    @Override
    public void run() {

        try {
            dataInputStream = new DataInputStream(client.getInputStream());
            dataOutputStream = new DataOutputStream(client.getOutputStream());
            dataOutputStream.writeUTF("[Server]Enter your unique User Name");
            userName = dataInputStream.readUTF();
            userNameMap.put(userName, client);
            System.out.println("[Server]Username: "+ userName);
            List<String> onlineClients = userNameMap.keySet().stream().collect(Collectors.toList());

            for (String user: onlineClients) {
                DataOutputStream dos = new DataOutputStream(userNameMap.get(user).getOutputStream());
                dos.writeUTF("[Server]New User Added:" +userName
                        +"\nFollowing is the new user list\n");

                onlineClients.forEach(name -> {
                    try {
                        dos.writeUTF(name);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
            dataOutputStream.writeUTF("[Server] To get the list of the current user, enter 'List' as the input.\n" +
                    "To send a message to someone from the list, enter his user-name first then after a space enter your message\n" +
                    "If you want to quit the application, enter 'Quit'");
            dataOutputStream.writeUTF("[Server]Start Chatting and Enjoy :)");
            System.out.println("Starting reading messages");

            String userInput = "";

            while(true && client.isConnected()) {

                userInput = dataInputStream.readUTF();
                if(userInput.equalsIgnoreCase("quit")) {
                    break;
                }
                else if(userInput.equalsIgnoreCase("list")) {
                    dataOutputStream.writeUTF("Following are the users, currently active\n");
                    userNameMap.keySet().stream().collect(Collectors.toList()).forEach(name -> {
                        try {
                            dataOutputStream.writeUTF(name);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }
                else {
                    StringTokenizer st = new StringTokenizer(userInput, "#");
                    if(st.countTokens() != 2) {
                        dataOutputStream.writeUTF("Fuck you! Wrong input bitch");
                        continue;
                    }
                    String destinationUserName = st.nextToken();
                    String userMessage = st.nextToken();

                    if(!userNameMap.containsKey(destinationUserName)) {
                        dataOutputStream.writeUTF("Shut up DAWG, there aint no person with that userName bitch");
                        continue;
                    }
                    else {
                        Socket destinationClient = userNameMap.get(destinationUserName);
                        DataOutputStream destinationDOS = new DataOutputStream(destinationClient.getOutputStream());
                        destinationDOS.writeUTF(userName + " : " + userMessage);
                    }
                }

            }
            if(!client.isConnected()) {
                System.out.println(userName+ " got disconnected");
            }
            System.out.println("Closing client connection");
            userNameMap.remove(userName);
            client.close();
            dataInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
