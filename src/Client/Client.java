package Client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private static Socket socket = null;
    private static DataInputStream input = null;
    private static DataOutputStream out = null;
    private static Scanner sc = new Scanner(System.in);
    private static boolean isOnline = true;

    public Client() {
        try {
            socket = new Socket("127.0.0.1", 12345);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            Client client = new Client();
            System.out.println("Connection accepted");
            out = new DataOutputStream(socket.getOutputStream());
            input = new DataInputStream(socket.getInputStream());


            Thread sendMessage = new Thread(new Runnable() {
                @Override
                public void run() {
                    while(true && isOnline) {
                        String msg = null;
                        try {
                            msg = sc.nextLine();
                            out.writeUTF(msg);
                            if(msg.equalsIgnoreCase("quit")) {
                                isOnline = false;
                                break;
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            Thread readMessage = new Thread(new Runnable() {
                @Override
                public void run() {

                    while (true && isOnline) {
                        try {
                            // read the message sent to this client
                            String msg = input.readUTF();
                            System.out.println(msg);
                        } catch (IOException e) {

                        }
                    }
                }
            });

            sendMessage.start();
            readMessage.start();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
