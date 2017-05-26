package com.pulseplus.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;

import com.armor.fontlib.CTextView;
import com.pulseplus.R;

public class RegisterDialog extends DialogFragment {

    CTextView textView1, txtConfirm;
    RegisterDialog.RegisterCallback callback;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_register, null, false);
        init(view);
        setListener();
        dialog.setView(view);
        return dialog.create();
    }

    private void setListener() {
        txtConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (callback != null) {
                    callback.onAccept();
                    dismiss();
                }
            }
        });
    }

    private void init(View view) {
        textView1 = (CTextView) view.findViewById(R.id.txtMsg);
        txtConfirm = (CTextView) view.findViewById(R.id.txtConfirm);
    }

    public void setCallback(RegisterDialog.RegisterCallback callback) {
        this.callback = callback;
    }

    public interface RegisterCallback {
        public void onAccept();
    }
}
