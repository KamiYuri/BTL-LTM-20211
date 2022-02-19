package com.kamiyuri.controller;

import com.kamiyuri.AuctionManager;
import com.kamiyuri.TCP.RequestType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public class Popup implements Initializable {
    private String content;
    @FXML
    private Label contentLabel;

    @FXML
    private TextField bidField;

    @FXML
    private Label bidLabel;

    private Consumer<String> callback;

    public void setCallback(Consumer<String> callback) {
        this.callback = callback;
    }

    public Popup(RequestType type, AuctionManager auctionManager) {
        switch (type) {
            case BID:
                content = "đấu giá";
                break;
            case BUY: {
                content = "mua";
                break;
            }
        }
    }

    @FXML
    void submitBtnAction() {
        Stage stage = (Stage)bidLabel.getScene().getWindow();
        callback.accept(bidField.getText());
        stage.close();

    }
    @FXML
    void cancelBtnAction() {
        Stage stage = (Stage) contentLabel.getScene().getWindow();
        stage.close();
    }

    public void set(){
        bidLabel.setVisible(true);
        bidField.setVisible(true);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        contentLabel.setText("Bạn chắc chắn muốn " + content + " sản phẩm này ?");
    }
}
