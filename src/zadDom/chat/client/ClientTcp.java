package zadDom.chat.client;

import zadDom.chat.Client;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ClientTcp extends Thread {
    private Socket socket;
    private Scanner in;
    private Client client;

    public ClientTcp(Socket socket, Client client) {
        this.socket = socket;
        this.client = client;
        try {
            in = new Scanner(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        String response = "";
        while (!socket.isClosed()) {
            if (in.hasNextLine())
                response = in.nextLine();
                System.out.println(response);
        }
    }

    public String register(Scanner stdIn) {
        String nick;
        String response;
        System.out.println("Name:");
        nick = stdIn.nextLine();
        client.sendTcpMessage(nick);
        //accepts name if it was not taken
        while (in.hasNextLine()) {
            response = in.nextLine();
            if (response.equals("ok"))
                break;
            else {
                System.out.println(response);
                nick = stdIn.nextLine();
                client.sendTcpMessage(nick);
            }
        }
        System.out.println("Hello " + nick + "!");
        return nick;
    }


}
