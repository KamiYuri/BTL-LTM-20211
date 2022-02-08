package com.kamiyuri.controller.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Vector;

public class TCPService {

    private Socket connSocket;
    private String serverIp = "127.0.0.1";
    private short serverPort = 5500;

    private PrintWriter out;
    private BufferedReader in;

    private static IOException exception;

    private TCPService(){
        try {
            connSocket = new Socket(serverIp, serverPort);
            out = new PrintWriter(connSocket.getOutputStream(), true);

            in = new BufferedReader(new InputStreamReader(connSocket.getInputStream()));
        } catch (IOException e) {
            exception = new IOException(e);
        }
    }

    private static class SocketHelper{
        private static final TCPService INSTANCE = new TCPService();
    }

    public static TCPService getInstance() throws IOException {
        TCPService communicator = SocketHelper.INSTANCE;
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
