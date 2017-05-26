package com.pulseplus.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.pulseplus.MainActivity;
import com.pulseplus.R;
import com.pulseplus.navigation.DrawerFragment;

/**
 * Created by Bright Bridge on 03-Jan-17.
 */

public class FaqFragment extends Fragment {
    WebView myWebView;
   //  private Toolbar toolbar;
   // ProgressDialog p ;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_terms_and_condition, container, false);
       // p.show();
        init(view);
        Setwebview();
       // setToolBar();
        return view;
    }

    private void init(View view) {
        myWebView = (WebView)view.findViewById(R.id.terms_condition_webview);
       // p = Global.initProgress(getActivity());
       //  toolbar = (Toolbar)view.findViewById(R.id.toolbar);
    }

    private void setToolBar() {

      /*  toolbar.setTitle("Terms and Condition");
        toolbar.setNavigationIcon(R.drawable.ic_back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onAttached(DrawerFragment.FAQ);
    }

    private void Setwebview() {
        String url = "http://pulseplus.in/faq/";
        myWebView.setWebViewClient(new WebViewClient());
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        myWebView.loadUrl(url);
       // p.show();
    }



}
