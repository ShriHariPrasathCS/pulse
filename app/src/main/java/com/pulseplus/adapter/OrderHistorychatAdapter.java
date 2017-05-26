package com.pulseplus.adapter;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.widget.AppCompatSeekBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;

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
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Bright Bridge on 06-Oct-16.
 */

public class OrderHistorychatAdapter extends BaseExpandableListAdapter {

    private static final String TEXT = "3";
    private static final String IMAGE = "1";
    private static final String AUDIO = "2";
    MediaPlayer player = new MediaPlayer();
    MediaPlayerListener listener;
    private LayoutInflater inflater;
    private Activity context;
    private ArrayList<Group> groupList;
    private File imageFile;
    private String imageUrl, audioUrl;
    boolean playing = true;
   // ProgressBar progressBar;


    public OrderHistorychatAdapter(Activity context, ArrayList<Group> groupList) {
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.groupList = groupList;
        // this.listener = listener;
        if (listener != null) {
            listener.playing(player);
        }
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

        final ChildHolder holder;

        if (convertView == null) {
            holder = new ChildHolder();
            convertView = inflater.inflate(R.layout.custom_msg, parent, false);
            holder.txtName = (CTextView) convertView.findViewById(R.id.txtName);
            holder.txtDesc = (CTextView) convertView.findViewById(R.id.txtDesc);
            holder.txtTime = (CTextView) convertView.findViewById(R.id.txtTime);
            holder.leftLayout = (LinearLayout) convertView.findViewById(R.id.leftLayout);
            holder.seekBar = (AppCompatSeekBar) convertView.findViewById(R.id.seekBar);
            holder.imageViewMeLeft = (ImageView) convertView.findViewById(R.id.imageViewMeLeft);
            holder.playLayoutLeft = (LinearLayout) convertView.findViewById(R.id.playLayoutLeft);

            holder.txtNameMe = (CTextView) convertView.findViewById(R.id.txtNameMe);
            holder.txtDescMe = (CTextView) convertView.findViewById(R.id.txtDescMe);
            holder.txtTimeMe = (CTextView) convertView.findViewById(R.id.txtTimeMe);
            holder.imageViewMe = (ImageView) convertView.findViewById(R.id.imageViewMe);
            holder.imgPlayMe = (ImageView) convertView.findViewById(R.id.imgPlayMe);
            holder.imgPauseMe = (ImageView) convertView.findViewById(R.id.imgPauseMe);
            holder.rightLayout = (LinearLayout) convertView.findViewById(R.id.rightLayout);
            holder.playLayout = (LinearLayout) convertView.findViewById(R.id.playLayout);
            holder.imgView = (ImageView) convertView.findViewById(R.id.imgView);

          //  progressBar = (ProgressBar) convertView.findViewById(R.id.imageProgress);

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

    private void setOtherChildView(final Child child, OrderHistorychatAdapter.ChildHolder holder) {
        holder.txtNameMe.setVisibility(View.GONE);
        String type = child.getMessage_type();
        String msgDate = Global.getDate(child.getSent_date(), "yyyy-MM-dd HH:mm:ss", "hh:mm a");
       /* if (type.equalsIgnoreCase(TEXT)) {
            holder.txtDesc.setVisibility(View.VISIBLE);
            //holder.imageLay.setVisibility(View.GONE);
            holder.txtDesc.setText(child.getMessage());
            holder.txtTime.setText(msgDate);
        }*/

        if (type.equalsIgnoreCase(TEXT)) {
            holder.txtDesc.setVisibility(View.VISIBLE);
            holder.txtDesc.setText(child.getMessage());
            holder.txtTime.setText(msgDate);
            holder.playLayoutLeft.setVisibility(View.GONE);
            holder.imageViewMeLeft.setVisibility(View.GONE);
           // progressBar.setVisibility(View.GONE);

        } else if (type.equalsIgnoreCase(IMAGE)) {
            holder.txtDesc.setVisibility(View.GONE);
            holder.imageViewMeLeft.setVisibility(View.VISIBLE);
            holder.playLayoutLeft.setVisibility(View.GONE);
            //holder.txtDesc.setText(child.getMessage());
            holder.txtTime.setText(msgDate);

            final Uri uri = Uri.parse(child.getMessage());
            imageFile = new File(Environment.getExternalStorageDirectory() + Global.IMAGE_RECEIVE + File.separator + uri.getLastPathSegment());
            if (imageFile.exists()) {
                //Picasso.with(context).load(imageFile).tag("thumbnail").fit().centerInside().into(holder.imageViewMeLeft);
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
                    //intent.putExtra("file", imageUri);
                    intent.putExtra("imageUrl", child.getMessage());
                    context.startActivity(intent);
                }
            });
        } else if (type.equalsIgnoreCase(AUDIO)) {
            holder.txtDesc.setVisibility(View.GONE);
            holder.imageViewMeLeft.setVisibility(View.GONE);
            holder.playLayoutLeft.setVisibility(View.VISIBLE);
            holder.imageViewMeLeft.setVisibility(View.GONE);
            //holder.txtDesc.setText(child.getMessage());
            holder.txtTime.setText(msgDate);
          //  progressBar.setVisibility(View.GONE);

            holder.playLayoutLeft.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //holder.audioFileImage.setVisibility(View.VISIBLE);
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
                            //       new MessageAdapter.DownloadTask(child.getMessage()).execute(Global.FILE_URL + child.getMessage());
                        }

                    }

                }
            });


        }
    }

    private void setMyChildView(final Child child, final OrderHistorychatAdapter.ChildHolder holder) {

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
                holder.playLayout.setVisibility(View.GONE);
                holder.txtDescMe.setVisibility(View.GONE);
                holder.txtTimeMe.setText(msgDate);

                Uri uri = Uri.parse(child.getMessage());
                imageFile = new File(Environment.getExternalStorageDirectory() + Global.IMAGE_SEND + File.separator + uri.getLastPathSegment());
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
             //   progressBar.setVisibility(View.GONE);


                /*holder.playLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent audioIntent = new Intent(context, FileViewActivity.class);
                        audioIntent.putExtra("audioUrl", child.getMessage());
                        context.startActivity(audioIntent);
                    }
                });
                break;*/


                holder.playLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // holder.jcplayer.setVisibility(View.VISIBLE);
                        //holder.jcplayer_layout.setVisibility(View.VISIBLE);
                        //holder.playLayout.setVisibility(View.GONE);
                        holder.imgView.setVisibility(View.VISIBLE);
                        File file = new File(Environment.getExternalStorageDirectory() + Global.AUDIO_SEND + File.separator + child.getMessage());
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

      /*  holder.imgPlayMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (player.isPlaying()) {
                    stopPlayer();
                } else if (child.getMessage() != null && !player.isPlaying()) {
                    //holder.imgPlayMe.setImageResource(R.drawable.pause);
                    holder.imgPlayMe.setVisibility(View.GONE);
                    holder.imgPauseMe.setVisibility(View.VISIBLE);
                    player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    startPlayer(String.valueOf(child.getMessage()));
                    holder.seekBar.setVisibility(View.VISIBLE);
                }
            }
        });
        holder.imgPauseMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.imgPlayMe.setVisibility(View.VISIBLE);
                holder.imgPauseMe.setVisibility(View.GONE);
                if (player.isPlaying()) {
                    stopPlayer();
                }
                holder.seekBar.setProgress(0);
            }
        });*/

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
        public ImageView imageViewMe, imgPlayMe, imgPauseMe, imgView, imageViewMeLeft;
        public AppCompatSeekBar seekBar;
        public LinearLayout leftLayout, rightLayout, playLayout, playLayoutLeft;
    }
}
