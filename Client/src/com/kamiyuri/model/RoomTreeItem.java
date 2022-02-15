package com.kamiyuri.model;

import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

public class RoomTreeItem<String> extends TreeItem<String> {
    private String name;

    public RoomTreeItem(String name) {
        super(name);
        this.name = name;
    }
}
