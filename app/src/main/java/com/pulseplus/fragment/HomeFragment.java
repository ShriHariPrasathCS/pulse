package com.pulseplus.fragment;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatImageView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.armor.fileupload.FilePath;
import com.armor.fileupload.PConstant;
import com.armor.fileupload.upload.ImageUpload;
import com.google.gson.Gson;
import com.iceteck.silicompressorr.SiliCompressor;
import com.pulseplus.AudioRecordActivity;
import com.pulseplus.MainActivity;
import com.pulseplus.OrderChatActivity;
import com.pulseplus.PendingOrdersActivity;
import com.pulseplus.R;
import com.pulseplus.database.DBHelper;
import com.pulseplus.dialog.PhotoChooserDialog;
import com.pulseplus.dialog.PhotoDialog;
import com.pulseplus.global.Global;
import com.pulseplus.model.ImageModel;
import com.pulseplus.model.Internet;
import com.pulseplus.model.OrderIdGen;
import com.pulseplus.model.ProfileModule;
import com.pulseplus.navigation.DrawerFragment;
import com.pulseplus.util.PrefConnect;
import com.pulseplus.web.APIService;
import com.pulseplus.web.RetrofitSingleton;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeFragment extends Fragment implements PhotoDialog.ImageListener {

    private static final String IMAGE = "1";
    private static final String AUDIO = "2";
    String MIME_TYPE;
    ArrayList<ProfileModule.Details> details;
    DBHelper dbHelper;
    private RelativeLayout prescripLayout, audioLayout;
    private TextView txtDate;
    private Uri fileUri;
    private ProgressDialog p;
    private int TYPE;
    private ImageView cart_imageView;
    private String userid;
    private APIService apiService;
    private AppCompatImageView imgCamera, imgAudio;
    private Date date;
    private MainActivity activity;
    private boolean isListenerSet = false;
    private int count = 0;
    private MenuItem notificationItem;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = DBHelper.getInstance(getActivity());
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        init(view);
        //    onPrepareOptionsMenu((Menu)toolbar);
        // list_count = Integer.parseInt(getArguments().getString("list_count"));

        //animation
        ObjectAnimator scaleDown = ObjectAnimator.ofPropertyValuesHolder(imgCamera,
                PropertyValuesHolder.ofFloat("scaleX", 1.2f),
                PropertyValuesHolder.ofFloat("scaleY", 1.2f));
        scaleDown.setDuration(310);
        scaleDown.setRepeatCount(ObjectAnimator.INFINITE);
        scaleDown.setRepeatMode(ObjectAnimator.REVERSE);
        scaleDown.start();

        ObjectAnimator scaleUP = ObjectAnimator.ofPropertyValuesHolder(imgAudio,
                PropertyValuesHolder.ofFloat("scaleX", 1.2f),
                PropertyValuesHolder.ofFloat("scaleY", 1.2f));

        scaleUP.setRepeatCount(ObjectAnimator.INFINITE);
        scaleUP.setRepeatMode(ObjectAnimator.REVERSE);
        scaleUP.start();


        //    gifImageView.setGifImageResource(R.drawable.bg_gif_800x1280);


        setListener();
        return view;


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_fragment, menu);
        notificationItem = menu.findItem(R.id.notification_bell);
        notificationItem.setIcon(buildCounterDrawable(dbHelper.getPendingOrder().size(), R.drawable.pending_notification_bell_white));
    }

    private Drawable buildCounterDrawable(int count, int backgroundImageId) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.counter_menuitem_layout, null);
        view.setBackgroundResource(backgroundImageId);
        if (count == 0) {
            View counterTextPanel = view.findViewById(R.id.counterValuePanel);
            counterTextPanel.setVisibility(View.VISIBLE);
        } else {
            TextView textView = (TextView) view.findViewById(R.id.count);
            textView.setText("" + count);
        }

        view.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        view.setDrawingCacheEnabled(true);
        view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);

        return new BitmapDrawable(getResources(), bitmap);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(getActivity(), PendingOrdersActivity.class);
        startActivity(intent);
        return super.onOptionsItemSelected(item);
    }


    private void init(View view) {
        prescripLayout = (RelativeLayout) view.findViewById(R.id.camera_layout);
        audioLayout = (RelativeLayout) view.findViewById(R.id.audio_layout);

        p = Global.initProgress(getActivity());
        userid = PrefConnect.readString(getActivity(), PrefConnect.USER_ID, "");
        apiService = RetrofitSingleton.createService(APIService.class);


        details = new ArrayList<>();

    }


    public void setListener() {
        prescripLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                PhotoDialog dialog = new PhotoDialog();
//                dialog.setImageListener(HomeFragment.this);
//                dialog.show(getFragmentManager(), "Image");
                PhotoChooserDialog dialog = new PhotoChooserDialog();
                dialog.setImageCallback(new PhotoChooserDialog.ImageCallback() {
                    @Override
                    public void onImageReceived(Uri imageUri) {
                        TYPE = 1;
                        MIME_TYPE = "image/jpeg";
                        Log.e("Image Uri", imageUri.toString());
                        String filePath = SiliCompressor.with(getActivity())
                                .compress(FilePath.getPath(getActivity(), imageUri),
                                        new File(Environment.getExternalStorageDirectory().getAbsolutePath() + Global.IMAGE_SEND), true);
                        uploadFile(new String[]{"file"}, filePath, null);
                    }
                });
                dialog.show(getChildFragmentManager(), "PhotoChooserDialog");
            }
        });

        audioLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AudioRecordActivity.class);
                startActivityForResult(intent, Global.AUDIO_REC);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(boolean b) {
        if (b) {
            Global.CustomToast(getActivity(), "Please check ");
        } else {
            Global.CustomToast(getActivity(), "Please check your internet connection");
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (MainActivity) activity;
        ((MainActivity) activity).onAttached(DrawerFragment.HOME);
    }

    @Override
    public void onImageSelected(Uri uri, Intent intent, int requestCode) {
        fileUri = uri;
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            String filePath;
            switch (requestCode) {
                case PConstant.CAMERA_CAPTURE:
                    TYPE = 1;
                    MIME_TYPE = "image/jpeg";
//                    Global.saveImage(getActivity(), fileUri, fileUri);
                    Log.e("Image Uri", fileUri.toString());
                    filePath = SiliCompressor.with(getActivity())
                            .compress(FilePath.getPath(getActivity(), fileUri),
                                    new File(Environment.getExternalStorageDirectory().getAbsolutePath() + Global.IMAGE_SEND), true);
                    uploadFile(new String[]{"file"}, filePath, null);
                    break;
                case PConstant.GALLERY_PICK:
                    TYPE = 1;
                    Uri selectedImageUri = data.getData();
                    MIME_TYPE = getActivity().getContentResolver().getType(selectedImageUri);
                    if (MIME_TYPE != null && MIME_TYPE.contains("image")) {

                        filePath = SiliCompressor.with(getActivity())
                                .compress(FilePath.getPath(getActivity(), selectedImageUri),
                                        new File(Environment.getExternalStorageDirectory().getAbsolutePath() + Global.IMAGE_SEND));
                        uploadFile(new String[]{"file"}, filePath, null);
                    }
                    break;
                case Global.AUDIO_REC:
                    TYPE = 2;
//                    if (data != null) {
//                        Bundle uri = data.getBundleExtra("uriBundle");
//                        String audioUri = uri.getString("uri");
//                        fileUri = Uri.parse(audioUri);
                    uploadFile(new String[]{"file"}, data.getStringExtra("audio_path"), null);
//                    }
                    break;
            }
        }
    }

    private void uploadFile(final String[] key, String filePath, final File thumb) {
        if (!TextUtils.isEmpty(filePath)) {
            final File file = new File(filePath);
            if (file.exists()) {
                p.setMessage("Generating Order");
                p.show();
                String url = RetrofitSingleton.BASE_URL + "/pulseplus/api/index.php/prescription_upload";
                ImageUpload imageUpload = new ImageUpload(getActivity(), url, new ImageUpload.UploadListener() {
                    @Override
                    public void onComplete(String s) {
                        Global.dismissProgress(p);
                        Gson gson = new Gson();
                        ImageModel image = gson.fromJson(s, ImageModel.class);
                        if (image.getResult().equalsIgnoreCase("Success")) {
//                            Global.Toast(ChatActivity.this, "Uploaded Successfully");
                            switch (key[0]) {
                                case "file":
                                    if (TYPE == 1)
                                        orderInit(userid, IMAGE, file.getName());
                                    else
                                        orderInit(userid, AUDIO, file.getName());
                                    break;
                            }
                        } else {
                            Global.CustomToast(getActivity(), "Upload failed");
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        Global.dismissProgress(p);
                    }
                });
                HashMap<String, File> map = new HashMap<>();
                map.put(key[0], file);
                if (thumb != null) {
                    map.put(key[1], thumb);
                }
                imageUpload.upload(map);
            }
        }
    }


    private void orderInit(String userId, String msgType, String fileName) {
        p.show();
        String myJid = PrefConnect.readString(getActivity(), PrefConnect.JID, "");
        OrderIdGen orderIdGen = new OrderIdGen(userId, msgType, fileName, myJid);
        Call<OrderIdGen.Result> call = apiService.orderIdGen(orderIdGen);
        call.enqueue(new Callback<OrderIdGen.Result>() {
            @Override
            public void onResponse(Call<OrderIdGen.Result> call, Response<OrderIdGen.Result> response) {
                Global.dismissProgress(p);
                if (response.isSuccessful()) {
                    OrderIdGen.Result result = response.body();
                    if (result.getResult().equalsIgnoreCase("Success")) {
                        PrefConnect.writeString(getActivity(), PrefConnect.ORDER_ID, result.getOrderid());
                        Intent intent = new Intent(getActivity(), OrderChatActivity.class);
                        startActivity(intent);
                    }
                }
            }

            @Override
            public void onFailure(Call<OrderIdGen.Result> call, Throwable t) {
                Log.e("TAG", t.getMessage());
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().invalidateOptionsMenu();
        super.onStop();
        EventBus.getDefault().unregister(this);
        if (notificationItem != null)
            notificationItem.setIcon(
                    buildCounterDrawable(dbHelper.getPendingOrder().size(),
                            R.drawable.pending_notification_bell_white));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onInternetConnectionCheck(Internet internet) {
        if (internet.isConnected) {
            Global.CustomToast(getActivity(), "Internet Avaliable");
        } else {
            Global.CustomToast(getActivity(), "Please check your internet connection");
            Global.Toast(getActivity(), "Check your internet connection");
        }
    }
}
