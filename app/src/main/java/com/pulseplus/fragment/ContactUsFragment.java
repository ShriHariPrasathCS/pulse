package com.pulseplus.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.armor.fontlib.CEditText;
import com.pulseplus.MainActivity;
import com.pulseplus.R;
import com.pulseplus.global.Global;
import com.pulseplus.model.ContactUs;
import com.pulseplus.model.Internet;
import com.pulseplus.navigation.DrawerFragment;
import com.pulseplus.web.APIService;
import com.pulseplus.web.RetrofitSingleton;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContactUsFragment extends Fragment {
    View view;
    APIService apiService;
    private CEditText edtMsg, edtEmail;
    private Button btnSend;
    private boolean error;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_contact_us, container, false);
        init();
        setListener();
        return view;
    }

    private void setListener() {
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validation();
                if (!error) {

                    ContactUs contactUs = new ContactUs(edtEmail.getText().toString(), edtMsg.getText().toString());
                    Call<ContactUs.Result> call = apiService.contactUs(contactUs);
                    call.enqueue(new Callback<ContactUs.Result>() {
                        @Override
                        public void onResponse(Call<ContactUs.Result> call, Response<ContactUs.Result> response) {

                            if (response.isSuccessful()) {
                                ContactUs.Result result = response.body();
                                if (result.getResult().equalsIgnoreCase("Success")) {
                                    // Global.dismissProgress(p);
                                    Global.CustomToast(getActivity(), result.getStatus());
                                    edtEmail.setText("");
                                    edtMsg.setText("");
                                } else {
                                    // Global.dismissProgress(p);
                                    Global.CustomToast(getActivity(), result.getStatus());
                                }
                            }

                        }

                        @Override
                        public void onFailure(Call<ContactUs.Result> call, Throwable t) {

                        }
                    });

                }


            }
        });
    }

    private void validation() {
        String email = edtEmail.getText().toString();
        if (!email.equals(" ") && !email.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")) {
            Global.CustomToast(getActivity(), "Enter valid EmailId");
            error = true;
        } else if (edtMsg.getText().toString().equals("")) {
            Global.CustomToast(getActivity(), "Enter the message");
            error = true;
        } else {
            error = false;
        }
    }

    private void init() {
        edtMsg = (CEditText) view.findViewById(R.id.edtMsg);
        btnSend = (Button) view.findViewById(R.id.btnSend);
        edtEmail = (CEditText) view.findViewById(R.id.edtEmail);
        apiService = RetrofitSingleton.createService(APIService.class);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onAttached(DrawerFragment.CONTACT);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onInternetConnectionCheck(Internet internet) {
        if (internet.isConnected) {
            //   Global.CustomToast(getActivity(), "Internet Avaliable");
        } else {
            Global.CustomToast(getActivity(), "Check your internet connection");
            //Global.Toast(MainActivity.this, "Check your internet connection");
        }
    }


}
