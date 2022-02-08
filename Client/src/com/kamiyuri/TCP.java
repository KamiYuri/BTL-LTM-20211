package com.kamiyuri;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TCP {
    private Socket connSocket;
    private String serverIp = "127.0.0.1";
    private short serverPort = 5500;

    private PrintWriter out;
    private BufferedReader in;

    TCP() throws IOException {
        connSocket = new Socket(serverIp, serverPort);
        out = new PrintWriter(connSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(connSocket.getInputStream()));
    }
}
