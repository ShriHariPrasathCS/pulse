package com.pulseplus.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import com.armor.fontlib.CTextView;
import com.pulseplus.R;

/**
 * Bright Bridge on 14-Nov-16.
 */

public class WhitelistConfirmDialog extends DialogFragment {

    CTextView txtOk;
    WhitelistConfirmDialog.Whitelist callback;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder WhitelistDialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_whitelist_confirm, null, false);
        init(view);
        setListener();
        WhitelistDialog.setView(view);
        return WhitelistDialog.create();


    }

    private void setListener() {
        txtOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback != null) {
                    callback.onAcceptWhite();
                    dismiss();
                }
            }
        });
    }

    private void init(View view) {
        txtOk = (CTextView) view.findViewById(R.id.txtOk);


    }

    public void setCallback(WhitelistConfirmDialog.Whitelist callback) {
        this.callback = callback;
    }

    public interface Whitelist {
        public void onAcceptWhite();
    }
}
