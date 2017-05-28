package com.pulseplus.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.pulseplus.MainActivity;
import com.pulseplus.R;
import com.pulseplus.adapter.PromotionAdapter;
import com.pulseplus.global.Global;
import com.pulseplus.model.Internet;
import com.pulseplus.model.Promotion;
import com.pulseplus.navigation.DrawerFragment;
import com.pulseplus.util.PrefConnect;
import com.pulseplus.web.APIService;
import com.pulseplus.web.RetrofitSingleton;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

/**
 * Bright Bridge on 26-Dec-16.
 */

public class PromotionFragment extends Fragment {
    private ListView promotion_list;
    ArrayList<Promotion.promotion> promotion_listview;
    private APIService apiService;
    private PromotionAdapter promotionAdapter;

    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_promotion, container, false);
        init();
        setListener();
        return view;
    }

    private void setListener() {

    }

    private void init() {
        promotion_list = (ListView) view.findViewById(R.id.promotion_list);
        apiService = RetrofitSingleton.createService(APIService.class);

    }

    private void loadData() {
        /*OrderHistory history = new OrderHistory();*/
        String user_id = PrefConnect.readString(getActivity(), PrefConnect.USER_ID, "");
        Call<Promotion> call = apiService.promotion(user_id);
        call.enqueue(new Callback<Promotion>() {
            @Override
            public void onResponse(Call<Promotion> call, Response<Promotion> response) {
                //Global.dismissProgress(p);
                if (response.isSuccessful()) {
                    Promotion promotion_page = response.body();
                    if (promotion_page.getResult().equalsIgnoreCase("Success")) {
                        //PrefConnect.writeString(getActivity(), PrefConnect.ORDER_ID, order.getOrderid());
                        // Global.dismissProgress(p);
                        // promotion_listview = null;
                        promotion_listview = promotion_page.getPromotion();
                        Log.e(TAG, "onResponse: " + promotion_page.getPromotion());
                        showList();
                    } else {
                        //Global.dismissProgress(p);
                    }
                }
            }


            @Override
            public void onFailure(Call<Promotion> call, Throwable t) {
                // Global.dismissProgress(p);
                Log.e("TAG", t.getMessage());

            }
        });
    }

    private void showList() {
        promotionAdapter = new PromotionAdapter(getActivity(), promotion_listview);
        promotion_list.setAdapter(promotionAdapter);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onAttached(DrawerFragment.PROMOTION);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }


    @Override
    public void onResume() {
        super.onResume();
        //  p.show();
        loadData();

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onInternetConnectionCheck(Internet internet) {
        if (internet.isConnected) {
            //  Global.CustomToast(getActivity(), "Internet Avaliable");
        } else {
            Global.CustomToast(getActivity(), "Check your internet connection");
            //Global.Toast(MainActivity.this, "Check your internet connection");
        }
    }
}
