package com.kamiyuri.controller;

import com.kamiyuri.AuctionManager;
import com.kamiyuri.view.ViewFactory;

public abstract class BaseController {
    protected AuctionManager auctionManager;
    protected ViewFactory viewFactory;
    protected String fxmlName;

    public BaseController(AuctionManager auctionManager, ViewFactory viewFactory, String fxmlName) {
        this.auctionManager = auctionManager;
        this.viewFactory = viewFactory;
        this.fxmlName = fxmlName;
    }

    public String getFxmlName() {
        return this.fxmlName;
    }

}
