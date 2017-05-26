package com.pulseplus.test;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

import com.armor.fontlib.CEditText;
import com.pulseplus.R;

/**
 * Created by Mani on 10/1/2016.
 */

public class Main extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_test);

        CustomEditText editText = (CustomEditText) findViewById(R.id.edt);

        editText.setKeyImeChangeListener(new CustomEditText.KeyImeChange() {
            @Override
            public void onKeyIme(int keyCode, KeyEvent event) {
                invalidate();
            }
        });
    }

    private void invalidate() {

    }
}
