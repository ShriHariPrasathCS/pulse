package com.pulseplus.background;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.pulseplus.global.Global;
import com.pulseplus.model.ProfileModule;
import com.pulseplus.util.PrefConnect;
import com.pulseplus.web.APIService;
import com.pulseplus.web.RetrofitSingleton;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */


public class RegStatusService extends Service implements Runnable {

    public static final String REFRESH = "com.pulseplus.background.extra.REFRESH";
    Handler handler;
    APIService apiService;
    private boolean sendOnce = true;
    private Binder mBinder = new RegBinder();
    private String DEVICE_TOKEN, USER_ID;

    @Override
    public void onCreate() {
        super.onCreate();
        apiService = RetrofitSingleton.createService(APIService.class);
     /*   if (isNetworkAvailable() == false){
            Global.Toast(RegStatusService.this,"Check your Internet Connection");
        }*/
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//
//        return START_NOT_STICKY;
//    }

    public void startRunning() {
        handler = new Handler();
        USER_ID = PrefConnect.readString(this, PrefConnect.USER_ID, "");
        DEVICE_TOKEN = PrefConnect.readString(this, PrefConnect.DEVICE_TOKEN, "");
        handler.postDelayed(this, 100);
    }

    @Override
    public void run() {
        Call<ProfileModule> call = apiService.profile(USER_ID);
        call.enqueue(new Callback<ProfileModule>() {
            @Override
            public void onResponse(Call<ProfileModule> call, Response<ProfileModule> response) {
                ProfileModule profile = response.body();
                if (profile != null)
                    if (profile.getResult().equalsIgnoreCase("Success")) {
                        ArrayList<ProfileModule.Details> details = profile.getDetails();
                        if (!DEVICE_TOKEN.equalsIgnoreCase(details.get(0).device_token)) {
                            Intent statusIntent = new Intent(REFRESH);
                            statusIntent.putExtra("showDialog", true);
                            sendBroadcast(statusIntent);
                            handler.removeCallbacks(RegStatusService.this);
                        } else {
                            if (sendOnce) {
                                Intent statusIntent = new Intent(REFRESH);
                                statusIntent.putExtra("setListener", true);
                                sendBroadcast(statusIntent);
                                sendOnce = false;
                            }
                            handler.postDelayed(RegStatusService.this, 1000);
                        }
                        if (details.get(0).getCustomer_status().equals("3")) {
                            Intent statusIntent = new Intent(REFRESH);
                            statusIntent.putExtra("banned", true);
                            sendBroadcast(statusIntent);
                            handler.removeCallbacks(RegStatusService.this);

                        }
                    }
            }

            @Override
            public void onFailure(Call<ProfileModule> call, Throwable t) {
                Global.Toast(RegStatusService.this, "Service error");
                Intent statusIntent = new Intent(REFRESH);
                statusIntent.putExtra("setListener", true);
                sendBroadcast(statusIntent);
                sendOnce = false;
            }
        });
    }

    public void setSendOnce(boolean sendOnce) {
        this.sendOnce = sendOnce;
    }
/*
*
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */

    public class RegBinder extends Binder {
        public RegStatusService getService() {
            // Return this instance of LocalService so clients can call public methods
            return RegStatusService.this;
        }
    }
  /*  private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }*/
}
