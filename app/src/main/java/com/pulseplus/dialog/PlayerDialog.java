package com.pulseplus.dialog;//package com.pulseplus.dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;

import com.pulseplus.R;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;

/**
 * Bright Bridge on 29-Nov-16.
 */

public class PlayerDialog extends DialogFragment {

    final Handler seekHandler = new Handler();
    AppCompatImageView btn_play;
    AppCompatImageView btn_pause;
    SeekBar seek_bar;
    //   BanConfirmDialog.BanCallback callback;
    MediaPlayer player = new MediaPlayer();
    boolean playing = true;
    private String audioUrl;
    private Runnable updateSeek = new Runnable() {
        @Override
        public void run() {
            try {
                int currentPosition = player.getCurrentPosition();
                seek_bar.setProgress(currentPosition);
                if (player.isPlaying())
                    seek_bar.postDelayed(this, 1000);
            } catch (Exception e) {
                player = new MediaPlayer();
            }

        }
    };

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder playerDialog = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_player, null, false);
        init(view);
        setListener();
        playerDialog.setView(view);
        return playerDialog.create();
    }

    private void init(View view) {
        btn_play = (AppCompatImageView) view.findViewById(R.id.btn_play);
        btn_pause = (AppCompatImageView) view.findViewById(R.id.btn_pause);
        seek_bar = (SeekBar) view.findViewById(R.id.seek_bar);

        audioUrl = getArguments().getString("audioURI");
        startPlayer(audioUrl);
        btn_pause.setVisibility(View.VISIBLE);
        btn_play.setVisibility(View.GONE);
    }

    private void setListener() {
        btn_play.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (player.isPlaying()) {
                    stopPlayer();
                } else if (audioUrl != null && !player.isPlaying()) {
                    //holder.btn_play.setVisibility(R.drawable.pause);
                    btn_play.setVisibility(View.GONE);
                    btn_pause.setVisibility(View.VISIBLE);
                    player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    startPlayer(String.valueOf(audioUrl));
                    seek_bar.setVisibility(View.VISIBLE);
                    seek_bar.setMax(100);
                }
            }
        });
        btn_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_play.setVisibility(View.VISIBLE);
                btn_pause.setVisibility(View.GONE);
                if (player.isPlaying()) {
                    stopPlayer();
                }
                seek_bar.setProgress(0);
            }
        });

        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                if (mp.isPlaying()) {
                    mp.stop();
                }
                seek_bar.setMax(mp.getDuration());
                mp.start();
                seekHandler.postDelayed(updateSeek, 100);
            }
        });
        player.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                seek_bar.setSecondaryProgress(percent);
            }
        });

        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                seekHandler.removeCallbacks(updateSeek);
                stopPlayer();
                if (!playing) {
                    player.seekTo(0);
                    seek_bar.setProgress(0);
                    btn_play.setVisibility(View.VISIBLE);
                    btn_pause.setVisibility(View.GONE);
                    playing = true;
                } else {
                    playing = false;
                }
            }
        });
        seek_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
            /*if (player != null) {
                String path;
                File file = new File(Environment.getExternalStorageDirectory() + Global.AUDIO_SEND + File.separator + audioUrl);
                if (file.exists()) {
                    player.setDataSource(file.getAbsolutePath());
                } else {
                    player.setDataSource(Global.FILE_URL + audioUrl);
                }
                player.prepareAsync();
                player.start();
            }*/

            if (player != null) {
                File file = new File(audioUrl);
                if (file.exists()) {
                    FileInputStream is = new FileInputStream(file);
                    FileDescriptor fileDescriptor = is.getFD();
                    player.setDataSource(getActivity(), Uri.fromFile(file));
                    player.prepareAsync();
                    seek_bar.setProgress(player.getDuration());
//               player.start();
                }
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
    public void onDestroyView() {
        super.onDestroyView();
        if (player != null) {
            if (player.isPlaying()) {
                stopPlayer();
            }
        }
    }
}
