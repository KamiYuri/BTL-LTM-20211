package com.kamiyuri.controller;

import com.kamiyuri.AuctionManager;
import com.kamiyuri.controller.services.BidResult;
import com.kamiyuri.controller.services.BidService;
import com.kamiyuri.view.ViewFactory;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.math.BigInteger;

public class BidWindowController extends BaseController {
    @FXML
    private Label errorLabel;
    @FXML
    private TextField bidPriceField;

    public BidWindowController(AuctionManager auctionManager, ViewFactory viewFactory, String fxmlName) {
        super(auctionManager, viewFactory, fxmlName);
    }

    @FXML
    void cancelBtnAction() {

    }

    @FXML
    void submitBtnAction() {
        if (fieldsAreValid()) {
            String bidPrice = bidPriceField.getText();

            BidService bidService = new BidService(auctionManager, bidPrice);
            auctionManager.setBidResponse(null);
            bidService.start();
            bidService.setOnSucceeded(event -> {
                BidResult bidResult = bidService.getValue();

                switch (bidResult) {
                    case SUCCESS:
                        showPopUp("Đấu giá thành công.", Alert.AlertType.INFORMATION);
                        break;
                    case CREATOR_CANT_BID:
                        showPopUp("Đấu giá thất bại. Chủ phòng không đươc tham gia đấu giá.", Alert.AlertType.ERROR);
                        break;
                    case LOWER_THAN_CURRENT_PRICE:
                        showPopUp("Đấu giá thất bại. Giá đưa ra thấp hơn giá hiện tại của vật phẩm.", Alert.AlertType.ERROR);
                        break;
                }
            });
            viewFactory.closeStage((Stage) bidPriceField.getScene().getWindow());
        }
    }

    private boolean fieldsAreValid() {

        if (bidPriceField.getText().isEmpty()) {
            errorLabel.setText("Giá mua trống.");
            return false;
        } else {
            if (!bidPriceField.getText().matches("[0-9]+")) {
                errorLabel.setText("Giá mua phải là số.");
                return false;
            } else if (checkOutOfRange(bidPriceField.getText())) {
                errorLabel.setText("Giá mua quá lớn.");
                return false;
            }
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

    private void showPopUp(String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
