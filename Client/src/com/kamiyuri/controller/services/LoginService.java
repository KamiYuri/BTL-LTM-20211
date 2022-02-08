package com.kamiyuri.controller.services;

import com.kamiyuri.AuctionManager;
import com.kamiyuri.controller.LoginResult;
import com.kamiyuri.model.Account;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class LoginService extends Service<LoginResult> {

    private AuctionManager auctionManager;
    private Account account;

    public LoginService(AuctionManager auctionManager, Account account) {
        this.auctionManager = auctionManager;
        this.account = account;
    }

    @Override
    protected Task<LoginResult> createTask() {
        return new Task<LoginResult>() {
            @Override
            protected LoginResult call() throws Exception {
                return login();
            }
        };
    }

    private LoginResult login() {
        return LoginResult.SUCCESS;
    }
}
