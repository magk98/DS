package zadDom.chat;

import zadDom.chat.client.ClientMulticast;
import zadDom.chat.client.ClientTcp;
import zadDom.chat.client.ClientUdp;

import java.io.*;
import java.net.*;

public class Client {

    private DatagramSocket datagramSocket = null;
    private Socket tcpSocket = null;
    private int port = 1234;
    private int multiPort = 9875;
    private String address = "localhost";
    private String multiAddress = "225.0.0.0";
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
            setTcpSocket(new Socket(getAddress(), port));
            setDatagramSocket(new DatagramSocket(getTcpSocket().getLocalPort()));
            setGroup(InetAddress.getByName(multiAddress));

            ClientTcp clientTcp = new ClientTcp(getTcpSocket(), this);
            setOut(new PrintWriter(getTcpSocket().getOutputStream(), true));
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            this.setNick(clientTcp.register(bufferedReader));
            clientTcp.start();
            ClientUdp clientUdp = new ClientUdp(getDatagramSocket());
            clientUdp.start();
            ClientMulticast multicast = new ClientMulticast(getMultiAddress(), multiPort);
            multicast.start();

            while (true) {
                if(bufferedReader.ready()) {
                        userInput = bufferedReader.readLine();
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
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        finally {
            if (getTcpSocket() != null) {
                try {
                    getTcpSocket().close();
                } catch (Exception e) {
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
            while (br.ready()) {
                tmpArt = br.readLine();
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

    public String getMultiAddress() {
        return multiAddress;
    }

    public void setMultiAddress(String multiAddress) {
        this.multiAddress = multiAddress;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
