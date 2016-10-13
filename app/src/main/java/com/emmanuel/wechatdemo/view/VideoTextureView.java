package com.emmanuel.wechatdemo.view;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.TextureView;
import android.widget.ImageView;
import android.widget.Toast;

import com.emmanuel.wechatdemo.App;
import com.emmanuel.wechatdemo.R;
import com.emmanuel.wechatdemo.util.ToastUtil;

import java.io.File;

/**
 * Created by user on 2016/9/23.
 */
public class VideoTextureView extends TextureView implements TextureView.SurfaceTextureListener {

    private MediaPlayer mediaPlayer;
    private Surface surface;
    private ImageView ivTip;
    private boolean isPlaying = false;
    private boolean isSurfaceTextureAvailable = false;
    private String videoPath;

    public VideoTextureView(Context context) {
        super(context);
        init(context);
    }

    public VideoTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public VideoTextureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setSurfaceTextureListener(this);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i1) {
        surface = new Surface(surfaceTexture);
        isSurfaceTextureAvailable = true;
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i1) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        surface=null;
        onVideoTextureViewDestroy();
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

    }

    public void startMediaPlayer(){
        if(isPlaying || !isSurfaceTextureAvailable)
            return;
        if(TextUtils.isEmpty(videoPath)){
            ToastUtil.showMessage("视频路径异常", Toast.LENGTH_SHORT, true);
            return;
        }
        try {
            if(videoPath.equals(App.videoPath))
                mediaPlayer = MediaPlayer.create(getContext(), R.raw.test);
            else {
                final File file = new File(videoPath);
                if (!file.exists()) {//文件不存在
                    ToastUtil.showMessage("视频不存在", Toast.LENGTH_SHORT, true);
                    return;
                }
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(file.getAbsolutePath());
            }
            mediaPlayer.setSurface(surface);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setVolume(0, 0); //设置左右音道的声音为0
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp){
                    mediaPlayer.start();
                    showIvTip(false);
                    isPlaying = true;
                }
            });
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    stopMediaPlayer();
                }
            });
            mediaPlayer.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showIvTip(boolean show){
        if(ivTip != null){
            int visibility = show?VISIBLE : GONE;
            ivTip.setVisibility(visibility);
        }
    }

    public void stopMediaPlayer(){
        if(mediaPlayer != null && isPlaying) {
            mediaPlayer.stop();
            mediaPlayer.reset();
        }
        showIvTip(true);
        isPlaying = false;
    }

    public void setIvTip(ImageView ivTip){
        this.ivTip = ivTip;
    }

    public boolean getPlayStatus(){
        return isPlaying;
    }

    public void setVideoPath(String path ){
        videoPath = path;
    }

    public void onVideoTextureViewDestroy(){
        isPlaying = false;
        if(mediaPlayer != null){
            stopMediaPlayer();
            mediaPlayer.release();
        }
    }
}
