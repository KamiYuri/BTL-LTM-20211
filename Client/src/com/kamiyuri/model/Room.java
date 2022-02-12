package com.kamiyuri.model;

import java.net.Socket;
import java.util.Timer;

public class Room {
    private int clientNumber;
    private Timer timer;

    private String id;
    private String itemName;
    private String itemDescription;
    private String startingPrice;
    private String currentPrice;
    private String buyImmediatelyPrice;
    private String owner;
}
