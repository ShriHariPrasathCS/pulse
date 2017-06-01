package com.pulseplus.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

import com.armor.fontlib.CTextView;
import com.pulseplus.R;

public class ConfirmationDialog extends DialogFragment {

    CTextView txtOk, txtCancel;
    ConfirmationCallback callback;
    SaveCallback saveCallback;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_confirmation, null, false);
        init(view);
        setListener();
        dialog.setView(view);
        return dialog.create();
    }

    private void setListener() {
        txtOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (callback != null) {
                    callback.onAccept();
                    dismiss();

                }
            }
        });
        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (saveCallback != null) {
                    saveCallback.onSave();
                }

            }
        });
    }

    private void init(View view) {
        txtOk = (CTextView) view.findViewById(R.id.txtOk);
        txtCancel = (CTextView) view.findViewById(R.id.txtCancel);
    }

    public void setCallback(ConfirmationCallback callback) {
        this.callback = callback;
    }

    public void setSaveCallback(SaveCallback saveCallback) {
        this.saveCallback = saveCallback;
    }

    public interface ConfirmationCallback {
        public void onAccept();
    }

    public interface SaveCallback {
        public void onSave();
    }
}
