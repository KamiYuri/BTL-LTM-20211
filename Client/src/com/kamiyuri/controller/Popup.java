package com.kamiyuri.controller;

import com.kamiyuri.AuctionManager;
import com.kamiyuri.TCP.RequestType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public class Popup{

    @FXML
    private Button cancelBtn;
    private Stage owner;
    private String content;
    @FXML
    private Label contentLabel;

    @FXML
    private TextField bidField;

    @FXML
    private Label bidLabel;

    private Consumer<Void> callback;

    public Popup(Stage owner) {
        this.owner = owner;
    }

    public void show(Consumer<Void> callback, String content){
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("popup.fxml"));
        fxmlLoader.setController(this);
        Parent parent;
        try {
            parent = fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        Scene scene = new Scene(parent);
        Stage stage = new Stage();

        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(owner);

        stage.setScene(scene);
        stage.setResizable(false);

        contentLabel.setText(content);

        if(callback == null){
            cancelBtn.setVisible(false);
        }else{
            this.callback = callback;
        }

        stage.show();
    }

    @FXML
    void submitBtnAction() {
        Stage stage = (Stage)bidLabel.getScene().getWindow();
        stage.close();
    }
    @FXML
    void cancelBtnAction() {
        Stage stage = (Stage) contentLabel.getScene().getWindow();
        stage.close();
    }

    public void setCallback(Consumer<Void> callback) {
        this.callback = callback;
    }
}
