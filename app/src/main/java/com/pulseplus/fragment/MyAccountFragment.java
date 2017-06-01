package com.pulseplus.fragment;


import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.armor.fontlib.CEditText;
import com.pulseplus.MainActivity;
import com.pulseplus.R;
import com.pulseplus.global.Global;
import com.pulseplus.model.EditProfile;
import com.pulseplus.model.Internet;
import com.pulseplus.model.ProfileModule;
import com.pulseplus.navigation.DrawerFragment;
import com.pulseplus.util.PrefConnect;
import com.pulseplus.web.APIService;
import com.pulseplus.web.RetrofitSingleton;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyAccountFragment extends Fragment {

    View view;
    CEditText edtName, edtPhone, edtMail, edtAddress;
    Button btnUpdate;
    APIService apiService;
    ProgressDialog p;
    boolean error;


    ArrayList<ProfileModule.Details> details;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_account, container, false);
        init();
        loadData();
        setListener();
        return view;
    }

    private void setListener() {
        edtName.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == event.KEYCODE_ENTER) {
                    edtMail.requestFocus();
                }
                return false;
            }
        });
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validity();
                if (!error) {
                    p.show();
                    String userId = PrefConnect.readString(getActivity(), PrefConnect.USER_ID, "");
                    String name = edtName.getText().toString();
                    String emailId = edtMail.getText().toString();
                    String address = edtAddress.getText().toString();
                    EditProfile editProfile = new EditProfile(name, emailId, userId, address);
                    Call<EditProfile.Result> call = apiService.editProfile(editProfile);
                    call.enqueue(new Callback<EditProfile.Result>() {
                        @Override
                        public void onResponse(Call<EditProfile.Result> call, Response<EditProfile.Result> response) {
                            if (response.isSuccessful()) {
                                EditProfile.Result result = response.body();
                                if (result.getResult().equalsIgnoreCase("Success")) {
                                    Global.dismissProgress(p);
                                    edtName.setEnabled(false);
                                    edtMail.setEnabled(false);
                                    edtAddress.setEnabled(false);
                                    btnUpdate.setVisibility(View.GONE);
                                    Global.CustomToast(getActivity(), result.getStatus());

                                } else {
                                    Global.dismissProgress(p);
                                    Global.CustomToast(getActivity(), result.getStatus());
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<EditProfile.Result> call, Throwable t) {
                            Log.e("TAG", t.getMessage());
                        }
                    });
                }
            }
        });
    }

    private void validity() {
        String email = edtMail.getText().toString();
        if (edtName.getText().length() == 0) {
            Global.CustomToast(getActivity(), "Enter Name");
            error = true;
        } else if (!email.equals("") && !email.matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")) {
            Global.CustomToast(getActivity(), "Enter Valid EmailId");

         /*   Context context = getActivity();
            LayoutInflater inflater = getLayoutInflater(getArguments());
            View toastRoot = inflater.inflate(R.layout.toast, null);
            Toast toast = new Toast(context);
            toast.setText("Enter Valid mail id");
            toast.setView(toastRoot);
            toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.show();
*/
            error = true;
        } else if (edtAddress.getText().length() == 0) {
            Global.CustomToast(getActivity(), "Enter Address");
            error = true;
        } else {
            error = false;
        }
    }

    private void loadData() {
        p.show();
        String userId = PrefConnect.readString(getActivity(), PrefConnect.USER_ID, "");
        Call<ProfileModule> call = apiService.profile(userId);
        call.enqueue(new Callback<ProfileModule>() {
            @Override
            public void onResponse(Call<ProfileModule> call, Response<ProfileModule> response) {
                if (response.isSuccessful()) {
                    ProfileModule profile = response.body();
                    if (profile.getResult().equalsIgnoreCase("Success")) {
                        Global.dismissProgress(p);
                        details = profile.getDetails();
                        PrefConnect.writeString(getActivity(), PrefConnect.NAME, details.get(0).getName());
                        PrefConnect.writeString(getActivity(), PrefConnect.EMAIL, details.get(0).getEmail());
                        PrefConnect.writeString(getActivity(), PrefConnect.USER_ID, details.get(0).getId());
                        PrefConnect.writeString(getActivity(), PrefConnect.ADDRESS, details.get(0).getAddress());
                        setFields();
                    } else {
                        Global.dismissProgress(p);
                    }
                }
            }

            @Override
            public void onFailure(Call<ProfileModule> call, Throwable t) {
                Global.dismissProgress(p);
                Log.e("TAG", t.getMessage());
            }
        });
    }

    private void setFields() {
        edtName.setTextColor(getActivity().getResources().getColor(R.color.black));
        edtMail.setTextColor(getActivity().getResources().getColor(R.color.black));
        edtAddress.setTextColor(getActivity().getResources().getColor(R.color.black));
        edtPhone.setTextColor(getActivity().getResources().getColor(R.color.black));

        edtName.setText(PrefConnect.readString(getActivity(), PrefConnect.NAME, ""));
        edtMail.setText(PrefConnect.readString(getActivity(), PrefConnect.EMAIL, ""));
        edtAddress.setText(PrefConnect.readString(getActivity(), PrefConnect.ADDRESS, ""));
        edtPhone.setText(PrefConnect.readString(getActivity(), PrefConnect.MOBILE, ""));
    }

    private void init() {
        edtName = (CEditText) view.findViewById(R.id.edtName);
        edtPhone = (CEditText) view.findViewById(R.id.edtPhone);
        edtMail = (CEditText) view.findViewById(R.id.edtMail);
        edtAddress = (CEditText) view.findViewById(R.id.edtAddress);
        btnUpdate = (Button) view.findViewById(R.id.btnUpdate);

        apiService = RetrofitSingleton.createService(APIService.class);
        p = Global.initProgress(getActivity());
        details = new ArrayList<>();

        edtName.setEnabled(true);
        edtPhone.setEnabled(false);
        edtMail.setEnabled(true);
        edtAddress.setEnabled(true);

        btnUpdate.setVisibility(View.VISIBLE);

    }

   /* @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
      //  inflater.inflate(R.menu.edit, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
      *//*  int id = item.getItemId();
        if (id == R.id.edit) {
            edtName.setEnabled(true);
            edtPhone.setEnabled(false);
            edtMail.setEnabled(true);
            edtAddress.setEnabled(true);
            edtName.requestFocus();

            edtName.setTextColor(getActivity().getResources().getColor(R.color.black));
            edtMail.setTextColor(getActivity().getResources().getColor(R.color.black));
            edtAddress.setTextColor(getActivity().getResources().getColor(R.color.black));
            edtPhone.setTextColor(getActivity().getResources().getColor(R.color.black));

            btnUpdate.setVisibility(View.VISIBLE);
        }*//*
        return false;
    }*/

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onAttached(DrawerFragment.MY_ACCOUNT);
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
            // Global.CustomToast(getActivity(), "Internet Avaliable");
        } else {
            Global.CustomToast(getActivity(), "Check your internet connection");
            //Global.Toast(MainActivity.this, "Check your internet connection");
        }
    }
}
