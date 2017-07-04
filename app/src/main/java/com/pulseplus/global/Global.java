package com.pulseplus.global;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.armor.fileupload.FilePath;
import com.pulseplus.R;
import com.pulseplus.web.RetrofitSingleton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Global {


    public static final String IMAGE_SEND = "/pulseplus/image/sent";
    public static final String AUDIO_SEND = "/pulseplus/audio/sent";
    public static final String AUDIO_RECEIVE = "/pulseplus/audio/receive";
    public static final String IMAGE_RECEIVE = "/pulseplus/image/receive";

    public static final String FILE_URL = RetrofitSingleton.BASE_URL + "/pulseplus/api/prescription/";

    public static final int AUDIO_REC = 1003;
    public static final String XMPP_SERVER = "pulseplus";
    public static final String SEND = "1";
    public static final String RECE = "2";
    public static boolean BROADCAST = false;


    /**
     * Returns new file created for storing audio
     *
     * @param folder folder to store MP3
     */
    public static File getAudioDirectory(String folder) {
        String audioFileName = "AUDIO_";
        File storageDir = new File(Environment.getExternalStorageDirectory() + folder);
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        File audio = null;
        try {
            audio = File.createTempFile(
                    audioFileName,  /* prefix */
                    ".m4a",         /* suffix */
                    storageDir      /* directory */
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
        return audio;
    }


    /**
     * Returns new file created for storing Image
     *
     * @param folder folder to store MP3
     */
    public static File getPhotoDirectory(String folder) {
        String fileName = "IMAGE_";
        File storageDir = new File(Environment.getExternalStorageDirectory() + folder);
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        File image = null;
        try {
            image = File.createTempFile(
                    fileName,       /* prefix */
                    ".png",         /* suffix */
                    storageDir      /* directory */
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    //Get Date as String in given pattern by Providing date

    /**
     * Returns the date object of the date given in string
     *
     * @param dateTime date and Time in string format
     * @param pattern  format of the date & time given (eg. dd MMM yyyy)
     */
    public static Date getDate(String dateTime, String pattern) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, Locale.ENGLISH);
        //dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            return dateFormat.parse(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date();
    }

    /**
     * Returns the date as String of the date given in string to different formats
     *
     * @param dateTime    date and Time in string format
     * @param fromPattern format of the date & time given (eg. dd MMM yyyy)
     * @param toPattern   format of the date & time given (eg. dd MMM yyyy)
     */
    public static String getDate(String dateTime, String fromPattern, String toPattern) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(fromPattern, Locale.ENGLISH);
        //dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        //dateFormat.applyPattern(fromPattern);
        try {
            Date date = dateFormat.parse(dateTime);
            //dateFormat.setTimeZone(TimeZone.getDefault());
            dateFormat.applyPattern(toPattern);
            return dateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Returns the date as String of the date Object
     *
     * @param date    date object
     * @param pattern format of the date & time given (eg. dd MMM yyyy)
     */
    public static String getDate(Date date, String pattern) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, Locale.ENGLISH);
        //dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        //dateFormat.applyPattern(pattern);
        try {
            return dateFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static boolean saveImage(Context context, Uri uri, Uri toUri) {

        File file = new File(FilePath.getPath(context, uri));
        File toFile = new File(FilePath.getPath(context, toUri));
        FileOutputStream out = null;

        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
            out = new FileOutputStream(toFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 70, out);
            try {
                out.close();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public static void saveThumb(Bitmap bitmap, File f) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Get Progress Dialog.
     *
     * @param context Application Context
     * @return new Progress Dialog
     */
    public static ProgressDialog initProgress(Context context) {
        ProgressDialog p = new ProgressDialog(context);
        p.setMessage(context.getString(R.string.loading));
        p.setIndeterminate(false);
        p.setCancelable(false);
        return p;
    }


    /**
     * Dismiss the ProgressBar Dialog from the window.
     *
     * @param progressDialog New Progress Dialog Object
     */
    public static void dismissProgress(ProgressDialog progressDialog) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }


    /**
     * Shows the Toast.
     *
     * @param context Application Context
     * @param text    Toast Message
     */
    public static void Toast(Context context, String text) {
        Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        toast.show();
    }


    /**
     * Shows the Toast.
     *
     * @param context Application Context
     * @param text    Toast Message
     */
    public static void ToastLong(Context context, String text) {
        Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
        toast.show();
    }


    /**
     * Shows the Toast.
     *
     * @param context Application Context
     * @param resId   Toast Message
     */
    public static void Toast(Context context, int resId) {
        Toast toast = Toast.makeText(context, resId, Toast.LENGTH_SHORT);
        toast.show();
    }


    public static void keyboardHide(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    public static void CustomToast(Activity context, String msg) {
        LayoutInflater inflater = context.getLayoutInflater();
        View toastLayout = inflater.inflate(R.layout.custom_toast, (ViewGroup) context.findViewById(R.id.custom_toast_layout));
        TextView custom_toast_message = (TextView) toastLayout.findViewById(R.id.custom_toast_message);
        Toast toast = new Toast(context);
        //toast.makeText(getApplicationContext(),"halloo's....",Toast.LENGTH_LONG);
        custom_toast_message.setText(msg);
        //toast.setText("hello, how are you....");
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(toastLayout);
        toast.show();
    }
}
