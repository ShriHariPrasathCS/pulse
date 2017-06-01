package com.pulseplus.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.pulseplus.R;

/**
 * Bright Bridge on 23-Mar-17.
 */

public class CartDialog extends DialogFragment {

    CartCallback callback;
    Button button_confirm;
    TextView tv_order_total;
    ListView popup_listview;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_cart, null, false);
        init(view);
        setListener();
        dialog.setView(view);
        return dialog.create();
    }

    private void setListener() {
        button_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();

            }
        });

        tv_order_total.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    private void init(View view) {
        button_confirm = (Button) view.findViewById(R.id.button_confirm);
        tv_order_total = (TextView) view.findViewById(R.id.tv_order_total);
        popup_listview = (ListView) view.findViewById(R.id.cart_listview);


    }

    public void setCallback(CartCallback callback) {
        this.callback = callback;
    }

    public interface CartCallback {
        public void onAccept();
    }
}
