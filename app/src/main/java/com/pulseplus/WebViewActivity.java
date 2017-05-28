package com.pulseplus;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarActivity;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;

import com.pulseplus.global.Global;

/**
 * Bright Bridge on 03-Jan-17.
 */

public class WebViewActivity extends ActionBarActivity{
    android.webkit.WebView myWebView;
    ProgressDialog p;
  //  private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_and_condition);

        init();
        Setwebview();
      //  setToolBar();
    }

    private void init() {
       // toolbar = (Toolbar) findViewById(R.id.toolbar);
        p = Global.initProgress(WebViewActivity.this);
    }

    private void setToolBar() {
      /*  toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Terms and Condition");
        toolbar.setNavigationIcon(R.drawable.ic_back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });*/

    }

    private void Setwebview() {

        String url = getIntent().getStringExtra("url");
        myWebView = (android.webkit.WebView) findViewById(R.id.terms_condition_webview);
        myWebView.setWebViewClient(new WebViewClient());
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        myWebView.loadUrl(url);


    }
}
