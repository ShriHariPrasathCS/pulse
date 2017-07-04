package com.pulseplus.dialog;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pulseplus.R;
import com.pulseplus.database.DBHelper;
import com.pulseplus.global.Global;
import com.pulseplus.model.EveningDelivery;
import com.pulseplus.model.MorningDelivery;
import com.pulseplus.service.LocalBinder;
import com.pulseplus.service.XmppService;
import com.pulseplus.util.PrefConnect;
import com.pulseplus.web.APIService;
import com.pulseplus.web.RetrofitSingleton;

import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Bright Bridge on 13-Apr-17.
 */

public class ScheduleOrder extends DialogFragment {

    TextView txtMorning, txtEvening, txtCancel;
    AppCompatImageView imgView;
    LinearLayout edtLayout, btnLayout;
    private APIService apiService;
    private String orderId;
    private XmppService xmppService;
    private String toJid;
    private boolean mBounded;
    private boolean chatEnd = false;
    private DBHelper dbHelper;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            xmppService = ((LocalBinder<XmppService>) service).getService();
            mBounded = true;
            //  PrefConnect.writeString(OrderChatActivity.this, PrefConnect.TO_JID, "");

            Log.d("TAG", "onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            xmppService = null;
            mBounded = false;
            Log.d("TAG", "onServiceDisconnected");
        }
    };

    private ScheduleCallback callback;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_schedule_order, null, false);
        init(view);
        setListener();
        dialog.setView(view);
        return dialog.create();
    }

    public XmppService getmService() {
        return xmppService;
    }

    public void setCallback(ScheduleCallback callback) {
        this.callback = callback;
    }

    public String getDate() {
        /*String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        currentDateTimeString = currentDateTimeString.substring();*/
        Date date = new Date();
        String currentTime = Global.getDate(date, "hh:mm a");
        return currentTime;
    }


    private void setListener() {

        txtMorning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                orderId = PrefConnect.readString(getActivity(), PrefConnect.ORDER_ID, "");
                Global.CustomToast(getActivity(), "Medicines will be delivered in Morning");

                Call<MorningDelivery> call = apiService.morningDelivery(orderId);
                call.enqueue(new Callback<MorningDelivery>() {
                    @Override
                    public void onResponse(Call<MorningDelivery> call, Response<MorningDelivery> response) {
                        if (response.isSuccessful()) {
                            // Global.dismissProgress(p);
                            final MorningDelivery morningdelivery = response.body();
                            if (morningdelivery.getResult().equalsIgnoreCase("Success")) {
                                //   Global.CustomToast(getActivity(), morningdelivery.getStatus());
                                //    String jid = PrefConnect.readString(getActivity(), PrefConnect.TO_JID, "");

                                getmService().xmpp.sendMessage("Your order has been confirmed,it will be delivered by 6-10AM", toJid, "3", getDate());

                                if (callback != null) {
                                    callback.onMorningClicked();
                                }
                                //TODO dbHelper.deletePendingOrderHistory(orderId);
                                ////    PrefConnect.writeString(getActivity(), PrefConnect.TO_JID, "");


                            } else {
                                Global.CustomToast(getActivity(), morningdelivery.getStatus());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<MorningDelivery> call, Throwable t) {
                        //   Global.dismissProgress(p);

                    }
                });
//
//                Intent intent = new Intent(getActivity(), OrderChatActivity.class);
//                startActivity(intent);
//                dismiss();
            }
        });
        txtEvening.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                orderId = PrefConnect.readString(getActivity(), PrefConnect.ORDER_ID, "");
                Global.CustomToast(getActivity(), "Medicines will be delivered in Evening ");

                Call<EveningDelivery> call = apiService.eveningDelivery(orderId);
                call.enqueue(new Callback<EveningDelivery>() {
                    @Override
                    public void onResponse(Call<EveningDelivery> call, Response<EveningDelivery> response) {
                        if (response.isSuccessful()) {
                            // Global.dismissProgress(p);
                            final EveningDelivery eveningdelivery = response.body();
                            if (eveningdelivery.getResult().equalsIgnoreCase("Success")) {


                                //  Global.CustomToast(getActivity(), eveningdelivery.getStatus());
                                String jid = PrefConnect.readString(getActivity(), PrefConnect.TO_JID, "");
                                getmService().xmpp.sendMessage("Your order has been confirmed,it will be delivered by 6-10PM", jid, "3", getDate());

                                if (callback != null) {
                                    callback.onEveningClicked();
                                }
                                //TODO dbHelper.deletePendingOrderHistory(orderId);
                                //   PrefConnect.writeString(OrderChatActivity.this, PrefConnect.TO_JID, "");
                                //  PrefConnect.writeString(OrderChatActivity.this, PrefConnect.ORDER_ID, "");


                            } else {
                                Global.CustomToast(getActivity(), eveningdelivery.getStatus());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<EveningDelivery> call, Throwable t) {
                        //   Global.dismissProgress(p);

                    }
                });
//
//                Intent intent = new Intent(getActivity(), OrderChatActivity.class);
//                startActivity(intent);
//                dismiss();
            }
        });
    }

    public void endChat() {
        imgView.setVisibility(View.VISIBLE);
        edtLayout.setVisibility(View.GONE);
        btnLayout.setVisibility(View.VISIBLE);
        chatEnd = true;
        //dbHelper.deleteOrder(orderId);
    }

    private void init(View view) {
        dbHelper = DBHelper.getInstance(getContext());
        txtMorning = (TextView) view.findViewById(R.id.txtMorning);
        txtEvening = (TextView) view.findViewById(R.id.txtEvening);
        txtCancel = (TextView) view.findViewById(R.id.txtCancel);
        apiService = RetrofitSingleton.createService(APIService.class);
        toJid = PrefConnect.readString(getActivity(), PrefConnect.TO_JID, "");
        imgView = (AppCompatImageView) view.findViewById(R.id.imgView);
        edtLayout = (LinearLayout) view.findViewById(R.id.edtLayout);
        btnLayout = (LinearLayout) view.findViewById(R.id.btnLayout);


    }

    @Override
    public void onResume() {
        super.onResume();
        doBindService();
    }

    void doBindService() {
        getActivity().bindService(new Intent(getActivity(), XmppService.class), connection, Context.BIND_AUTO_CREATE);
    }

    void doUnbindService() {
        if (connection != null) {
            getActivity().unbindService(connection);
        }
    }

    @Override
    public void onDestroy() {
        doUnbindService();
        super.onDestroy();
    }

    public interface ScheduleCallback {
        void onMorningClicked();

        void onEveningClicked();
    }


}
