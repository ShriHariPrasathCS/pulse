package com.pulseplus.model;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

/**
 * Bright Bridge on 11-Apr-17.
 */

public class PendingOrder {
    @Expose
    public String Result;
    @Expose
    public ArrayList<Pending_orders> Pending_orders;

    public String getResult() {
        return Result;
    }

    public ArrayList<PendingOrder.Pending_orders> getPending_orders() {
        return Pending_orders;
    }

    public static class Pending_orders {

        @Expose
        public String id;
        @Expose
        public String jid_id;
        @Expose
        public String user_id;
        @Expose
        public String orderid;
        @Expose
        public String precription_type;
        @Expose
        public String prescription;
        @Expose
        public String chat_by;
        @Expose
        public String total;
        @Expose
        public String status;
        @Expose
        public String minimize;
        @Expose
        public String mini;
        @Expose
        public String cancel;
        @Expose
        public String order_status;
        @Expose
        public String refill;
        @Expose
        public String offers;
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

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getJid_id() {
            return jid_id;
        }

        public void setJid_id(String jid_id) {
            this.jid_id = jid_id;
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

        public String getChat_by() {
            return chat_by;
        }

        public void setChat_by(String chat_by) {
            this.chat_by = chat_by;
        }

        public String getTotal() {
            return total;
        }

        public void setTotal(String total) {
            this.total = total;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getMinimize() {
            return minimize;
        }

        public void setMinimize(String minimize) {
            this.minimize = minimize;
        }

        public String getMini() {
            return mini;
        }

        public void setMini(String mini) {
            this.mini = mini;
        }

        public String getCancel() {
            return cancel;
        }

        public void setCancel(String cancel) {
            this.cancel = cancel;
        }

        public String getOrder_status() {
            return order_status;
        }

        public void setOrder_status(String order_status) {
            this.order_status = order_status;
        }

        public String getRefill() {
            return refill;
        }

        public void setRefill(String refill) {
            this.refill = refill;
        }

        public String getOffers() {
            return offers;
        }

        public void setOffers(String offers) {
            this.offers = offers;
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

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}
