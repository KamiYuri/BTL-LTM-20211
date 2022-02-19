package com.kamiyuri.TCP;

import java.util.Properties;

public class RequestFactory {
    public RequestFactory() {
    }

    public static String getRequest(RequestType type, Properties data){
        switch (type){
            case LOGIN:
                return createLoginRequest(data);
            case LOGOUT:
                return createLogoutRequest(data);
            case SHOW_ROOM:
                return createShowRoomRequest();
            case JOIN_ROOM:
                return createJoinRoomRequest(data);
            case BID:
                return createBidRequest(data);
            case BUY:
                return createBuyRequest(data);
            case CREATE_ROOM:
                return createCreateRoomRequest(data);
            default:
                return null;
        }
    }

    private static String createCreateRoomRequest(Properties data) {
        return "CREATE" + data.getProperty("itemName") + Delimiter.Two() + data.getProperty("itemDescription")  + data.getProperty("startingPrice") + Delimiter.Two() + data.getProperty("buyImmediatelyPrice") + Delimiter.One();
    }

    private static String createBuyRequest(Properties data) {
        return "BUYNOW" + data.getProperty("userId") + Delimiter.Two() + data.getProperty("roomId") + Delimiter.One();
    }

    private static String createBidRequest(Properties data) {
        return "BID___" + data.getProperty("price") + Delimiter.Two() + data.getProperty("userId") + Delimiter.Two() + data.getProperty("roomId") + Delimiter.One();
    }

    private static String createJoinRoomRequest(Properties data) {
        return "JOIN__" + data.getProperty("userId") + Delimiter.Two() + data.getProperty("roomId") + Delimiter.One();
    }

    private static String createShowRoomRequest() {
        return "SHOW__" + Delimiter.One();
    }

    private static String createLogoutRequest(Properties data) {
        return "LOGOUT" + data.getProperty("userId") + Delimiter.One();
    }

    private static String createLoginRequest(Properties data) {
        return "LOGIN_" + data.getProperty("username") + Delimiter.Two() + data.getProperty("password") + Delimiter.One();
    }
}
