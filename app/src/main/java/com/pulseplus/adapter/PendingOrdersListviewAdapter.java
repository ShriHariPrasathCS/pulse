package com.pulseplus.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.armor.fontlib.CTextView;
import com.pulseplus.OrderChatActivity;
import com.pulseplus.R;
import com.pulseplus.database.DBHelper;
import com.pulseplus.global.Global;
import com.pulseplus.model.ContinuePendingOrder;
import com.pulseplus.model.PendingOrder;
import com.pulseplus.util.PrefConnect;
import com.pulseplus.web.APIService;
import com.pulseplus.web.RetrofitSingleton;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Bright Bridge on 07-Apr-17.
 */

public class PendingOrdersListviewAdapter extends ArrayAdapter {
    LayoutInflater inflater;
    Context context;
    ArrayList<PendingOrder.Pending_orders> pendingOrder;
    private String Orderid;
    private Handler handler = new Handler();
    private DBHelper dbHelper;
    private APIService apiService;

    public PendingOrdersListviewAdapter(Context context, ArrayList<PendingOrder.Pending_orders> pendingOrder) {
        super(context, R.layout.pending_order_listview_item, pendingOrder);
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.pendingOrder = pendingOrder;
        apiService = RetrofitSingleton.createService(APIService.class);
        dbHelper = DBHelper.getInstance(context);
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.pending_order_listview_item, parent, false);
            holder.txtListTitle = (CTextView) convertView.findViewById(R.id.txtListTitle);
            holder.txtViewDate = (CTextView) convertView.findViewById(R.id.txtViewDate);
            holder.txtListContent = (CTextView) convertView.findViewById(R.id.txtListContent);
            holder.continue_order = (ImageView) convertView.findViewById(R.id.continue_order);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        holder.txtListTitle.setText(pendingOrder.get(position).getOrderid());
        holder.txtListContent.setText(pendingOrder.get(position).getMessage());
        String msgDate = Global.getDate(pendingOrder.get(position).getAdded_date(), "yyyy-MM-dd HH:mm:ss", "MMMM dd");
        holder.txtViewDate.setText(msgDate);

        holder.continue_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Orderid = pendingOrder.get(position).getOrderid();
                //  Orderid = PrefConnect.readString(context, PrefConnect.ORDER_ID, "");
                Call<ContinuePendingOrder> call = apiService.continuePendingOrder(Orderid);
                call.enqueue(new Callback<ContinuePendingOrder>() {
                    @Override
                    public void onResponse(Call<ContinuePendingOrder> call, Response<ContinuePendingOrder> response) {
                        if (response.isSuccessful()) {
                            //   Global.dismissProgress(p);
                            ContinuePendingOrder continuePendingOrder = response.body();
                            if (continuePendingOrder.getResult().equalsIgnoreCase("Success")) {
                                // Global.CustomToast(context, continuePendingOrder.getStatus());
                                PrefConnect.writeString(context, PrefConnect.ORDER_ID, Orderid);
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        dbHelper.deleteOrder(Orderid);
                                        //     dbHelper.deletePendingOrderHistory(Orderid);
                                    }
                                }, 5000);
                                Intent i = new Intent(context, OrderChatActivity.class);
                                context.startActivity(i);

                            } else {
                                //Global.CustomToast(PendingOrderChat.this, continuePendingOrder.getStatus());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ContinuePendingOrder> call, Throwable t) {

                    }

                });
            }
        });


        return convertView;

    }

    public class ViewHolder {
        CTextView txtListTitle;
        CTextView txtViewDate;
        CTextView txtListContent;
        ImageView continue_order;

    }
}
