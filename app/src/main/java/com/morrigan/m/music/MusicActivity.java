package com.morrigan.m.music;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.morrigan.m.BaseActivity;
import com.morrigan.m.R;
import com.morrigan.m.ble.BleController;
import com.morrigan.m.c.MassageController;
import com.morrigan.m.main.VisualizerView;
import com.morrigan.m.music.MusicLoader.MusicInfo;

import java.io.IOException;
import java.util.List;

/**
 * 音乐跟随界面
 * Created by y on 2016/10/19.
 */
public class MusicActivity extends BaseActivity implements MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, SeekBar.OnSeekBarChangeListener
        , MusicAdapter.Callback, OpenPopup {


    private MediaPlayer mediaPlayer;
    private Visualizer visualizer;
    private VisualizerView visualizerView;
    List<MusicInfo> musics;
    protected static final int SEARCH_MUSIC_SUCCESS = 0;// 搜索成功标记

    private TextView tv_currTime, tv_totalTime, tv_showName, tv_artist;
    private int currIndex = 0;// 表示当前播放的音乐索引
    private boolean flag = true;//控制进度条线程标记

    // 定义当前播放器的状态״̬
    private static final int IDLE = 0;
    private static final int PAUSE = 1;
    private static final int START = 2;
    private static final int CURR_TIME_VALUE = 1;

    private int currState = IDLE; // 当前播放器的状态
    private Context context;
    private Activity activity;
    private ImageButton btnPre;
    private ImageButton btnPlay;
    private ImageButton btnNext;
    private SeekBar seekBar;
    MusicLoader loader;
    private FlingUpImageView iv_up;
    MusicsPopupWindow popupWindow;
    long time;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        activity = this;
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        setContentView(R.layout.activity_music);
        tv_currTime = (TextView) this.findViewById(R.id.tv_currTime);
        tv_totalTime = (TextView) this.findViewById(R.id.tv_totalTime);
        tv_showName = (TextView) this.findViewById(R.id.tv_showName);
        tv_artist = (TextView) this.findViewById(R.id.tv_artist);
        btnPre = (ImageButton) this.findViewById(R.id.btnPre);
        btnPlay = (ImageButton) this.findViewById(R.id.btnPlay);
        btnNext = (ImageButton) this.findViewById(R.id.btnNext);
        seekBar = (SeekBar) this.findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(this);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        setupVisualizerFxAndUI();
        iv_up = (FlingUpImageView) this.findViewById(R.id.iv_up);
        popupWindow = new MusicsPopupWindow(activity);
        iv_up.setOpenPopup(this);
        initMusic();
    }

    private void initMusic() {
        loader = MusicLoader.instance(context.getApplicationContext().getContentResolver());
        new Thread(new Runnable() {
            public void run() {
                musics = loader.getMusicList();
                hander.sendEmptyMessage(SEARCH_MUSIC_SUCCESS);
            }
        }).start();
    }

    public void onClickScan(View view) {
        MassageController.getInstance().onClickConnect(this);
    }

    @Override
    public void openPopup() {
        popupWindow.showAtLocation(MusicActivity.this.getWindow().getDecorView(), Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
    }


    private Handler hander = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case SEARCH_MUSIC_SUCCESS:
                    popupWindow.setData(musics);
                    //搜索音乐文件结束时
                    break;
                case CURR_TIME_VALUE:
                    //设置当前时间
                    tv_currTime.setText(msg.obj.toString());
                    updateSeek(500);//500毫秒更新一次
                    break;
                default:
                    break;
            }
        }

        ;
    };

    //开始播放
    private void start() {
        if (musics != null && musics.size() > 0) {
            MusicInfo info = musics.get(currIndex);
            mediaPlayer.reset();
            try {
                mediaPlayer.setDataSource(info.getUrl());
                mediaPlayer.prepare();
                mediaPlayer.start();
                initSeekBar();
                flag = true;
                updateSeek(0);
                tv_showName.setText(info.getTitle());
                tv_artist.setText(info.getArtist());
                btnPlay.setImageResource(R.drawable.music_pause);
                currState = PAUSE;
                popupWindow.setPlayIndex(currIndex);
                visualizer.setEnabled(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            visualizer.setEnabled(false);
            Toast.makeText(this, "播放列表为空", Toast.LENGTH_SHORT).show();
        }
    }

    public void updateSeek(int delay) {
        if (isFinishing()) {
            return;
        }
        if (flag) {
            if (mediaPlayer.getCurrentPosition() <= seekBar.getMax()) {
                seekBar.setProgress(mediaPlayer.getCurrentPosition());
                Message msg = hander.obtainMessage(CURR_TIME_VALUE, loader.toTime(mediaPlayer.getCurrentPosition()));
                hander.sendMessageDelayed(msg, delay);
            } else {
                flag = false;
            }
        }
    }

    //初始化SeekBar
    private void initSeekBar() {
        seekBar.setMax(mediaPlayer.getDuration());
        seekBar.setProgress(0);
        tv_totalTime.setText(loader.toTime(mediaPlayer.getDuration()));
    }

    private void setupVisualizerFxAndUI() {
        try {
            visualizerView = (VisualizerView) findViewById(R.id.visualizer);
            visualizer = new Visualizer(mediaPlayer.getAudioSessionId());
            visualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
            visualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
                @Override
                public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {
                    if (SystemClock.elapsedRealtime() - time > 200) {
                        Log.i("music", "onWaveFormDataCapture " + bytes.length);
                        time = SystemClock.elapsedRealtime();
                        visualizerView.updateVisualizer(bytes);
                        int vol = 128 - Math.abs(bytes[0]);
                        int decibel = (int) (vol * 160 / 128.0);
                        BleController.getInstance().musicMassageAsync(decibel);
                    }
                }

                @Override
                public void onFftDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {
                }
            }, Visualizer.getMaxCaptureRate() / 2, true, false);
        } catch (Throwable e) {
            Log.e("e", e.getMessage());
        }


    }

    public void onClickBack(View view) {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BleController.getInstance().manualStopAsync();
        mediaPlayer.stop();
        mediaPlayer.release();
    }

    public void onClickPrev(View view) {
        previous();
    }

    public void onClickStart(View view) {
        play();
    }

    public void onClickNext(View view) {
        next();
    }

    private void play() {
        switch (currState) {
            case IDLE:
                start();
                break;
            case PAUSE:
                BleController.getInstance().manualStopAsync();
                visualizer.setEnabled(false);
                mediaPlayer.pause();
                btnPlay.setImageResource(R.drawable.music_play);
                currState = START;
                break;
            case START:
                visualizer.setEnabled(true);
                mediaPlayer.start();
                btnPlay.setImageResource(R.drawable.music_pause);
                currState = PAUSE;
        }
    }

    //上一首
    private void previous() {
        if (musics != null && musics.size() > 0) {
            currIndex = (currIndex - 1 + currIndex) % musics.size();
            start();
        }

    }

    //下一自首
    private void next() {
        if (musics != null && musics.size() > 0) {
            currIndex = (currIndex + 1) % musics.size();
            start();
        }
    }

    //监听器，当当前歌曲播放完时触发，播放下一首
    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

        if (musics != null && musics.size() > 0) {
            next();
        } else {
            visualizer.setEnabled(false);
            Toast.makeText(this, "播放列表为空", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        visualizer.setEnabled(false);
        BleController.getInstance().manualStopAsync();
        mediaPlayer.reset();
        return false;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        //是否由用户改变
        if (b) {
            mediaPlayer.seekTo(i);
        }
    }

    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onListItemClick(View v, int index) {
        currIndex = index;
        start();
    }
}
