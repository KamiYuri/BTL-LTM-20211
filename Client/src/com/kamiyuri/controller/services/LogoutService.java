package com.kamiyuri.controller.services;

import com.kamiyuri.AuctionManager;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.util.Properties;

public class LogoutService extends Service<LogoutResult> {
    private final AuctionManager auctionManager;

    public LogoutService(AuctionManager auctionManager) {
        this.auctionManager = auctionManager;
    }

    @Override
    protected Task<LogoutResult> createTask() {
        return new Task<LogoutResult>() {
            @Override
            protected LogoutResult call() throws Exception {
                return logout();
            }
        };
    }

    private LogoutResult logout() {
        Properties data = new Properties();
        data.put("userId", auctionManager.getAccount().getUserId());

        String request = RequestFactory.getRequest(RequestType.LOGOUT, data);
        auctionManager.sendRequest(request);

        String response;
        do {
            response = auctionManager.getLogoutResponse();
        } while (response == null);


        if (response == "20") return LogoutResult.SUCCESS;
        return LogoutResult.FAILED;
    }
}
