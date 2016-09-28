package com.emmanuel.wechatdemo.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
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
import com.emmanuel.wechatdemo.util.LogUtil;
import com.emmanuel.wechatdemo.util.ToastUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

/**
 * Created by user on 2016/9/27.
 */
@TargetApi(Build.VERSION_CODES.M)
public class NewVideoRecordActivity extends BaseActivity implements View.OnClickListener, SurfaceHolder.Callback {

    private static final String TAG = "NewVideoRecordActivity";

    final String VIDEO_PATH = "/sdcard/1.mp4";
    private MediaRecorder recorder;
    private SurfaceHolder holder;
    SurfaceView cameraView;
    private boolean recording = false;
    private boolean isPrepare = false;
    private Button btnStart;
    private TextView tvTime;
    private ProgressBar progressBar;
    private Integer sensorOrientation;
    private CameraDevice cameraDevice;
    private CameraManager cameraManager;
    private CameraCaptureSession mSession;
    private Handler handler;
    private String cameraId;

    private int mState;
    private final int STATE_WAITING_CAPTURE = 1001;
    private final int STATE_PREVIEW = 1000;

    private Timer timer;
    private int time = 0;
    private boolean hasSave = false;

    /**
     * A {@link Semaphore} to prevent the app from exiting before closing the camera.
     */
    private Semaphore mCameraOpenCloseLock = new Semaphore(1);
    private CaptureRequest.Builder mPreviewBuilder;

    private static final String[] VIDEO_PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

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
        initCamera();
        if(hasPermissionsGranted(VIDEO_PERMISSIONS)){
            initRecorder();
        } else {
            ToastUtil.showMessage("没有足够权限，请先设置", Toast.LENGTH_SHORT, true);
            finish();
        }
        initViews();
    }

    private void initViews() {
        cameraView = (SurfaceView) findViewById(R.id.camera_view);
        holder = cameraView.getHolder();
        holder.addCallback(this);

        btnStart = (Button) findViewById(R.id.btn_start);
        btnStart.setOnClickListener(this);
        tvTime = (TextView) findViewById(R.id.tv_time);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
    }

    private void initCamera() {
        cameraManager = (CameraManager) this.getSystemService(Context.CAMERA_SERVICE);
    }

    private void initRecorder() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
//        recorder.setOrientationHint(90); //旋转90度
        recorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setVideoSize(320, 240);
        recorder.setVideoFrameRate(15);
        recorder.setOutputFile(VIDEO_PATH);
        recorder.setMaxDuration(10000); // 10 seconds
        recorder.setMaxFileSize(5000000); // Approximately 5 megabytes
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        recorder.setPreviewDisplay(holder.getSurface());
        try {
            recorder.prepare();
            isPrepare = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        initCameraAndPreview();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
//        configureTransform(width, height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btn_start){
            if (recording) {
                stopRecord();
            } else {
                startRecord();
            }
        }
    }

    private boolean hasPermissionsGranted(String[] permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void initCameraAndPreview() {
        HandlerThread handlerThread = new HandlerThread("Camera2");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
        try {
            cameraId = "" + CameraCharacteristics.LENS_FACING_FRONT; //前置或后置摄像头
//            mImageReader = ImageReader.newInstance(cameraView.getWidth(), cameraView.getHeight(),
//                    ImageFormat.JPEG,/*maxImages*/7);
//            mImageReader.setOnImageAvailableListener(mOnImageAvailableListener, mHandler);

            if (!hasPermissionsGranted(VIDEO_PERMISSIONS)) {
                return;
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            cameraManager.openCamera(cameraId, DeviceStateCallback, handler);
        } catch (CameraAccessException e) {
            LogUtil.logE(TAG, "open camera failed." + e.getMessage());
        }
    }

    private CameraDevice.StateCallback DeviceStateCallback = new CameraDevice.StateCallback() {

        @Override
        public void onOpened(CameraDevice camera) {
            LogUtil.logD(TAG,"DeviceStateCallback:camera was opend.");
            mCameraOpenCloseLock.release();
            cameraDevice = camera;
            createCameraCaptureSession();
        }

        @Override
        public void onDisconnected(CameraDevice cameraDevice) {

        }

        @Override
        public void onError(CameraDevice cameraDevice, int i) {

        }
    };

    private void createCameraCaptureSession() {
        LogUtil.logD(TAG,"createCameraCaptureSession");
        try {
            mPreviewBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mPreviewBuilder.addTarget(holder.getSurface());
            mState = STATE_PREVIEW;
            cameraDevice.createCaptureSession(Arrays.asList(holder.getSurface()), mSessionPreviewStateCallback, handler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }

    private CameraCaptureSession.StateCallback mSessionPreviewStateCallback = new CameraCaptureSession.StateCallback() {

        @Override
        public void onConfigured(CameraCaptureSession session) {
            LogUtil.logD(TAG,"mSessionPreviewStateCallback onConfigured");
            mSession = session;
            try {
                mPreviewBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                        CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                mPreviewBuilder.set(CaptureRequest.CONTROL_AE_MODE,
                        CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
                mSession.setRepeatingRequest(mPreviewBuilder.build(), mSessionCaptureCallback, handler);
            } catch (CameraAccessException e) {
                e.printStackTrace();
                LogUtil.logE(TAG, "set preview builder failed."+e.getMessage());
            }
        }

        @Override
        public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {

        }
    };

    private CameraCaptureSession.CaptureCallback mSessionCaptureCallback = new CameraCaptureSession.CaptureCallback() {

        @Override
        public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
            mSession = session;
            checkState(result);
        }

        @Override
        public void onCaptureProgressed(CameraCaptureSession session, CaptureRequest request, CaptureResult partialResult) {
            LogUtil.logD(TAG,"mSessionCaptureCallback,  onCaptureProgressed");
            mSession = session;
            checkState(partialResult);
        }

    };

    private void checkState(CaptureResult result) {
        switch (mState) {
            case STATE_PREVIEW:
                // NOTHING
                break;
            case STATE_WAITING_CAPTURE:
                int afState = result.get(CaptureResult.CONTROL_AF_STATE);

                if (CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED == afState ||
                        CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED == afState
                        ||  CaptureResult.CONTROL_AF_STATE_PASSIVE_FOCUSED == afState
                        || CaptureResult.CONTROL_AF_STATE_PASSIVE_UNFOCUSED == afState) {
                    //do something like save picture
                }
                break;
        }
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
