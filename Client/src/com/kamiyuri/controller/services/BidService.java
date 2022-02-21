package com.kamiyuri.controller.services;

import com.kamiyuri.AuctionManager;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.util.Properties;

public class BidService extends Service<BidResult> {
    private final AuctionManager auctionManager;
    private final String price;

    public BidService(AuctionManager auctionManager, String price) {
        this.auctionManager = auctionManager;
        this.price = price;
    }

    @Override
    protected Task<BidResult> createTask() {
        return new Task<BidResult>() {
            @Override
            protected BidResult call() throws Exception {
                return bid();
            }
        };
    }

    private BidResult bid() {
        Properties data = new Properties();
        data.put("price", price);
        data.put("userId", auctionManager.getAccount().getUserId());
        data.put("roomId", auctionManager.getSelectedRoom().getRoomId());

        String request = RequestFactory.getRequest(RequestType.BID, data);
        auctionManager.sendRequest(request);

        String response;
        do {
            response = auctionManager.getBidResponse();
        } while (response == null);

        response = response.substring(0, 2);

        switch (response) {
            case "50":
                return BidResult.SUCCESS;
            case "51":
                return BidResult.LOWER_THAN_CURRENT_PRICE;
            case "52":
                return BidResult.CREATOR_CANT_BID;
        }
        return null;
    }
}
