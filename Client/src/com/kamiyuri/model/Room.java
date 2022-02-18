package com.kamiyuri.model;

import java.net.Socket;
import java.util.Timer;

public class Room {
    private String roomId;
    private String itemName;
    private String itemDescription;
    private String startingPrice;
    private String currentPrice;
    private String buyImmediatelyPrice;

    public Room(String roomId, String itemName, String itemDescription, String currentPrice, String buyImmediatelyPrice) {
        this.roomId = roomId;
        this.itemName = itemName;
        this.itemDescription = itemDescription;
        this.currentPrice = currentPrice;
        this.buyImmediatelyPrice = buyImmediatelyPrice;
    }

    public String getRoomId() {
        return roomId;
    }

    public String getItemName() {
        return itemName;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public String getCurrentPrice() {
        return currentPrice;
    }

    public String getBuyImmediatelyPrice() {
        return buyImmediatelyPrice;
    }
}
