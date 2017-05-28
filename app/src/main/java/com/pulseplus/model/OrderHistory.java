package com.pulseplus.model;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

/**
 * Bright Bridge on 05-Oct-16.
 */

public class OrderHistory {

    @Expose
    public String Result;
    @Expose
    public ArrayList<Order_history> Order_history;

    public String getResult() {
        return Result;
    }

    public ArrayList<OrderHistory.Order_history> getOrder_history() {
        return Order_history;
    }

    public static class Order_history {
        @Expose
        public String id;
        @Expose
        public String user_id;
        @Expose
        public String orderid;
        @Expose
        public String precription_type;
        @Expose
        public String prescription;
        @Expose
        public String status;
        @Expose
        public String order_date;
        @Expose
        public String notes;
        @Expose
        public String added_date;
        @Expose
        public String updated_date;
        @Expose
        public String message;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getOrderid() {
            return orderid;
        }

        public void setOrderid(String orderid) {
            this.orderid = orderid;
        }

        public String getPrecription_type() {
            return precription_type;
        }

        public void setPrecription_type(String precription_type) {
            this.precription_type = precription_type;
        }

        public String getPrescription() {
            return prescription;
        }

        public void setPrescription(String prescription) {
            this.prescription = prescription;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getOrder_date() {
            return order_date;
        }

        public void setOrder_date(String order_date) {
            this.order_date = order_date;
        }

        public String getNotes() {
            return notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }

        public String getAdded_date() {
            return added_date;
        }

        public void setAdded_date(String added_date) {
            this.added_date = added_date;
        }

        public String getUpdated_date() {
            return updated_date;
        }

        public void setUpdated_date(String updated_date) {
            this.updated_date = updated_date;
        }
    }
}
