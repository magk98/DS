package zadDom.chat.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class ClientUdp extends Thread{
    private DatagramSocket socket;

    public ClientUdp(DatagramSocket socket) {
        this.setSocket(socket);
    }

    public void run() {
        while (!getSocket().isClosed()) {
            //max buffer size was set to 4096 B, bigger files (ascii arts) are rejected
            byte[] receiveBuffer = new byte[4096];
            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            try {
                getSocket().receive(receivePacket);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String msg = new String(receivePacket.getData());
            System.out.println(msg);
        }
    }

    //getters and setters
    public DatagramSocket getSocket() {
        return socket;
    }

    public void setSocket(DatagramSocket socket) {
        this.socket = socket;
    }
}
