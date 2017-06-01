package com.pulseplus.dialog;


import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.armor.fileupload.PConstant;
import com.pulseplus.R;
import com.pulseplus.global.Global;

import java.io.File;

public class PhotoDialog extends DialogFragment implements View.OnClickListener {

    public ImageListener listener;
    private Uri imageUri = null;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_camera, null);

        TextView txtCancel = (TextView) view.findViewById(R.id.txtCancel);
        TextView txtCamera = (TextView) view.findViewById(R.id.txtCamera);
        TextView txtGallery = (TextView) view.findViewById(R.id.txtGallery);

        txtCancel.setOnClickListener(this);
        txtCamera.setOnClickListener(this);
        txtGallery.setOnClickListener(this);

        builder.setView(view);
        return builder.create();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.txtCamera:
                try {
                    imageUri = getImageUri();
                    Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    captureImage.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

                    if (listener != null) {
                        listener.onImageSelected(imageUri, captureImage, PConstant.CAMERA_CAPTURE);

                    }
                    dismiss();
                } catch (ActivityNotFoundException e) {
                    String errorMessage = "Your device doesn't support capturing images!";
                    Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.txtGallery:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                if (listener != null) {
                    listener.onImageSelected(null, intent, PConstant.GALLERY_PICK);
                }
                dismiss();
                break;
            case R.id.txtCancel:
                dismiss();
                break;
        }
    }

    private Uri getImageUri() {
        Uri photoUri = null;
        File outputDir = Global.getPhotoDirectory(Global.IMAGE_SEND);
        if (outputDir != null) {
            photoUri = Uri.fromFile(outputDir);
        }
        return photoUri;
    }

    public void setImageListener(ImageListener listener) {
        this.listener = listener;
    }

    public interface ImageListener {
        public void onImageSelected(Uri uri, Intent intent, int requestCode);
    }

  /*  @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        listener = (ImageListener) activity;
    }*/
}
