package com.pulseplus.model;

import com.google.gson.annotations.Expose;

/**
 * Created by Bright Bridge on 18-Apr-17.
 */

public class ContinuePendingOrder {

    @Expose
    public String Result;
    @Expose
    public String Status;
    @Expose
    public String Orderid;

    public String getResult() {
        return Result;
    }

    public String getStatus() {
        return Status;
    }

    public String getOrderid() {
        return Orderid;
    }
}
