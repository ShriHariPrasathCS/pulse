package com.pulseplus.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.pulseplus.model.Child;
import com.pulseplus.model.Group;
import com.pulseplus.model.OrderHistory;
import com.pulseplus.model.PendingOrder;

import java.util.ArrayList;

import static com.pulseplus.database.DBHelper.Order.COL_ORDERID;

/**
 * Created by Bright Bridge on 19-Jan-17.
 */

public class DBHelper extends SQLiteOpenHelper {

    private static final String DBNAME = "PULSE";
    private static DBHelper dbHelper;
    private String orderId;

    private DBHelper(Context context) {
        super(context, DBNAME, null, 1);
    }

    public static DBHelper getInstance(Context context) {
        if (dbHelper == null) {
            dbHelper = new DBHelper(context);
        }
        return dbHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Order.QUERY);
        db.execSQL(OrderHistoryChat.QUERY);
        db.execSQL(DBPendingOrder.QUERY);
        db.execSQL(DBPendingOrderChat.QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void insertOrderHistory(String orderId, String chat_usertype, String message_type, String message, String sent_date) {
//        if (!TextUtils.isEmpty(message)) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(OrderHistoryChat.COL_ORDERID, orderId);
            values.put(OrderHistoryChat.COL_CHATUSERTYPE, chat_usertype);
            values.put(OrderHistoryChat.COL_MESSAGETYPE, message_type);
            values.put(OrderHistoryChat.COL_MESSAGE, message);
            values.put(OrderHistoryChat.COL_SENTDATE, sent_date);
            long id = db.insert(OrderHistoryChat.TABLE, null, values);
            db.close();
            insertOrUpdateOrder(orderId, message, sent_date);
//        }
    }

    public void insertPendingOrder(String orderId, String chat_usertype, String message_type, String message, String sent_date) {
//        if (!TextUtils.isEmpty(message)) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DBPendingOrderChat.COL_ORDERID, orderId);
            values.put(DBPendingOrderChat.COL_CHATUSERTYPE, chat_usertype);
            values.put(DBPendingOrderChat.COL_MESSAGETYPE, message_type);
            values.put(DBPendingOrderChat.COL_MESSAGE, message);
            values.put(DBPendingOrderChat.COL_SENTDATE, sent_date);
            long id = db.insert(DBPendingOrderChat.TABLE, null, values);
            db.close();
            saveOrder(orderId, message, sent_date);
//        }
    }

    public void deleteOrderHistory(String orderId) {

        SQLiteDatabase db = this.getWritableDatabase();
        //   Cursor cursor = db.query(Order.TABLE, null, COL_ORDERID + "=?", new String[]{orderId},null, null, null);
        db.delete(OrderHistoryChat.TABLE, COL_ORDERID + "=?", new String[]{orderId});
        db.delete(Order.TABLE, COL_ORDERID + "=?", new String[]{orderId});
        // db.execSQL("delete from "+Order.TABLE+" where COL_ORDERID='"+orderId+"'")
        db.close();
    }

    public void deletePendingOrderHistory(String orderId) {

        SQLiteDatabase db = this.getWritableDatabase();
        //   Cursor cursor = db.query(Order.TABLE, null, COL_ORDERID + "=?", new String[]{orderId},null, null, null);
        db.delete(DBPendingOrderChat.TABLE, COL_ORDERID + "=?", new String[]{orderId});
        db.delete(DBPendingOrder.TABLE, COL_ORDERID + "=?", new String[]{orderId});
        // db.execSQL("delete from "+Order.TABLE+" where COL_ORDERID='"+orderId+"'")
        db.close();
    }

    public void saveOrder(String orderId, String message, String date) {

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(DBPendingOrder.TABLE, null, COL_ORDERID + "=?", new String[]{orderId}, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                //Update
                ContentValues values = new ContentValues();
                values.put(DBPendingOrder.COL_MESSAGE, message);
                db.update(DBPendingOrder.TABLE, values, COL_ORDERID + "=?", new String[]{orderId});
            } else {
                //Insert
                ContentValues values = new ContentValues();
                values.put(COL_ORDERID, orderId);
                values.put(DBPendingOrder.COL_MESSAGE, message);
                values.put(DBPendingOrder.COL_DATE, date);
                db.insert(DBPendingOrder.TABLE, null, values);
            }
        }
        if (cursor != null)
            cursor.close();
        db.close();
    }

