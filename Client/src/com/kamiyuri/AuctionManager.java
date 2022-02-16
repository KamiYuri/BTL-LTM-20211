package com.kamiyuri;

import com.kamiyuri.TCP.ConnectionThread;
import com.kamiyuri.TCP.RequestType;
import com.kamiyuri.model.Account;
import com.kamiyuri.model.RoomTreeItem;
import javafx.scene.control.TreeItem;

import java.io.IOException;
import java.util.function.Function;

public class AuctionManager {
    private ConnectionThread connectionThread;
    private Account account;
    private String loginResponse, logoutResponse, refreshResponse;

    public AuctionManager() throws IOException {
        this.connectionThread = new ConnectionThread();
        setupConnectionThread();
        this.connectionThread.start();
    }

    private void setupConnectionThread() {
        connectionThread.setResponseCallback(response -> {
            System.out.println(response);
            RequestType code = RequestType.values()[Character.getNumericValue(response.charAt(0)) - 1];

            switch (code) {
                case LOGIN:
                    System.out.println(response);
                    if (response.charAt(1) == '1') {
                        loginResponse = "";
                    }
                    loginResponse = response.substring(2).replace("\r\n", "");
                    break;
                case LOGOUT:
                    if (response.charAt(1) == '1') {
                        logoutResponse = "";
                    }
                    logoutResponse = "1";
                    break;
                case REFRESH:
                    if (response != refreshResponse){
                        System.out.println(response);
                        refreshResponse = response;
                    }
            }
        });
    }

    private RoomTreeItem<String> root = new RoomTreeItem<>("");

    public TreeItem<String> getRoomsRoot() {
        return this.root;
    }

    public void getRooms(TreeItem<String> room) {

    }

    public void getUserRooms(TreeItem<String> userRoom) {

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

    public void sendRequest(String request) {
        this.connectionThread.send(request);
    }

    public String getLoginResponse() {
        return loginResponse;
    }

    public String getLogoutResponse() {
        return logoutResponse;
    }
}
