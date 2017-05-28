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

public class BlacklistConfirmDialog extends DialogFragment{
    CTextView txtOk;
    BlacklistConfirmDialog.Blacklist callback;




    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder BlacklistDialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_blacklist_confirm, null, false);
        init(view);
        setListener();
        BlacklistDialog.setView(view);
        return BlacklistDialog.create();


    }

    private void setListener() {
        txtOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback != null) {
                    callback.onAcceptBlacklist();
                    dismiss();
                }
            }
        });
    }

    private void init(View view) {
        txtOk = (CTextView) view.findViewById(R.id.txtOk);
    }
    public void setCallback(BlacklistConfirmDialog.Blacklist callback) {
        this.callback = callback;
    }

    public interface Blacklist {
        public void onAcceptBlacklist();
    }
}
