package com.morrigan.m.music;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.yzeaho.log.Lg;
import com.morrigan.m.BaseActivity;
import com.morrigan.m.BuildConfig;
import com.morrigan.m.R;
import com.morrigan.m.ble.BleCallback;
import com.morrigan.m.ble.BleController;
import com.morrigan.m.ble.SimpleBleCallback;
import com.morrigan.m.c.MassageController;
import com.morrigan.m.c.UserController;
import com.morrigan.m.main.VisualizerView;
import com.morrigan.m.music.MusicLoader.MusicInfo;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * 音乐跟随界面
 * Created by y on 2016/10/19.
 */
public class MusicActivity extends BaseActivity implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener, SeekBar.OnSeekBarChangeListener, MusicAdapter.Callback, OpenPopup {

    private static final String TAG = "music";

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
    private long updateUiTime;
    private long sendMassageTime;
    private long sendMassageTimeInterval;
    private MusicInfo currentMusicInfo;
    private long startTime = System.currentTimeMillis();
    private BleController ble = BleController.getInstance();
    private byte[] decibel = new byte[5];
    private static final int MSG_MASSAGE = 1;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_MASSAGE:
                    if (isFinishing() || currState != PAUSE || !ble.isDeviceReady()) {
                        return;
                    }
                    ble.musicMassageAsync(decibel);
                    sendEmptyMessageDelayed(MSG_MASSAGE, sendMassageTimeInterval);
                    break;
                default:
                    break;
            }
        }
    };
    private boolean massageStart;
    private BleCallback cb = new SimpleBleCallback() {
        @Override
        public void onGattDisconnected(BluetoothDevice device) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (currState == PAUSE && massageStart) {
                        stopPlay();
                    }
                }
            });
        }

        @Override
        public void onBluetoothOff() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (currState == PAUSE && massageStart) {
                        stopPlay();
                    }
                }
            });
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        activity = this;
        sendMassageTimeInterval = UserController.getInstance().getMusicTimeInterval(this);
        ble.addCallback(cb);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        setContentView(R.layout.activity_music);
        tv_currTime = (TextView) this.findViewById(R.id.tv_currTime);
        if (BuildConfig.TEST_MUSIC) {
            findViewById(R.id.back).setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    showTestMusicDialog();
                    return true;
                }
            });
        }
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
        popupWindow.setOpenPopup(this);
        iv_up.setOpenPopup(this);
        initMusic();
    }

    private void showTestMusicDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_test_music, null);
        final EditText editText = (EditText) view.findViewById(R.id.edit);
        editText.setText(String.valueOf(sendMassageTimeInterval));
        builder.setView(view);
        builder.setTitle("修改音乐模式时间间隔");
        builder.setNegativeButton(R.string.action_cancel, null);
        builder.setPositiveButton(R.string.action_confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sendMassageTimeInterval = Integer.parseInt(editText.getText().toString().trim());
                UserController.getInstance().setMusicTimeInterval(getApplicationContext(), sendMassageTimeInterval);
            }
        });
        builder.show();
    }

    private void initMusic() {
        new Thread(new Runnable() {
            public void run() {
                if (isFinishing()) {
                    return;
                }
                loader = MusicLoader.instance(context.getApplicationContext());
                loader.loadContentMusic();
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

    @Override
    public void closePopup() {
        if (popupWindow != null) {
            popupWindow.dismiss();
        }
    }

    private Handler hander = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case SEARCH_MUSIC_SUCCESS:
                    //搜索音乐文件结束时
                    popupWindow.setData(musics);
                    if (musics != null && musics.size() > 0) {
                        currentMusicInfo = musics.get(0);
                        refreshTitle();
                    }
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
        if (musics == null || musics.size() == 0) {
            return;
        }
        currentMusicInfo = musics.get(currIndex);
        if (currentMusicInfo != null) {
            mediaPlayer.reset();
            try {
                mediaPlayer.setDataSource(getApplicationContext(), currentMusicInfo.getUrl());
                mediaPlayer.prepare();
                mediaPlayer.start();
                initSeekBar();
                flag = true;
                updateSeek(0);
                tv_showName.setText(currentMusicInfo.getTitle());
                tv_artist.setText(currentMusicInfo.getArtist());
                btnPlay.setImageResource(R.drawable.music_pause);
                currState = PAUSE;
                popupWindow.setPlayIndex(currIndex, true);
                visualizer.setEnabled(true);
                if (ble.isDeviceReady() && !massageStart) {
                    massageStart = true;
                    startTime = System.currentTimeMillis();
                    // handler.sendEmptyMessageDelayed(MSG_MASSAGE, sendMassageTimeInterval);
                    ble.musicRandomMassageAsync();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Lg.e(TAG, "music_start", e);
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
                Message msg = hander.obtainMessage(CURR_TIME_VALUE, MusicLoader.toTime(mediaPlayer.getCurrentPosition()));
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
        tv_totalTime.setText(MusicLoader.toTime(mediaPlayer.getDuration()));
    }

    private void setupVisualizerFxAndUI() {
        try {
            visualizerView = (VisualizerView) findViewById(R.id.visualizer);
            visualizer = new Visualizer(mediaPlayer.getAudioSessionId());
            visualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
            visualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {

                private Random random = new Random();

                @Override
                public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {
                    if (SystemClock.elapsedRealtime() - updateUiTime > 200) {
                        updateUiTime = SystemClock.elapsedRealtime();
                        visualizerView.updateVisualizer(bytes);
                    }
                    int length = bytes.length;
                    decibel[0] = p(bytes[length - 5]);
                    decibel[1] = p(bytes[length - 4]);
                    decibel[2] = p(bytes[length - 3]);
                    decibel[3] = p(bytes[length - 2]);
                    decibel[4] = p(bytes[length - 1]);
                    if (decibel[0] == 0 && decibel[1] == 0 && decibel[2] == 0 && decibel[3] == 0 && decibel[4] == 0) {
                        if (mediaPlayer != null) {
                            long duration = mediaPlayer.getDuration();
                            int cp = mediaPlayer.getCurrentPosition();
                            // Log.i(TAG, "onWaveFormDataCapture " + duration + " " + cp);
                            if (cp > 3000 && duration - cp > 3000) {
                                decibel[0] = (byte) random.nextInt(30);
                                decibel[1] = (byte) random.nextInt(30);
                                decibel[2] = (byte) random.nextInt(30);
                                decibel[3] = (byte) random.nextInt(30);
                                decibel[4] = (byte) random.nextInt(30);
                            }
                        }
                    }
                    // Log.i(TAG, "onWaveFormDataCapture " + Arrays.toString(decibel));
                }

                private byte p(byte b) {
                    return (byte) Math.round(30f * (b + 128) / 256);
                }

                @Override
                public void onFftDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {
                }
            }, Visualizer.getMaxCaptureRate() / 2, true, false);
        } catch (Throwable e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public void onClickBack(View view) {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        massageStopAsync();
        ble.removeCallback(cb);
        hander.removeMessages(MSG_MASSAGE);
        currState = START;
        visualizer.setEnabled(false);
        visualizer.release();
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
                Lg.d(TAG, "first_click_start");
                start();
                break;
            case PAUSE:
                Lg.d(TAG, "click_pause");
                stopPlay();
                break;
            case START:
                Lg.d(TAG, "click_start");
                visualizer.setEnabled(true);
                mediaPlayer.start();
                btnPlay.setImageResource(R.drawable.music_pause);
                currState = PAUSE;
                flag = true;
                updateSeek(0);
                if (ble.isDeviceReady() && !massageStart) {
                    massageStart = true;
                    startTime = System.currentTimeMillis();
                    // handler.sendEmptyMessageDelayed(MSG_MASSAGE, sendMassageTimeInterval);
                    ble.musicRandomMassageAsync();
                }
                popupWindow.setPlayIndex(currIndex, true);
                break;
            default:
                break;
        }
    }

    private void stopPlay() {
        hander.removeMessages(MSG_MASSAGE);
        visualizer.setEnabled(false);
        mediaPlayer.pause();
        btnPlay.setImageResource(R.drawable.music_play);
        currState = START;
        popupWindow.setPlayIndex(currIndex, false);
        massageStopAsync();
    }

    //上一首
    private void previous() {
        if (musics != null && musics.size() > 0) {
            currIndex = (currIndex - 1 + musics.size()) % musics.size();
            currentMusicInfo = musics.get(currIndex);
            if (currState == PAUSE) {
                start();
            } else {
                refreshTitle();
            }
        }
    }

    //下一自首
    private void next() {
        if (musics != null && musics.size() > 0) {
            currIndex = (currIndex + 1) % musics.size();
            currentMusicInfo = musics.get(currIndex);
            if (currState == PAUSE) {
                start();
            } else {
                refreshTitle();
            }
        }
    }

    private void refreshTitle() {
        if (currentMusicInfo != null) {
            tv_currTime.setText("00:00");
            tv_totalTime.setText(MusicLoader.toTime(currentMusicInfo.getDuration()));
            tv_showName.setText(currentMusicInfo.getTitle());
            tv_artist.setText(currentMusicInfo.getArtist());
            mediaPlayer.reset();
            try {
                mediaPlayer.setDataSource(getApplicationContext(), currentMusicInfo.getUrl());
                mediaPlayer.prepare();
            } catch (Exception e) {
                // ignore
            }
            initSeekBar();
            popupWindow.setPlayIndex(currIndex, true);
        }
    }

    //监听器，当当前歌曲播放完时触发，播放下一首
    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        start();
//        if (musics != null && musics.size() > 0) {
//            next();
//        } else {
//            visualizer.setEnabled(false);
//            Toast.makeText(this, "播放列表为空", Toast.LENGTH_SHORT).show();
//        }
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        visualizer.setEnabled(false);
        massageStopAsync();
        mediaPlayer.reset();
        return false;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        //是否由用户改变
//        if (b && currState == PAUSE) {
//            mediaPlayer.seekTo(i);
//        }
        //是否由用户改变
        if (b) {
            Lg.d(TAG, "由用户改变进度条");
            mediaPlayer.seekTo(i);
            updateSeek(500);
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

    private void massageStopAsync() {
        ble.massageStopAsync();
        saveRecord();
    }

    private void saveRecord() {
        if (massageStart) {
            massageStart = false;
            long endTime = System.currentTimeMillis();
            String address = BleController.getInstance().getBindDeviceAddress();
            MassageController.getInstance().save(this, address, startTime, endTime);
        }
    }
}
