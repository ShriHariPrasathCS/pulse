package com.pulseplus;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.pulseplus.adapter.CartListViewAdapter;
import com.pulseplus.dialog.ScheduleOrder;
import com.pulseplus.global.Global;
import com.pulseplus.model.Cart;
import com.pulseplus.model.EditCart;
import com.pulseplus.util.PrefConnect;
import com.pulseplus.web.APIService;
import com.pulseplus.web.RetrofitSingleton;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Bright Bridge on 25-Mar-17.
 */

public class OrderConfirmationActivity extends AppCompatActivity implements CartListViewAdapter.CartListener, ScheduleOrder.ScheduleCallback {
    ArrayList<Cart.CartList> cartlist;
    String tot;
    String orderid;
    private ListView listView;
    private APIService apiService;
    private ProgressDialog p;
    private CartListViewAdapter cartAdapter;
    private Button button_confirm;
    private TextView tv_order_total;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);


        init();
        loadData();
        setListener();

    }

    private void setListener() {

        button_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ArrayList<EditCart.Cart> list = new ArrayList<EditCart.Cart>();
                for (Cart.CartList cart : cartlist) {
                    EditCart.Cart cart1 = new EditCart.Cart(cart.getName(), cart.getQty(), cart.getTotal());
                    list.add(cart1);
                }
                EditCart.Request request = new EditCart.Request(orderid, list);
                Call<EditCart.Response> call = apiService.editCart(request);
                call.enqueue(new Callback<EditCart.Response>() {
                    @Override
                    public void onResponse(Call<EditCart.Response> call, Response<EditCart.Response> response) {
                        if (response.isSuccessful()) {
                            EditCart.Response medicineDelete = response.body();
                            if (medicineDelete.getResult().equalsIgnoreCase("Success")) {
                                // PrefConnect.readString(getContext(), PrefConnect.ORDER_ID, "");
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<EditCart.Response> call, Throwable t) {
                        Log.e("TAG", t.getMessage());
                    }
                });


                ScheduleOrder scheduleOrderDialog = new ScheduleOrder();
                scheduleOrderDialog.setCallback(OrderConfirmationActivity.this);
                scheduleOrderDialog.show(getSupportFragmentManager(), "ScheduleOrder");

            }
        });


        //     cartAdapter = new CartListViewAdapter(OrderConfirmationActivity.this, cartlist);
        //  tv_order_total.setText(cartAdapter.cart_protuct_final_total);


    }

    private void loadData() {
        String orderId = PrefConnect.readString(OrderConfirmationActivity.this, PrefConnect.ORDER_ID, "");
        Call<Cart> call = apiService.cart(orderId);
        call.enqueue(new Callback<Cart>() {
            @Override
            public void onResponse(Call<Cart> call, Response<Cart> response) {
                Global.dismissProgress(p);
                if (response.isSuccessful()) {
                    Cart order = response.body();
                    if (order.getResult().equalsIgnoreCase("Success")) {
                        //PrefConnect.writeString(getActivity(), PrefConnect.ORDER_ID, order.getOrderid());
                        Global.dismissProgress(p);
                        cartlist = order.getCart_list();
                        //  Log.e(TAG, "onResponse: " + order.getOrder_history().get(0).getOrderid());
                        showList();
                    } else {
                        Global.dismissProgress(p);

                    }
                }
            }

            @Override
            public void onFailure(Call<Cart> call, Throwable t) {
                Global.dismissProgress(p);
                // Log.e("TAG", t.getMessage());

            }
        });
    }

    public void showList() {

        cartAdapter = new CartListViewAdapter(OrderConfirmationActivity.this, cartlist, this);
        listView.setAdapter(cartAdapter);
        tv_order_total.setText("Rs." + cartAdapter.getCartTotal());
    }

    private void init() {
        listView = (ListView) findViewById(R.id.cart_listview);
        button_confirm = (Button) findViewById(R.id.button_confirm);
        tv_order_total = (TextView) findViewById(R.id.tv_order_total);
        apiService = RetrofitSingleton.createService(APIService.class);
    }

    @Override
    public void onCardAddOrRemove(String total) {
        tv_order_total.setText("Rs." + total);
    }

    @Override
    public void onMorningClicked() {
        Intent intent = new Intent();
    //    intent.putExtra("message", "Your order has been confirmed,it will be delivered by 6-10AM");
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onEveningClicked() {
        Intent intent = new Intent();
    //    intent.putExtra("message", "Your order has been confirmed,it will be delivered by 6-10PM");
        setResult(RESULT_OK, intent);
        finish();
    }
}
