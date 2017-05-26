package com.pulseplus.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.armor.fontlib.CTextView;
import com.pulseplus.MainActivity;
import com.pulseplus.R;
import com.pulseplus.WebViewActivity;
import com.pulseplus.global.Global;
import com.pulseplus.model.Internet;
import com.pulseplus.navigation.DrawerFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by Bright Bridge on 18-Nov-16.
 */

public class AboutUsFragment extends Fragment {
    View view;
    LinearLayout terms_condition, about_pulse_plus, sub_about_us_layout;
    //RelativeLayout sub_about_us_layout;
    ImageView img_left_about_pp, img_down_about_pp;
    TextView about_us_our_team, about_us_activity, about_us_our_mission;
    Boolean visible = false;
    ProgressDialog p;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_about_us, container, false);
        init();
        setListener();
        return view;
    }

    private void setListener() {
        terms_condition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), WebViewActivity.class);
                intent.putExtra("url","http://pulseplus.in/terms/");
                startActivity(intent);
            }
        });

        about_pulse_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!visible){
                    img_left_about_pp.setVisibility(View.GONE);
                    img_down_about_pp.setVisibility(View.VISIBLE);
                    sub_about_us_layout.setVisibility(View.VISIBLE);
                    visible = true;
                }else {
                    img_down_about_pp.setVisibility(View.GONE);
                    img_left_about_pp.setVisibility(View.VISIBLE);
                    sub_about_us_layout.setVisibility(View.GONE);
                    visible = false;
                }
            }
        });

       /* img_left_about_pp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                img_left_about_pp.setVisibility(View.GONE);
                img_down_about_pp.setVisibility(View.VISIBLE);
                sub_about_us_layout.setVisibility(View.VISIBLE);
                img_left_about_pp.setVisibility(View.GONE);
                img_down_about_pp.setVisibility(View.VISIBLE);
            }
        });

        img_down_about_pp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                img_down_about_pp.setVisibility(View.GONE);
                img_left_about_pp.setVisibility(View.VISIBLE);
                sub_about_us_layout.setVisibility(View.GONE);
            }
        });*/

        about_us_our_team.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), WebViewActivity.class);
                intent.putExtra("url","http://pulseplus.in/our-team/");
                startActivity(intent);



            }
        });

        about_us_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), WebViewActivity.class);
                intent.putExtra("url","http://pulseplus.in/activity/");
                startActivity(intent);

            }
        });

        about_us_our_mission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), WebViewActivity.class);
                intent.putExtra("url","http://pulseplus.in/our-mission/");
                startActivity(intent);

            }
        });



    }

   /* public class myWebClient extends WebViewClient
    {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // TODO Auto-generated method stub
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub
            progressBar.setVisibility(View.VISIBLE);
            view.loadUrl(url);
            return true;

        }

        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO Auto-generated method stub
            super.onPageFinished(view, url);

            progressBar.setVisibility(View.GONE);
        }
    }
*/
    private void init() {
        terms_condition = (LinearLayout)view.findViewById(R.id.terms_condition);
        about_pulse_plus = (LinearLayout)view.findViewById(R.id.about_pulse_plus);
        sub_about_us_layout = (LinearLayout) view.findViewById(R.id.sub_about_us_layout);
        img_left_about_pp = (ImageView) view.findViewById(R.id.img_left_about_pp);
        img_down_about_pp = (ImageView) view.findViewById(R.id.img_down_about_pp);
        about_us_our_team = (CTextView) view.findViewById(R.id.about_us_our_team);
        about_us_activity = (CTextView) view.findViewById(R.id.about_us_activity);
        about_us_our_mission = (CTextView) view.findViewById(R.id.about_us_our_mission);
        p = Global.initProgress(getActivity());

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onAttached(DrawerFragment.ABOUT);
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onInternetConnectionCheck(Internet internet) {
        if (internet.isConnected) {
           // Global.CustomToast(getActivity(), "Internet Avaliable");
        } else {
            Global.CustomToast(getActivity(), "Check your internet connection");
            //Global.Toast(MainActivity.this, "Check your internet connection");
        }
    }
}
