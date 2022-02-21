package com.kamiyuri.model;

import java.util.Properties;

public class Account {
    private final String username;
    private final String password;
    private String userId;

    public Account(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Properties getData() {
        Properties properties = new Properties();
        properties.put("username", this.username);
        properties.put("password", this.password);

        return properties;
    }

}


