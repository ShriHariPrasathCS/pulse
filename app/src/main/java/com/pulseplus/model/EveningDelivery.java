package com.pulseplus.model;

import com.google.gson.annotations.Expose;

/**
 * Created by Bright Bridge on 24-Apr-17.
 */

public class EveningDelivery {
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
