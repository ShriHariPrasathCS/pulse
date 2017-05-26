package com.pulseplus.xmpp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.pulseplus.global.Global;
import com.pulseplus.service.XmppService;
import com.pulseplus.util.PrefConnect;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.DefaultExtensionElement;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;

import java.io.IOException;
import java.util.Date;


public class XmppConnect {
    public AbstractXMPPConnection connection;
    public String textMessage;
    public String username;
    public String password;
    ChatManagerListenerImpl chatManagerListener;
    Date date = new Date();
    ChatManager chatManager;
    ProgressDialog p;
    private XMPPConnectionListener xmppConnectionListener = new XMPPConnectionListener();
    private String serverAddress;
    private String loginUser;
    private String passwordUser;
    private boolean connected;
    private boolean isToasted;
    private boolean chat_created;
    private boolean loggedin;
    private Connection connect = new Connection();
    private XmppService context;


    public XmppConnect(XmppService context, String serverAddress, String loginUser, String passwordUser) {

        // p = Global.initProgress(context);

        this.serverAddress = serverAddress;
        this.loginUser = loginUser;
        this.passwordUser = passwordUser;
        this.context = context;
        init();
    }

    private void init() {
        chatManagerListener = new ChatManagerListenerImpl();
        connect();
    }

    /**
     * This is to connect to xmpp
     */
    public void connect() {
        try {
            XMPPTCPConnectionConfiguration.Builder config = XMPPTCPConnectionConfiguration.builder();
            config.setUsernameAndPassword(loginUser, passwordUser);
            config.setServiceName(serverAddress);
            config.setHost(serverAddress);
            config.setPort(5222);
            config.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
            config.setDebuggerEnabled(true);
            connection = new XMPPTCPConnection(config.build());
            connection.addConnectionListener(xmppConnectionListener);
            connect.execute();
        } catch (Exception e) {
            Log.e("TAG", e.getMessage());
        }
    }

    private void login() {
        try {
            connection.login(loginUser, passwordUser);
            //connection.login(loginUser, passwordUser);
            Log.e("TAG", "Connection Success");
            // Global.dismissProgress(p);
        } catch (XMPPException e) {
            Global.dismissProgress(p);
            Log.e("TAG", "Connection failed");
            e.printStackTrace();
        } catch (SmackException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(final String body, final String toJid, String type, String orderId) {
        /*if (!loggedin) {
             connect.execute();
        }*/
        Log.e("TAG", "Sending message to :" + toJid);
        String msgDate = Global.getDate(date, "hh:ss a");
        Intent intent1 = new Intent(XmppService.SEND_MESSAGE);
        intent1.setPackage(context.getPackageName());
        intent1.putExtra("send message", body);
        intent1.putExtra("type", type);
        context.sendBroadcast(intent1);
        Global.BROADCAST = true;


        final Message message = new Message();
        DefaultExtensionElement defaultExtensionElement = new DefaultExtensionElement("arguments", "extra_arg");
        defaultExtensionElement.setValue("messagetype", type);
        message.setFrom(PrefConnect.readString(context, PrefConnect.JID, ""));
        message.setTo(toJid);
        message.setType(Message.Type.chat);
        message.addExtension(defaultExtensionElement);
       /* message.addExtension(new DefaultExtensionElement("post_time", msgDate));
        message.addExtension(new DefaultExtensionElement("app_user_name", PrefConnect.readString(context, PrefConnect.NAME, "")));
        message.addExtension(new DefaultExtensionElement("orderid", orderId));*/
        message.setBody(body);
        try {
            connection.sendStanza(message);
        } catch (SmackException.NotConnectedException e) {
            e.printStackTrace();
        }
    }

    private class Connection extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //   p.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                connection.setPacketReplyTimeout(20000);
                connection.connect();
                connected = false;
                chatManager = ChatManager.getInstanceFor(connection);
                chatManager.addChatListener(chatManagerListener);
            } catch (SmackException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (XMPPException e) {
                e.printStackTrace();

            }

            return null;
        }
    }

