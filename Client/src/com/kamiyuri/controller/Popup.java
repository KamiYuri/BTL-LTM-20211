package com.kamiyuri.controller;

import com.kamiyuri.AuctionManager;
import com.kamiyuri.TCP.RequestType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class Popup implements Initializable {
    private String content;
    @FXML
    private Label contentLabel;

    public Popup(RequestType type, AuctionManager auctionManager) {
        switch (type) {
            case BID:
                content = "đấu giá";
            case BUY:
                content = "mua";
        }
    }

    @FXML
    void cancelBtnAction() {

    }

    @FXML
    void submitBtnAction() {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        contentLabel.setText("Bạn chắc chắn muốn " + content + " sản phẩm này ?");
    }
}
