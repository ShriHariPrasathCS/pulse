package com.pulseplus.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pulseplus.R;
import com.pulseplus.global.Global;
import com.pulseplus.model.Cart;
import com.pulseplus.model.CartMedicineDelete;
import com.pulseplus.util.PrefConnect;
import com.pulseplus.web.APIService;
import com.pulseplus.web.RetrofitSingleton;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Bright Bridge on 23-Mar-17.
 */

public class CartListViewAdapter extends ArrayAdapter {
    public int total_amount;
    Intent intent;
    ArrayList<Cart.CartList> cartLists;
    ProgressDialog mProgressDialog;
    int minteger = 0;
    int quantity_increase = 0;
    int quantity_decrease = 0;
    String ttt;
    int total_product_price;
    private LayoutInflater inflater;
    private Context context;
    private String mGlobalVarValue;
    private APIService apiService;
    String orderid, name, qty, total;

    private CartListener cartListener;

    public CartListViewAdapter(Context context, ArrayList<Cart.CartList> cartLists, CartListener cartListener) {
        super(context, R.layout.cart_cardview_item, cartLists);
        this.context = context;
        this.cartLists = cartLists;
        this.inflater = LayoutInflater.from(context);
        this.cartListener = cartListener;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {

        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.cart_cardview_item, parent, false);
            holder.tv_medicine_name = (TextView) convertView.findViewById(R.id.tv_medicine_name);
            holder.tv_quantity = (TextView) convertView.findViewById(R.id.tv_quantity);
            holder.tv_product_price = (TextView) convertView.findViewById(R.id.tv_product_price);
            holder.tv_total_product_price = (TextView) convertView.findViewById(R.id.tv_total_product_price);
            holder.img_close = (ImageView) convertView.findViewById(R.id.img_close);
            holder.img_plus = (ImageView) convertView.findViewById(R.id.img_plus);
            holder.img_minus = (ImageView) convertView.findViewById(R.id.img_minus);
            apiService = RetrofitSingleton.createService(APIService.class);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String medicine_name = cartLists.get(position).getName();
        final int[] quantity = {Integer.parseInt(cartLists.get(position).getQty())};
        final int product_price = Integer.parseInt(cartLists.get(position).getPrice());


        holder.tv_medicine_name.setText(medicine_name);
        holder.tv_quantity.setText(String.valueOf(quantity[0]));
        holder.tv_product_price.setText("Rs." + product_price);



        total_product_price = quantity[0] * product_price;
        holder.tv_total_product_price.setText("Rs." + total_product_price);


        holder.img_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quantity[0]++;
                holder.tv_quantity.setText(String.valueOf(quantity[0]));
                total_product_price = quantity[0] * product_price;
                holder.tv_total_product_price.setText("Rs." + total_product_price);
                String tttt = String.valueOf(total_product_price);
                cartLists.get(position).setTotal(tttt);
                if (cartListener != null) {
                    cartListener.onCardAddOrRemove(getCartTotal());
                }
                return;


            }
        });

        holder.img_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (quantity[0] >= 2) {
                    quantity[0]--;
                    holder.tv_quantity.setText(String.valueOf(quantity[0]));
                    total_product_price = quantity[0] * product_price;
                    holder.tv_total_product_price.setText("Rs." + total_product_price);
                    String tttt = String.valueOf(total_product_price);
                    cartLists.get(position).setTotal(tttt);
                    if (cartListener != null) {
                        cartListener.onCardAddOrRemove(getCartTotal());
                    }
                    return;

                }

            }
        });

      /*  holder.img_plus.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                quantity[0]++;
                holder.tv_quantity.setText(String.valueOf(quantity[0]));
                total_product_price = quantity[0] * product_price;
                holder.tv_total_product_price.setText("Rs." + total_product_price);
                return true;
            }
        });

        holder.img_minus.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (quantity[0] >= 2){
                    quantity[0]--;
                    holder.tv_quantity.setText(String.valueOf(quantity[0]));
                    total_product_price = quantity[0] * product_price;
                    holder.tv_total_product_price.setText("Rs." + total_product_price);
                }
                return false;
            }
        });*/

        holder.img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String orderid = PrefConnect.readString(getContext(), PrefConnect.ORDER_ID, "");
                String name = cartLists.get(position).getName();
                CartMedicineDelete.Request request = new CartMedicineDelete.Request(orderid,name);
                Call<CartMedicineDelete.Response> call = apiService.cartMedicineDelete(request);
                call.enqueue(new Callback<CartMedicineDelete.Response>() {
                    @Override
                    public void onResponse(Call<CartMedicineDelete.Response> call, Response<CartMedicineDelete.Response> response) {
                        if (response.isSuccessful()) {
                            CartMedicineDelete.Response medicineDelete = response.body();
                            if (medicineDelete.getResult().equalsIgnoreCase("Success")) {
                                PrefConnect.readString(getContext(), PrefConnect.ORDER_ID, "");
                                cartLists.remove(position);
                                if (cartListener != null) {
                                    cartListener.onCardAddOrRemove(getCartTotal());
                                    Global.CustomToast((Activity) context,"Product Deleted");
                                }
                                notifyDataSetChanged();
                                Global.CustomToast((Activity) context,"Product Deleted");
                            } else {
                                Global.CustomToast((Activity) context,"Product Deletion Failed");
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<CartMedicineDelete.Response> call, Throwable t) {
                        Log.e("TAG", t.getMessage());
                    }
                });
            }
        });
        return convertView;
    }

    public String getCartTotal() {
        int total_amount = 0;

        for (int i = 0; i < cartLists.size(); i++) {
            int costValue = Integer.parseInt(cartLists.get(i).getTotal());
            total_amount += costValue;
        }
        return String.valueOf(total_amount);
//        Toast.makeText(getContext(), "Total number of Items are:" + cart_protuct_final_total, Toast.LENGTH_LONG).show();
    }

    public interface CartListener {
        void onCardAddOrRemove(String total);
    }


    public class ViewHolder {
        TextView tv_medicine_name;
        TextView tv_quantity;
        TextView tv_product_price;
        TextView tv_total_product_price;
        ImageView img_close;
        ImageView img_minus;
        ImageView img_plus;
        //  TableRow tableRow;


    }
}
