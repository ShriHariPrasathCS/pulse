package com.pulseplus.model;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

public class Register {

    @Expose
    public String mobile;
    @Expose
    public String device_type;
    @Expose
    public String device_token;
    @Expose
    public String udid;

    public Register(String mobile, String device_type, String device_token, String udid) {
        this.mobile = mobile;
        this.device_type = device_type;
        this.device_token = device_token;
        this.udid = udid;

    }

    public class Result {
        @Expose
        public String Result;
        @Expose
        public String Status;
        @Expose
        public ArrayList<Userdetails> Userdetails;
        @Expose
        public String jpass;

        public String getJpass() {
            return jpass;
        }

        public String getResult() {
            return Result;
        }

        public String getStatus() {
            return Status;
        }

        public ArrayList<Register.Userdetails> getUserdetails() {
            return Userdetails;
        }
    }

    public class Userdetails {
        @Expose
        public String id;
        @Expose
        public String name;
        @Expose
        public String mobile;
        @Expose
        public String email;
        @Expose
        public String notes;
        @Expose
        public String status;
        @Expose
        public String otp;
        @Expose
        public String otp_verify;
        @Expose
        public String resend_otp;
        @Expose
        public String otp_time;
        @Expose
        public String address;
        @Expose
        public String chat_status;
        @Expose
        public String device_type;
        @Expose
        public String device_token;
        @Expose
        public String added_time;
        @Expose
        public String updated_time;
        @Expose
        public String jid;
        @Expose
        public String jid_password;
        @Expose
        public String customer_status;


        public String getCustomer_status() {
            return customer_status;
        }

        public String getJid() {
            return jid;
        }

        public String getJid_password() {
            return jid_password;
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

        public String getNotes() {
            return notes;
        }

        public String getStatus() {
            return status;
        }

        public String getOtp() {
            return otp;
        }

        public String getOtp_verify() {
            return otp_verify;
        }

        public String getResend_otp() {
            return resend_otp;
        }

        public String getOtp_time() {
            return otp_time;
        }

        public String getAddress() {
            return address;
        }

        public String getChat_status() {
            return chat_status;
        }

        public String getDevice_type() {
            return device_type;
        }

        public String getDevice_token() {
            return device_token;
        }

        public String getAdded_time() {
            return added_time;
        }

        public String getUpdated_time() {
            return updated_time;
        }
    }


}
