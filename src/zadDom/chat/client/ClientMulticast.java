package zadDom.chat.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class ClientMulticast extends Thread {
    private MulticastSocket socket = null;
    private byte[] buf = new byte[4096];
    private String address;
    private int multiPort;

    private InetAddress group;

    public ClientMulticast(String address, int multiPort){
        this.setAddress(address);
        this.setMultiPort(multiPort);
    }

    public void run() {
        try {
            setGroup(InetAddress.getByName(getAddress()));
            setSocket(new MulticastSocket(getMultiPort()));
            getSocket().joinGroup(getGroup());
            while (true) {
                DatagramPacket packet = new DatagramPacket(getBuf(), getBuf().length);
                getSocket().receive(packet);
                String received = new String(packet.getData(), 0, packet.getLength());
                System.out.println(received);
            }
        }catch(IOException e) {
            e.printStackTrace();
            }
        finally {
            try {
                getSocket().leaveGroup(getGroup());
            } catch (IOException e) {
                e.printStackTrace();
            }
            getSocket().close();
        }

    }

    //getters and setters
    public MulticastSocket getSocket() {
        return socket;
    }

    public void setSocket(MulticastSocket socket) {
        this.socket = socket;
    }

    public byte[] getBuf() {
        return buf;
    }

    public void setBuf(byte[] buf) {
        this.buf = buf;
    }

    public InetAddress getGroup() {
        return group;
    }

    public void setGroup(InetAddress group) {
        this.group = group;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getMultiPort() {
        return multiPort;
    }

    public void setMultiPort(int multiPort) {
        this.multiPort = multiPort;
    }
}
