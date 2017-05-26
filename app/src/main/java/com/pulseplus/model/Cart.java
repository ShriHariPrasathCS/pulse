package com.pulseplus.model;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

/**
 * Created by Bright Bridge on 23-Mar-17.
 */

public class Cart {

    @Expose
    public String Result;
    @Expose
    public ArrayList<CartList> cart_list;

    public String getResult() {
        return Result;
    }

    public ArrayList<Cart.CartList> getCart_list() {
        return cart_list;
    }

    public static class CartList{

        @Expose
        public String id;
        @Expose
        public String userid;
        @Expose
        public String name;
        @Expose
        public String qty;
        @Expose
        public String price;
        @Expose
        public String total;
        @Expose
        public String added_date;
        @Expose
        public String updated_date;

        public void setId(String id) {
            this.id = id;
        }

        public void setUserid(String userid) {
            this.userid = userid;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setQty(String qty) {
            this.qty = qty;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public void setTotal(String total) {
            this.total = total;
        }

        public void setAdded_date(String added_date) {
            this.added_date = added_date;
        }

        public void setUpdated_date(String updated_date) {
            this.updated_date = updated_date;
        }

        public String getId() {
            return id;
        }

        public String getUserid() {
            return userid;
        }

        public String getName() {
            return name;
        }

        public String getQty() {
            return qty;
        }

        public String getPrice() {
            return price;
        }

        public String getTotal() {
            return total;
        }

        public String getAdded_date() {
            return added_date;
        }

        public String getUpdated_date() {
            return updated_date;
        }
    }

}
