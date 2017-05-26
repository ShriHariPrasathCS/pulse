package com.pulseplus.model;

import com.google.gson.annotations.Expose;

public class SendMessage {
    @Expose
    public String user_id;
    @Expose
    public String order_id;
    @Expose
    public String to_id;
    @Expose
    public String message;
    @Expose
    public String message_type;

    public SendMessage(String user_id, String order_id, String to_id, String message, String message_type) {
        this.user_id = user_id;
        this.order_id = order_id;
        this.to_id = to_id;
        this.message = message;
        this.message_type = message_type;
    }

    public class Result {
        @Expose
        public String Result;
        @Expose
        public String Status;

        public String getStatus() {
            return Status;
        }

        public String getResult() {
            return Result;
        }
    }
}
