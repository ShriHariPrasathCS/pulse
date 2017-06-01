package com.pulseplus;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.pulseplus.global.Global;
import com.pulseplus.model.ChatBean;
import com.pulseplus.model.Internet;
import com.pulseplus.util.PrefConnect;
import com.pulseplus.web.APIService;
import com.pulseplus.web.RetrofitSingleton;
import com.wang.avi.AVLoadingIndicatorView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends AppCompatActivity {
    AVLoadingIndicatorView avi;
    private ProgressDialog p;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        p = Global.initProgress(SplashActivity.this);
        avi = (AVLoadingIndicatorView) findViewById(R.id.avi);
        // MyFirebaseInstanceIDService id = new MyFirebaseInstanceIDService();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                APIService apiService = RetrofitSingleton.createService(APIService.class);
                final String user_id = PrefConnect.readString(SplashActivity.this, PrefConnect.USER_ID, "");
                final String otp = PrefConnect.readString(SplashActivity.this, PrefConnect.OTP, "");
                final String verify_status = PrefConnect.readString(SplashActivity.this, PrefConnect.VERIFY_STATUS, "0");
                final String otp_verify = PrefConnect.readString(SplashActivity.this, PrefConnect.OTP_VERIFY, "");
                final String orderId = PrefConnect.readString(SplashActivity.this, PrefConnect.ORDER_ID, "");
                if (!orderId.equals("")) {
                    Call<ChatBean> call = apiService.chatBean(orderId);
                    call.enqueue(new Callback<ChatBean>() {
                        @Override
                        public void onResponse(Call<ChatBean> call, Response<ChatBean> response) {
                            // Global.dismissProgress(p);
                            if (response.isSuccessful()) {
                                ChatBean chatBean = response.body();
                                if (chatBean.getResult().equalsIgnoreCase("Success")) {
                                    if (chatBean.getChat_status().equalsIgnoreCase("3")) {
                                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ChatBean> call, Throwable t) {
                            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                }
                if (user_id.equalsIgnoreCase("")) {
                    Intent intent = new Intent(SplashActivity.this, WalkThroughActivity.class);
                    startActivity(intent);
                    finish();
                } else if (otp.equals("")) {
                    Intent intent = new Intent(SplashActivity.this, VerifyPhoneActivity.class);
                    startActivity(intent);
                    finish();
                } else if (!otp.equals("") && otp_verify.equals("0")) {
                    if (verify_status.equals("1") || verify_status.equals("2")) {
                        Intent intent = new Intent(SplashActivity.this, VerifyOTPActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
                if (otp_verify.equals("1") && orderId.equals("")) {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        }, 3000);
    }

    @Override
    public void onStart() {
        super.onStart();
        startAnim();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        stopAnim();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onInternetConnectionCheck(Internet internet) {
        if (internet.isConnected) {
            //   Global.CustomToast(SplashActivity.this, "Internet Avaliable");
        } else {
            Global.CustomToast(SplashActivity.this, "Check your internet connection");
            //Global.Toast(MainActivity.this, "Check your internet connection");
        }
    }

    void startAnim() {
        avi.show();
        // or avi.smoothToShow();
    }

    void stopAnim() {
        avi.hide();
        // or avi.smoothToHide();
    }


}
