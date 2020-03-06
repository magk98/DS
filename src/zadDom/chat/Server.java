package zadDom.chat;

import zadDom.chat.server.TcpServer;
import zadDom.chat.server.UdpServer;

import java.net.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Server {
    //one Udp Server for all clients, one Tcp Server per one Client
    private Map<String, TcpServer> clientsThreadsMap = new LinkedHashMap<>();
    private Map<String, Integer> clientsUdpPortMap = new LinkedHashMap<>();
    private Lock tcpMapLock = new ReentrantLock();
    private Lock udpMapLock = new ReentrantLock();
    private UdpServer udpServer;

    public static void main(String[] args) {
        int portNumber = 1234;
        Server server = new Server();
        server.startServer(portNumber);
    }

    private void startServer(int portNumber) {
        try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
            this.setUdpServer(new UdpServer(this, portNumber, InetAddress.getByName("localhost")));
            getUdpServer().start();
            ExecutorService executor = Executors.newFixedThreadPool(10);
            System.out.println("Server Tcp ready");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                executor.submit(new TcpServer(clientSocket, this));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean registerNewClient(String nick, TcpServer thread) {
        //to ensure that nobody is modifying clients map while checking for taken nicknames
        getTcpMapLock().lock();
        if(getClientsThreadsMap().containsKey(nick)) {
            thread.sendMessageWithTcp("Nickname is already taken");
            getTcpMapLock().unlock();
            return false;
        }
        //if nickname is not taken then add new client
        getClientsThreadsMap().put(nick, thread);
        getUdpMapLock().lock();
        getClientsUdpPortMap().put(nick, thread.getClientPort());
        getUdpMapLock().unlock();
        for(String client: getClientsThreadsMap().keySet()) {
            if(!client.equals(nick)) {
                getClientsThreadsMap().get(client).sendMessageWithTcp(nick + " has joined chat");
            }
        }
        System.out.println(nick + " has joined chat");

        getTcpMapLock().unlock();
        return true;
    }

    public void sendMessageWithTCP(String message, String nick) {
        //to ensure that no client was added while we were sending message
        getTcpMapLock().lock();
        for(String client: getClientsThreadsMap().keySet()) {
            //sending to everyone except us
            if(!client.equals(nick)) {
                getClientsThreadsMap().get(client).sendMessageWithTcp(message);
            }
        }
        getTcpMapLock().unlock();
    }

    public void sendMessageWithUdp(String message, String nick) {
        //to ensure that no client was added while we were sending message
        getUdpMapLock().lock();
        for(String client: getClientsUdpPortMap().keySet()) {
            //sending to everyone except us
            if(!client.equals(nick)) {
                getUdpServer().sendMessageWithUdp(message, getClientsUdpPortMap().get(client));
            }
        }
        getUdpMapLock().unlock();
    }


    public void unregisterClient(String nick) {
        //unregister from both tcp and udp, making sure nobody else is modifying in the moment
        getTcpMapLock().lock();
        getUdpMapLock().lock();
        getClientsThreadsMap().remove(nick);
        getClientsUdpPortMap().remove(nick);
        getUdpMapLock().unlock();
        for(TcpServer thread: getClientsThreadsMap().values()) {
                thread.sendMessageWithTcp(nick + " has disconnected");
            }
        getTcpMapLock().unlock();
    }

    //getters and setters
    public Map<String, TcpServer> getClientsThreadsMap() {
        return clientsThreadsMap;
    }

    public void setClientsThreadsMap(Map<String, TcpServer> clientsThreadsMap) {
        this.clientsThreadsMap = clientsThreadsMap;
    }

    public Map<String, Integer> getClientsUdpPortMap() {
        return clientsUdpPortMap;
    }

    public void setClientsUdpPortMap(Map<String, Integer> clientsUdpPortMap) {
        this.clientsUdpPortMap = clientsUdpPortMap;
    }

    public Lock getTcpMapLock() {
        return tcpMapLock;
    }

    public void setTcpMapLock(Lock tcpMapLock) {
        this.tcpMapLock = tcpMapLock;
    }

    public Lock getUdpMapLock() {
        return udpMapLock;
    }

    public void setUdpMapLock(Lock udpMapLock) {
        this.udpMapLock = udpMapLock;
    }

    public UdpServer getUdpServer() {
        return udpServer;
    }

    public void setUdpServer(UdpServer udpServer) {
        this.udpServer = udpServer;
    }
}
