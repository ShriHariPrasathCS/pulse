package com.pulseplus.model;

import com.google.gson.annotations.Expose;

/**
 * Bright Bridge on 18-Oct-16.
 */

public class CancelOrder {
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
