package com.kamiyuri.controller.services;

import com.kamiyuri.AuctionManager;
import com.kamiyuri.model.Account;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import static com.kamiyuri.controller.services.RequestType.LOGIN;

public class LoginService extends Service<LoginResult> {

    private final AuctionManager auctionManager;
    private final Account account;

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
        String request = RequestFactory.getRequest(LOGIN, account.getData()); //requse
        auctionManager.sendRequest(request);
        String response;
        do {
            response = auctionManager.getLoginResponse();
        } while (response == null);


        if (response.isEmpty()) {
            return LoginResult.FAILED_BY_UNEXPECTED_ERROR;
        } else if (response.charAt(1) == '1') {
            return LoginResult.FAILED_BY_CREDENTIALS;
        }

        account.setUserId(response.substring(2));
        auctionManager.setAccount(account);
        return LoginResult.SUCCESS;
    }
}
