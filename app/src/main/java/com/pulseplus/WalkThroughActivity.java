package com.pulseplus;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class WalkThroughActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST = 1235;
    ArrayList<Integer> views;
    ViewPager viewPager;
    CustomPager adapter;
    TextView textView;
    LinearLayout indicatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walkthrough);
        init();
        setAdapter();
        setListener();
        checkPermission();
    }

    private void checkPermission() {

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            ArrayList<String> permissionList = new ArrayList<>();
            if (checkSelfPermission(android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                permissionList.add(android.Manifest.permission.CAMERA);
            }
            if (checkSelfPermission(android.Manifest.permission.GET_ACCOUNTS) == PackageManager.PERMISSION_DENIED) {
                permissionList.add(android.Manifest.permission.GET_ACCOUNTS);
            }
            if (checkSelfPermission(android.Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_DENIED) {
                permissionList.add(android.Manifest.permission.READ_CONTACTS);
            }
            if (checkSelfPermission(android.Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED) {
                permissionList.add(android.Manifest.permission.RECORD_AUDIO);
            }
            if (checkSelfPermission(android.Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_DENIED) {
                permissionList.add(android.Manifest.permission.READ_PHONE_STATE);
            }
            if (checkSelfPermission(android.Manifest.permission.READ_SMS) == PackageManager.PERMISSION_DENIED) {
                permissionList.add(android.Manifest.permission.READ_SMS);
            }
            if (checkSelfPermission(android.Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_DENIED) {
                permissionList.add(android.Manifest.permission.RECEIVE_SMS);
            }
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                permissionList.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (permissionList.size() > 0) {
                String[] permissionArray = new String[permissionList.size()];
                permissionList.toArray(permissionArray);
                requestPermissions(permissionArray, PERMISSION_REQUEST);
            }
        }
    }

    private void init() {
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        textView = (TextView) findViewById(R.id.txt_skip);
        indicatorLayout = (LinearLayout) findViewById(R.id.indicatorLayout);

        views = new ArrayList<>();

        // adding walk screens
        views.add(R.layout.walk_page1);
        views.add(R.layout.walk_page2);
        views.add(R.layout.walk_page3);
        views.add(R.layout.walk_page4);
    }

    private void setAdapter() {
        adapter = new CustomPager();
        viewPager.setAdapter(adapter);
        setIndicator(0);
    }

    private void setListener() {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                setIndicator(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*final String otp = PrefConnect.readString(WalkThroughActivity.this, PrefConnect.OTP, "");
                if (otp.equals("")) {*/
                Intent intent = new Intent(WalkThroughActivity.this, VerifyPhoneActivity.class);
                startActivity(intent);
                finish();
                //}
            }
        });
    }

    private void setIndicator(int position) {
        switch (position) {
            case 0:
                textView.setEnabled(true);
                textView.setText(getString(R.string.skip));
                ((ImageView) indicatorLayout.getChildAt(0)).setImageResource(R.drawable.circle_white);
                ((ImageView) indicatorLayout.getChildAt(1)).setImageResource(R.drawable.circle_white1);
                ((ImageView) indicatorLayout.getChildAt(2)).setImageResource(R.drawable.circle_white2);
                ((ImageView) indicatorLayout.getChildAt(3)).setImageResource(R.drawable.circle_white3);
                break;
            case 1:
                textView.setEnabled(true);
                textView.setText(getString(R.string.skip));
                ((ImageView) indicatorLayout.getChildAt(1)).setImageResource(R.drawable.circle_white);
                ((ImageView) indicatorLayout.getChildAt(0)).setImageResource(R.drawable.circle_white);
                ((ImageView) indicatorLayout.getChildAt(2)).setImageResource(R.drawable.circle_white2);
                ((ImageView) indicatorLayout.getChildAt(3)).setImageResource(R.drawable.circle_white3);
                break;
            case 2:
                textView.setEnabled(true);
                textView.setText(getString(R.string.skip));
                ((ImageView) indicatorLayout.getChildAt(1)).setImageResource(R.drawable.circle_white);
                ((ImageView) indicatorLayout.getChildAt(2)).setImageResource(R.drawable.circle_white);
                ((ImageView) indicatorLayout.getChildAt(0)).setImageResource(R.drawable.circle_white);
                ((ImageView) indicatorLayout.getChildAt(3)).setImageResource(R.drawable.circle_white3);
                break;
            case 3:
                textView.setEnabled(true);
                textView.setText(getString(R.string.getstart));
                ((ImageView) indicatorLayout.getChildAt(3)).setImageResource(R.drawable.circle_white);
                ((ImageView) indicatorLayout.getChildAt(0)).setImageResource(R.drawable.circle_white);
                ((ImageView) indicatorLayout.getChildAt(1)).setImageResource(R.drawable.circle_white);
                ((ImageView) indicatorLayout.getChildAt(2)).setImageResource(R.drawable.circle_white);
                break;
        }
    }

    // Custom PageViewer adapter
    private class CustomPager extends PagerAdapter {
        LayoutInflater inflater;

        CustomPager() {
            inflater = LayoutInflater.from(WalkThroughActivity.this);
        }

        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return object == view;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = inflater.inflate(views.get(position), container, false);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
