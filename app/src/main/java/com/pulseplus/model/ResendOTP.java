package com.pulseplus.model;

import com.google.gson.annotations.Expose;

public class ResendOTP {
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
