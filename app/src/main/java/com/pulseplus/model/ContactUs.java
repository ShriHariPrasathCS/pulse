package com.pulseplus.model;

import com.google.gson.annotations.Expose;

/**
 * Created by Bright Bridge on 31-Jan-17.
 */

public class ContactUs {

    @Expose
    public String email;
    @Expose
    public String message;

    public String getEmail() {
        return email;
    }

    public String getMessage() {
        return message;
    }

    public ContactUs(String email, String message){
        this.email = email;
        this.message = message;
    }

    public class Result{

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


}
