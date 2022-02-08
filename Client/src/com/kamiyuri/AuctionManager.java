package com.kamiyuri;

import java.io.IOException;

public class AuctionManager {
    private TCP tcp;

    public AuctionManager() throws IOException {
        this.tcp = new TCP();
    }
}
