package com.kamiyuri.controller;

import com.kamiyuri.AuctionManager;
import com.kamiyuri.controller.services.LoginResult;
import com.kamiyuri.controller.services.LoginService;
import com.kamiyuri.model.Account;
import com.kamiyuri.view.ViewFactory;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class LoginWindowController extends BaseController implements Initializable {

    @FXML
    private Label errorLabel;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField usernameField;

    public LoginWindowController(AuctionManager auctionManager, ViewFactory viewFactory, String fxmlName) {
        super(auctionManager, viewFactory, fxmlName);
    }

    @FXML
    void loginBtnAction() {
        if (fieldsAreValid()) {
            Account account = new Account(usernameField.getText(), passwordField.getText());

            LoginService loginService = new LoginService(auctionManager, account);
            loginService.start();
            loginService.setOnSucceeded(event -> {
                LoginResult loginResult = loginService.getValue();
                switch (loginResult) {
                    case SUCCESS:
                        viewFactory.showMainWindow();
                        Stage stage = (Stage) errorLabel.getScene().getWindow();
                        viewFactory.closeStage(stage);
                        break;
                    case FAILED_BY_CREDENTIALS:
                        errorLabel.setText("Sai tên đăng nhập hoặc mật khẩu");
                        return;
                    case FAILED_BY_UNEXPECTED_ERROR:
                        errorLabel.setText("Lỗi không xác định!");
                }
            });
        }
    }

    private boolean fieldsAreValid() {
        if (usernameField.getText().isEmpty()) {
            errorLabel.setText("Please fill email");
            return false;
        }
        if (passwordField.getText().isEmpty()) {
            errorLabel.setText("Please fill password");
            return false;
        }
        return true;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setUpCloseConnection();
    }

    private void setUpCloseConnection() {
        Stage stage = (Stage) errorLabel.getScene().getWindow();
        stage.setOnCloseRequest(event -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setHeaderText("Tắt chương trình?");

            Optional<ButtonType> option = alert.showAndWait();
            if (option.get() == ButtonType.OK) {
                auctionManager.diconnect();
                stage.close();
            }
        });
    }
}
