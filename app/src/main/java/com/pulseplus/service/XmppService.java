package com.pulseplus.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.IBinder;

import com.pulseplus.util.PrefConnect;
import com.pulseplus.xmpp.XmppConnect;

public class XmppService extends Service {

    public static ConnectivityManager cm;
    public static XmppConnect xmpp;
    public static final String NEW_MESSAGE = "com.pulseplus.newmessage";
    public static final String SEND_MESSAGE = "com.pulseplus.sendmessage";
    public static final String BANNED_MESSAGE = "com.pulseplus.bannedmessage";

    public static boolean isNetworkConnected() {
        return cm.getActiveNetworkInfo() != null;
    }

    @Override
    public IBinder onBind(final Intent intent) {
        return new LocalBinder<XmppService>(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        String jid = PrefConnect.readString(this, PrefConnect.JID, "");
        String jid_pass = PrefConnect.readString(this, PrefConnect.JID_PASS, "");
        xmpp = new XmppConnect(this, "52.11.250.70", jid, jid_pass);
        //  xmpp = new XmppConnect(this, "192.168.0.118", username, password);
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags,
                              final int startId) {
        return Service.START_NOT_STICKY;
    }

    @Override
    public boolean onUnbind(final Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        xmpp.connection.disconnect();

    }
}
