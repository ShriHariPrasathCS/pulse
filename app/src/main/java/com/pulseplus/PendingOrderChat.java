package com.pulseplus;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;

import com.pulseplus.adapter.PendingOrderChatAdapter;
import com.pulseplus.database.DBHelper;
import com.pulseplus.global.Global;
import com.pulseplus.model.Child;
import com.pulseplus.model.ContinuePendingOrder;
import com.pulseplus.model.Group;
import com.pulseplus.util.PrefConnect;
import com.pulseplus.web.APIService;
import com.pulseplus.web.RetrofitSingleton;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Bright Bridge on 10-Apr-17.
 */

public class PendingOrderChat extends AppCompatActivity {
    String orderID, ord;
    String Orderid, ORDERID;
    ImageView cart_imageView;
    private ExpandableListView listView;
    private ProgressDialog p;
    private APIService apiService;
    private PendingOrderChatAdapter adapter;
    private ArrayList<Child> childList;
    private ArrayList<Group> groupList;
    private Toolbar toolbar;
    private Handler handler = new Handler();
    private DBHelper dbHelper;
    private Button btn_continue_pending_order;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pending_order_chat);
        Bundle bundle = getIntent().getExtras();
        orderID = bundle.getString("orderId");
        init();
        showList();
        setToolbar();
        setListner();
    }

    private void init() {
        listView = (ExpandableListView) findViewById(R.id.list);
        apiService = RetrofitSingleton.createService(APIService.class);
        p = Global.initProgress(PendingOrderChat.this);
        btn_continue_pending_order = (Button) findViewById(R.id.btn_continue_pending_order);
        dbHelper = DBHelper.getInstance(this);
        cart_imageView = (ImageView) findViewById(R.id.cart_imageView);
        //  cart_imageView.setVisibility(View.VISIBLE);


        //  ORDERID = toolbar.getTitle().toString();


    }


    public void showList() {
        DBHelper dbHelper = DBHelper.getInstance(PendingOrderChat.this);
        // orderID = getIntent().getStringExtra("orderId");
        groupList = dbHelper.getPendingOrders(orderID);
        adapter = new PendingOrderChatAdapter(PendingOrderChat.this, groupList);
        listView.setAdapter(adapter);
        expandAll();
        listView.setSelection(groupList.get(0).getChildren().size());
//        if (listView.equals("Your Order has been generated. please view the cart")){
//            cart_imageView.setVisibility(View.VISIBLE);
//        }


        for (Group group : groupList) {
            for (Child child : group.getChildren()) {
                if (child.getMessage().equals("Your Order has been generated. Please view the cart")) {
                    cart_imageView.setVisibility(View.VISIBLE);
                }
            }
        }

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
                Intent intent = new Intent(PendingOrderChat.this, PendingOrderCart.class);
                intent.putExtra("orderId", orderID);
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

        btn_continue_pending_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Orderid = PrefConnect.readString(PendingOrderChat.this, PrefConnect.ORDER_ID, "");
                Call<ContinuePendingOrder> call = apiService.continuePendingOrder(orderID);
                call.enqueue(new Callback<ContinuePendingOrder>() {
                    @Override
                    public void onResponse(Call<ContinuePendingOrder> call, Response<ContinuePendingOrder> response) {
                        if (response.isSuccessful()) {
                            Global.dismissProgress(p);
                            ContinuePendingOrder continuePendingOrder = response.body();
                            if (continuePendingOrder.getResult().equalsIgnoreCase("Success")) {
                                Global.CustomToast(PendingOrderChat.this, continuePendingOrder.getStatus());
                                PrefConnect.writeString(PendingOrderChat.this, PrefConnect.TO_JID, "");
                                PrefConnect.writeString(PendingOrderChat.this, PrefConnect.ORDER_ID, orderID);
                              //  dbHelper.insertPendingOrder(orderID, "3", "3", "","");

                                Intent i = new Intent(PendingOrderChat.this, OrderChatActivity.class);
                                startActivity(i);

                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        dbHelper.deleteOrderHistory(orderID);
                                        dbHelper.deletePendingOrderHistory(orderID);
                                    }
                                }, 5000);
                                finish();

                            } else {
                                Global.CustomToast(PendingOrderChat.this, continuePendingOrder.getStatus());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ContinuePendingOrder> call, Throwable t) {

                    }

                });

            }
        });
    }
}
