package com.pulseplus.model;

import com.google.gson.annotations.Expose;

public class OrderIdGen {

    @Expose
    public String user_id;
    @Expose
    public String precription_type;
    @Expose
    public String prescription;
    @Expose
    public String jid_id;


    public OrderIdGen(String user_id, String precription_type, String prescription, String jid_id) {
        this.user_id = user_id;
        this.precription_type = precription_type;
        this.prescription = prescription;
        this.jid_id = jid_id;
    }

    public class Result {
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
