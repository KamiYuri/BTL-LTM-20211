package com.ltm.client.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class LoginController {
    @FXML
    TextField us_input, pw_input;

    @FXML
    public void submit(ActionEvent e){
        String username, password;
        username = us_input.getText();
        password = pw_input.getText();

        System.out.println(username + ":" + password);
    }
}
