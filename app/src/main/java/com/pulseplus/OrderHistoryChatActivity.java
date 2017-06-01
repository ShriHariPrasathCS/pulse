package com.pulseplus;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;

import com.armor.fontlib.CButton;
import com.pulseplus.adapter.OrderHistorychatAdapter;
import com.pulseplus.database.DBHelper;
import com.pulseplus.global.Global;
import com.pulseplus.model.Child;
import com.pulseplus.model.Group;
import com.pulseplus.model.OrderHistoryChat;
import com.pulseplus.model.Reorder;
import com.pulseplus.util.PrefConnect;
import com.pulseplus.web.APIService;
import com.pulseplus.web.RetrofitSingleton;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class OrderHistoryChatActivity extends AppCompatActivity {

    String orderid, orderID;
    Button reorder, neworder;
    ImageView cart_imageView;
    private ExpandableListView listView;
    private ProgressDialog p;
    private APIService apiService;
    private ArrayList<OrderHistoryChat.Chat_history> chatHistory;
    private OrderHistorychatAdapter adapter;
    private ArrayList<Child> childList;
    private ArrayList<Group> groupList;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history_chat);
        init();
        //  p.show();
        //  loadData();
        showList();
        setToolbar();
        setListner();


    }

    private void function() {
        orderID = getIntent().getStringExtra("orderId");
        Intent intent = new Intent(OrderHistoryChatActivity.this, OrderHistoryCart.class);
        intent.putExtra("orderid", orderID);
        startActivity(intent);


    }

    private void init() {
        listView = (ExpandableListView) findViewById(R.id.list);
        apiService = RetrofitSingleton.createService(APIService.class);
        reorder = (CButton) findViewById(R.id.btn_Reorder);
        neworder = (CButton) findViewById(R.id.btn_Neworder);
        p = Global.initProgress(OrderHistoryChatActivity.this);
        cart_imageView = (ImageView) findViewById(R.id.cart_imageView);
        cart_imageView.setVisibility(View.VISIBLE);
    }

    private void loadData() {
        orderID = getIntent().getStringExtra("orderId");
        Call<OrderHistoryChat> call = apiService.orderHistoryChat(orderID);
        call.enqueue(new Callback<OrderHistoryChat>() {
            @Override
            public void onResponse(Call<OrderHistoryChat> call, Response<OrderHistoryChat> response) {
                Global.dismissProgress(p);
                if (response.isSuccessful()) {
                    OrderHistoryChat order = response.body();
                    if (order.getResult().equalsIgnoreCase("Success")) {
                        //PrefConnect.writeString(getActivity(), PrefConnect.ORDER_ID, order.getOrderid());
                        Global.dismissProgress(p);
                        chatHistory = order.getChat_history();
                        Log.e("TAG", "onResponse: " + order.getChat_history().get(0).getOrder_id());

                        // showList(order);

                    } else {
                        Global.dismissProgress(p);
                    }
                }
            }

            @Override
            public void onFailure(Call<OrderHistoryChat> call, Throwable t) {
                Global.dismissProgress(p);
                Log.e("TAG", t.getMessage());
            }
        });
    }

    public void showList() {
        DBHelper dbHelper = DBHelper.getInstance(OrderHistoryChatActivity.this);
        orderID = getIntent().getStringExtra("orderId");
        groupList = dbHelper.getOrders(orderID);
        adapter = new OrderHistorychatAdapter(OrderHistoryChatActivity.this, groupList);
        listView.setAdapter(adapter);
        expandAll();
        listView.setSelection(groupList.get(0).getChildren().size());
    }

    private void expandAll() {
        int count = adapter.getGroupCount();
        for (int i = 0; i < count; i++) {
            if (!listView.isGroupExpanded(i)) {
                listView.expandGroup(i);
            }
        }
    }

    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(orderID);
        toolbar.setNavigationIcon(R.drawable.ic_back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        cart_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OrderHistoryChatActivity.this, OrderHistoryCart.class);
                intent.putExtra("orderid", orderID);
                startActivity(intent);
            }
        });

    }


    private void setListner() {
        listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if (groupPosition == 0) {
                    listView.expandGroup(groupPosition);
                    return true;
                }
                return false;
            }
        });

        reorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Reorder.Request request = new Reorder.Request(orderID);
                Call<Reorder.Response> call = apiService.reorder(request);
                call.enqueue(new Callback<Reorder.Response>() {
                    @Override
                    public void onResponse(Call<Reorder.Response> call, Response<Reorder.Response> response) {
                        if (response.isSuccessful()) {
                            Reorder.Response resend = response.body();
                            if (resend.getResult().equalsIgnoreCase("Success")) {
                                Global.dismissProgress(p);
                                PrefConnect.writeString(OrderHistoryChatActivity.this, PrefConnect.ORDER_ID, resend.getOrderid());
                                Intent i = new Intent(OrderHistoryChatActivity.this, OrderChatActivity.class);
                                startActivity(i);
                                finish();
                                //  i.putExtra("orderId", order_id);
                            } else {
                                Global.dismissProgress(p);
                            }

                        }
                    }

                    @Override
                    public void onFailure(Call<Reorder.Response> call, Throwable t) {

                    }
                });
            }
        });
        neworder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(OrderHistoryChatActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

   /* @Override
    public void onStart() {
        super.onStart();
        function();
    }

    @Override
    public void onStop() {
        super.onStop();
        finish();

    }*/
}
