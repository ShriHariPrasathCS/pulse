package com.pulseplus;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.pulseplus.adapter.CartListViewAdapter;
import com.pulseplus.global.Global;
import com.pulseplus.model.Cart;
import com.pulseplus.web.APIService;
import com.pulseplus.web.RetrofitSingleton;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Bright Bridge on 25-Apr-17.
 */

public class PendingOrderCart extends AppCompatActivity implements CartListViewAdapter.CartListener {
    ArrayList<Cart.CartList> cartlist;
    String tot;
    private ListView listView;
    private APIService apiService;
    private ProgressDialog p;
    private CartListViewAdapter cartAdapter;
    private Button btnContinue;
    private TextView tv_order_total;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_order_cart);

        init();
        loadData();
        setListener();

    }

    private void setListener() {

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void loadData() {
        // String orderId = PrefConnect.readString(PendingOrderCart.this, PrefConnect.ORDER_ID, "");
        Bundle bundle = getIntent().getExtras();
        String orderId = bundle.getString("orderId");
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

        cartAdapter = new CartListViewAdapter(PendingOrderCart.this, cartlist, this);
        listView.setAdapter(cartAdapter);
        tv_order_total.setText("Rs." + cartAdapter.getCartTotal());
    }

    private void init() {
        listView = (ListView) findViewById(R.id.cart_listview);
        btnContinue = (Button) findViewById(R.id.btnContinue);
        tv_order_total = (TextView) findViewById(R.id.tv_order_total);
        apiService = RetrofitSingleton.createService(APIService.class);
    }

    @Override
    public void onCardAddOrRemove(String total) {
        tv_order_total.setText("Rs." + total);
    }
}
