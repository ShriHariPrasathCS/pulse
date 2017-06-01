package com.pulseplus.util;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefConnect {

    public static final String PREF_NAME = "MY_PREF";
    public static final int MODE = Context.MODE_PRIVATE;
    public static final String FCMTOKEN = "FCMTOKEN";
    public static final String APP_VERSION = "APP_VERSION";
    public static final String USER_ID = "USER_ID";
    public static final String OTP = "OTP";
    public static final String OTP_VERIFY = "OTP_VERIFY";
    public static final String VERIFY_STATUS = "VERIFY_STATUS";
    public static final String MOBILE = "MOBILE";
    public static final String ORDER_ID = "ORDER_ID";
    public static final String MSGCOUNT = "MSGCOUNT";
    public static final String OFFERCODE = "OFFERCODE";
    public static final String NAME = "NAME";
    public static final String EMAIL = "EMAIL";
    public static final String ADDRESS = "ADDRESS";
    public static final String JID = "JID";
    public static final String JID_PASS = "JID_PASS";
    public static final String TO_JID = "TO_JID";
    public static final String DEVICE_TOKEN = "DEVICE_TOKEN";
    public static final String UDID = "UDID";
    public static final String ID = "Id";


    public static void clearAllPrefs(Context context) {
        getEditor(context).clear().commit();
    }

    /**
     * @param context
     * @param key
     * @param value
     */
    public static void writeBoolean(Context context, String key, boolean value) {
        getEditor(context).putBoolean(key, value).commit();
    }

    /**
     * @param context
     * @param key
     * @param defValue
     * @return
     */
    public static boolean readBoolean(Context context, String key,
                                      boolean defValue) {
        return getPreferences(context).getBoolean(key, defValue);
    }

    /**
     * @param context
     * @param key
     * @param value
     */
    public static void writeInteger(Context context, String key, int value) {
        getEditor(context).putInt(key, value).commit();

    }

    /**
     * @param context
     * @param key
     * @param defValue
     * @return
     */
    public static int readInteger(Context context, String key, int defValue) {
        return getPreferences(context).getInt(key, defValue);
    }

    /**
     * @param context
     * @param key
     * @param value
     */
    public static String writeString(Context context, String key, String value) {
        getEditor(context).putString(key, value).commit();

        return key;
    }

    /**
     * @param context
     * @param key
     * @param defValue
     * @return
     */
    public static String readString(Context context, String key, String defValue) {
        return getPreferences(context).getString(key, defValue);
    }

    /**
     * @param context
     * @param key
     * @param value
     */
    public static void writeFloat(Context context, String key, float value) {
        getEditor(context).putFloat(key, value).commit();
    }

    /**
     * @param context
     * @param key
     * @param defValue
     * @return
     */
    public static float readFloat(Context context, String key, float defValue) {
        return getPreferences(context).getFloat(key, defValue);
    }

    /**
     * @param context
     * @param key
     * @param value
     */
    public static void writeLong(Context context, String key, long value) {
        getEditor(context).putLong(key, value).commit();
    }

    /**
     * @param context
     * @param key
     * @param defValue
     * @return
     */
    public static long readLong(Context context, String key, long defValue) {
        return getPreferences(context).getLong(key, defValue);
    }

    /**
     * @param context
     * @return
     */
    public static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREF_NAME, MODE);
    }

    /**
     * @param context
     * @return
     */
    public static SharedPreferences.Editor getEditor(Context context) {
        return getPreferences(context).edit();
    }

}
