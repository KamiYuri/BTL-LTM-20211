package com.ltm.client.Utils;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Vector;

public class SocketCommunicator {

    private Socket connSocket;
    private String serverIp = "127.0.0.1";
    private short serverPort = 5500;

    private PrintWriter out;
    private BufferedReader in;

    private static IOException exception;

    private SocketCommunicator(){
        try {
            connSocket = new Socket(serverIp, serverPort);
            out = new PrintWriter(connSocket.getOutputStream(), true);

            in = new BufferedReader(new InputStreamReader(connSocket.getInputStream()));
        } catch (IOException e) {
            exception = new IOException(e);
        }
    }

    private static class SocketHelper{
        private static final SocketCommunicator INSTANCE = new SocketCommunicator();
    }

    public static SocketCommunicator getInstance() throws IOException {
        SocketCommunicator communicator = SocketHelper.INSTANCE;
        if(exception != null){
            throw exception;
        }
        return SocketHelper.INSTANCE;
    }

    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public short getServerPort() {
        return serverPort;
    }

    public void setServerPort(short serverPort) {
        this.serverPort = serverPort;
    }

    public Socket getConnSocket() {
        return this.connSocket;
    }

    public Vector<String> getResponse() throws IOException {
        Vector<String> responses = new Vector<>();
        String temp;
        while ((temp = this.in.readLine()) != null) {
            responses.add(temp);
        }
        return responses;
    }

    public void sendRequest(String request) {
        this.out.println(request);
    }
}
