package com.pulseplus;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.armor.fontlib.CButton;
import com.armor.fontlib.CEditText;
import com.armor.fontlib.CTextView;
import com.pulseplus.global.Global;
import com.pulseplus.model.OTPVerification;
import com.pulseplus.model.ResendOTP;
import com.pulseplus.util.PrefConnect;
import com.pulseplus.web.APIService;
import com.pulseplus.web.RetrofitSingleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifyOTPActivity extends AppCompatActivity {

    CButton verify, resend;
    CTextView txtNumber, titleTxt;
    ImageView back_otp;
    String number;
    //CEditText edt1, edt2, edt3, edt4, edt5, edt6;
    CEditText edittext;
    APIService apiservice;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);
        init();
        setListener();
    }

    private void setListener() {
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                otpVerification();
            }
        });

        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user_id = PrefConnect.readString(VerifyOTPActivity.this, PrefConnect.USER_ID, "");
                Call<ResendOTP> call = apiservice.resendOtp(user_id);
                call.enqueue(new Callback<ResendOTP>() {
                    @Override
                    public void onResponse(Call<ResendOTP> call, Response<ResendOTP> response) {
                        if (response.isSuccessful()) {
                            ResendOTP resend = response.body();
                            if (resend.getResult().equalsIgnoreCase("Success")) {
                                Toast.makeText(VerifyOTPActivity.this, resend.getStatus(), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(VerifyOTPActivity.this, resend.getStatus(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResendOTP> call, Throwable t) {

                    }
                });
            }
        });

        back_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void otpVerification() {

        final String edt_otp = edittext.getText().toString();
        String user_id = PrefConnect.readString(VerifyOTPActivity.this, PrefConnect.USER_ID, "");
        OTPVerification otpVerification = new OTPVerification(user_id, edt_otp);
        Call<OTPVerification.Result> call = apiservice.otpVerify(otpVerification);
        call.enqueue(new Callback<OTPVerification.Result>() {
            @Override
            public void onResponse(Call<OTPVerification.Result> call, Response<OTPVerification.Result> response) {
                if (response.isSuccessful()) {
                    OTPVerification.Result result = response.body();
                    if (result.getResult().equalsIgnoreCase("Success")) {
                        OTPVerification.Result.Details details = result.getDetails();
                        Toast.makeText(VerifyOTPActivity.this, result.getStatus(), Toast.LENGTH_SHORT).show();
                        PrefConnect.writeString(VerifyOTPActivity.this, PrefConnect.USER_ID, details.getId());
                        PrefConnect.writeString(VerifyOTPActivity.this, PrefConnect.OTP, details.getOtp());
                        PrefConnect.writeString(VerifyOTPActivity.this, PrefConnect.OTP_VERIFY, details.getOtp_verify());
                        Intent intent = new Intent(VerifyOTPActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } else {
                        edittext.setText("");
                        Toast.makeText(VerifyOTPActivity.this, result.getStatus(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<OTPVerification.Result> call, Throwable t) {

            }
        });
    }


    private void init() {
        resend = (CButton) findViewById(R.id.btn_resend);
        verify = (CButton) findViewById(R.id.btn_verify);
        edittext = (CEditText) findViewById(R.id.otp);
        txtNumber = (CTextView) findViewById(R.id.text);
        titleTxt = (CTextView) findViewById(R.id.titleTxt);
        number = getIntent().getStringExtra("number");
        apiservice = RetrofitSingleton.createService(APIService.class);
        String mobileNo = PrefConnect.readString(VerifyOTPActivity.this, PrefConnect.MOBILE, "");
        txtNumber.setText(mobileNo);
        back_otp = (ImageView) findViewById(R.id.back_otp);
        titleTxt.setText("Verify " + number);


    }
    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        //Checking for fragment count on backstack
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else if (!doubleBackToExitPressedOnce) {
            this.doubleBackToExitPressedOnce = true;
            // Toast.makeText(this, "Please click BACK again to exit.", Toast.LENGTH_SHORT).show();
            Global.CustomToast(VerifyOTPActivity.this, "Please click BACK again to exit.");

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        } else {
            super.onBackPressed();
        }
    }
}
