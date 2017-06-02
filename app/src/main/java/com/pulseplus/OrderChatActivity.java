package com.pulseplus;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Process;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.armor.fileupload.PConstant;
import com.armor.fileupload.upload.ImageUpload;
import com.armor.fontlib.CButton;
import com.armor.fontlib.CEditText;
import com.google.gson.Gson;
import com.iceteck.silicompressorr.SiliCompressor;
import com.pulseplus.adapter.CartListViewAdapter;
import com.pulseplus.adapter.MessageAdapter;
import com.pulseplus.database.DBHelper;
import com.pulseplus.dialog.BanConfirmDialog;
import com.pulseplus.dialog.BlacklistConfirmDialog;
import com.pulseplus.dialog.CartDialog;
import com.pulseplus.dialog.ConfirmationDialog;
import com.pulseplus.dialog.PhotoDialog;
import com.pulseplus.dialog.WhitelistConfirmDialog;
import com.pulseplus.global.Global;
import com.pulseplus.listener.MediaPlayerListener;
import com.pulseplus.model.CancelOrder;
import com.pulseplus.model.Cart;
import com.pulseplus.model.ChatBean;
import com.pulseplus.model.Child;
import com.pulseplus.model.Group;
import com.pulseplus.model.ImageModel;
import com.pulseplus.model.Internet;
import com.pulseplus.model.Reorder;
import com.pulseplus.model.SaveToCart;
import com.pulseplus.model.SendMessage;
import com.pulseplus.service.LocalBinder;
import com.pulseplus.service.XmppService;
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
import uk.co.senab.photoview.PhotoViewAttacher;

public class OrderChatActivity extends AppCompatActivity implements PhotoDialog.ImageListener, MediaPlayerListener, BanConfirmDialog.BanCallback, CartListViewAdapter.CartListener {

    private static final int ORDER_CONFIRM_REQUEST = 7;
    private static final String TEXT = "3";
    private static final String IMAGE = "1";
    private static final String AUDIO = "2";
    ProgressBar progressBar;
    String oederid;
    boolean banned = false;
    int length = 0;
    ChatBean chatBean;
    ImageView cart_imageView;
    private String fromId;
    private MessageAdapter adapter;
    private CartListViewAdapter cartListViewAdapter;
    private ExpandableListView listView;
    private Boolean actionSend = false;
    private String MIME_TYPE;
    private Thread thread;
    private Handler handler = new Handler();
    private ArrayList<Child> childList;
    private ArrayList<Cart.CartList> cartLists;
    private Boolean load = true;
    private MediaPlayer mediaPlayer;
    private PhotoViewAttacher attacher;
    private Toolbar toolbar;
    private CEditText edtMsg;
    private AppCompatImageView imgCam, imgMic, imgView;
    private ImageView imageViewMe;
    private CButton btnNew, btnReorder;
    private LinearLayout edtLayout, playLyaout, jcplayer_layout;
    private FrameLayout frameLayout;
    private ProgressDialog p;
    private Activity context;
    private APIService apiService;
    private int TYPE;
    private ArrayList<ChatBean.Details> msgList;
    private ArrayList<Group> groupList;
    private Uri fileUri;
    private boolean isInFront = false;
    private Group group;
    private LinearLayout btnLayout;
    private XmppService xmppService;
    private boolean mBounded;
    private XmppReciever reciever;
    private String toJid;
    private String orderId;
    private String userid;
    private boolean chatEnd = false;
    private BroadcastReceiver sendBroadcastReceiver;
    private BroadcastReceiver deliveryBroadcastReceiver;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            xmppService = ((LocalBinder<XmppService>) service).getService();
            mBounded = true;
            //  PrefConnect.writeString(OrderChatActivity.this, PrefConnect.TO_JID, "");

