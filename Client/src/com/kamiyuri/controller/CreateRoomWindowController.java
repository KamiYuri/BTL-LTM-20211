package com.kamiyuri.controller;

import com.kamiyuri.AuctionManager;
import com.kamiyuri.controller.services.CreateRoomResult;
import com.kamiyuri.controller.services.CreateRoomService;
import com.kamiyuri.view.ViewFactory;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.math.BigInteger;
import java.util.Properties;

public class CreateRoomWindowController extends BaseController {
    @FXML
    private TextField itemBuyField;
    @FXML
    private TextArea itemDescriptionField;
    @FXML
    private TextField itemNameField;
    @FXML
    private TextField itemStartPriceField;
    @FXML
    private Label errorLabel;

    public CreateRoomWindowController(AuctionManager auctionManager, ViewFactory viewFactory, String fxmlName) {
        super(auctionManager, viewFactory, fxmlName);
    }

    @FXML
    void cancelBtnAction() {
        Stage thisStage = (Stage) itemBuyField.getScene().getWindow();
        thisStage.close();
    }

    @FXML
    void submitBtnAct() {
        if (fieldsAreValid()) {

            Properties data = new Properties();
            data.put("itemName", itemNameField.getText());
            data.put("itemDescription", itemDescriptionField.getText());
            data.put("itemStartPrice", itemStartPriceField.getText());
            data.put("itemBuyPrice", itemBuyField.getText());

            auctionManager.setCreateRoomResponse(null);


            CreateRoomService createRoomService = new CreateRoomService(auctionManager, data);
            createRoomService.start();
            createRoomService.setOnSucceeded(event -> {
                CreateRoomResult createRoomResult = createRoomService.getValue();
                switch (createRoomResult) {
                    case SUCCESS:
                        successHandle();
                        close();
                        break;
                    case FAILED_BY_UNEXPECTED_ERROR:
                        close();
                        break;
                }
            });

        }
    }

    private void successHandle() {

    }

    private void close() {
        Stage stage = (Stage) itemNameField.getScene().getWindow();
        viewFactory.closeStage(stage);
    }

    private boolean fieldsAreValid() {

        if (itemNameField.getText().isEmpty()) {
            errorLabel.setText("T??n v???t ph???m tr???ng.");
            return false;
        }

        if (itemStartPriceField.getText().isEmpty()) {
            errorLabel.setText("Gi?? kh???i ??i???m tr???ng.");
            return false;
        } else {
            if (!itemStartPriceField.getText().matches("[0-9]+")) {
                errorLabel.setText("Gi?? kh???i ??i???m ph???i l?? s???.");
                return false;
            } else if (checkOutOfRange(itemStartPriceField.getText())) {
                errorLabel.setText("Gi?? kh???i ??i???m qu?? l???n.");
                return false;
            }
        }

        if (itemBuyField.getText().isEmpty()) {
            errorLabel.setText("Gi?? mua tr???ng.");
            return false;
        } else {
            if (!itemBuyField.getText().matches("[0-9]+")) {
                errorLabel.setText("Gi?? mua ph???i l?? s???.");
                return false;
            } else if (checkOutOfRange(itemBuyField.getText())) {
                errorLabel.setText("Gi?? mua qu?? l???n.");
                return false;
            }
        }

        if (Integer.parseInt(itemStartPriceField.getText()) > Integer.parseInt(itemBuyField.getText())) {
            errorLabel.setText("Gi?? mua ph???i l???n h??n gi?? kh???i ??i???m.");
            return false;
        }

        return true;
    }

    private boolean checkOutOfRange(String input) {
        String max = String.valueOf(Integer.MAX_VALUE);
        String min = String.valueOf(Integer.MIN_VALUE);
        BigInteger b1 = new BigInteger(input);
        BigInteger b_max = new BigInteger(max);
        BigInteger b_min = new BigInteger(min);
        return b1.compareTo(b_max) > 0 || b1.compareTo(b_min) < 0;
    }

}
