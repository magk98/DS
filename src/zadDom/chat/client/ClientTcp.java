package zadDom.chat.client;

import zadDom.chat.Client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientTcp extends Thread {
    private Socket socket;
    private BufferedReader in;
    private Client client;

    public ClientTcp(Socket socket, Client client) {
        this.socket = socket;
        this.client = client;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            String response = "";
            while (!socket.isClosed()) {
                if (in.ready())
                    response = in.readLine();
                    System.out.println(response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String register(BufferedReader stdIn) {
        String nick;
        String response;
        System.out.println("Name:");
        try {
            nick = stdIn.readLine();
            client.sendTcpMessage(nick);
            //accepts name if it was not taken
            while (in.ready()) {
                response = in.readLine();
                if (response.equals("ok"))
                    break;
                else {
                    System.out.println(response);
                    nick = stdIn.readLine();
                    client.sendTcpMessage(nick);
                }
            }
            System.out.println("Hello " + nick + "!");
            return nick;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
