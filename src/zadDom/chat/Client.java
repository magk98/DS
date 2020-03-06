package zadDom.chat;

import zadDom.chat.client.ClientMulticast;
import zadDom.chat.client.ClientTcp;
import zadDom.chat.client.ClientUdp;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;

public class Client {

    private DatagramSocket datagramSocket = null;
    private Socket tcpSocket = null;
    private int port = 1234;
    private int multiPort = 9875;
    private PrintWriter out;
    private String nick;
    private InetAddress group;

    public static void main(String[] args) {
        Client client = new Client();
        client.start();
    }

    private void start() {
        String userInput;
        try {
            setTcpSocket(new Socket("localhost", port));
            setDatagramSocket(new DatagramSocket(getTcpSocket().getLocalPort()));
            setGroup(InetAddress.getByName("230.0.0.0"));

            ClientTcp clientTcp = new ClientTcp(getTcpSocket(), this);
            setOut(new PrintWriter(getTcpSocket().getOutputStream(), true));
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            this.setNick(clientTcp.register(bufferedReader));
            clientTcp.start();
            ClientUdp clientUdp = new ClientUdp(getDatagramSocket());
            clientUdp.start();
            new ClientMulticast().start();

            while (true) {
                if(bufferedReader.ready()) {
                    if ((userInput = bufferedReader.readLine()) != null) {
                        if(userInput.equals("U")) {
                            sendAscii("charmander", false);
                            continue;
                        }
                        else if(userInput.equals("M")) {
                            sendAscii("bulbasaur", true);
                            continue;
                        }
                        getOut().println(userInput);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (getTcpSocket() != null) {
                try {
                    getTcpSocket().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(getDatagramSocket() != null) {
                getDatagramSocket().close();
            }
        }
    }

    private void sendUdpMessage(String message) {
        byte[] sendBuffer = message.getBytes();

        try {
            DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, InetAddress.getByName("localhost"), getPort());
            getDatagramSocket().send(sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMulticastMessage(String message) {
        byte[] sendBuffer = message.getBytes();

        try {
            DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, getGroup(), getMultiPort());
            getDatagramSocket().send(sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendTcpMessage(String message) {
        getOut().println(message);
    }

    public void sendAscii(String fileName, boolean multicast){
        File file = new File(fileName + ".txt");
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(file));
            String art = "";
            String tmpArt;
            while ((tmpArt = br.readLine()) != null) {
                if (art.length() + tmpArt.length() + getNick().length() + 1 > 4096) {
                    System.out.println("Message exceeded max size (4096 B)");
                }
                art = art.concat(tmpArt).concat("\n");
            }
            if(art.length() > 0) {
                if(!multicast)
                    sendUdpMessage(getNick() + ":\n" + art);
                else
                    sendMulticastMessage(getNick() + ":\n" + art);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //getters and setters
    public void setNick(String nick) {
        this.nick = nick;
    }


    public DatagramSocket getDatagramSocket() {
        return datagramSocket;
    }

    public void setDatagramSocket(DatagramSocket datagramSocket) {
        this.datagramSocket = datagramSocket;
    }

    public Socket getTcpSocket() {
        return tcpSocket;
    }

    public void setTcpSocket(Socket tcpSocket) {
        this.tcpSocket = tcpSocket;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getMultiPort() {
        return multiPort;
    }

    public void setMultiPort(int multiPort) {
        this.multiPort = multiPort;
    }

    public PrintWriter getOut() {
        return out;
    }

    public void setOut(PrintWriter out) {
        this.out = out;
    }

    public String getNick() {
        return nick;
    }

    public InetAddress getGroup() {
        return group;
    }

    public void setGroup(InetAddress group) {
        this.group = group;
    }
}
