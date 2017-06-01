package com.pulseplus.model;

import com.google.gson.annotations.Expose;

/**
 * Bright Bridge on 05-Apr-17.
 */

public class CartMedicineDelete {

    public static class Response {
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

    public static class Request {
        @Expose
        public String orderid;
        @Expose
        public String name;

        public Request(String orderid, String name) {
            this.orderid = orderid;
            this.name = name;
        }
    }


}
