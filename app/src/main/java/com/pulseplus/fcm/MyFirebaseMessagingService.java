package com.pulseplus.fcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.pulseplus.MainActivity;
import com.pulseplus.R;
import com.pulseplus.util.PrefConnect;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        createNotification(remoteMessage.getNotification().getBody());
    }
    // Creates notification based on title and body received
    private void createNotification(String message) {
        Intent resultIntent = null;
        NotificationManager mNotificationManager = null;
        Uri ringtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.sender);
        Context context = getBaseContext();

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)

                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                        R.mipmap.ic_launcher))
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentText(message).setSound(ringtone);
       /* if (questionaire_type.equalsIgnoreCase("1")) {
            resultIntent = new Intent(this, QuizActivity.class);
        } else {
            resultIntent = new Intent(this, MatchQuizActivity.class);
        }*/
        if(!TextUtils.isEmpty(PrefConnect.readString(this,PrefConnect.USER_ID,""))){
            resultIntent = new Intent(this, MainActivity.class);
            resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
// Because clicking the notification opens a new ("special") activity, there's
// no need to create an artificial back stack.
            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            this,
                            0,
                            resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );


            mBuilder.setContentIntent(resultPendingIntent);
        }

        mNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(123456789, mBuilder.build());
    }
}
