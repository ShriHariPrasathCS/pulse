package com.pulseplus.adapter;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v7.widget.AppCompatSeekBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.armor.fontlib.CTextView;
import com.pulseplus.FileViewActivity;
import com.pulseplus.R;
import com.pulseplus.dialog.PlayerDialog;
import com.pulseplus.global.Global;
import com.pulseplus.listener.MediaPlayerListener;
import com.pulseplus.model.Child;
import com.pulseplus.model.Group;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

//import com.example.jean.jcplayer.JcPlayerView;

public class MessageAdapter extends BaseExpandableListAdapter {

    private static final String TEXT = "3";
    private static final String IMAGE = "1";
    private static final String AUDIO = "2";
    MediaPlayer player = new MediaPlayer();
    MediaPlayerListener listener;
    private LayoutInflater inflater;
    private ProgressDialog p;
   // ProgressBar progressBar, imageLoading;
    private Activity context;
    private ArrayList<Group> groupList;
    private File imageFile;
    private String imageUrl, audioUrl;
    boolean playing = true;
    ProgressDialog mProgressDialog;
    ChildHolder holder;


    //JcPlayerView jcplayer;

    public MessageAdapter(Activity context, ArrayList<Group> groupList, MediaPlayerListener listener) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.groupList = groupList;
        this.listener = listener;
        if (listener != null) {
            listener.playing(player);
        }
        // instantiate it within the onCreate method
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setMessage("Downloading...");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(true);
    }

    @Override
    public int getGroupCount() {
        return groupList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        ArrayList<Child> children = groupList.get(groupPosition).getChildren();
        return children.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        ArrayList<Child> children = groupList.get(groupPosition).getChildren();
        return children.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        Group group = groupList.get(groupPosition);
        Date date = new Date();
        String msgDate = Global.getDate(date, "MMMM dd");
        GroupHolder holder;
        if (convertView == null) {
            holder = new GroupHolder();
            convertView = inflater.inflate(R.layout.custom_msg_group, parent, false);
            holder.txtDate = (CTextView) convertView.findViewById(R.id.txtDate);
            convertView.setTag(holder);
        } else {
            holder = (GroupHolder) convertView.getTag();
        }
        holder.txtDate.setText(msgDate);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        Child child = groupList.get(groupPosition).getChildren().get(childPosition);


        if (convertView == null) {
            holder = new ChildHolder();
            convertView = inflater.inflate(R.layout.custom_msg, parent, false);
            holder.txtName = (CTextView) convertView.findViewById(R.id.txtName);
            holder.txtDesc = (CTextView) convertView.findViewById(R.id.txtDesc);
            holder.txtTime = (CTextView) convertView.findViewById(R.id.txtTime);
            holder.leftLayout = (LinearLayout) convertView.findViewById(R.id.leftLayout);
            holder.seekBar = (AppCompatSeekBar) convertView.findViewById(R.id.seekBar);
            holder.imageViewMeLeft = (ImageView) convertView.findViewById(R.id.imageViewMeLeft);

            holder.txtNameMe = (CTextView) convertView.findViewById(R.id.txtNameMe);
            holder.txtDescMe = (CTextView) convertView.findViewById(R.id.txtDescMe);
            holder.txtTimeMe = (CTextView) convertView.findViewById(R.id.txtTimeMe);
            holder.imageViewMe = (ImageView) convertView.findViewById(R.id.imageViewMe);
            holder.imgPlayMe = (ImageView) convertView.findViewById(R.id.imgPlayMe);
            holder.imgPauseMe = (ImageView) convertView.findViewById(R.id.imgPauseMe);
            holder.rightLayout = (LinearLayout) convertView.findViewById(R.id.rightLayout);
            holder.playLayout = (LinearLayout) convertView.findViewById(R.id.playLayout);
            holder.playLayoutLeft = (LinearLayout) convertView.findViewById(R.id.playLayoutLeft);
            p = Global.initProgress(context);

          // progressBar = (ProgressBar) convertView.findViewById(R.id.imageProgress);
           // imageLoading = (ProgressBar)convertView.findViewById(R.id.imageLoading);


            holder.imgView = (ImageView) convertView.findViewById(R.id.imgView);

            convertView.setTag(holder);
        } else {
            holder = (ChildHolder) convertView.getTag();
        }
        if (child.getChat_usertype().equals("1")) {
            holder.leftLayout.setVisibility(View.GONE);
            holder.rightLayout.setVisibility(View.VISIBLE);
            setMyChildView(child, holder);

        } else {
            holder.leftLayout.setVisibility(View.VISIBLE);
            holder.rightLayout.setVisibility(View.GONE);
            setOtherChildView(child, holder);
        }
        return convertView;
    }


    private void setOtherChildView(final Child child, final ChildHolder holder) {
        holder.txtNameMe.setVisibility(View.GONE);
        String type = child.getMessage_type();
        String msgDate = Global.getDate(child.getSent_date(), "yyyy-MM-dd HH:mm:ss", "hh:mm a");
        if (msgDate.equals("")) {
            msgDate = child.getSent_date();
        }

        if (type.equalsIgnoreCase(TEXT)) {
            holder.txtDesc.setVisibility(View.VISIBLE);
            holder.txtDesc.setText(child.getMessage());
            holder.txtTime.setText(msgDate);
            holder.playLayoutLeft.setVisibility(View.GONE);
            holder.imageViewMeLeft.setVisibility(View.GONE);
           // progressBar.setVisibility(View.GONE);
           // progressBar.setVisibility(View.GONE);

        } else if (type.equalsIgnoreCase(IMAGE)) {
            holder.txtDesc.setVisibility(View.GONE);
            holder.imageViewMeLeft.setVisibility(View.VISIBLE);
            holder.playLayoutLeft.setVisibility(View.GONE);
            holder.txtTime.setText(msgDate);
          //  progressBar.setVisibility(View.VISIBLE);
          //  imageLoading.setVisibility(View.VISIBLE);

            final Uri uri = Uri.parse(child.getMessage());
            imageFile = new File(Environment.getExternalStorageDirectory() + Global.IMAGE_RECEIVE + File.separator + uri.getLastPathSegment());
            if (imageFile.exists()) {
                Picasso.with(context).load(imageFile).resize(50, 50).centerCrop().into(holder.imageViewMeLeft);
            } else {
                imageUrl = String.format("%s%s", Global.FILE_URL, child.getMessage());
                Picasso.with(context).load(imageUrl).tag("thumbnail").fit().centerInside().into(holder.imageViewMeLeft);
            }
            holder.imageViewMeLeft.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String imageUri = null;
                    if (imageFile != null) {
                        imageUri = String.valueOf(imageFile);
                    }
                    Intent intent = new Intent(context, FileViewActivity.class);
                    intent.putExtra("imageUrl", child.getMessage());
                    context.startActivity(intent);
                }
            });
        } else if (type.equalsIgnoreCase(AUDIO)) {
            holder.txtDesc.setVisibility(View.GONE);
            holder.imageViewMeLeft.setVisibility(View.GONE);
            holder.playLayoutLeft.setVisibility(View.VISIBLE);
            holder.imageViewMeLeft.setVisibility(View.GONE);
            holder.txtTime.setText(msgDate);
          //  progressBar.setVisibility(View.GONE);

            holder.playLayoutLeft.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    File file;
                    if (child.getChat_usertype().equalsIgnoreCase("1")) {
                        file = new File(Environment.getExternalStorageDirectory() + Global.AUDIO_SEND + File.separator + child.getMessage());
                        String audioString = file.getAbsolutePath();
                        PlayerDialog dialog = new PlayerDialog();
                        Bundle bundle = new Bundle();
                        bundle.putString("audioURI", audioString);
                        dialog.setArguments(bundle);
                        dialog.show(context.getFragmentManager(), "");
                    } else {
                        file = new File(Environment.getExternalStorageDirectory() + Global.AUDIO_RECEIVE + File.separator + child.getMessage());
                        if (file.exists()) {
                            String audioString = file.getAbsolutePath();
                            PlayerDialog dialog = new PlayerDialog();
                            Bundle bundle = new Bundle();
                            bundle.putString("audioURI", audioString);
                            dialog.setArguments(bundle);
                            dialog.show(context.getFragmentManager(), "");
                        } else {
                            new DownloadTask(child.getMessage()).execute(Global.FILE_URL + child.getMessage());
                        }

                    }

                }
            });


        }
    }

    private void setMyChildView(final Child child, final ChildHolder holder) {

        final Handler seekHandler = new Handler();
        final Runnable updateSeek = new Runnable() {
            @Override
            public void run() {
                try {
                    int currentPosition = player.getCurrentPosition();
                    holder.seekBar.setProgress(currentPosition);
                    if (player.isPlaying())
                        holder.seekBar.postDelayed(this, 1000);
                } catch (Exception e) {
                    player = new MediaPlayer();
                }
            }
        };

        holder.txtNameMe.setVisibility(View.GONE);
        String type = child.getMessage_type();
        String msgDate = Global.getDate(child.getSent_date(), "yyyy-MM-dd HH:mm:ss", "hh:mm a");
        if (msgDate.equals("")) {
            msgDate = child.getSent_date();
        }
        //String msgDate = child.getSent_date();


        switch (type) {
            case TEXT:
                holder.txtDescMe.setVisibility(View.VISIBLE);
                holder.imageViewMe.setVisibility(View.GONE);
                holder.playLayout.setVisibility(View.GONE);
                holder.txtDescMe.setText(child.getMessage());
                holder.txtTimeMe.setText(msgDate);
              //  progressBar.setVisibility(View.GONE);
                break;
            case IMAGE:
                holder.imageViewMe.setVisibility(View.VISIBLE);
               // holder.imageViewMe.setVisibility(View.VISIBLE);
                holder.playLayout.setVisibility(View.GONE);
                holder.txtDescMe.setVisibility(View.GONE);
                holder.txtTimeMe.setText(msgDate);
              //  progressBar.setVisibility(View.VISIBLE);


                final Uri uri = Uri.parse(child.getMessage());
                imageFile = new File(Environment.getExternalStorageDirectory() + Global.IMAGE_SEND + File.separator + uri.getLastPathSegment());
             //   progressBar.setProgress(0);
                if (imageFile.exists()) {
                    Picasso.with(context).load(imageFile).tag("thumbnail").fit().centerInside().into(holder.imageViewMe);
                } else {
                    imageUrl = String.format("%s%s", Global.FILE_URL, child.getMessage());
                    Picasso.with(context).load(imageUrl).tag("thumbnail").fit().centerInside().into(holder.imageViewMe);
                }
                holder.imageViewMe.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String imageUri = null;
                        if (imageFile != null) {
                            imageUri = String.valueOf(imageFile);
                        }
                        Intent intent = new Intent(context, FileViewActivity.class);
                        //intent.putExtra("file", imageUri);
                        intent.putExtra("imageUrl", child.getMessage());
                        context.startActivity(intent);
                    }
                });
                break;
            case AUDIO:
                holder.imageViewMe.setVisibility(View.GONE);
                holder.playLayout.setVisibility(View.VISIBLE);
                holder.txtDescMe.setVisibility(View.GONE);
                holder.txtTimeMe.setText(msgDate);
              //  progressBar.setVisibility(View.GONE);


                holder.playLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        holder.imgView.setVisibility(View.VISIBLE);
                        File file;

                        file = new File(Environment.getExternalStorageDirectory() + Global.AUDIO_SEND + File.separator + child.getMessage());
                        String audioString = file.getAbsolutePath();
                        PlayerDialog dialog = new PlayerDialog();
                        Bundle bundle = new Bundle();
                        bundle.putString("audioURI", audioString);
                        dialog.setArguments(bundle);
                        dialog.show(context.getFragmentManager(), "");


                    }
                });
                break;
        }

        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                if (mp.isPlaying()) {
                    mp.stop();
                }
                holder.seekBar.setMax(mp.getDuration());
                mp.start();
                seekHandler.postDelayed(updateSeek, 100);
            }
        });
        player.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                holder.seekBar.setSecondaryProgress(percent);
            }
        });

        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                           @Override
                                           public void onCompletion(MediaPlayer mp) {
                                               //mp.stop();
                                               stopPlayer();
                                               if (!playing) {
                                                   seekHandler.removeCallbacks(updateSeek);
                                                   holder.imgPlayMe.setVisibility(View.VISIBLE);
                                                   holder.imgPauseMe.setVisibility(View.GONE);
                                                   playing = true;
                                                   holder.seekBar.setProgress(0);
                                               } else {
                                                   playing = false;
                                               }
                                               //seekHandler.removeCallbacks(updateSeek);
                                           }
                                       }
        );
        holder.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    player.seekTo(progress);
                    player.start();
                    seekBar.setEnabled(false);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (player != null && player.isPlaying())
                    player.pause();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (player != null) {
                    player.seekTo(seekBar.getProgress());
                    player.start();
                }
              /*  int CurrentLevel = seekBar.getProgress();
                if (CurrentLevel < 100){
                    CurrentLevel = 100;
                    seekBar.setProgress(CurrentLevel);
                }*/
            }
        });
    }

    private void startPlayer(String audioUrl) {
        if (player != null) {
            if (player.isPlaying()) {
                stopPlayer();
            } else {
                player.reset();
            }
        }
        try {
            if (player != null) {
                String path;
                File file = new File(Environment.getExternalStorageDirectory() + Global.AUDIO_SEND + File.separator + audioUrl);
                if (file.exists()) {
                    player.setDataSource(file.getAbsolutePath());
                } else {
                    player.setDataSource(Global.FILE_URL + audioUrl);
                }
                player.prepareAsync();
                player.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopPlayer() {
        if (player != null) {
            if (player.isPlaying()) {
                player.pause();
                player.stop();
                player.reset();
            }
        }
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    public class GroupHolder {
        CTextView txtDate;
    }

    public class ChildHolder {
        public CTextView txtName, txtDesc, txtTime;

        public CTextView txtNameMe, txtDescMe, txtTimeMe;
        public ImageView imageViewMe, imgPlayMe, imgPauseMe, imgView, imageViewMeLeft, audioFileImage;
        public AppCompatSeekBar seekBar;

        public LinearLayout leftLayout, rightLayout, playLayout, playLayoutLeft;
        //public JcPlayerView jcplayer;
    }

    private class DownloadTask extends AsyncTask<String, Integer, String> {


        private PowerManager.WakeLock mWakeLock;
        File storageDir;
        long millis = System.currentTimeMillis() % 1000;
        OutputStream output = null;
        String FilePath;
        String filename;

        public DownloadTask(String filename) {
            this.filename = filename;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // take CPU lock to prevent CPU from going off if the user
            // presses the power button during download
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
            mProgressDialog.show();

        }

        @Override
        protected String doInBackground(String... sUrl) {

            InputStream input = null;

            HttpURLConnection connection = null;
            try {
                URL url = new URL(sUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                // download the file
                input = connection.getInputStream();
                storageDir = new File(Environment.getExternalStorageDirectory() + Global.AUDIO_RECEIVE);
                if (!storageDir.exists()) {
                    storageDir.mkdirs();
                }
                FilePath = storageDir + "/" + filename;
                output = new FileOutputStream(FilePath);

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // if we get here, length is known, now set indeterminate to false
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();
            mProgressDialog.dismiss();
            if (result != null)
                Toast.makeText(context, "Download error: " + result, Toast.LENGTH_LONG).show();
            else
                Toast.makeText(context, "File downloaded", Toast.LENGTH_SHORT).show();
            String audioString = FilePath;
            PlayerDialog dialog = new PlayerDialog();
            Bundle bundle = new Bundle();
            bundle.putString("audioURI", audioString);
            dialog.setArguments(bundle);
            dialog.show(context.getFragmentManager(), "");
        }
    }
}