    private class ChatManagerListenerImpl implements ChatManagerListener {


        @Override
        public void chatCreated(Chat chat, boolean createdLocally) {
            Log.e("Chat Create locally", "" + createdLocally);
            chat.addMessageListener(new ChatMessageListener() {
                @Override
                public void processMessage(Chat chat, Message message) {
                    Log.e("TAG", "Received message: " + message.getBody());
                    String toJid = message.getFrom();
                    toJid = toJid.split("/")[0];
                    PrefConnect.writeString(context, PrefConnect.TO_JID, toJid);
                    if (message.getType() == Message.Type.chat || message.getType() == Message.Type.normal) {
                        if (message.getBody() != null) {
                            String msg_type;
                            if (message.getBody().contains(".png") || message.getBody().contains(".jpg")) {
                                msg_type = "1";
                            } else if (message.getBody().contains(".m4a") || message.getBody().contains(".mp3")) {
                                msg_type = "2";
                            } else {
                                msg_type = "3";
                            }

                            if (message.getBody().equalsIgnoreCase("Banned")) {
                                Global.BROADCAST = true;
                                Intent intent = new Intent(XmppService.BANNED_MESSAGE);
                                intent.setPackage(context.getPackageName());
                                context.sendBroadcast(intent);
                            } else if (!message.getBody().equals("Order has been saved to cart") &&
                                    ((!message.getBody().equals("Your order has been confirmed,it will be delivered by 6-10AM")) || (!message.getBody().equals("Your order has been confirmed,it will be delivered by 6-10PM")) ))  {
                                Global.BROADCAST = true;
                                Intent intent = new Intent(XmppService.NEW_MESSAGE);
                                intent.setPackage(context.getPackageName());
                                intent.putExtra("new message", message.getBody());
                                intent.putExtra("type", msg_type);
                                intent.putExtra("jid", toJid);
                                context.sendBroadcast(intent);
                            }
                        }
                    }
                    Log.e("TAG", "Received message: " + chat.toString());
                }
            });
        }
    }


    //Connection Listener to check connection state
    public class XMPPConnectionListener implements ConnectionListener {
        @Override
        public void connected(final XMPPConnection connection) {

            Log.d("xmpp", "Connected!");
            connected = true;
            if (!connection.isAuthenticated()) {
                login();
            }
        }

        @Override
        public void connectionClosed() {

            if (isToasted)

                new Handler(Looper.getMainLooper()).post(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub


                    }
                });
            Log.d("xmpp", "ConnectionClosed!");
            connected = false;
            chat_created = false;
            loggedin = false;
        }

        @Override
        public void connectionClosedOnError(Exception arg0) {
            if (isToasted)

                new Handler(Looper.getMainLooper()).post(new Runnable() {

                    @Override
                    public void run() {

                    }
                });
            Log.d("xmpp", "ConnectionClosedOn Error!");
            connected = false;

            chat_created = false;
            loggedin = false;
        }

        @Override
        public void reconnectingIn(int arg0) {

            Log.d("xmpp", "Reconnectingin " + arg0);

            loggedin = false;
        }

        @Override
        public void reconnectionFailed(Exception arg0) {
            if (isToasted)

                new Handler(Looper.getMainLooper()).post(new Runnable() {

                    @Override
                    public void run() {


                    }
                });
            Log.d("xmpp", "ReconnectionFailed!");
            connected = false;

            chat_created = false;
            loggedin = false;
        }

        @Override
        public void reconnectionSuccessful() {
            if (isToasted)

                new Handler(Looper.getMainLooper()).post(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub


                    }
                });
            Log.d("xmpp", "ReconnectionSuccessful");
            connected = true;

            chat_created = false;
            loggedin = false;
        }

        @Override
        public void authenticated(XMPPConnection arg0, boolean arg1) {
            Log.d("xmpp", "Authenticated!");
            loggedin = true;

            chat_created = false;
            new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
            }).start();
            if (isToasted)

                new Handler(Looper.getMainLooper()).post(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                    }
                });
        }
    }

}