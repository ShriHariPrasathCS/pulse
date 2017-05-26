package com.pulseplus;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.pulseplus.adapter.PendingOrdersListviewAdapter;
import com.pulseplus.database.DBHelper;
import com.pulseplus.model.PendingOrder;

/**
 * Created by Bright Bridge on 07-Apr-17.
 */

public class PendingOrdersActivity extends AppCompatActivity {

    public Toolbar toolbar;
    int list_count;
    private ListView listView;
    private PendingOrdersListviewAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_orders);

        init();
        setToolbar();
        showList();
        setListner();
    }


    private void init() {
        listView = (ListView) findViewById(R.id.pending_order_listview);
    }

    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle("Pending Order");
            toolbar.setNavigationIcon(R.drawable.ic_back_arrow);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }

    private void showList() {

        DBHelper dbHelper = DBHelper.getInstance(PendingOrdersActivity.this);
        adapter = new PendingOrdersListviewAdapter(PendingOrdersActivity.this, dbHelper.getPendingOrderHis());
        list_count = listView.getCount();
        listView.setAdapter(adapter);




        /*list_count = String.valueOf(listView.getCount());
        Intent intent = new Intent(PendingOrdersActivity.this, HomeFragment.class);
        Bundle bundle = new Bundle();
        bundle.putString("list_count", list_count);
        HomeFragment homeFragment = new HomeFragment();
        homeFragment.setArguments(bundle);
        intent.putExtra("list_count", bundle);
        startActivity(intent);*/

    }

    private void setListner() {

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PendingOrder.Pending_orders chat = (PendingOrder.Pending_orders) listView.getItemAtPosition(position);
                String order_id = chat.getOrderid();
                Intent intent = new Intent(PendingOrdersActivity.this, PendingOrderChat.class);
                intent.putExtra("orderId", order_id);
//                Intent intentAdapter = new Intent(PendingOrdersActivity.this, PendingOrdersListviewAdapter.class);
//                intentAdapter.putExtra("orderId", order_id);
                startActivity(intent);
//                startActivity(intentAdapter);

            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }


}
