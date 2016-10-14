package com.emmanuel.wechatdemo.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.CamcorderProfile;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import java.nio.ByteBuffer;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

/**
 * Created by user on 2016/9/27.
 */
@TargetApi(Build.VERSION_CODES.M)
public class NewVideoRecordActivity extends BaseActivity implements View.OnClickListener, SurfaceHolder.Callback {

    private static final String TAG = "NewVideoRecordActivity";
    private static final boolean takePhoto = false; //true拍照 ， false录制视频
    private static final String VIDEO_PATH = "/sdcard/1.mp4"; //默认路径

    private SurfaceView surfaceView;
    private SurfaceHolder holder;
    private Button btnStart;
    private TextView tvTime;
    private ProgressBar progressBar;
    private ImageView ivResult;

    private static final String[] VIDEO_PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private CameraManager cameraManager;
    private CameraDevice cameraDevice;
    private CameraCaptureSession cameraCaptureSession;
    private CaptureRequest.Builder previewBuilder;
    private Handler handler;
    //LENS_FACING_FRONT:表示手机后置摄像头，LENS_FACING_BACK：表示前置摄像头
    private final String cameraId = "" + CameraCharacteristics.LENS_FACING_FRONT;

    /*** 拍照相关*/
    private ImageReader imageReader;
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    ///为了使照片竖直显示
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    /*** 录制视频相关*/
    private MediaRecorder mediaRecorder;
    private Size size;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_video_record);
        setTitle("视频录制");
        setTvRight1Text("完成");

        initImageReader();
        initCamera();
        initViews();

    }

    private void initImageReader() {
        imageReader = ImageReader.newInstance(1080, 1920, ImageFormat.JPEG,1);
        imageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() { //可以在这里处理拍照得到的临时照片 例如，写入本地
            @Override
            public void onImageAvailable(ImageReader reader) {
                cameraDevice.close();
                // 拿到拍照照片数据
                Image image = reader.acquireNextImage();
                ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                byte[] bytes = new byte[buffer.remaining()];
                buffer.get(bytes);//由缓冲区存入字节数组
                final Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                if (bitmap != null) {
                    ivResult.setImageBitmap(bitmap);
                }
            }
        }, new Handler(getMainLooper()));
    }

    private void initViews() {
        surfaceView = (SurfaceView) findViewById(R.id.camera_view);
        holder = surfaceView.getHolder();
        holder.addCallback(this);

        btnStart = (Button) findViewById(R.id.btn_start);
        btnStart.setOnClickListener(this);
        tvTime = (TextView) findViewById(R.id.tv_time);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        ivResult = (ImageView)findViewById(R.id.iv_result);
    }

    private void initCamera() {
        cameraManager = (CameraManager) this.getSystemService(Context.CAMERA_SERVICE);
        //camera2是异步的需要新建个线程。
        HandlerThread handlerThread = new HandlerThread("Camera2");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        openCamera();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_start:
                if(takePhoto)
                    takePicture();
                else
                    recordVideo();
                break;
        }
    }

    private void openCamera() {
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            cameraManager.openCamera(cameraId, DeviceStateCallback, handler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private CameraDevice.StateCallback DeviceStateCallback = new CameraDevice.StateCallback() {

        @Override
        public void onOpened(CameraDevice camera) {
            LogUtil.logD(TAG, "onOpened");
            cameraDevice = camera;
            createCameraCaptureSession();
        }

        @Override
        public void onDisconnected(CameraDevice cameraDevice) {
            LogUtil.logD(TAG, "onDisconnected");
        }

        @Override
        public void onError(CameraDevice cameraDevice, int i) {

        }
    };

    private void createCameraCaptureSession() {
        try {
            //TEMPLATE_RECORD为录制模板，TEMPLATE_STILL_CAPTURE拍照模板
            if(takePhoto)
                previewBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            else
                previewBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
            previewBuilder.addTarget(holder.getSurface());
            cameraDevice.createCaptureSession(Arrays.asList(holder.getSurface(), imageReader.getSurface()), mSessionPreviewStateCallback, handler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }

    private CameraCaptureSession.StateCallback mSessionPreviewStateCallback = new CameraCaptureSession.StateCallback() {

        @Override
        public void onConfigured(CameraCaptureSession session) {
            cameraCaptureSession = session;
            try {
                previewBuilder.set(CaptureRequest.CONTROL_AF_MODE, CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                previewBuilder.set(CaptureRequest.CONTROL_AE_MODE, CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
                cameraCaptureSession.setRepeatingRequest(previewBuilder.build(), mSessionCaptureCallback, handler);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {

        }
    };

    private CameraCaptureSession.CaptureCallback mSessionCaptureCallback = new CameraCaptureSession.CaptureCallback() {

        @Override
        public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
            cameraCaptureSession = session;
        }

        @Override
        public void onCaptureProgressed(CameraCaptureSession session, CaptureRequest request, CaptureResult partialResult) {
            cameraCaptureSession = session;
        }

    };

    private boolean hasPermissionsGranted(String[] permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }


    /**
     * 比较容易出现如下错误，之前我也遇到这个，主要由the requests target Surfaces not currently configured as outputs引起
     * IllegalArgumentException
     * If the requests target no Surfaces,
     * or the requests target Surfaces not currently configured as outputs;
     * or one of the requests targets a set of Surfaces that cannot be submitted simultaneously in a reprocessable capture session;
     * or a reprocess capture request is submitted in a non-reprocessable capture session;
     * or one of the reprocess capture requests was created with a TotalCaptureResult from a different session;
     * or one of the captures targets a Surface in the middle of being prepared;
     * or if the handler is null, the listener is not null, and the calling thread has no looper.
     */
    private void takePicture() {
        if (cameraDevice == null)
            return;
        // 创建拍照需要的CaptureRequest.Builder
        final CaptureRequest.Builder captureRequestBuilder;
        try {
            captureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            // 将imageReader的surface作为CaptureRequest.Builder的目标
            captureRequestBuilder.addTarget(imageReader.getSurface());
            captureRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                    CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);

            // Orientation
            int rotation = getWindowManager().getDefaultDisplay().getRotation();
            captureRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));
            CaptureRequest mCaptureRequest = captureRequestBuilder.build();
            CameraCaptureSession.CaptureCallback mCaptureCallback
                    = new CameraCaptureSession.CaptureCallback() {
                @Override
                public void onCaptureCompleted(CameraCaptureSession session,
                                               CaptureRequest request,
                                               TotalCaptureResult result) {
                }
            };
            cameraCaptureSession.stopRepeating();  //停止画面捕捉
            cameraCaptureSession.capture(mCaptureRequest, mCaptureCallback, handler); //拍照
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void recordVideo() {
        startRecordingVideo();
    }

    private void setUpMediaRecorder() throws IOException {
        mediaRecorder = new MediaRecorder();
        CameraCharacteristics characteristics = null;
        try {
            characteristics = cameraManager.getCameraCharacteristics(cameraId);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        if(characteristics == null)
            return;
        StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        if (TextUtils.isEmpty(VIDEO_PATH)) {
            return;
        }
        mediaRecorder.setOutputFile(VIDEO_PATH);
        mediaRecorder.setVideoEncodingBitRate(10000000);
        mediaRecorder.setVideoFrameRate(30);
        size = chooseVideoSize(map.getOutputSizes(MediaRecorder.class));
        mediaRecorder.setVideoSize(size.getWidth(), size.getHeight());
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        mediaRecorder.setOrientationHint(ORIENTATIONS.get(rotation));
        mediaRecorder.prepare();
    }

    private static Size chooseVideoSize(Size[] choices) {
        for (Size size : choices) {
            if (size.getWidth() == size.getHeight() * 4 / 3 && size.getWidth() <= 1080) {
                return size;
            }
        }
        LogUtil.logE(TAG, "Couldn't find any suitable video size");
        return choices[choices.length - 1];
    }

    private void startRecordingVideo() {
        if (null == cameraDevice) {
            return;
        }
        try {
            setUpMediaRecorder();
            previewBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_RECORD);
            List<Surface> surfaces = new ArrayList<>();

            // Set up Surface for the camera preview
            surfaces.add(holder.getSurface());
            previewBuilder.addTarget(holder.getSurface());

            // Set up Surface for the MediaRecorder
            Surface mRecorderSurface = mediaRecorder.getSurface();
            surfaces.add(mRecorderSurface);
            previewBuilder.addTarget(mRecorderSurface);

            // Start a capture session
            // Once the session starts, we can update the UI and start recording
            cameraDevice.createCaptureSession(surfaces, new CameraCaptureSession.StateCallback() {
                @Override
                public void onConfigured(CameraCaptureSession captureSession) {
                    cameraCaptureSession = captureSession;
                    NewVideoRecordActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mediaRecorder.start();
                        }
                    });
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {

                }
            }, handler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void closeCamera() {
        closePreviewSession();
        if (null != cameraDevice) {
            cameraDevice.close();
            cameraDevice = null;
        }
        if (null != mediaRecorder) {
            mediaRecorder.release();
            mediaRecorder = null;
        }
    }

    private void closePreviewSession() {
        if (cameraCaptureSession != null) {
            cameraCaptureSession.close();
            cameraCaptureSession = null;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        closeCamera();
    }
}
