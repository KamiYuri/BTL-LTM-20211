package com.kamiyuri.controller.services;

import com.kamiyuri.AuctionManager;
import com.kamiyuri.model.Room;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.TreeItem;

import java.util.Scanner;

public class GetRoomsService extends Service<Void> {
    private final AuctionManager auctionManager;
    private final ObservableList<Room> roomObservableList;
    private final TreeItem<String> root;

    public GetRoomsService(AuctionManager auctionManager, TreeItem<String> root) {
        this.auctionManager = auctionManager;
        this.root = root;
        this.roomObservableList = auctionManager.getRoomList();
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                getRooms();
                return null;
            }
        };
    }

    private void getRooms() {
        String request = RequestFactory.getRequest(RequestType.SHOW_ROOM, null);
        auctionManager.sendRequest(request);
        String response;
        do {
            response = auctionManager.getShowRoomResponse();
        } while (response == null);

        handleResponse(response);
    }

    private void handleResponse(String response) {
        Scanner roomsScanner = new Scanner(response.substring(2));
        roomsScanner.useDelimiter(Delimiter.Two());
        while (roomsScanner.hasNext()) {
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
            TreeItem<String> treeItem = new TreeItem<>(room.getRoomId());
            root.getChildren().add(treeItem);
        }

        if (roomObservableList.isEmpty()) {
            TreeItem<String> treeItem = new TreeItem<>("");
            root.getChildren().add(treeItem);
        }
    }
}
