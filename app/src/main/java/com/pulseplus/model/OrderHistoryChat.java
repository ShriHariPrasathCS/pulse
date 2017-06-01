package com.pulseplus.model;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

/**
 * Bright Bridge on 06-Oct-16.
 */

public class OrderHistoryChat {

    @Expose
    public String Result;
    @Expose
    public ArrayList<Chat_history> Chat_history;

    public String getResult() {
        return Result;
    }

    public ArrayList<Chat_history> getChat_history() {
        return Chat_history;
    }

    public class Chat_history {

        @Expose
        public String id;
        @Expose
        public String order_id;
        @Expose
        public String chat_usertype;
        @Expose
        public String from_id;
        @Expose
        public String to_id;
        @Expose
        public String message;
        @Expose
        public String message_type;
        @Expose
        public String chat_type;
        @Expose
        public String sent_date;
        @Expose
        public String isget_alert;
        @Expose
        public String chat_closed_by;
        @Expose
        public String is_viewed_user;
        @Expose
        public String is_viewed_admin;
        @Expose
        public String added_date;
        @Expose
        public String updated_date;

        public String getMessage_type() {
            return message_type;
        }

        public String getId() {
            return id;
        }

        public String getOrder_id() {
            return order_id;
        }

        public String getChat_usertype() {
            return chat_usertype;
        }

        public String getFrom_id() {
            return from_id;
        }

        public String getTo_id() {
            return to_id;
        }

        public String getMessage() {
            return message;
        }

        public String getChat_type() {
            return chat_type;
        }

        public String getSent_date() {
            return sent_date;
        }

        public String getIsget_alert() {
            return isget_alert;
        }

        public String getChat_closed_by() {
            return chat_closed_by;
        }

        public String getIs_viewed_user() {
            return is_viewed_user;
        }

        public String getIs_viewed_admin() {
            return is_viewed_admin;
        }

        public String getAdded_date() {
            return added_date;
        }

        public String getUpdated_date() {
            return updated_date;
        }
    }
}
