package com.kamiyuri.controller.services;

import com.kamiyuri.AuctionManager;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.util.Properties;

public class CreateRoomService extends Service<CreateRoomResult> {
    private final AuctionManager auctionManager;
    private final Properties data;

    public CreateRoomService(AuctionManager auctionManager, Properties data) {
        this.auctionManager = auctionManager;
        this.data = data;
    }

    @Override
    protected Task<CreateRoomResult> createTask() {
        return new Task<>() {
            @Override
            protected CreateRoomResult call() {
                return createRoom();
            }
        };
    }

    private CreateRoomResult createRoom() {
        data.put("userId", auctionManager.getAccount().getUserId());
        String request = RequestFactory.getRequest(RequestType.CREATE_ROOM, data); //requse
        auctionManager.sendRequest(request);
        String response;
        do {
            response = auctionManager.getCreateRoomResponse();
        } while (response == null);

        if (response.charAt(1) == '0') {
            return CreateRoomResult.SUCCESS;
        }

        return CreateRoomResult.FAILED_BY_UNEXPECTED_ERROR;
    }
}
