package com.emmanuel.wechatdemo.activity;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.emmanuel.wechatdemo.R;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by user on 2016/9/22.
 */
public class VideoRecordActivity extends Activity implements View.OnClickListener, SurfaceHolder.Callback {

    private MediaRecorder recorder;
    private SurfaceHolder holder;
    private boolean recording = false;
    private Button btnStart;
    private TextView tvTime;

    private Timer timer;
    private int time = 0;
    private boolean isPrepare = false;
    private Camera camera;

    private Handler timeHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            time += 1;
            tvTime.setText(time + "秒");
            if(time >= 10)
                stopRecord();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_video_record);
        recorder = new MediaRecorder();
        initRecorder();
        initViews();
    }

    private void initViews() {
        SurfaceView cameraView = (SurfaceView) findViewById(R.id.camera_view);
        holder = cameraView.getHolder();
        holder.addCallback(this);
//        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        btnStart = (Button)findViewById(R.id.btn_start);
        btnStart.setOnClickListener(this);
        tvTime = (TextView)findViewById(R.id.tv_time);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(getRequestedOrientation()!=ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    private void initRecorder() {
        camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
        if (camera != null) {
            camera.setDisplayOrientation(90);//摄像图旋转90度
            camera.unlock();
            recorder.setCamera(camera);
        }
        recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        recorder.setOrientationHint(90); //旋转90度

        CamcorderProfile cpHigh = CamcorderProfile.get(CamcorderProfile.QUALITY_480P); //480的清晰度
        recorder.setProfile(cpHigh);
        recorder.setOutputFile("/sdcard/videocapture_example.mp4");
        recorder.setMaxDuration(10000); // 10 seconds
        recorder.setMaxFileSize(5000000); // Approximately 5 megabytes

    }

    private void prepareRecorder() {
        recorder.setPreviewDisplay(holder.getSurface());
        try {
            recorder.prepare();
            isPrepare = true;
        } catch (IllegalStateException e) {
            e.printStackTrace();
            finish();
        } catch (IOException e) {
            e.printStackTrace();
            finish();
        }
    }

    public void onClick(View v) {
        if(v.getId() == R.id.btn_start) {
            if (recording) {
                stopRecord();
            } else {
                startRecord();
            }
        }
    }

    private void startRecord(){
        if(isPrepare){
            recording = true;
            recorder.start();
            btnStart.setText("stop");
            startTimerCount();
        }
    }

    private void stopRecord(){
        recorder.stop();
        recording = false;
        prepareRecorder();
        btnStart.setText("start");
        stopTimerCount();
    }

    public void surfaceCreated(SurfaceHolder holder) {
        prepareRecorder();
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        if (recording) {
            recorder.stop();
            recording = false;
        }
        recorder.reset();
        recorder.release();
        finish();
    }

    //时间计数
    private void startTimerCount() {
        if(timer == null) {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                public void run() {
                    timeHandler.sendEmptyMessage(1);
                }
            }, 1000, 1000);
        }
    }

    private void stopTimerCount(){
        if (timer != null) {
            timer.cancel();
            timer = null;
            time = 0;
        }
    }
}
