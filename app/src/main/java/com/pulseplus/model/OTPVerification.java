package com.pulseplus.model;

import android.telecom.Call;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.Since;

public class OTPVerification {
    @Expose
    String user_id;
    @Expose
    String OTP;

    public OTPVerification(String user_id, String OTP) {
        this.user_id = user_id;
        this.OTP = OTP;
    }

    public class Result {
        @Expose
        String Result;
        @Expose
        String Status;

        public Details getDetails() {
            return details;
        }

        @Expose
        public Details details;

        public String getResult() {
            return Result;
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
            public String address;
            @Expose
            public String tags;
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

            public String getAddress() {
                return address;
            }

            public String getTags() {
                return tags;
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
}
