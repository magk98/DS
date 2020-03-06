package zadDom.chat.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class ClientMulticast extends Thread {
    private MulticastSocket socket = null;
    private byte[] buf = new byte[4096];

    private InetAddress group;

    public void run() {
        try {
            setGroup(InetAddress.getByName("230.0.0.0"));
            setSocket(new MulticastSocket(9875));
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
}
