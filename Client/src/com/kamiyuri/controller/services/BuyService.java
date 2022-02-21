package com.kamiyuri.controller.services;

import com.kamiyuri.AuctionManager;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.util.Properties;

public class BuyService extends Service<BuyResult> {
    private final AuctionManager auctionManager;

    public BuyService(AuctionManager auctionManager) {
        this.auctionManager = auctionManager;
    }

    @Override
    protected Task<BuyResult> createTask() {
        return new Task<BuyResult>() {
            @Override
            protected BuyResult call() throws Exception {
                return buy();
            }
        };
    }

    private BuyResult buy() {
        Properties data = new Properties();
        data.put("userId", auctionManager.getAccount().getUserId());
        data.put("roomId", auctionManager.getSelectedRoom().getRoomId());

        String request = RequestFactory.getRequest(RequestType.BUY, data);
        System.out.println("Buỷequest" + request);
        auctionManager.sendRequest(request);

        String response;
        do {
            response = auctionManager.getBuyResponse();
        } while (response == null);

        response = response.substring(0, 2);

        System.out.println("buy réponse" + response);

        switch (response) {
            case "60":
                return BuyResult.SUCCESS;
            case "61":
                return BuyResult.ALREADY_SOLD;
            case "62":
                return BuyResult.CREATOR_CANT_BID;
        }
        return null;
    }
}
