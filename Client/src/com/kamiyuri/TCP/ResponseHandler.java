package com.kamiyuri.TCP;


import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class ResponseHandler {
    public static String handle(String response) {
        RequestType code = RequestType.values()[Character.getNumericValue(response.charAt(0)) - 1];

        switch (code) {
            case LOGIN:
                return loginHandle(response);
            case LOGOUT:
                return logoutHandle(response);
            default:
                return null;
        }
    }

    private static String logoutHandle(String response) {
        if (response.charAt(1) == '1') {
            return null;
        }

        return "1";
    }

    private static String loginHandle(String response) {
        if (response.charAt(1) == '1') {
            return null;
        }
        return response.substring(2).replace("\r\n", "");
    }
}
