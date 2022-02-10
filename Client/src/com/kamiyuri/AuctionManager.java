package com.kamiyuri;

import com.kamiyuri.model.RoomTreeItem;
import javafx.scene.control.TreeItem;

import java.io.IOException;

public class AuctionManager {
    private TCP tcp;

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
}
