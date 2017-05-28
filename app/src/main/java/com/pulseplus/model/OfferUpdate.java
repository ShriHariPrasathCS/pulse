package com.pulseplus.model;

import com.google.gson.annotations.Expose;

/**
 * Bright Bridge on 13-Jan-17.
 */

public class OfferUpdate {
    @Expose
    public String Result;
    @Expose
    public String Status;

    public String getResult() {
        return Result;
    }

    public String getStatus() {
        return Status;
    }
}
