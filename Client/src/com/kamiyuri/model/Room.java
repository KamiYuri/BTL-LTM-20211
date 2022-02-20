package com.kamiyuri.model;

public class Room {
    private final String roomId;
    private final String itemName;
    private final String itemDescription;
    private final String currentPrice;
    private final String buyImmediatelyPrice;

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
