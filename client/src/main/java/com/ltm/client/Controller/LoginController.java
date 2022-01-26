package com.ltm.client.Controller;

import com.ltm.client.Utils.RequestMaker;
import com.ltm.client.Utils.SocketCommunicator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.io.IOException;

public class LoginController {
    @FXML
    TextField us_input, pw_input;

    @FXML
    public void submit(ActionEvent e){
        String username, password;
        username = us_input.getText();
        password = pw_input.getText();

        try{
            SocketCommunicator.getInstance().sendRequest(RequestMaker.login(username, password));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
