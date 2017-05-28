package com.pulseplus.model;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

/**
 * Bright Bridge on 25-Apr-17.
 */

public class EditCart {

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
        @Expose
        public String qty;
        @Expose
        public String total;

        @Expose
        public ArrayList<Cart> cart;

//        public Request(String orderid, String name, String qty, String total) {
//            this.orderid = orderid;
//            this.name = name;
//            this.qty = qty;
//            this.total = total;
//        }

        public Request(String orderid, ArrayList<Cart> cart) {
            this.orderid = orderid;
            this.cart = cart;
        }
    }

    public static class Cart {
        @Expose
        public String name;
        @Expose
        public String qty;
        @Expose
        public String total;

        public Cart(String name, String qty, String total) {
            this.name = name;
            this.qty = qty;
            this.total = total;
        }
    }
}
