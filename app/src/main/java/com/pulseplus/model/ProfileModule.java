package com.pulseplus.model;


import com.google.gson.annotations.Expose;

import java.util.ArrayList;

public class ProfileModule {

    @Expose
    public String Result;
    @Expose
    public ArrayList<Details> details;
    @Expose
    public String Status;

    public String getResult() {
        return Result;
    }

    public ArrayList<Details> getDetails() {
        return details;
    }

    public String getStatus() {
        return Status;
    }

    public class Details {
        @Expose
        public String id;
        @Expose
        public String name;
        @Expose
        public String mobile;
        @Expose
        public String email;
        @Expose
        public String address;
        @Expose
        public String device_token;
        @Expose
        public String customer_status;

        public String getCustomer_status() {
            return customer_status;
        }

        public String getDevice_token() {
            return device_token;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getMobile() {
            return mobile;
        }

        public String getEmail() {
            return email;
        }

        public String getAddress() {
            return address;
        }
    }
}
