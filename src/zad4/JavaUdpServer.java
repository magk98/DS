package zad4;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

public class JavaUdpServer {
    public static void main(String[] args) {
        System.out.println("Java UDP Server");
        DatagramSocket socket = null;
        int portNumber = 9008;

        try{
            socket = new DatagramSocket(portNumber);
            byte[] receiveBuffer = new byte[1024];
            byte[] sendBufferJava = "Pong Java Udp".getBytes();
            byte[] sendBufferPython = "Pong Python Udp".getBytes();

            while (true){
                Arrays.fill(receiveBuffer, (byte) 0);
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                socket.receive(receivePacket);
                String msg = new String(receivePacket.getData());
                System.out.println("received msg: " + msg);

                Thread.sleep(2000);

                InetAddress senderAddress = receivePacket.getAddress();
                int senderPort = receivePacket.getPort();
                if(msg.toLowerCase().contains("java")) {
                    DatagramPacket sendPacket = new DatagramPacket(sendBufferJava, sendBufferJava.length, senderAddress, senderPort);
                    socket.send(sendPacket);
                }
                if(msg.toLowerCase().contains("python")){
                    DatagramPacket sendPacket = new DatagramPacket(sendBufferPython, sendBufferPython.length, senderAddress, senderPort);
                    socket.send(sendPacket);
                }
                else{
                    byte[] anotherBuffer = "another Udp".getBytes();
                    DatagramPacket sendPacket = new DatagramPacket(anotherBuffer, anotherBuffer.length, senderAddress, senderPort);
                    socket.send(sendPacket);
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            if (socket != null) {
                socket.close();
            }
        }
    }
}
