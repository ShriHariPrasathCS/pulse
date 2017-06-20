package com.pulseplus;

import android.app.ProgressDialog;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.pulseplus.global.Global;
import com.pulseplus.listener.MediaPlayerListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;

import uk.co.senab.photoview.PhotoViewAttacher;

public class FileViewActivity extends AppCompatActivity {

    public AppCompatSeekBar seekBar;
    public ImageView imageViewMe, imgPlayMe, imgPauseMe;
    public LinearLayout leftLayout, rightLayout, playLayout;
    MediaPlayer player = new MediaPlayer();
    MediaPlayerListener listener;
    boolean playing = true;
    TextView audio_time;
    ProgressDialog p;
    private ProgressBar progressBar;
    private ImageView imageView;
    private Toolbar toolbar;
    private String imageUrl, audioUrl;
    private PhotoViewAttacher attacher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_view);
        init();
        setToolbar();
        if (imageUrl != null) {
            p.show();
            setImage();
        } else {
            setAudio();
            startPlayer(audioUrl);
            imgPauseMe.setVisibility(View.VISIBLE);
            imgPlayMe.setVisibility(View.GONE);
        }

    }


    private void init() {
        imageView = (ImageView) findViewById(R.id.imageView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        imageUrl = getIntent().getStringExtra("imageUrl");
        audioUrl = getIntent().getStringExtra("audioUrl");
        imgPlayMe = (ImageView) findViewById(R.id.imgPlayMe);
        imgPauseMe = (ImageView) findViewById(R.id.imgPauseMe);
        seekBar = (AppCompatSeekBar) findViewById(R.id.seekBar);
        playLayout = (LinearLayout) findViewById(R.id.playLayout);
        audio_time = (TextView) findViewById(R.id.audio_time);
        p = Global.initProgress(this);


        //detector = new ScaleGestureDetector(FileViewActivity.this, new ScaleListener());
    }

    private void setToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            if (imageUrl != null) {
                toolbar.setTitle("Image Preview");
            } else {
                toolbar.setTitle("Audio File");
            }
            toolbar.setNavigationIcon(R.drawable.ic_back_arrow);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }

    /*Transformation blurTransformation = new Transformation() {
        @Override
        public Bitmap transform(Bitmap source) {
          //  Bitmap blurred = Blur.fastblur(FileViewActivity.this, source, 10);
            Bitmap blurred = Blur.SOLID
            source.recycle();
            return blurred;
        }

        @Override
        public String key() {
            return "blur()";
        }
    };*/


    private void setImage() {
        progressBar.setVisibility(View.GONE);
        playLayout.setVisibility(View.GONE);
        final String image = String.format("%s%s", Global.FILE_URL, imageUrl);
        Picasso.with(FileViewActivity.this).load(image).into(imageView, new Callback() {
            @Override
            public void onSuccess() {
                /*Picasso.with(FileViewActivity.this)
                        .load(image) // image url goes here
                        .placeholder(imageView.getDrawable())
                        .into(imageView);*/

                attacher = new PhotoViewAttacher(imageView);
                attacher.update();
                // Global.dismissProgress(p);
                p.dismiss();
            }

            @Override
            public void onError() {
                finish();

            }
        });
    }

    /*public class ChildHolder {
        public CTextView txtName, txtDesc, txtTime;

        public CTextView txtNameMe, txtDescMe, txtTimeMe;
        public ImageView imageViewMe, imgPlayMe, imgPauseMe;
        public AppCompatSeekBar seekBar;

        public LinearLayout leftLayout, rightLayout, playLayout;
    }*/
    private void setAudio() {
        // final ChildHolder holder = new ChildHolder();
        // final Child child = new Child();


        //  player = new MediaPlayer();
        playLayout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        final Handler seekHandler = new Handler();
        final Runnable updateSeek = new Runnable() {
            @Override
            public void run() {
                try {
                    int currentPosition = player.getCurrentPosition();
                    seekBar.setProgress(currentPosition);
                    if (player.isPlaying())
                        seekBar.postDelayed(this, 1000);
                } catch (Exception e) {
                    player = new MediaPlayer();
                }

              /*  try {
                    int currentPosition = player.getCurrentPosition();
                    seekBar.setProgress(currentPosition);
                    long minute = TimeUnit.MILLISECONDS.toMinutes(currentPosition);
                    long second = TimeUnit.MILLISECONDS.toSeconds(currentPosition) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(currentPosition));
                    audio_time.setText(String.format(Locale.getDefault(), "%02d:%02d", minute, second));
                    if (player.isPlaying())
                        seekBar.postDelayed(this, 1000);
                } catch (Exception e) {
                    player = new MediaPlayer();
                }*/
            }

        };


        imgPlayMe.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (player.isPlaying()) {
                    stopPlayer();
                } else if (audioUrl != null && !player.isPlaying()) {
                    //holder.imgPlayMe.setVisibility(R.drawable.pause);
                    imgPlayMe.setVisibility(View.GONE);
                    imgPauseMe.setVisibility(View.VISIBLE);
                    player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    startPlayer(String.valueOf(audioUrl));
                    seekBar.setVisibility(View.VISIBLE);
                    seekBar.setMax(100);
                }
            }
        });
        imgPauseMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgPlayMe.setVisibility(View.VISIBLE);
                imgPauseMe.setVisibility(View.GONE);
                if (player.isPlaying()) {
                    stopPlayer();
                }
                seekBar.setProgress(0);
            }
        });

        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                if (mp.isPlaying()) {
                    mp.stop();
                }
                seekBar.setMax(mp.getDuration());
                mp.start();
                seekHandler.postDelayed(updateSeek, 100);
            }
        });
        player.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                seekBar.setSecondaryProgress(percent);
            }
        });

        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                seekHandler.removeCallbacks(updateSeek);
                stopPlayer();
                if (!playing) {
                    player.seekTo(0);
                    seekBar.setProgress(0);
                    imgPlayMe.setVisibility(View.VISIBLE);
                    imgPauseMe.setVisibility(View.GONE);
                    playing = true;
                } else {
                    playing = false;
                }
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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

    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            if (player.isPlaying()) {
                stopPlayer();
            }
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (player != null) {
            if (player.isPlaying()) {
                stopPlayer();
            }
        }
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


}
