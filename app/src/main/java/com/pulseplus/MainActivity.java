package com.pulseplus;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;

import com.pulseplus.background.RegStatusService;
import com.pulseplus.database.DBHelper;
import com.pulseplus.dialog.BanConfirmDialog;
import com.pulseplus.dialog.RegisterDialog;
import com.pulseplus.fragment.AboutUsFragment;
import com.pulseplus.fragment.ContactUsFragment;
import com.pulseplus.fragment.FaqFragment;
import com.pulseplus.fragment.HomeFragment;
import com.pulseplus.fragment.MyAccountFragment;
import com.pulseplus.fragment.OrderHistoryFragment;
import com.pulseplus.fragment.PromotionFragment;
import com.pulseplus.global.Global;
import com.pulseplus.model.Internet;
import com.pulseplus.navigation.DrawerFragment;
import com.pulseplus.service.LocalBinder;
import com.pulseplus.service.XmppService;
import com.pulseplus.web.APIService;
import com.pulseplus.web.RetrofitSingleton;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


public class MainActivity extends AppCompatActivity implements DrawerFragment.NavigationDrawerCallBack, RegisterDialog.RegisterCallback, BanConfirmDialog.BanCallback {

    private static final String TAG = MainActivity.class.getName();
    boolean doubleBackToExitPressedOnce = false;
    private Toolbar toolbar;
    private DrawerFragment drawerFragment;
    private String mTitle;
    private ProgressDialog p;
    private APIService apiService;
    private XmppService xmppService;
    private boolean mBounded;
    private RegisterDialog registerDialog;
    private StatusReceiver receiver;
    private RegStatusService regStatusService;
    private BandXmppReciever recieverBanned;
    private boolean bound = false;
    private DBHelper dbHelper;
    private ServiceConnection regConnect = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            RegStatusService.RegBinder binder = (RegStatusService.RegBinder) service;
            regStatusService = binder.getService();
            regStatusService.startRunning();
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound = false;
        }
    };
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //   LocalBinder binder = (LocalBinder)service;
            //   MainActivity.this.service =()
            LocalBinder<XmppService> localBinder = new LocalBinder<>(xmppService);
            xmppService = (localBinder).getService();
            mBounded = true;
            Log.d("TAG", "onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            xmppService = null;
            mBounded = false;
            Log.d("TAG", "onServiceDisconnected");
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
     /*   if (isNetworkAvailable() == false) {
            Global.Toast(MainActivity.this, "Check your Internet Connection");
        }
          Global.CustomToast(MainActivity.this,"hello bb");*/
        init();


    }

    private void init() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawerFragment = (DrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        setupToolbar();

        p = Global.initProgress(this);
        apiService = RetrofitSingleton.createService(APIService.class);
        dbHelper = DBHelper.getInstance(this);

    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        int position = getIntent().getIntExtra("position", 0);
        drawerFragment.initDrawer((DrawerLayout) findViewById(R.id.drawerLayout), toolbar, position);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!drawerFragment.isDrawerOpen())
            toolbar.setTitle(mTitle);
        return true;
    }

    @Override
    public void onNavigationItemSelected(int position) {
        switch (position) {
            case DrawerFragment.HOME:
                getSupportFragmentManager().beginTransaction().replace(R.id.frame, new HomeFragment()).commit();
                if (regStatusService != null && bound) {
                    regStatusService.setSendOnce(true);
                }
                break;
            case DrawerFragment.MY_ACCOUNT:
                getSupportFragmentManager().beginTransaction().replace(R.id.frame, new MyAccountFragment()).commit();
                break;
            case DrawerFragment.ORDER_HISTORY:
                getSupportFragmentManager().beginTransaction().replace(R.id.frame, new OrderHistoryFragment()).commit();
                break;
            case DrawerFragment.PROMOTION:
                getSupportFragmentManager().beginTransaction().replace(R.id.frame, new PromotionFragment()).commit();
                break;
            case DrawerFragment.ABOUT:
                getSupportFragmentManager().beginTransaction().replace(R.id.frame, new AboutUsFragment()).commit();
                break;
            case DrawerFragment.HELP:
                getSupportFragmentManager().beginTransaction().replace(R.id.frame, new HomeFragment()).commit();
                break;
            case DrawerFragment.FAQ:
                getSupportFragmentManager().beginTransaction().replace(R.id.frame, new FaqFragment()).commit();
                break;
            case DrawerFragment.CONTACT:
                getSupportFragmentManager().beginTransaction().replace(R.id.frame, new ContactUsFragment()).commit();
                break;
        }
    }

    public void onAttached(int position) {
        switch (position) {
            case DrawerFragment.HOME:
                mTitle = getString(R.string.app_name);
                break;
            case DrawerFragment.MY_ACCOUNT:
                mTitle = "My Account";
                break;
            case DrawerFragment.ORDER_HISTORY:
                mTitle = "Order History";
                break;
            case DrawerFragment.PROMOTION:
                mTitle = "Promotion";
                break;
            case DrawerFragment.ABOUT:
                mTitle = "About Us";
                break;
            case DrawerFragment.FAQ:
                mTitle = "FAQ";
                break;
            case DrawerFragment.CONTACT:
                mTitle = "Contact Us";
                break;
        }
    }

    @Override
    public void onAccept() {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        doBindService();

        recieverBanned = new BandXmppReciever();
        IntentFilter xmppFilter = new IntentFilter(XmppService.BANNED_MESSAGE);
        registerReceiver(recieverBanned, xmppFilter);
        receiver = new StatusReceiver();
        IntentFilter filter = new IntentFilter(RegStatusService.REFRESH);
        registerReceiver(receiver, filter);

        if (registerDialog == null || !registerDialog.isVisible()) {
            Intent intent = new Intent(this, RegStatusService.class);
            bindService(intent, regConnect, Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (recieverBanned != null) {
            unregisterReceiver(recieverBanned);
        }
        if (receiver != null) {
            unregisterReceiver(receiver);
            if (bound) {
                unbindService(regConnect);
                bound = false;
            }
        }
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
            //  Global.CustomToast(MainActivity.this, "Internet Avaliable");
        } else {
            Global.CustomToast(MainActivity.this, "Check your internet connection");
            //Global.Toast(MainActivity.this, "Check your internet connection");
        }
    }
/*
    private void startRegStatusService() {
        Intent intent = new Intent(this, RegStatusService.class);
        ComponentName component = startService(intent);
        if (component != null) {
            Log.e(TAG, component.getClassName() + " Started");
        }
    }

    private void stopRegStatusService() {
        Intent intent = new Intent(this, RegStatusService.class);
        if (stopService(intent)) {
            Log.e(TAG, "RegStatus Stopped");
        }
    }*/

    @Override
    public void onAcceptBanned() {
        finish();
    }

    void doBindService() {
        bindService(new Intent(this, XmppService.class), connection, Context.BIND_AUTO_CREATE);
    }

    void doUnbindService() {
        if (connection != null) {
            unbindService(connection);
        }
    }

    @Override
    protected void onDestroy() {
        Global.CustomToast(MainActivity.this, "CLOSED");
//        String orderId = PrefConnect.readString(MainActivity.this, PrefConnect.ORDER_ID, "");
//        dbHelper.saveToPendingOrder(orderId);
        super.onDestroy();
        Global.CustomToast(MainActivity.this, "CLOSED");
        doUnbindService();
    }

    public XmppService getmService() {
        return xmppService;
    }

    @Override
    public void onBackPressed() {
        //Checking for fragment count on backstack
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else if (!doubleBackToExitPressedOnce) {
            this.doubleBackToExitPressedOnce = true;
            // Toast.makeText(this, "Please click BACK again to exit.", Toast.LENGTH_SHORT).show();
            Global.CustomToast(MainActivity.this, "Please click BACK again to exit.");

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        } else {
            super.onBackPressed();
        }
    }

    public void BanConfirmation() {
        final BanConfirmDialog bandialog = new BanConfirmDialog();
        bandialog.setCallback(this);
        bandialog.show(getFragmentManager(), "");

    }

    private class StatusReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getBooleanExtra("showDialog", false)) {
                if (bound) {
                    unbindService(regConnect);
                    bound = false;
                }
                registerDialog = new RegisterDialog();
                registerDialog.setCallback(MainActivity.this);
                registerDialog.setCancelable(false);
                registerDialog.show(getSupportFragmentManager(), "Confirm");
            } else if (intent.getBooleanExtra("setListener", false)) {
                HomeFragment fragment = (HomeFragment) getSupportFragmentManager().findFragmentByTag("HomeFragment");
                if (fragment != null) {
                    fragment.setListener();
                }
            } else if (intent.getBooleanExtra("banned", false)) {
                BanConfirmation();
            }
        }
    }

    public class BandXmppReciever extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equalsIgnoreCase(XmppService.BANNED_MESSAGE)) {
                if (Global.BROADCAST) {
                    BanConfirmation();
                    Global.BROADCAST = false;
                }
            }
        }
    }

   /* private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }*/
}
