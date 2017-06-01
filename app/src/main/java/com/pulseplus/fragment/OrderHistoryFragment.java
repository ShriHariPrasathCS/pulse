package com.pulseplus.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.armor.fontlib.CButton;
import com.pulseplus.MainActivity;
import com.pulseplus.OrderHistoryChatActivity;
import com.pulseplus.R;
import com.pulseplus.adapter.OrderHistoryListViewAdapter;
import com.pulseplus.database.DBHelper;
import com.pulseplus.global.Global;
import com.pulseplus.model.OrderHistory;
import com.pulseplus.navigation.DrawerFragment;
import com.pulseplus.util.PrefConnect;
import com.pulseplus.web.APIService;
import com.pulseplus.web.RetrofitSingleton;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;
import static com.pulseplus.util.PrefConnect.ORDER_ID;

public class OrderHistoryFragment extends Fragment {

    ArrayList<OrderHistory.Order_history> orderHistory;
    private ListView listView;
    private ProgressDialog p;
    private APIService apiService;
    private String orderid;
    private OrderHistoryListViewAdapter adapter;
    private CButton btnOrder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_history, container, false);
        init(view);
        // p.show();
        showList();
        setListner();
        return view;


    }

    public void init(View view) {
        listView = (ListView) view.findViewById(R.id.orderList);
        p = Global.initProgress(getActivity());
        orderid = PrefConnect.readString(getActivity(), ORDER_ID, "");
        apiService = RetrofitSingleton.createService(APIService.class);
        btnOrder = (CButton) view.findViewById(R.id.btnOrder);


    }

    private void loadData() {
        /*OrderHistory history = new OrderHistory();*/
        String userId = PrefConnect.readString(getActivity(), PrefConnect.USER_ID, "");
        Call<OrderHistory> call = apiService.orderHistory(userId);
        call.enqueue(new Callback<OrderHistory>() {
            @Override
            public void onResponse(Call<OrderHistory> call, Response<OrderHistory> response) {
                Global.dismissProgress(p);
                if (response.isSuccessful()) {
                    OrderHistory order = response.body();
                    if (order.getResult().equalsIgnoreCase("Success")) {
                        //PrefConnect.writeString(getActivity(), PrefConnect.ORDER_ID, order.getOrderid());
                        Global.dismissProgress(p);
                        orderHistory = order.getOrder_history();
                        Log.e(TAG, "onResponse: " + order.getOrder_history().get(0).getOrderid());
                        showList();
                    } else {
                        Global.dismissProgress(p);

                    }
                }
            }


            @Override
            public void onFailure(Call<OrderHistory> call, Throwable t) {
                Global.dismissProgress(p);
                // Log.e("TAG", t.getMessage());

            }
        });
    }

    public void showList() {
        DBHelper dbHelper = DBHelper.getInstance(getActivity());
        //Collections.reverse(orderHistory);
        adapter = new OrderHistoryListViewAdapter(getActivity(), dbHelper.getOrderHis());
        listView.setAdapter(adapter);

    }

    private void setListner() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                OrderHistory.Order_history chat = (OrderHistory.Order_history) listView.getItemAtPosition(position);
                String order_id = chat.getOrderid();
                Intent intent = new Intent(getActivity(), OrderHistoryChatActivity.class);
                intent.putExtra("orderId", order_id);
                startActivity(intent);

            }
        });

        btnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getActivity(), MainActivity.class);
                startActivity(i);
            }
        });


    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onAttached(DrawerFragment.ORDER_HISTORY);
    }

    @Override
    public void onResume() {
        super.onResume();
        // p.show();
        // loadData();

    }
}
