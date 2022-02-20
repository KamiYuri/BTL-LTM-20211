package com.kamiyuri.controller.services;

import com.kamiyuri.AuctionManager;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.util.Properties;
import java.util.Scanner;

public class JoinRoomService extends Service<JoinRoomResult> {
    private final AuctionManager auctionManager;

    public JoinRoomService(AuctionManager auctionManager) {
        this.auctionManager = auctionManager;
    }

    @Override
    protected Task<JoinRoomResult> createTask() {
        return new Task<JoinRoomResult>() {
            @Override
            protected JoinRoomResult call() throws Exception {
                return joinRoom();
            }
        };
    }

    private JoinRoomResult joinRoom() {
        Properties data = new Properties();
        data.put("userId", auctionManager.getAccount().getUserId());
        data.put("roomId", auctionManager.getSelectedRoom().getRoomId());

        String request = RequestFactory.getRequest(RequestType.JOIN_ROOM, data);
        auctionManager.sendRequest(request);
        String response;
        do {
            response = auctionManager.getJoinRoomResponse();
        } while (response == null);
        return handleResponse(response);
    }

    private JoinRoomResult handleResponse(String response) {
        if (response.charAt(1) == '1') {
            Scanner scanner = new Scanner(response.substring(2));
            scanner.useDelimiter(Delimiter.Two());

            String owner = scanner.next();
            String currentPrice = scanner.next();

            auctionManager.setSelectedRoomCurrentPrice(currentPrice);

            if (owner.equals("0")) {
                return JoinRoomResult.NO_ONE_BUY;
            } else {
                if (owner.equals(auctionManager.getAccount().getUserId())) {
                    return JoinRoomResult.BOUGHT_BY_USER;
                } else {
                    return JoinRoomResult.BOUGHT_BY_ANOTHER;
                }
            }
        } else {
            return JoinRoomResult.SUCCESS;
        }
    }
}
