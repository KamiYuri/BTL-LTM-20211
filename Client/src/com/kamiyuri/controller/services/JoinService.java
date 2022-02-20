package com.kamiyuri.controller.services;

import com.kamiyuri.AuctionManager;
import com.kamiyuri.model.Account;
import com.kamiyuri.model.Room;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.util.Scanner;

public class JoinService extends Service<JoinResult> {
    private final AuctionManager auctionManager;
    private final Account account;
    private final Room room;

    public JoinService(AuctionManager auctionManager, Account account, Room room) {
        this.auctionManager = auctionManager;
        this.account = account;
        this.room = room;
    }

    @Override
    protected Task<JoinResult> createTask() {
        return new Task<JoinResult>() {
            @Override
            protected JoinResult call() throws Exception {
                return joinRoom();
            }
        };
    }

    private JoinResult joinRoom() {
        String request = RequestFactory.getRequest(RequestType.JOIN_ROOM, account.getData()); //requse
        auctionManager.sendRequest(request);
        String response;
        do {
            response = auctionManager.getLoginResponse();
        } while (response == null);


        if (response.charAt(1) == '1') {
            Scanner scanner = new Scanner(response.substring(2));
            scanner.useDelimiter(Delimiter.Two());

            String owner = scanner.next();
            String currentPrice = scanner.next();

            if (owner.equals('0')) {
                return JoinResult.NO_ONE_BUY;
            } else {
                if (owner.equals(account.getUserId())) {
                    return JoinResult.BOUGT_BY_USER;
                } else {
                    return JoinResult.BOUGT;
                }
            }
        } else {
            return JoinResult.SUCCESS;
        }
    }
}
