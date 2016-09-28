package com.emmanuel.wechatdemo.activity;

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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.emmanuel.wechatdemo.App;
import com.emmanuel.wechatdemo.R;
import com.emmanuel.wechatdemo.bean.ShuoShuo;
import com.emmanuel.wechatdemo.event.ShuoShuoPushEvent;
import com.emmanuel.wechatdemo.util.ToastUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by user on 2016/9/22.
 */
public class VideoRecordActivity extends BaseActivity implements View.OnClickListener, SurfaceHolder.Callback {

    private MediaRecorder recorder;
    private SurfaceHolder holder;
    private boolean recording = false;
    private Button btnStart;
    private TextView tvTime;
    private ProgressBar progressBar;

    private Timer timer;
    private int time = 0;
    private boolean isPrepare = false;
    private Camera camera;

    final String VIDEO_PATH = "/sdcard/1.mp4";
    private boolean hasSave = false;

    private Handler timeHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            time += 1;
            progressBar.setProgress(time);
            tvTime.setText(time + "秒");
            if(time >= 10)
                stopRecord();
        }
    };

    @Override
    protected void initView(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_video_record);
        setTitle("视频录制");
        setTvRight1Text("完成");

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
        progressBar = (ProgressBar)findViewById(R.id.progress_bar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(getRequestedOrientation()!=ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    private void initRecorder() {
        recorder = new MediaRecorder();
        camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
        if (camera != null) {
            camera.setDisplayOrientation(90);//摄像图旋转90度
            camera.unlock();
            recorder.setCamera(camera);
        }
        recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        recorder.setOrientationHint(90); //旋转90度
        //480的清晰度，相当于高清视频与标清视频之间水准，10秒大概5mb左右
        CamcorderProfile cpHigh = CamcorderProfile.get(CamcorderProfile.QUALITY_480P);
        recorder.setProfile(cpHigh);
        recorder.setOutputFile(VIDEO_PATH); //文件名先随便写吧，毕竟只是demo
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
        hasSave = true;
//        prepareRecorder();
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
            progressBar.setProgress(time);
        }
    }

    @Override
    protected void onTextRight1() {
        super.onTextRight1();
        if(!hasSave && !recording) {
            ToastUtil.showMessage("视频未保存", Toast.LENGTH_SHORT, true);
            return;
        }
        ShuoShuo shuoShuo = new ShuoShuo();
        shuoShuo.content = "视频测试";
        shuoShuo.videoPath = VIDEO_PATH;
        shuoShuo.user = App.getUser();
        shuoShuo.picList = new ArrayList<>();
        shuoShuo.commentList = new ArrayList<>();
        EventBus.getDefault().post(new ShuoShuoPushEvent(shuoShuo));
        this.finish();
    }
}
