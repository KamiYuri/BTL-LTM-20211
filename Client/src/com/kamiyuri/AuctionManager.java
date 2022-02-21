package com.kamiyuri;

import com.kamiyuri.controller.services.GetRoomsService;
import com.kamiyuri.controller.services.LeaveRoomService;
import com.kamiyuri.controller.services.RequestType;
import com.kamiyuri.model.Account;
import com.kamiyuri.model.Room;
import com.kamiyuri.tcp.ConnectionThread;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

import java.io.IOException;
import java.util.function.Consumer;

public class AuctionManager {
    private final ConnectionThread connectionThread;
    private Account account;
    private final ObservableList<Room> roomObservableList = FXCollections.observableArrayList();
    private String loginResponse, logoutResponse, getRoomsResponse, createRoomResponse, joinRoomResponse, bidResponse, buyResponse, leaveRoomResponse;
    private final ObservableList<String> notifications = FXCollections.observableArrayList();
    private final Consumer<String> getResponseCallback = response -> {
        RequestType code = RequestType.values()[Character.getNumericValue(response.charAt(0)) - 1];
        switch (code) {
            case LOGIN:
                loginResponse = response;
                break;
            case LOGOUT:
                logoutResponse = response;
                break;
            case SHOW_ROOM:
                getRoomsResponse = response;
                break;
            case CREATE_ROOM:
                createRoomResponse = response;
                break;
            case JOIN_ROOM:
                joinRoomResponse = response;
                break;
            case BID:
                bidResponse = response;
                break;
            case BUY:
                buyResponse = response;
                break;
            case NOTIFICATION:
                notifications.add(response);
                notifications.removeAll();
                break;
            case LEAVE_ROOM:
                leaveRoomResponse = response;
                break;
        }
    };
    private Room selectedRoom;
    private String selectedRoomCurrentPrice;

    public AuctionManager() throws IOException {
        this.connectionThread = new ConnectionThread();
        setupConnectionThread();
        this.connectionThread.start();
    }

    public void setGetRoomsResponse(String getRoomsResponse) {
        this.getRoomsResponse = getRoomsResponse;
    }

    private void setupConnectionThread() {
        this.connectionThread.setResponseCallback(getResponseCallback);
    }

    public String getLoginResponse() {
        return loginResponse;
    }

    public void sendRequest(String request) {
        this.connectionThread.send(request);
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public TreeItem<String> getTreeRoot() {
        TreeItem<String> treeRoot = new TreeItem<>("");
        GetRoomsService getRoomsService = new GetRoomsService(this, treeRoot);
        getRoomsService.start();

        do {
            TreeItem<String> t = treeRoot;
        } while (treeRoot.getChildren().isEmpty());

        if (treeRoot.getChildren().get(0).getValue() == "") return null;

        return treeRoot;
    }

    public String getCreateRoomResponse() {
        return createRoomResponse;
    }

    public void setCreateRoomResponse(String createRoomResponse) {
        this.createRoomResponse = createRoomResponse;
    }

    public String getJoinRoomResponse() {
        return joinRoomResponse;
    }

    public void setJoinRoomResponse(String joinRoomResponse) {
        this.joinRoomResponse = joinRoomResponse;
    }

    public String getBidResponse() {
        return bidResponse;
    }

    public void setBidResponse(String bidResponse) {
        this.bidResponse = bidResponse;
    }

    public String getBuyResponse() {
        return buyResponse;
    }

    public ObservableList<String> getNotifications() {
        return notifications;
    }

    public String getShowRoomResponse() {
        return getRoomsResponse;
    }

    public ObservableList<Room> getRoomList() {
        return this.roomObservableList;
    }

    public Room getSelectedRoom() {
        return this.selectedRoom;
    }

    public void setSelectedRoom(Room item) {
        if (selectedRoom.getRoomId() != item.getRoomId()) {
            LeaveRoomService leaveRoomService = new LeaveRoomService(this);
            leaveRoomService.start();
        }

        this.selectedRoom = item;
    }

    public void setSelectedRoomCurrentPrice(String currentPrice) {
        this.selectedRoomCurrentPrice = currentPrice;
    }

    public String getLogoutResponse() {
        return this.logoutResponse;
    }

    public String getLeaveRoomResponse() {
        return leaveRoomResponse;
    }

    public void setLeaveRoomResponse(String leaveRoomResponse) {
        this.leaveRoomResponse = leaveRoomResponse;
    }

    public void diconnect() {
        connectionThread.disconnect();
    }
}
