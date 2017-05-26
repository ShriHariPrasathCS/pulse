package com.pulseplus.model;


import android.graphics.Bitmap;

import com.google.gson.annotations.Expose;

public class Child {

    @Expose
    public String chat_usertype;
    @Expose
    public String message;
    @Expose
    public String message_type;
    @Expose
    public String sent_date;
    @Expose
    public Bitmap dum_image;


    public Child(String chat_usertype, String message_type, String message, String sent_date) {
        this.chat_usertype = chat_usertype;
        this.message = message;
        this.message_type = message_type;
        this.sent_date = sent_date;
        this.dum_image = dum_image;
    }

    public String getChat_usertype() {
        return chat_usertype;
    }

    public String getMessage() {
        return message;
    }

    public String getMessage_type() {
        return message_type;
    }

    public String getSent_date() {
        return sent_date;
    }

    public Bitmap getDum_image() {
        return dum_image;
    }
}
