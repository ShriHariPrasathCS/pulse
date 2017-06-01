package com.pulseplus;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.armor.fileupload.FilePath;
import com.pulseplus.global.Global;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class AudioRecordActivity extends AppCompatActivity {


    public AppCompatImageView imgPlay, imgPauseMe, imgPlayGrey, imgPauseMeGrey, audio_stop, audio_record, refresh_image_grey, refresh_image, audio_send_icon, audio_send_icon_green;
    public TextView txtTimer;
    public CircularSeekBar seekBar;
    public Toolbar toolbar;
    //CTextView  audio_record;
    LinearLayout audio_send, audio_recordagain;
    boolean duration = false;
    private MediaRecorder mediaRecorder;
    private Uri audioURI;
    private long startTime = 0L;
    private long timeInMilliseconds = 0L;
    private long timeSwapBuff = 0L;
    private long updatedTime = 0L;
    private MediaPlayer player = new MediaPlayer();
    private int time;
    private boolean isRecording = false;

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            updatedTime = timeSwapBuff + timeInMilliseconds;
            long min = TimeUnit.MILLISECONDS.toMinutes(updatedTime);
            long second = TimeUnit.MILLISECONDS.toSeconds(updatedTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(updatedTime));
            txtTimer.setText(String.format(Locale.getDefault(), "%02d:%02d", min, second));
            handler.postDelayed(this, 1000);
        }
    };

    private Handler seekHandler = new Handler();
    private Runnable updateSeek = new Runnable() {
        @Override
        public void run() {
            try {
                timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
                updatedTime = timeSwapBuff + timeInMilliseconds;
                long min = TimeUnit.MILLISECONDS.toMinutes(updatedTime);
                long second = TimeUnit.MILLISECONDS.toSeconds(updatedTime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(updatedTime));
                int currentPosition = player.getCurrentPosition();
                seekBar.setProgress(currentPosition);
                txtTimer.setText(String.format(Locale.getDefault(), "%02d:%02d", min, second));
                if (player.isPlaying())
                    seekBar.postDelayed(this, 1000);
            } catch (Exception e) {
                player = new MediaPlayer();
            }
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_screen);
        init();
        setToolbar();
        setListener();
    }

    private void init() {
        audio_record = (AppCompatImageView) findViewById(R.id.imgRecord);
        audio_recordagain = (LinearLayout) findViewById(R.id.record_again);
        audio_stop = (AppCompatImageView) findViewById(R.id.imgStop);
        imgPlay = (AppCompatImageView) findViewById(R.id.imgPlay);
        audio_send = (LinearLayout) findViewById(R.id.send);
        txtTimer = (TextView) findViewById(R.id.txtTimer);
        seekBar = (CircularSeekBar) findViewById(R.id.seekBar);
        imgPauseMe = (AppCompatImageView) findViewById(R.id.imgPauseMe);
        imgPlayGrey = (AppCompatImageView) findViewById(R.id.imgPlayGrey);
        imgPauseMeGrey = (AppCompatImageView) findViewById(R.id.imgPauseMeGrey);
        refresh_image_grey = (AppCompatImageView) findViewById(R.id.refresh_image_grey);
        refresh_image = (AppCompatImageView) findViewById(R.id.refresh_image);
        audio_send_icon = (AppCompatImageView) findViewById(R.id.audio_send_icon);
        audio_send_icon_green = (AppCompatImageView) findViewById(R.id.audio_send_icon_green);


        refresh_image.setVisibility(View.GONE);
        refresh_image_grey.setVisibility(View.VISIBLE);
        audio_send_icon.setVisibility(View.GONE);
        audio_send_icon_green.setVisibility(View.VISIBLE);
        audio_stop.setClickable(false);
        imgPlay.setClickable(false);
        audio_send.setClickable(false);
        audio_recordagain.setClickable(false);
        audio_stop.setEnabled(false);
        imgPlay.setEnabled(false);
        audio_send.setEnabled(false);
        audio_recordagain.setEnabled(false);
        imgPlayGrey.setClickable(false);
        imgPlayGrey.setEnabled(false);
        imgPauseMeGrey.setEnabled(false);
        imgPauseMeGrey.setClickable(false);
        seekBar.setEnabled(false);
        seekBar.setClickable(false);
        seekBar.setIsTouchEnabled(false);
        seekBar.setFocusableInTouchMode(false);

    }

    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle(R.string.audio_recored);
            toolbar.setNavigationIcon(R.drawable.ic_back_arrow);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    closeActivity();
                }
            });
        }
    }

    private void closeActivity() {
        if (player != null) {
            player.pause();
            player.stop();
            player = null;
            seekHandler.removeCallbacks(updateSeek);
        }
        if (isRecording) {
            isRecording = false;
            handler.removeCallbacks(runnable);
            stopRecording();
        }
        finish();
    }

    @Override
    public void onBackPressed() {
        closeActivity();
        super.onBackPressed();
    }

    private void setListener() {
        audio_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRecording) {
                    visibility(1);
                    audioURI = getAudioUri();
                    audio_stop.setVisibility(View.VISIBLE);
                    audio_record.setVisibility(View.GONE);
                    imgPlay.setVisibility(View.GONE);
                    refresh_image_grey.setVisibility(View.VISIBLE);
                    refresh_image.setVisibility(View.GONE);
                    audio_send_icon_green.setVisibility(View.VISIBLE);
                    audio_send_icon.setVisibility(View.GONE);
                    //  imgPlayGrey.setVisibility(View.VISIBLE);

                    audio_stop.setClickable(true);
                    audio_stop.setEnabled(true);
                    audio_send.setClickable(false);
                    imgPlayGrey.setClickable(false);
                    imgPlayGrey.setEnabled(false);
                    audio_recordagain.setEnabled(false);
                    audio_recordagain.setClickable(false);
                    audio_send.setEnabled(false);
                    audio_send.setClickable(false);
                    refresh_image_grey.setEnabled(false);


                    startTime = SystemClock.uptimeMillis();
                    txtTimer.setText("00:00");
                    handler.postDelayed(runnable, 100);
                    startRecording();
                }
            }
        });


        audio_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                visibility(2);
                if (isRecording) {
                    isRecording = false;
                    handler.removeCallbacks(runnable);
                    stopRecording();

                    audio_stop.setVisibility(View.GONE);
                    audio_recordagain.setVisibility(View.VISIBLE);
                    imgPlay.setVisibility(View.VISIBLE);
                    refresh_image_grey.setVisibility(View.GONE);
                    refresh_image.setVisibility(View.VISIBLE);
                    audio_send_icon_green.setVisibility(View.GONE);
                    audio_send_icon.setVisibility(View.VISIBLE);
                    //  imgPlayGrey.setVisibility(View.GONE);

                    audio_recordagain.setClickable(true);
                    audio_recordagain.setEnabled(true);
                    imgPlay.setClickable(true);
                    audio_send.setClickable(true);


                    imgPlay.setEnabled(true);
                    audio_send.setEnabled(true);
                    audio_send.setClickable(true);
                    refresh_image_grey.setEnabled(true);

                }
                if (player.isPlaying()) {
                    //visibility(2);
                    try {
                        stopPlayer();
                        seekHandler.removeCallbacks(updateSeek);

                        audio_stop.setVisibility(View.GONE);
                        audio_recordagain.setVisibility(View.VISIBLE);
                        imgPlay.setVisibility(View.VISIBLE);
                        //    imgPlayGrey.setVisibility(View.GONE);

                        imgPlay.setClickable(true);
                        audio_recordagain.setClickable(true);
                        audio_send.setClickable(true);


                        imgPlay.setEnabled(true);
                        audio_recordagain.setEnabled(true);
                        audio_send.setEnabled(true);


                    } catch (IllegalStateException | NullPointerException e) {
                        player = new MediaPlayer();
                    }
                }
            }
        });

        audio_recordagain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRecording) {
                    visibility(1);
                    audioURI = getAudioUri();
                    audio_stop.setVisibility(View.VISIBLE);
                    audio_record.setVisibility(View.GONE);
                    imgPlay.setVisibility(View.GONE);
                    //  imgPlayGrey.setVisibility(View.VISIBLE);
                    refresh_image_grey.setVisibility(View.VISIBLE);
                    refresh_image.setVisibility(View.GONE);
                    audio_send_icon_green.setVisibility(View.VISIBLE);
                    audio_send_icon.setVisibility(View.GONE);


                    audio_stop.setClickable(true);
                    audio_stop.setEnabled(true);
                    audio_send.setClickable(false);
                    audio_send.setEnabled(false);
                    imgPlayGrey.setEnabled(false);
                    imgPlayGrey.setClickable(false);


                    startTime = SystemClock.uptimeMillis();
                    txtTimer.setText("00:00");
                    handler.postDelayed(runnable, 100);
                    startRecording();

                }
            }
        });
        audio_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (audioURI != null) {
                    String filePath = FilePath.getPath(AudioRecordActivity.this, audioURI);
                    Intent intent = new Intent();
                    intent.putExtra("audio_path", filePath);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    Global.CustomToast(AudioRecordActivity.this, "Record Your Order and Send");
                }
            }
        });

        imgPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (audioURI != null) {
                    imgPlay.setVisibility(View.GONE);
                    imgPauseMe.setVisibility(View.VISIBLE);
                    audio_stop.setVisibility(View.GONE);
                    audio_recordagain.setVisibility(View.VISIBLE);
                    refresh_image_grey.setVisibility(View.VISIBLE);
                    refresh_image.setVisibility(View.GONE);
                    audio_send_icon_green.setVisibility(View.VISIBLE);
                    audio_send_icon.setVisibility(View.GONE);


                    imgPlay.setEnabled(false);
                    imgPauseMe.setEnabled(true);
                    audio_stop.setEnabled(false);
                    audio_send.setEnabled(false);
                    audio_record.setEnabled(false);
                    audio_recordagain.setEnabled(false);


                    audio_send.setClickable(false);
                    imgPlay.setClickable(false);
                    imgPauseMe.setClickable(true);
                    audio_stop.setClickable(false);
                    audio_record.setClickable(false);
                    audio_recordagain.setClickable(false);

                    if (player == null) {
                        player = new MediaPlayer();
                    }

                    player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    File filename = new File(FilePath.getPath(AudioRecordActivity.this, audioURI));
                    startPlayer(String.valueOf(filename.getAbsolutePath()));
                    imgPauseMe.setVisibility(View.VISIBLE);
                    seekBar.setVisibility(View.VISIBLE);
                    //audioURI = getAudioUri();
                    startTime = SystemClock.uptimeMillis();
                }
            }
        });

        imgPauseMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgPlay.setVisibility(View.VISIBLE);
                imgPauseMe.setVisibility(View.GONE);
                refresh_image_grey.setVisibility(View.GONE);
                refresh_image.setVisibility(View.VISIBLE);
                audio_send_icon_green.setVisibility(View.GONE);
                audio_send_icon.setVisibility(View.VISIBLE);
                if (player.isPlaying()) {
                    stopPlayer();
                }
                seekBar.setProgress(0);

            }
        });


        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                seekBar.setVisibility(View.VISIBLE);
                imgPlay.setVisibility(View.GONE);
                imgPauseMe.setVisibility(View.VISIBLE);
                audio_stop.setVisibility(View.GONE);
                audio_recordagain.setVisibility(View.VISIBLE);

                imgPlay.setEnabled(false);
                imgPlay.setClickable(false);
                imgPauseMe.setEnabled(true);
                imgPauseMe.setClickable(true);
               /* audio_send.setEnabled(true);
                audio_send.setClickable(true);*/
                audio_stop.setEnabled(false);
                audio_stop.setClickable(false);
                audio_recordagain.setClickable(false);
                audio_recordagain.setEnabled(false);

                //seekBar.setEnabled(false);
                //seekBar.setClickable(false);
                seekBar.setMax(mp.getDuration());
                mp.start();
                seekHandler.postDelayed(updateSeek, 100);
                //handler.postDelayed(runnable, 100);
            }
        });
        player.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                //seekBar.setSecondaryProgress(percent);
            }
        });

        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

                //seekBar.setVisibility(View.GONE);
                /*if (duration) {*/
                handler.removeCallbacks(runnable);
                seekBar.setProgress(50);
                audio_send.setEnabled(true);
                audio_send.setClickable(true);
                txtTimer.setText("00:00");
                // }

                imgPlay.setVisibility(View.VISIBLE);
                imgPauseMe.setVisibility(View.GONE);
                audio_stop.setVisibility(View.GONE);
                audio_recordagain.setVisibility(View.VISIBLE);
                refresh_image_grey.setVisibility(View.GONE);
                refresh_image.setVisibility(View.VISIBLE);
                audio_send_icon_green.setVisibility(View.GONE);
                audio_send_icon.setVisibility(View.VISIBLE);

                imgPlay.setEnabled(true);
                imgPlay.setClickable(true);

                audio_stop.setEnabled(false);
                audio_stop.setClickable(false);
                audio_recordagain.setClickable(true);
                audio_recordagain.setEnabled(true);
                imgPauseMe.setClickable(false);
                imgPauseMe.setEnabled(false);
                duration = true;
            }
        });

        seekBar.setOnSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
            @Override
            public void onProgressChanged(CircularSeekBar circularSeekBar, int progress, boolean fromUser) {
                Log.e("TAG", "onProgressChanged called");
                if (fromUser) {
                    player.seekTo(progress);
                    player.start();
                }
            }

            @Override
            public void onStopTrackingTouch(CircularSeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(CircularSeekBar seekBar) {

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
                File file = new File(audioUrl);
                if (file.exists()) {
                    player.setDataSource(this, Uri.fromFile(file));
                    player.prepareAsync();

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

    private void visibility(int key) {
        switch (key) {
            case 1:
                imgPlay.setVisibility(View.VISIBLE);
                audio_stop.setVisibility(View.GONE);
                imgPlay.setEnabled(false);
                audio_send.setEnabled(false);
                break;
            case 2:
                imgPlay.setVisibility(View.VISIBLE);
                audio_stop.setVisibility(View.VISIBLE);
                imgPlay.setEnabled(true);
                audio_send.setEnabled(true);
                audio_record.setEnabled(false);
                break;
        }

    }

    private void stopRecording() {
        mediaRecorder.stop();
        mediaRecorder.reset();
        mediaRecorder = null;
    }

    private void startRecording() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
        String filePath = FilePath.getPath(AudioRecordActivity.this, audioURI);
        mediaRecorder.setOutputFile(filePath);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            isRecording = true;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private Uri getAudioUri() {
        Uri audioUri = null;
        File outUri = Global.getAudioDirectory(Global.AUDIO_SEND);
        if (outUri != null) {
            audioUri = Uri.fromFile(outUri);
        }
        return audioUri;
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopPlayer();
    }
}
