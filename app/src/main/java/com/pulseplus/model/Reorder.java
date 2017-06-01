package com.pulseplus.model;

import com.google.gson.annotations.Expose;

/**
 * Bright Bridge on 06-Oct-16.
 */

public class Reorder {

    public static class Request {
        @Expose
        public String orderid;

        public Request(String orderid) {
            this.orderid = orderid;
        }
    }

    public static class Response {
        @Expose
        public String Result;
        @Expose
        public String Status;
        @Expose
        public String orderid;

        public String getResult() {
            return Result;
        }

        public String getStatus() {
            return Status;
        }

        public String getOrderid() {
            return orderid;
        }
    }
}
