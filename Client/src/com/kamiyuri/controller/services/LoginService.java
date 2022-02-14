package com.kamiyuri.controller.services;

import com.kamiyuri.AuctionManager;
import com.kamiyuri.TCP.RequestFactory;
import com.kamiyuri.controller.LoginResult;
import com.kamiyuri.model.Account;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.io.IOException;

import static com.kamiyuri.TCP.RequestType.LOGIN;

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

    private LoginResult login(){
        String request = RequestFactory.getRequest(LOGIN, account.getData());
        try {
            String response = auctionManager.exchangeMessage(request);
            if (response.isEmpty()){
                return LoginResult.FAILED_BY_UNEXPECTED_ERROR;
            } else {
                account.setUserId(response);
                auctionManager.setAccount(account);
            }
        } catch (IOException e){
            return LoginResult.FAILED_BY_UNEXPECTED_ERROR;
        }
        return LoginResult.SUCCESS;
    }
}
