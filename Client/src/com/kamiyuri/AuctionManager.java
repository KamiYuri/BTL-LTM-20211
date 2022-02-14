package com.kamiyuri;

import com.kamiyuri.TCP.Response;
import com.kamiyuri.TCP.TCP;
import com.kamiyuri.model.Account;
import com.kamiyuri.model.RoomTreeItem;
import javafx.scene.control.TreeItem;

import java.io.IOException;

public class AuctionManager {
    private TCP tcp;
    private Account account;

    public AuctionManager() throws IOException {
        this.tcp = new TCP();
    }

    private RoomTreeItem<String> root = new RoomTreeItem<>("");

    public TreeItem<String> getRoomsRoot() {
        return this.root;
    }

    public void getRooms(TreeItem<String> room) {

    }

    public void getUserRooms(TreeItem<String> userRoom) {

    }

    public String exchangeMessage(String request) throws IOException {
        String response =  tcp.exchangeMessage(request);
        return Response.handle(response);
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public String getUserId() {
        return account.getUserId();
    }

    public String getUserName() {
        return account.getUsername();
    }
}
