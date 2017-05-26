package com.pulseplus;

import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.armor.fontlib.CButton;
import com.armor.fontlib.CEditText;
import com.armor.fontlib.CTextView;
import com.pulseplus.dialog.BanConfirmDialog;
import com.pulseplus.dialog.NumberConfirmDialog;
import com.pulseplus.global.Global;
import com.pulseplus.model.Register;
import com.pulseplus.util.PrefConnect;
import com.pulseplus.web.APIService;
import com.pulseplus.web.RetrofitSingleton;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifyPhoneActivity extends AppCompatActivity implements NumberConfirmDialog.NumberCallback, BanConfirmDialog.BanCallback {

    CEditText edtCountry, edtPhone;
    CTextView titleTxt;
    CButton audio_send;
    ImageView back_otp;
    APIService apiService;
    String number, mobile_no;
    NumberConfirmDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_verify);
        init();
        setListener();
    }

    private void setListener() {
        audio_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callDialog();
            }
        });
        edtPhone.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    callDialog();
                }
                return false;
            }
        });
    }

    private void callDialog() {
        Bundle bundle = new Bundle();
        mobile_no = edtPhone.getText().toString();
        if (mobile_no.length() == 10) {
            number = String.valueOf(edtCountry.getText()) + "" + String.valueOf(edtPhone.getText());
            if (!number.equalsIgnoreCase("")) {
                bundle.putString("number", number);
                dialog = new NumberConfirmDialog();
                dialog.setArguments(bundle);
                dialog.setCallback(VerifyPhoneActivity.this);
                dialog.show(getSupportFragmentManager(), "Confirm");
                View currentView = getCurrentFocus();
                Global.keyboardHide(VerifyPhoneActivity.this);
            }
        } else if (mobile_no.length() == 0) {
            Toast.makeText(VerifyPhoneActivity.this, "Enter Mobile Number", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(VerifyPhoneActivity.this, "Enter Valid Mobile Number", Toast.LENGTH_SHORT).show();
        }
    }

    private void init() {
        edtCountry = (CEditText) findViewById(R.id.edtCountry);
        edtPhone = (CEditText) findViewById(R.id.edtPhone);
        audio_send = (CButton) findViewById(R.id.btn_send);
        titleTxt = (CTextView) findViewById(R.id.titleTxt);
        back_otp = (ImageView) findViewById(R.id.back_otp);

        apiService = RetrofitSingleton.createService(APIService.class);
        dialog = new NumberConfirmDialog();
        titleTxt.setText("Verify Your Phone Number");
        back_otp.setVisibility(View.GONE);
    }

    public void BanConfirmation() {
        final BanConfirmDialog bandialog = new BanConfirmDialog();
        bandialog.setCallback(this);
        bandialog.show(getFragmentManager(), "");

    }

    @Override
    public void onAccept() {
         String deviceToken = PrefConnect.readString(VerifyPhoneActivity.this, PrefConnect.FCMTOKEN, "");

       // String deviceToken = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        PrefConnect.writeString(VerifyPhoneActivity.this, PrefConnect.DEVICE_TOKEN, deviceToken);
        final Register register = new Register(number, "2", deviceToken, null);
        Call<Register.Result> call = apiService.registerUser(register);
        Toast.makeText(VerifyPhoneActivity.this, "OTP will be send to your mobile number shortly", Toast.LENGTH_SHORT).show();

        call.enqueue(new Callback<Register.Result>() {
            @Override
            public void onResponse(Call<Register.Result> call, Response<Register.Result> response) {
                if (response.isSuccessful()) {
                    Register.Result result = response.body();
                    if (result.getResult().equalsIgnoreCase("Success")) {
                        ArrayList<Register.Userdetails> userdetails = result.getUserdetails();
                        PrefConnect.writeString(VerifyPhoneActivity.this, PrefConnect.USER_ID, userdetails.get(0).getId());
                        PrefConnect.writeString(VerifyPhoneActivity.this, PrefConnect.OTP, userdetails.get(0).getOtp());
                        PrefConnect.writeString(VerifyPhoneActivity.this, PrefConnect.OTP_VERIFY, userdetails.get(0).getOtp_verify());
                        PrefConnect.writeString(VerifyPhoneActivity.this, PrefConnect.VERIFY_STATUS, userdetails.get(0).getCustomer_status());
                        PrefConnect.writeString(VerifyPhoneActivity.this, PrefConnect.MOBILE, userdetails.get(0).getMobile());
                        PrefConnect.writeString(VerifyPhoneActivity.this, PrefConnect.JID, userdetails.get(0).getJid());
                        PrefConnect.writeString(VerifyPhoneActivity.this, PrefConnect.JID_PASS, result.getJpass());



                        if (userdetails.get(0).getCustomer_status().equals("3")) {
                            BanConfirmation();
                        } else {
                            Intent intent = new Intent(VerifyPhoneActivity.this, VerifyOTPActivity.class);
                            intent.putExtra("number", number);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                    } else {
                        Toast.makeText(VerifyPhoneActivity.this, result.getStatus(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Register.Result> call, Throwable t) {
                Log.e("TAG", t.toString());
            }
        });
    }

    @Override
    public void onAcceptBanned() {
        finish();
    }

    @Override
    protected void onDestroy() {
        Process.killProcess(Process.myPid());
        super.onDestroy();
    }
}
