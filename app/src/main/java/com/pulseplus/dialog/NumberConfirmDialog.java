package com.pulseplus.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;

import com.armor.fontlib.CTextView;
import com.pulseplus.R;


public class NumberConfirmDialog extends AppCompatDialogFragment {

    CTextView textView1, txtNumber, textView2, txtEdit, txtConfirm;
    NumberCallback callback;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_number_confirm, null, false);
        init(view);
        setListener();
        dialog.setView(view);
        return dialog.create();
    }

    private void setListener() {
        txtEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
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
        txtNumber = (CTextView) view.findViewById(R.id.txtNumber);
        textView2 = (CTextView) view.findViewById(R.id.txtMsg1);
        txtEdit = (CTextView) view.findViewById(R.id.txtEdit);
        txtConfirm = (CTextView) view.findViewById(R.id.txtConfirm);

        Bundle bundle = getArguments();
        String number = bundle.getString("number");
        txtNumber.setText(number);

    }

    public void setCallback(NumberCallback callback) {
        this.callback = callback;
    }

    public interface NumberCallback {
        public void onAccept();
    }
}
