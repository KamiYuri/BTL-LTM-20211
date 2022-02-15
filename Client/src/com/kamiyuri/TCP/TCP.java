package com.kamiyuri.TCP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.CharBuffer;
import java.util.Scanner;
import java.util.stream.Collectors;

public class TCP {
    private Socket connSocket;
    private String serverIp = "127.0.0.1";
    private short serverPort = 5500;

    private PrintWriter out;
    private BufferedReader in;

    public TCP() throws IOException {
        connSocket = new Socket(serverIp, serverPort);
        out = new PrintWriter(connSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(connSocket.getInputStream()));
    }

    public void send(String request){
        out.println(request);
    }

    public String receive() throws IOException {
        char[] charArray = new char[1024 * 1024];
        StringBuilder response = new StringBuilder();
        int numCharsRead;
        numCharsRead = in.read(charArray, 0, charArray.length);
        response.append(charArray, 0, numCharsRead);

        return response.toString();
    }

    public void cancel() throws IOException {
        connSocket.close();
    }
}