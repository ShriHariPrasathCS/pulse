package com.pulseplus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.pulseplus.model.Internet;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Bright Bridge on 28-Dec-16.
 */

public class InternetReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        new OnlineTask().execute();
    }

    public class OnlineTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            try {
                URL url = new URL("http://52.11.250.70/pulseplus");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {

                    return true;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            EventBus.getDefault().post(new Internet(aBoolean));

        }
    }

}
