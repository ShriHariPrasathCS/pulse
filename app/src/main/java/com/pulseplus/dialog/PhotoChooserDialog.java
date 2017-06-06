package com.pulseplus.dialog;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.LinearLayout;

import com.pulseplus.R;
import com.pulseplus.util.camera.PickerBuilder;

/**
 * Administrator on 04-May-17.
 */
public class PhotoChooserDialog extends BottomSheetDialogFragment
        implements PickerBuilder.onImageReceivedListener {

    LinearLayout btnCamera, btnGallery;

    private PickerBuilder pickerCamera = null, pickerGallery = null;

    private ImageCallback imageCallback;
    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback =
            new BottomSheetBehavior.BottomSheetCallback() {

                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {
                    if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                        dismiss();
                    }
                }

                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                }
            };

    public void setImageCallback(ImageCallback imageCallback) {
        this.imageCallback = imageCallback;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pickerCamera =
                new PickerBuilder(getActivity(), PickerBuilder.SELECT_FROM_CAMERA)
                        .setOnImageReceivedListener(this)
                        .setImageFolderName(getString(R.string.app_name))
                        .setCropScreenColor(
                                ContextCompat.getColor(getActivity(), R.color.colorPrimary))
                        .withTimeStamp(true)
                        .setCropScreenColor(
                                ContextCompat.getColor(getActivity(), R.color.colorPrimary));
        pickerGallery =
                new PickerBuilder(getActivity(), PickerBuilder.SELECT_FROM_GALLERY)
                        .setOnImageReceivedListener(this)
                        .setImageFolderName(getString(R.string.app_name))
                        .setCropScreenColor(
                                ContextCompat.getColor(getActivity(), R.color.colorPrimary))
                        .withTimeStamp(true)
                        .setCropScreenColor(
                                ContextCompat.getColor(getActivity(), R.color.colorPrimary));
    }


    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.dialog_photo_chooser, null);
        init(contentView);
        dialog.setContentView(contentView);
        attachListener();
        CoordinatorLayout.LayoutParams params =
                (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }
    }

    void init(View view) {
        btnCamera = (LinearLayout) view.findViewById(R.id.btnCamera);
        btnGallery = (LinearLayout) view.findViewById(R.id.btnGallery);
    }

    private void attachListener() {
        btnCamera.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pickerCamera.start();
                    }
                });
        btnGallery.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pickerGallery.start();
                    }
                });
    }

    @Override
    public void onImageReceived(Uri imageUri) {
        if (imageCallback != null) {
            imageCallback.onImageReceived(imageUri);
            dismiss();
        }
    }

    public interface ImageCallback {
        void onImageReceived(Uri imageUri);
    }
}
