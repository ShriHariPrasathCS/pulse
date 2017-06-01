package com.pulseplus.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.armor.fontlib.CTextView;
import com.pulseplus.R;
import com.pulseplus.global.Global;
import com.pulseplus.model.OrderHistory;

import java.util.ArrayList;

public class OrderHistoryListViewAdapter extends ArrayAdapter<OrderHistory.Order_history> {
    LayoutInflater inflater;
    Context context;
    ArrayList<OrderHistory.Order_history> orderHistory;

    public OrderHistoryListViewAdapter(Context context, ArrayList<OrderHistory.Order_history> orderHistory) {
        super(context, R.layout.order_history_listview_item, orderHistory);
        this.context = context;
        this.orderHistory = orderHistory;
        this.inflater = LayoutInflater.from(context);

    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.order_history_listview_item, parent, false);
            holder.txtListTitle = (CTextView) convertView.findViewById(R.id.txtListTitle);
            holder.txtViewDate = (CTextView) convertView.findViewById(R.id.txtViewDate);
            holder.txtListContent = (CTextView) convertView.findViewById(R.id.txtListContent);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }


        holder.txtListTitle.setText(orderHistory.get(position).getOrderid());
        holder.txtListContent.setText(orderHistory.get(position).getMessage());
        String msgDate = Global.getDate(orderHistory.get(position).getAdded_date(), "yyyy-MM-dd HH:mm:ss", "MMMM dd");
        holder.txtViewDate.setText(msgDate);


        return convertView;

    }

    public class ViewHolder {
        CTextView txtListTitle;
        CTextView txtViewDate;
        CTextView txtListContent;
    }
}
