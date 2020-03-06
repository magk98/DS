package zadDom.chat.server;

import zadDom.chat.Server;

import java.net.*;
import java.io.*;

    public class TcpServer extends Thread {
        private Socket socket;
        private Server server;
        private Boolean registered = false;
        private String clientNick;
        private PrintWriter clientOut;

        public TcpServer(Socket socket, Server server) {
            super("Tcp Socket Thread");
            this.setSocket(socket);
            this.setServer(server);
        }

        public void run() {
            try {
                PrintWriter out = new PrintWriter(getSocket().getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(getSocket().getInputStream()));
                String inputLine, outputLine;
                setClientOut(out);

                while ((inputLine = in.readLine()) != null) {
                    //if not registered in the server then register client
                    if(!getRegistered()) {
                        if(getServer().registerNewClient(inputLine.trim(), this)) {
                            setClientNick(inputLine.trim());
                            setRegistered(true);
                            out.println("ok");
                        }
                        continue;
                    }
                    //send Tcp message
                    outputLine = inputLine;
                    getServer().sendMessageWithTCP(getClientNick() + ": " + outputLine, getClientNick());
                }
                getSocket().close();
            } catch (SocketException e) {
                //disconnect client
                System.out.println(getClientNick() + " has disconnected.");
                getServer().unregisterClient(getClientNick());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void sendMessageWithTcp(String message) {
            getClientOut().println(message);
        }

        //getters and setters
        public int getClientPort() {return getSocket().getPort();}

        public Socket getSocket() {
            return socket;
        }

        public void setSocket(Socket socket) {
            this.socket = socket;
        }

        public Server getServer() {
            return server;
        }

        public void setServer(Server server) {
            this.server = server;
        }

        public Boolean getRegistered() {
            return registered;
        }

        public void setRegistered(Boolean registered) {
            this.registered = registered;
        }

        public String getClientNick() {
            return clientNick;
        }

        public void setClientNick(String clientNick) {
            this.clientNick = clientNick;
        }

        public PrintWriter getClientOut() {
            return clientOut;
        }

        public void setClientOut(PrintWriter clientOut) {
            this.clientOut = clientOut;
        }
    }
