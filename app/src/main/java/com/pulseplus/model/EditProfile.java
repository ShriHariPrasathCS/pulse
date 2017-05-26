package com.pulseplus.model;

import com.google.gson.annotations.Expose;

public class EditProfile {
    @Expose
    public String name;
    @Expose
    public String email;
    @Expose
    public String user_id;
    @Expose
    public String address;

    public EditProfile(String name, String email, String user_id, String address) {
        this.name = name;
        this.email = email;
        this.user_id = user_id;
        this.address = address;
    }

    public class Result {
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

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getAddress() {
        return address;
    }
}
