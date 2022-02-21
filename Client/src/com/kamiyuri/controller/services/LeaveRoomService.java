package com.kamiyuri.controller.services;

import com.kamiyuri.AuctionManager;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class LeaveRoomService extends Service<LeaveRoomResult> {
    private final AuctionManager auctionManager;

    public LeaveRoomService(AuctionManager auctionManager) {
        this.auctionManager = auctionManager;
    }

    @Override
    protected Task<LeaveRoomResult> createTask() {
        return new Task<LeaveRoomResult>() {
            @Override
            protected LeaveRoomResult call() throws Exception {
                return leaveRoom();
            }
        };
    }

    private LeaveRoomResult leaveRoom() {

        Properties data = new Properties();
        data.put("userId", auctionManager.getAccount().getUserId());
        data.put("roomId", auctionManager.getSelectedRoom().getRoomId());

        String request = RequestFactory.getRequest(RequestType.LEAVE_ROOM, data);

        System.out.println("leave request: " + request);

        auctionManager.sendRequest(request);

        String response;
        do {
            response = auctionManager.getLeaveRoomResponse();
        } while (response == null);


        if (response.substring(0, 2) == "90") return LeaveRoomResult.SUCCESS;
        return LeaveRoomResult.FAILED;
    }
}
