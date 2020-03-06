package zadDom.chat.server;

import zadDom.chat.Server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class UdpServer extends Thread {

    private DatagramSocket socket;
    private Server server;
    private byte[] receiveBuffer;
    private int port;
    private InetAddress address;

    public UdpServer(Server server, int port, InetAddress address) {
        try {
            this.setPort(port);
            this.setAddress(address);
            setReceiveBuffer(new byte[4096]);
            this.setServer(server);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (getSocket() != null) getSocket().close();
        }
    }

    public void run() {
        try {
            this.setSocket(new DatagramSocket(getPort()));
            while (true) {
                DatagramPacket receivePacket = new DatagramPacket(getReceiveBuffer(), getReceiveBuffer().length);
                try {
                    getSocket().receive(receivePacket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String msg = new String(receivePacket.getData());
                String nick = msg.split(":")[0];
                getServer().sendMessageWithUdp(msg, nick);
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } finally {
            //close unclosed socket
            if(getSocket() != null && !getSocket().isClosed())
                getSocket().close();
        }
    }

    public void sendMessageWithUdp(String message, int port) {
        byte[] sendBuffer = message.getBytes();
            try {
                DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, InetAddress.getByName("localhost"), port);
                getSocket().send(sendPacket);
            } catch (IOException e) {
                e.printStackTrace();
            }

    }

    //getters and setters
    public DatagramSocket getSocket() {
        return socket;
    }

    public void setSocket(DatagramSocket socket) {
        this.socket = socket;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    public byte[] getReceiveBuffer() {
        return receiveBuffer;
    }

    public void setReceiveBuffer(byte[] receiveBuffer) {
        this.receiveBuffer = receiveBuffer;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public InetAddress getAddress() {
        return address;
    }

    public void setAddress(InetAddress address) {
        this.address = address;
    }
}