    public void insertOrUpdateOrder(String orderId, String message, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(Order.TABLE, null, COL_ORDERID + "=?", new String[]{orderId}, null, null, null);

        if (cursor != null) {
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                //Update
                ContentValues values = new ContentValues();
                values.put(Order.COL_MESSAGE, message);
                db.update(Order.TABLE, values, COL_ORDERID + "=?", new String[]{orderId});
            } else {
                //Insert
                ContentValues values = new ContentValues();
                values.put(COL_ORDERID, orderId);
                values.put(Order.COL_MESSAGE, message);
                values.put(Order.COL_DATE, date);
                db.insert(Order.TABLE, null, values);
            }
        }
        if (cursor != null)
            cursor.close();
        db.close();
    }

    public ArrayList<Group> getOrders(String orderId) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Group> groupList = new ArrayList<>();
        // com.pulseplus.model.OrderHistoryChat.Chat_history details = null;
        Cursor cursor = db.query(OrderHistoryChat.TABLE, null, OrderHistoryChat.COL_ORDERID + "=?", new String[]{orderId}, null, null, null);

        if (cursor != null) {
            ArrayList<Child> childList = new ArrayList<>();
            Group group = new Group();
            while (cursor.moveToNext()) {
                String chat_usertype = cursor.getString(cursor.getColumnIndex(OrderHistoryChat.COL_CHATUSERTYPE));
                String message_type = cursor.getString(cursor.getColumnIndex(OrderHistoryChat.COL_MESSAGETYPE));
                String message = cursor.getString(cursor.getColumnIndex(OrderHistoryChat.COL_MESSAGE));
                String sent_date = cursor.getString(cursor.getColumnIndex(OrderHistoryChat.COL_SENTDATE));

                Child child = new Child(chat_usertype, message_type, message, sent_date);
                childList.add(child);

//                fromId = chatBean.getDetails().get(0).getFrom_id();
            }
            group.setChildren(childList);
            groupList.add(group);
        }
        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return groupList;
    }


    public ArrayList<Group> getPendingOrders(String orderId) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Group> groupList = new ArrayList<>();
        // com.pulseplus.model.OrderHistoryChat.Chat_history details = null;
        Cursor cursor = db.query(DBPendingOrderChat.TABLE, null, DBPendingOrderChat.COL_ORDERID + "=?", new String[]{orderId}, null, null, null);

        if (cursor != null) {
            ArrayList<Child> childList = new ArrayList<>();
            Group group = new Group();
            while (cursor.moveToNext()) {
                String chat_usertype = cursor.getString(cursor.getColumnIndex(DBPendingOrderChat.COL_CHATUSERTYPE));
                String message_type = cursor.getString(cursor.getColumnIndex(DBPendingOrderChat.COL_MESSAGETYPE));
                String message = cursor.getString(cursor.getColumnIndex(DBPendingOrderChat.COL_MESSAGE));
                String sent_date = cursor.getString(cursor.getColumnIndex(DBPendingOrderChat.COL_SENTDATE));

                Child child = new Child(chat_usertype, message_type, message, sent_date);
                childList.add(child);

//                fromId = chatBean.getDetails().get(0).getFrom_id();
            }
            group.setChildren(childList);
            groupList.add(group);
        }
        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return groupList;
    }

    public ArrayList<OrderHistory.Order_history> getOrderHis() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<OrderHistory.Order_history> histories = new ArrayList<>();
        // descending order
        Cursor cursor = db.query(Order.TABLE, null, null, null, null, null, COL_ORDERID + " DESC");

        if (cursor != null) {
            while (cursor.moveToNext()) {
                OrderHistory.Order_history order_history = new OrderHistory.Order_history();
                order_history.setOrderid(cursor.getString(cursor.getColumnIndex(COL_ORDERID)));
                order_history.setMessage(cursor.getString(cursor.getColumnIndex(Order.COL_MESSAGE)));
                order_history.setAdded_date(cursor.getString(cursor.getColumnIndex(Order.COL_DATE)));
                histories.add(order_history);

            }
        }
        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return histories;
    }

    public ArrayList<PendingOrder.Pending_orders> getPendingOrderHis() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<PendingOrder.Pending_orders> pendingHistories = new ArrayList<>();
        Cursor cursor = db.query(DBPendingOrder.TABLE, null, null, null, null, null, COL_ORDERID + " DESC");

        if (cursor != null) {
            while (cursor.moveToNext()) {
                PendingOrder.Pending_orders pending_orders = new PendingOrder.Pending_orders();
                pending_orders.setOrderid(cursor.getString(cursor.getColumnIndex(COL_ORDERID)));
                pending_orders.setMessage(cursor.getString(cursor.getColumnIndex(DBPendingOrder.COL_MESSAGE)));
                pending_orders.setAdded_date(cursor.getString(cursor.getColumnIndex(DBPendingOrder.COL_DATE)));
                pendingHistories.add(pending_orders);

            }
        }
        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return pendingHistories;

    }


    public static class Order {
        public static final String TABLE = "Order_";
        public static final String COL_ORDERID = "order_id";
        public static final String COL_DATE = "created_date";
        public static final String COL_MESSAGE = "last_message";

        public static final String QUERY = "CREATE TABLE " + TABLE + "(" + COL_ORDERID + " TEXT," + COL_DATE + " TEXT," + COL_MESSAGE + " TEXT)";
    }

    public static class OrderHistoryChat {
        public static final String TABLE = "OrderHistoryChat";
        public static final String COL_ORDERID = "order_id";
        public static final String COL_CHATUSERTYPE = "chat_usertype";
        public static final String COL_MESSAGETYPE = "message_type";
        public static final String COL_MESSAGE = "message";
        public static final String COL_SENTDATE = "sent_date";

        public static final String QUERY = "CREATE TABLE " + TABLE + "(" + COL_ORDERID + " TEXT," + COL_CHATUSERTYPE + " TEXT," + COL_MESSAGETYPE + " TEXT," + COL_MESSAGE + " TEXT," + COL_SENTDATE + " TEXT)";

    }

    public static class DBPendingOrder {
        public static final String TABLE = "DBPendingOrder";
        public static final String COL_ORDERID = "order_id";
        public static final String COL_DATE = "created_date";
        public static final String COL_MESSAGE = "last_message";

        public static final String QUERY = "CREATE TABLE " + TABLE + "(" + COL_ORDERID + " TEXT," + COL_DATE + " TEXT," + COL_MESSAGE + " TEXT)";
    }

    public static class DBPendingOrderChat {
        public static final String TABLE = "DBPendingOrderChat";
        public static final String COL_ORDERID = "order_id";
        public static final String COL_CHATUSERTYPE = "chat_usertype";
        public static final String COL_MESSAGETYPE = "message_type";
        public static final String COL_MESSAGE = "message";
        public static final String COL_SENTDATE = "sent_date";

        public static final String QUERY = "CREATE TABLE " + TABLE + "(" + COL_ORDERID + " TEXT," + COL_CHATUSERTYPE + " TEXT," + COL_MESSAGETYPE + " TEXT," + COL_MESSAGE + " TEXT," + COL_SENTDATE + " TEXT)";

    }
}
