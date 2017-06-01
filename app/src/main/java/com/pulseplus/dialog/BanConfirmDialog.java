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
 * Bright Bridge on 10-Nov-16.
 */

public class BanConfirmDialog extends DialogFragment {
    CTextView txtOk;
    BanCallback callback;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder bandialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_ban_confirmation, null, false);
        init(view);
        setListener();
        bandialog.setView(view);
        return bandialog.create();
    }

    private void setListener() {
        txtOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callback != null) {
                    callback.onAcceptBanned();
                    dismiss();
                }
            }
        });
    }

    private void init(View view) {
        txtOk = (CTextView) view.findViewById(R.id.txtOk);

    }

    public void setCallback(BanCallback callback) {
        this.callback = callback;
    }

    public interface BanCallback {
        public void onAcceptBanned();
    }
}
