package com.kamiyuri.TCP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.function.Function;

public class ConnectionThread implements Runnable{
    private Thread thread;
    private Socket connSocket;
    private String serverIp = "127.0.0.1";
    private short serverPort = 5500;

    private PrintWriter out;
    private BufferedReader in;

    Consumer<String> getResponseCallback;


    public ConnectionThread() throws IOException {
        connSocket = new Socket(serverIp, serverPort);
        connSocket.setTcpNoDelay(true);

        out = new PrintWriter(connSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(connSocket.getInputStream()));
    }

    public void setResponseCallback(Consumer<String> getResponseCallback) {
        this.getResponseCallback = getResponseCallback;
    }

    @Override
    public void run() {
        Scanner scanner = new Scanner(this.in);
        scanner.useDelimiter(Delimiter.One());

        String response = "";
        while (true){
            response = scanner.next();
            getResponseCallback.accept(response);
        }
    }

    public void start(){
        if(this.thread == null){
            this.thread = new Thread(this);
            this.thread.start();
        }
    }

    public void send(String message) {
        this.out.print(message);
        this.out.flush();
    }

    public void disconnect(){
        try {
            this.connSocket.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}