package com.pulseplus.model;

/**
 * Bright Bridge on 28-Dec-16.
 */

public class Internet {
    public boolean isConnected;

    public Internet(boolean isConnected) {
        this.isConnected = isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    public boolean isConnected() {
        return isConnected;
    }
}
