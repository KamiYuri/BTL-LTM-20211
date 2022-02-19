package com.kamiyuri;

import com.kamiyuri.TCP.ConnectionThread;
import com.kamiyuri.TCP.Delimiter;
import com.kamiyuri.TCP.RequestFactory;
import com.kamiyuri.TCP.RequestType;
import com.kamiyuri.model.Account;
import com.kamiyuri.model.Room;
import com.kamiyuri.model.RoomTreeItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.TreeItem;

import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;
import java.util.function.Consumer;

public class AuctionManager {
    private final ConnectionThread connectionThread;
    private final RoomTreeItem<String> root = new RoomTreeItem<>("");
    private ObservableList<Room> roomObservableList = FXCollections.observableArrayList();
    private Room selectedRoom;
    private Account account;
    private String loginResponse, logoutResponse, roomResponse, createRoomResponse;
    Consumer<String> getResponseCallback = response -> {
        RequestType code = RequestType.values()[Character.getNumericValue(response.charAt(0)) - 1];

        switch (code) {
            case LOGIN:
                loginResponse = response;
                break;
            case LOGOUT:
                break;
            case SHOW_ROOM:
                roomResponse = response;
                break;
            case CREATE_ROOM:
                createRoomResponse = response;
                break;
        }
    };

    public AuctionManager() throws IOException {
        this.connectionThread = new ConnectionThread();
        setupConnectionThread();
        this.connectionThread.start();
    }

    private void setupConnectionThread() {
        this.connectionThread.setResponseCallback(this.getResponseCallback);
    }

    public TreeItem<String> getRoomsRoot() {
        return this.root;
    }

    public void getRooms(TreeItem<String> roomTreeItem) {
        Service service = new Service() {
            @Override
            protected Task createTask() {
                return new Task<>() {
                    @Override
                    protected Void call() throws Exception {
                        String request = RequestFactory.getRequest(RequestType.SHOW_ROOM, null);
                        sendRequest(request);
                        return null;
                    }
                };
            }
        };

        service.start();
        service.setOnSucceeded(event -> {
            handleRoomsRespone();
            roomTreeItem.getChildren().clear();

            for (Room room : roomObservableList) {
                RoomTreeItem treeItem = new RoomTreeItem<String>(room.getRoomId());
                roomTreeItem.getChildren().add(treeItem);
            }
        });
    }

    private void handleRoomsRespone() {
        roomObservableList.clear();
        Scanner roomsScanner = new Scanner(roomResponse.substring(2));
        roomsScanner.useDelimiter(Delimiter.Two());
        while(roomsScanner.hasNext()){
            Scanner roomDataScanner = new Scanner(roomsScanner.next());
            roomDataScanner.useDelimiter(Delimiter.Three());

            String roomId = roomDataScanner.next();
            String itemName = roomDataScanner.next();
            String itemDescription = roomDataScanner.next();
            String itemCurrentPrice = roomDataScanner.next();
            String itemBuyImmediatelyPrice = roomDataScanner.next();

            Room room = new Room(
                    roomId,
                    itemName,
                    itemDescription,
                    itemCurrentPrice,
                    itemBuyImmediatelyPrice
            );

            roomObservableList.add(room);
        }
    }

    public Room getSelectedRoom(String roomId){
        FilteredList<Room> roomFilteredList = roomObservableList.filtered(room -> room.getRoomId().equals(roomId));
        selectedRoom = roomFilteredList.get(0);
        return selectedRoom;
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

    public void createRoom(Properties data) {
        data.put("userId", account.getUserId());

        Service service = new Service() {
            @Override
            protected Task createTask() {
                return new Task() {
                    @Override
                    protected Object call() throws Exception {
                        String request = RequestFactory.getRequest(RequestType.CREATE_ROOM, data);
                        sendRequest(request);
                        return null;
                    }
                };
            }
        };

        service.start();
        service.setOnSucceeded(event -> {
        });
    }
}
