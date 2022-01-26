package com.ltm.client.Utils;

public final class RequestMaker {
    private RequestMaker(){
        throw new UnsupportedOperationException("Utility class and cannot be instantiated");
    }

    public static String login(String username, String password){
        return username + password;
    }
}
