package com.pulseplus.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.pulseplus.R;
import com.pulseplus.global.Global;
import com.pulseplus.model.OfferUpdate;
import com.pulseplus.model.Promotion;
import com.pulseplus.util.PrefConnect;
import com.pulseplus.web.APIService;
import com.pulseplus.web.RetrofitSingleton;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Bright Bridge on 10-Jan-17.
 */

public class PromotionAdapter extends ArrayAdapter {
    ArrayList<Promotion.promotion> promotion;
    LayoutInflater inflater;
    Context context;
    String offer_id;
    private APIService apiService;

    public PromotionAdapter(Context context, ArrayList<Promotion.promotion> promotion) {
        super(context, R.layout.promotion_listview_item, promotion);
        this.context = context;
        this.promotion = promotion;
        this.inflater = LayoutInflater.from(context);

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.promotion_listview_item, parent, false);
            holder.promotion_offer_tv = (TextView) convertView.findViewById(R.id.promotion_offer_tv);
            holder.promotion_min_order_tv = (TextView) convertView.findViewById(R.id.promotion_min_order_tv);
            holder.offer_code_tv = (TextView) convertView.findViewById(R.id.offer_code_tv);
            holder.promotion_button = (Button) convertView.findViewById(R.id.promotion_button);
            apiService = RetrofitSingleton.createService(APIService.class);
            convertView.setTag(holder);


        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.promotion_offer_tv.setText(promotion.get(position).getOffers());
        holder.promotion_min_order_tv.setText(promotion.get(position).getMinimum_order());
        holder.offer_code_tv.setText(promotion.get(position).getOffer_code());

        holder.promotion_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user_id = PrefConnect.readString(getContext(), PrefConnect.USER_ID, "");
                Call<OfferUpdate> call = apiService.offerUpdate(user_id, promotion.get(position).getOffer_code());
                call.enqueue(new Callback<OfferUpdate>() {
                    @Override
                    public void onResponse(Call<OfferUpdate> call, Response<OfferUpdate> response) {
                        if (response.isSuccessful()) {
                            // Global.dismissProgress(p);
                            OfferUpdate offerUpdate = response.body();
                            if (offerUpdate.getResult().equalsIgnoreCase("Success")) {
                                // Global.CustomToast(v.getContext(), orderCancel.getStatus());
                                // String jid = PrefConnect.readString(OrderChatActivity.this, PrefConnect.TO_JID, "");
                                // getmService().xmpp.sendMessage("Customer has cancelled the order", jid, "3", getDate());

                                PrefConnect.readString(getContext(), PrefConnect.USER_ID, "");
                              //  Toast.makeText(context, "Offer has updated successfully", Toast.LENGTH_LONG).show();
                                Global.CustomToast((Activity) context,"Offer has updated successfully");
                                // finish();
                            } else {
                                //Global.CustomToast(PromotionAdapter.this, orderCancel.getStatus());
                                Global.CustomToast((Activity) context,"failed");
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<OfferUpdate> call, Throwable t) {
                        Log.e("TAG", t.getMessage());
                    }
                });

            }
        });
        return convertView;


    }

    public class ViewHolder {
        TextView promotion_offer_tv;
        TextView promotion_min_order_tv;
        TextView offer_code_tv;
        Button promotion_button;

    }
}
