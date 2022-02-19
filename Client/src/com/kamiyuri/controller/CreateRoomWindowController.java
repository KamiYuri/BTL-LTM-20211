package com.kamiyuri.controller;

import com.kamiyuri.AuctionManager;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.Properties;

public class CreateRoomWindowController {
    private final AuctionManager auctionManager;
    @FXML
    private TextField itemBuyField;
    @FXML
    private TextArea itemDescriptionField;
    @FXML
    private TextField itemNameField;
    @FXML
    private TextField itemStartPriceField;

    public CreateRoomWindowController(AuctionManager auctionManager) {
        this.auctionManager = auctionManager;
    }

    @FXML
    void cancelBtnAction() {
        Stage thisStage = (Stage) itemBuyField.getScene().getWindow();
        thisStage.close();
    }

    @FXML
    void submitBtnAct() {
        Properties data = new Properties();
        data.put("itemName", itemNameField.getText());
        data.put("itemDescription", itemDescriptionField.getText());
        data.put("itemStartPrice", itemStartPriceField.getText());
        data.put("itemBuyPrice", itemBuyField.getText());

        auctionManager.createRoom(data);

        Stage stage = (Stage) itemNameField.getScene().getWindow();
        stage.close();
    }
}