            Log.d("TAG", "onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            xmppService = null;
            mBounded = false;
            Log.d("TAG", "onServiceDisconnected");
        }
    };
    private DBHelper dbHelper;
    private ListView cart_listView;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_chat);

        init();
        loadData();
        setListener();
        setToolbar();

    }

    private void setListener() {
        listView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if (groupPosition == 0) {
                    listView.expandGroup(groupPosition);
                    return true;
                }
                return false;
            }
        });
        imgCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closePlayer();
                PhotoDialog dialog = new PhotoDialog();
                dialog.setImageListener(OrderChatActivity.this);
                dialog.show(getSupportFragmentManager(), "Image");
            }
        });

        edtMsg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String input = edtMsg.getText().toString();
                input = input.trim();

                if (input.length() > 0) {
                    imgMic.setImageResource(R.drawable.dark_send);
                    actionSend = true;
                } else {
                    imgMic.setImageResource(R.drawable.chat_audio_green);
                    actionSend = false;
                }
            }
        });

        btnNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closePlayer();
                finish();
            }
        });

        btnReorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Reorder.Request request = new Reorder.Request(orderId);
                Call<Reorder.Response> call = apiService.reorder(request);
                call.enqueue(new Callback<Reorder.Response>() {
                    @Override
                    public void onResponse(Call<Reorder.Response> call, Response<Reorder.Response> response) {
                        if (response.isSuccessful()) {
                            Reorder.Response resend = response.body();
                            if (resend.getResult().equalsIgnoreCase("Success")) {
                                Global.dismissProgress(p);
                                PrefConnect.writeString(OrderChatActivity.this, PrefConnect.ORDER_ID, resend.getOrderid());
                                Intent i = new Intent(OrderChatActivity.this, OrderChatActivity.class);
                                startActivity(i);
                                finish();
                            } else {
                                Global.dismissProgress(p);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Reorder.Response> call, Throwable t) {

                    }
                });
            }
        });

        imgMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (actionSend) {
                    toJid = PrefConnect.readString(OrderChatActivity.this, PrefConnect.TO_JID, "");
                    closePlayer();
                    if (!toJid.equals("")) {
                        sendMessage(TEXT, edtMsg.getText().toString().trim(), "");
                        TYPE = 3;
                        //toJid = PrefConnect.readString(OrderChatActivity.this, PrefConnect.TO_JID, "");
                        getmService().xmpp.sendMessage(edtMsg.getText().toString().trim(), toJid, String.valueOf(TYPE), getDate());
                    } else {
                        Global.CustomToast(OrderChatActivity.this, "Wait until admin connects with you");
                    }

                    edtMsg.setText("");

                } else {
                    closePlayer();
                    Intent intent = new Intent(OrderChatActivity.this, AudioRecordActivity.class);
                    startActivityForResult(intent, Global.AUDIO_REC);
                }
            }
        });
    }

    private void closePlayer() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                mediaPlayer.stop();
                mediaPlayer.reset();
            }
        }
    }

    private void sendMessage(String messageType, final String content, String thumbnail) {
//        p.show();
        String userId = PrefConnect.readString(OrderChatActivity.this, PrefConnect.USER_ID, "");
        orderId = PrefConnect.readString(OrderChatActivity.this, PrefConnect.ORDER_ID, "");
        final SendMessage sendMessage = new SendMessage(userId, orderId, fromId, content, messageType);
        Call<SendMessage.Result> call = apiService.sendMessage(sendMessage);
        call.enqueue(new Callback<SendMessage.Result>() {
            @Override
            public void onResponse(Call<SendMessage.Result> call, Response<SendMessage.Result> response) {
//                Global.dismissProgress(p);
                if (response.isSuccessful()) {
                    SendMessage.Result result = response.body();
                    if (result.getResult().equalsIgnoreCase("Success")) {
                        onResume();
//                        if (adapter != null) {
//                            adapter.notifyDataSetChanged();
//                        }
                    } else {
                        adapter.notifyDataSetChanged();
                        Toast.makeText(OrderChatActivity.this, result.getStatus(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<SendMessage.Result> call, Throwable t) {

            }
        });


    }

    private void loadData() {
        if (load) {
            p.show();
            load = false;
        }
        Call<ChatBean> call = apiService.chatBean(orderId);
        call.enqueue(new Callback<ChatBean>() {
            @Override
            public void onResponse(Call<ChatBean> call, Response<ChatBean> response) {
                Global.dismissProgress(p);
                if (response.isSuccessful()) {
                    ChatBean chatBean = response.body();
                    if (chatBean.getResult().equalsIgnoreCase("Success")) {
                        if (chatBean.getChat_status().equalsIgnoreCase("2")) {
                            // chatEnd = true;
                            imgView.setVisibility(View.VISIBLE);
                            edtLayout.setVisibility(View.GONE);
                            btnLayout.setVisibility(View.VISIBLE);
                            chatEnd = true;
                        } else {
                            setAdapter(chatBean);
//                            setPendingAdapter(chatBean);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ChatBean> call, Throwable t) {

            }
        });
    }

    private void setAdapter(ChatBean chatBean) {
        groupList = new ArrayList<>();
        group = new Group();
        childList = new ArrayList<>();
        for (ChatBean.Details details : chatBean.getDetails()) {
            Child child = new Child(details.getChat_usertype(), details.getMessage_type(), details.getMessage(), details.getSent_date());
            childList.add(child);
            dbHelper.insertOrderHistory(orderId, details.getChat_usertype(), details.getMessage_type(), details.getMessage(), details.getSent_date());
        }
        fromId = chatBean.getDetails().get(0).getFrom_id();
        PrefConnect.writeInteger(OrderChatActivity.this, PrefConnect.MSGCOUNT, childList.size());
        group.setChildren(childList);
        groupList.add(group);
        adapter = new MessageAdapter(OrderChatActivity.this, groupList, this);
        listView.setAdapter(adapter);
        expandAll();
        listView.setSelection(childList.size());
        for (Group group : groupList) {
            for (Child child : group.getChildren()) {
                if (child.getMessage().equals("Your Order has been generated. Please view the cart")) {
                    cart_imageView.setVisibility(View.VISIBLE);
                }
            }
        }
    }

//    private void setPendingAdapter(ChatBean chatBean) {
//        groupList = new ArrayList<>();
//        group = new Group();
//        childList = new ArrayList<>();
//        for (ChatBean.Details details : chatBean.getDetails()) {
//            Child child = new Child(details.getChat_usertype(), details.getMessage_type(), details.getMessage(), details.getSent_date());
//            childList.add(child);
//            dbHelper.insertPendingOrder(orderId, details.getChat_usertype(), details.getMessage_type(), details.getMessage(), details.getSent_date());
//        }
//
//        fromId = chatBean.getDetails().get(0).getFrom_id();
//
//        PrefConnect.writeInteger(OrderChatActivity.this, PrefConnect.MSGCOUNT, childList.size());
//
//        group.setChildren(childList);
//        groupList.add(group);
//
//        adapter = new MessageAdapter(OrderChatActivity.this, groupList, this);
//        listView.setAdapter(adapter);
//        expandAll();
//        listView.setSelection(childList.size());
//
//    }

    private void expandAll() {
        int count = adapter.getGroupCount();
        for (int i = 0; i < count; i++) {
            if (!listView.isGroupExpanded(i)) {
                listView.expandGroup(i);
            }
        }
    }

    private void init() {
        dbHelper = DBHelper.getInstance(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        edtMsg = (CEditText) findViewById(R.id.edtMsg);
        imgCam = (AppCompatImageView) findViewById(R.id.imgCam);
        imgMic = (AppCompatImageView) findViewById(R.id.imgMic);
        frameLayout = (FrameLayout) findViewById(R.id.frameLayout);
        edtLayout = (LinearLayout) findViewById(R.id.edtLayout);
        imgView = (AppCompatImageView) findViewById(R.id.imgView);
        imageViewMe = (ImageView) findViewById(R.id.imageViewMe);
        btnNew = (CButton) findViewById(R.id.btnNew);
        btnReorder = (CButton) findViewById(R.id.btnReorder);
        btnLayout = (LinearLayout) findViewById(R.id.btnLayout);
        playLyaout = (LinearLayout) findViewById(R.id.playLayout);
        cart_imageView = (ImageView) findViewById(R.id.cart_imageView);
        // progressBar = (ProgressBar) findViewById(R.id.imageProgress);

        listView = (ExpandableListView) findViewById(R.id.list);
        cart_listView = (ListView) findViewById(R.id.cart_listview);
        p = Global.initProgress(OrderChatActivity.this);
        apiService = RetrofitSingleton.createService(APIService.class);
        orderId = PrefConnect.readString(OrderChatActivity.this, PrefConnect.ORDER_ID, "");

    }

    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(orderId);
        toolbar.setNavigationIcon(R.drawable.ic_back_arrow);
        // cart_imageView.setVisibility(View.VISIBLE);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chatEnd) {
                    finish();
                    PrefConnect.writeString(OrderChatActivity.this, PrefConnect.TO_JID, "");
                    PrefConnect.writeString(OrderChatActivity.this, PrefConnect.ORDER_ID, "");

                } else {
                    confirmation();
                }
            }
        });

        cart_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OrderChatActivity.this, OrderConfirmationActivity.class);
                startActivityForResult(intent, ORDER_CONFIRM_REQUEST);
                // cartconfirmation();
            }
        });

    }

    public void cartconfirmation() {
        final String userid = PrefConnect.readString(OrderChatActivity.this, PrefConnect.USER_ID, "");
        CartDialog cartDialog = new CartDialog();
        Call<Cart> call = apiService.cart(userid);
        call.enqueue(new Callback<Cart>() {
            @Override
            public void onResponse(Call<Cart> call, Response<Cart> response) {

                Global.dismissProgress(p);
                if (response.isSuccessful()) {
                    Cart order = response.body();
                    if (order.getResult().equalsIgnoreCase("Success")) {
                        //PrefConnect.writeString(getActivity(), PrefConnect.ORDER_ID, order.getOrderid());
                        Global.dismissProgress(p);
                        cartLists = order.getCart_list();

                        showList();
                        //  orderHistory = order.getOrder_history();
                        //  Log.e(TAG, "onResponse: " + order.getCart_list().get(0).getUserid());

                    } else {
                        Global.dismissProgress(p);

                    }
                }

            }

            @Override
            public void onFailure(Call<Cart> call, Throwable t) {

            }


        });

        cartDialog.show(getFragmentManager(), "");
    }

    public void showList() {
        // Collections.reverse(orderHistory);
        cartListViewAdapter = new CartListViewAdapter(OrderChatActivity.this, cartLists, this);
        cart_listView.setAdapter(cartListViewAdapter);
    }


    @Override
    public void onImageSelected(Uri uri, Intent intent, int requestCode) {
        fileUri = uri;
        // p.show();
        onResume();
        startActivityForResult(intent, requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            String filePath;
            File myFile;
            switch (requestCode) {
                case PConstant.CAMERA_CAPTURE:
                    // p.show();
                    p.dismiss();
                    TYPE = 1;
                    filePath = SiliCompressor.with(this)
                            .compress(fileUri.toString(),
                                    new File(Environment.getExternalStorageDirectory().getAbsolutePath() + Global.IMAGE_SEND), true);
                    uploadFile(new String[]{"file"}, filePath, null);
                    myFile = new File(fileUri.getPath());
                    if (myFile.exists()) {
                        toJid = PrefConnect.readString(OrderChatActivity.this, PrefConnect.TO_JID, "");
                        sendMessage(IMAGE, myFile.getName(), "");
                        getmService().xmpp.sendMessage(myFile.getName(), toJid, String.valueOf(TYPE), getDate());
                        //              dbHelper.insertOrderHistory(orderId, "1", "1", myFile.getAbsolutePath(), getDate());

                        adapter.notifyDataSetChanged();
                    }
                    PrefConnect.writeInteger(OrderChatActivity.this, PrefConnect.MSGCOUNT, childList.size());
                    break;
                case PConstant.GALLERY_PICK:
                    //  p.show()
                    p.dismiss();
                    TYPE = 1;
                    Uri selectedImageUri = data.getData();
                    MIME_TYPE = getContentResolver().getType(selectedImageUri);
                    if (MIME_TYPE != null && MIME_TYPE.contains("image")) {
                        filePath = SiliCompressor.with(this)
                                .compress(selectedImageUri.toString(),
                                        new File(Environment.getExternalStorageDirectory().getAbsolutePath() + Global.IMAGE_SEND), true);
                        uploadFile(new String[]{"file"}, filePath, null);
                        myFile = new File(fileUri.getPath());
                        if (myFile.exists()) {
                            toJid = PrefConnect.readString(OrderChatActivity.this, PrefConnect.TO_JID, "");
                            sendMessage(IMAGE, myFile.getName(), "");
                            getmService().xmpp.sendMessage(myFile.getName(), toJid, String.valueOf(TYPE), getDate());
                            //       dbHelper.insertOrderHistory(orderId, "1", "1", myFile.getAbsolutePath(), getDate());
                            // progressBar.setVisibility(View.VISIBLE);
                            adapter.notifyDataSetChanged();
                        }
                        PrefConnect.writeInteger(OrderChatActivity.this, PrefConnect.MSGCOUNT, childList.size());
                    }
                    break;
                case Global.AUDIO_REC:
                    p.show();
                    TYPE = 2;
                    if (data != null) {
                        uploadFile(new String[]{"file"}, data.getStringExtra("audio_path"), null);
                        PrefConnect.writeInteger(OrderChatActivity.this, PrefConnect.MSGCOUNT, childList.size());
                    }
                    break;

                case ORDER_CONFIRM_REQUEST:
                    sendMessage(TEXT, data.getStringExtra("message"), "");
                    TYPE = 3;
                    //toJid = PrefConnect.readString(OrderChatActivity.this, PrefConnect.TO_JID, "");
                    getmService().xmpp.sendMessage(data.getStringExtra("message"), toJid, String.valueOf(TYPE), getDate());
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (chatEnd) {
            PrefConnect.writeString(OrderChatActivity.this, PrefConnect.TO_JID, "");
            PrefConnect.writeString(OrderChatActivity.this, PrefConnect.ORDER_ID, "");
            finish();

        } else {

            confirmation();
        }
    }

    private void uploadFile(final String[] key, String filePath, final File thumb) {
        if (!TextUtils.isEmpty(filePath)) {
            final File file = new File(filePath);
            if (file.exists()) {
                //  p.show();
                String url = RetrofitSingleton.BASE_URL + "/pulseplus/api/index.php/prescription_upload";
                // p.show();
                final ImageUpload imageUpload = new ImageUpload(OrderChatActivity.this, url, new ImageUpload.UploadListener() {
                    @Override
                    public void onComplete(String s) {
                        Gson gson = new Gson();
                        ImageModel image = gson.fromJson(s, ImageModel.class);
                        if (image.getResult().equalsIgnoreCase("Success")) {
                            if (TYPE == 1) {
                                toJid = PrefConnect.readString(OrderChatActivity.this, PrefConnect.TO_JID, "");
                                if (!toJid.equals("")) {
                                    sendMessage(IMAGE, file.getName(), "");
                                    getmService().xmpp.sendMessage(file.getName(), toJid, String.valueOf(TYPE), getDate());
                                    Global.CustomToast(OrderChatActivity.this, "Uploaded Successfully");
                                    //           dbHelper.insertOrderHistory(orderId,"1","1", file.getAbsolutePath(), getDate());
                                    progressBar.setVisibility(View.GONE);
                                    adapter.notifyDataSetChanged();
                                    Global.dismissProgress(p);
                                } else {
                                    Global.CustomToast(OrderChatActivity.this, "wait until Admin connects with you");
                                    Global.dismissProgress(p);
                                }
                            } else {
                                toJid = PrefConnect.readString(OrderChatActivity.this, PrefConnect.TO_JID, "");
                                if (!toJid.equals("")) {
                                    sendMessage(AUDIO, file.getName(), "");
                                    //toJid = PrefConnect.readString(OrderChatActivity.this, PrefConnect.TO_JID, "");
                                    getmService().xmpp.sendMessage(file.getName(), toJid, String.valueOf(TYPE), getDate());
                                    Global.CustomToast(OrderChatActivity.this, "Uploaded Successfully");
                                    //            dbHelper.insertOrderHistory(orderId, "1", "2", file.getAbsolutePath(), getDate());
                                    //           adapter.notifyDataSetChanged();
                                    Global.dismissProgress(p);
                                } else {
                                    Global.CustomToast(OrderChatActivity.this, "wait until Admin connects with you ");
                                    Global.dismissProgress(p);
                                }
                            }
                        } else {
                            Global.dismissProgress(p);
                            Global.CustomToast(OrderChatActivity.this, "Upload failed");
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        Global.dismissProgress(p);
                    }
                }

                );
                HashMap<String, File> map = new HashMap<>();
                map.put(key[0], file);
                if (thumb != null)

                {
                    map.put(key[1], thumb);
                }

                imageUpload.upload(map);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        doBindService();

        reciever = new XmppReciever();
        IntentFilter filter = new IntentFilter(XmppService.NEW_MESSAGE);
        registerReceiver(reciever, filter);

        IntentFilter filter1 = new IntentFilter(XmppService.SEND_MESSAGE);
        registerReceiver(reciever, filter1);

        IntentFilter filter2 = new IntentFilter(XmppService.BANNED_MESSAGE);
        registerReceiver(reciever, filter2);

        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        isInFront = false;
        PrefConnect.writeInteger(OrderChatActivity.this, PrefConnect.MSGCOUNT, 0);
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                closePlayer();
            }
        }

        //thread.stop();

    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
        isInFront = false;
        PrefConnect.writeInteger(OrderChatActivity.this, PrefConnect.MSGCOUNT, 0);

    }

    @Override
    public void playing(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }

    @Override
    public void playerVisible() {
        imgView.setVisibility(View.VISIBLE);
    }


    public void confirmation() {
        ConfirmationDialog dialog = new ConfirmationDialog();
        dialog.setCallback(new ConfirmationDialog.ConfirmationCallback() {
            @Override
            public void onAccept() {
                p.show();
                Call<CancelOrder> call = apiService.cancelOrder(orderId);
                call.enqueue(new Callback<CancelOrder>() {
                    @Override
                    public void onResponse(Call<CancelOrder> call, Response<CancelOrder> response) {
                        if (response.isSuccessful()) {
                            Global.dismissProgress(p);
                            CancelOrder orderCancel = response.body();
                            if (orderCancel.getResult().equalsIgnoreCase("Success")) {
                                Global.CustomToast(OrderChatActivity.this, orderCancel.getStatus());
                                String jid = PrefConnect.readString(OrderChatActivity.this, PrefConnect.TO_JID, "");
                                getmService().xmpp.sendMessage("Customer has cancelled the order", jid, "3", getDate());
                                PrefConnect.writeString(OrderChatActivity.this, PrefConnect.TO_JID, "");
                                //  PrefConnect.writeString(OrderChatActivity.this, PrefConnect.ORDER_ID, "");

                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        dbHelper.deleteOrder(orderId);
//                                        dbHelper.deletePendingOrderHistory(orderId);
                                    }
                                }, 4000);

                                finish();

                            } else {
                                Global.CustomToast(OrderChatActivity.this, orderCancel.getStatus());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<CancelOrder> call, Throwable t) {
                        Global.dismissProgress(p);

                    }
                });
            }
        });

        dialog.setSaveCallback(new ConfirmationDialog.SaveCallback() {

            @Override
            public void onSave() {
                p.show();
                Call<SaveToCart> call = apiService.saveToCart(orderId);
                call.enqueue(new Callback<SaveToCart>() {
                    @Override
                    public void onResponse(Call<SaveToCart> call, Response<SaveToCart> response) {
                        if (response.isSuccessful()) {
                            Global.dismissProgress(p);
                            final SaveToCart saveToCart = response.body();
                            if (saveToCart.getResult().equalsIgnoreCase("Success")) {
                                Global.CustomToast(OrderChatActivity.this, saveToCart.getStatus());
                                String jid = PrefConnect.readString(OrderChatActivity.this, PrefConnect.TO_JID, "");

                                getmService().xmpp.sendMessage("Order has been saved to cart", jid, "3", getDate());
                                PrefConnect.writeString(OrderChatActivity.this, PrefConnect.TO_JID, "");
                                dbHelper.saveToPendingOrder(orderId);
                                //  PrefConnect.writeString(OrderChatActivity.this, PrefConnect.ORDER_ID, "");

//                                handler.postDelayed(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        dbHelper.deleteOrder(orderId);
//                                    }
//                                }, 5000);
                                finish();

                            } else {
                                Global.CustomToast(OrderChatActivity.this, saveToCart.getStatus());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<SaveToCart> call, Throwable t) {
                        Global.dismissProgress(p);

                    }
                });

            }
        });


        dialog.show(getSupportFragmentManager(), "confirmation");
    }

    public void BanConfirmation() {
        final BanConfirmDialog bandialog = new BanConfirmDialog();
        bandialog.setCallback(this);
        bandialog.show(getFragmentManager(), "");

    }

    public void BlacklistConfirmation() {
        final BlacklistConfirmDialog BlacklistDialog = new BlacklistConfirmDialog();
        BlacklistDialog.setCallback(new BlacklistConfirmDialog.Blacklist() {
            @Override
            public void onAcceptBlacklist() {
                BlacklistDialog.dismiss();

            }
        });
        BlacklistDialog.show(getFragmentManager(), "");

    }

    public void WhitelistConfirmation() {
        final WhitelistConfirmDialog WhitelistDialog = new WhitelistConfirmDialog();
        WhitelistDialog.setCallback(new WhitelistConfirmDialog.Whitelist() {
            @Override
            public void onAcceptWhite() {
                WhitelistDialog.dismiss();

            }
        });
        WhitelistDialog.show(getFragmentManager(), "");

    }


    protected void onDestroy() {
        if (banned) {
            Process.killProcess(Process.myPid());
        } else {
            doUnbindService();
            unregisterReceiver(reciever);
            PrefConnect.writeString(OrderChatActivity.this, PrefConnect.TO_JID, "");
            //PrefConnect.writeString(OrderChatActivity.this, PrefConnect.ORDER_ID, "");
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    closePlayer();
                }
            }
            super.onDestroy();
        }
    }

    void doBindService() {
        bindService(new Intent(this, XmppService.class), connection, Context.BIND_AUTO_CREATE);
    }

    void doUnbindService() {
        if (connection != null) {
            unbindService(connection);
        }
    }

    public String getDate() {
        /*String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        currentDateTimeString = currentDateTimeString.substring();*/
        Date date = new Date();
        String currentTime = Global.getDate(date, "hh:mm a");
        return currentTime;
    }

    public void endChat() {
        imgView.setVisibility(View.VISIBLE);
        edtLayout.setVisibility(View.GONE);
        btnLayout.setVisibility(View.VISIBLE);
        //TODO dbHelper.deletePendingOrderHistory(orderId);
        chatEnd = true;
        //dbHelper.deleteOrder(orderId);
    }

    public void adminEnd() {
        imgView.setVisibility(View.VISIBLE);
        edtLayout.setVisibility(View.GONE);
        btnLayout.setVisibility(View.VISIBLE);
        chatEnd = true;
        dbHelper.deleteOrder(orderId);
    }

    public void slotEnd() {
        imgView.setVisibility(View.VISIBLE);
        edtLayout.setVisibility(View.GONE);
        btnLayout.setVisibility(View.VISIBLE);
        chatEnd = true;
    }

    public XmppService getmService() {
        return xmppService;
    }

    @Override
    public void onAcceptBanned() {
        //System.exit(0);
        banned = true;
        finish();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onInternetConnectionCheck(Internet internet) {
        if (internet.isConnected) {
            Global.CustomToast(OrderChatActivity.this, "Internet Avaliable");
        } else {
            Global.CustomToast(OrderChatActivity.this, "Check your internet connection");
            //Global.Toast(MainActivity.this, "Check your internet connection");
        }
    }

    @Override
    public void onCardAddOrRemove(String total) {

    }

    private class XmppReciever extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case XmppService.NEW_MESSAGE:
                    String body = intent.getStringExtra("new message");
                    String type = intent.getStringExtra("type");

                    if (body.equalsIgnoreCase("Banned")) {
                        BanConfirmation();
                    } else if (body.equalsIgnoreCase("Blacklist")) {
                        BlacklistConfirmation();
                    } else if (body.equalsIgnoreCase("Whitelist")) {
                        WhitelistConfirmation();
                    } else if (body.equalsIgnoreCase("Unfortunately, We cancelled your order")) {
                        adminEnd();
                        //  Toast.makeText(OrderChatActivity.this, "Admin has cancelled the order", Toast.LENGTH_SHORT).show();
                        Global.CustomToast(OrderChatActivity.this, "Unfortunately, We cancelled your order");
                    } else if (body.equalsIgnoreCase("Hurray! Your order has been assigned")) {
                        endChat();
                        Global.CustomToast(OrderChatActivity.this, "Hurray! Your order has been assigned");
                    } else {
                        if (body.equalsIgnoreCase("Your Order has been generated. please view the cart"))
                            cart_imageView.setVisibility(View.VISIBLE);

                        if (Global.BROADCAST) {
                            Child childReceivemsg = new Child(Global.RECE, type, body, getDate());
                            childList.add(childReceivemsg);
                            group.setChildren(childList);
                            groupList.clear();
                            groupList.add(group);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (adapter == null) {
                                        adapter = new MessageAdapter(OrderChatActivity.this, groupList, OrderChatActivity.this);
                                        listView.setAdapter(adapter);
                                    } else {
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                            });

                            intent.removeExtra("new message");
                            intent.removeExtra("type");
                            Global.BROADCAST = false;

//                            if (body != null && !body.equalsIgnoreCase("Order has been saved to cart"))
//                                dbHelper.insertPendingOrder(orderId, Global.RECE, type, body, getDate());
                            dbHelper.insertOrderHistory(orderId, Global.RECE, type, body, getDate());


                        }
                    }

                    break;
                case XmppService.SEND_MESSAGE:
                    String bodySend = intent.getStringExtra("send message");
                    String typeSend = intent.getStringExtra("type");

                    if (bodySend != null && (bodySend.equalsIgnoreCase("Your order has been confirmed,it will be delivered by 6-10AM") || bodySend.equalsIgnoreCase("Your order has been confirmed,it will be delivered by 6-10PM"))) {
                        endChat();
                        cart_imageView.setVisibility(View.GONE);
                    }

                    if (Global.BROADCAST) {
                        Child childSendMsg = new Child(Global.SEND, typeSend, bodySend, getDate());
                        childList.add(childSendMsg);
                        group.setChildren(childList);
                        groupList.clear();
                        groupList.add(group);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (adapter == null) {
                                    adapter = new MessageAdapter(OrderChatActivity.this, groupList, OrderChatActivity.this);
                                    listView.setAdapter(adapter);
                                } else {
                                    //loadData();
                                    // adapter.notifyDataSetChanged();
                                }
                            }
                        });

                        intent.removeExtra("send message");
                        intent.removeExtra("type");
                        Global.BROADCAST = false;
                        String jid = PrefConnect.readString(OrderChatActivity.this, PrefConnect.TO_JID, "");
//                        if (bodySend != null && !bodySend.equalsIgnoreCase("Order has been saved to cart") && !TextUtils.isEmpty(jid))
                        dbHelper.insertOrderHistory(orderId, Global.SEND, typeSend, bodySend, getDate());
//                        dbHelper.insertPendingOrder(orderId, Global.SEND, typeSend, bodySend, getDate());

//                        if (bodySend != null && (bodySend.equalsIgnoreCase("Selected Morning Slot") || bodySend.equalsIgnoreCase("Selected Evening Slot"))) {
//                            endChat();
//                            cart_imageView.setVisibility(View.GONE);
//                        }
                    }
                    break;

                case XmppService.BANNED_MESSAGE:
                    if (Global.BROADCAST) {
                        BanConfirmation();
                        Global.BROADCAST = false;
                    }
            }
        }
    }
}
