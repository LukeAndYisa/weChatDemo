package com.emmanuel.wechatdemo.activity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AbsListView;
import android.widget.TextView;

import com.emmanuel.wechatdemo.R;
import com.emmanuel.wechatdemo.adapter.PictureAdapter;
import com.emmanuel.wechatdemo.bean.Picture;
import com.emmanuel.wechatdemo.event.BrowsePictureEvent;
import com.emmanuel.wechatdemo.event.DefaultEvent;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by user on 2016/9/9.
 */
public class PhotoSelectActivity extends BaseActivity implements View.OnClickListener{

    private RecyclerView recyclerView;
    private PictureAdapter adapter;
    private List<Picture> listPic;

    private TextView tvLocalPic, tvLookUp;
    private GridLayoutManager layoutManager;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_photo_select);
        EventBus.getDefault().register(this);

        setTitle(getString(R.string.title_pic_select));
        setTvRight1Text(getString(R.string.text_done));
        setLeftBtnVisibility(View.VISIBLE);

        initViews();
    }

    private void initViews() {
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        layoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == AbsListView.OnScrollListener.SCROLL_STATE_FLING){
                    ImageLoader.getInstance().pause();
                } else if(newState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE){
                    ImageLoader.getInstance().resume();
                } else {
                    ImageLoader.getInstance().pause();
                }
            }
        });

        tvLocalPic = (TextView) findViewById(R.id.tv_local_picture);
        tvLookUp = (TextView) findViewById(R.id.tv_look_up);

        tvLocalPic.setOnClickListener(this);
        tvLookUp.setOnClickListener(this);
        findViewById(R.id.tv_look_up).setOnClickListener(this);

        initData();
    }

    private void initData() {
        listPic = new ArrayList<>();
        adapter = new PictureAdapter(this, listPic, new PictureAdapter.OnSelectedListener() {
            @Override
            public void onSelected(int position) {
                if(adapter.getSelectedPicPosition().size() == 0)
                    setTvRight1Text(getString(R.string.text_done));
                else
                    setTvRight1Text("(" + adapter.getSelectedPicPosition().size() + "/9)完成");
            }

            @Override
            public void onRemove(int position) {
                if(adapter.getSelectedPicPosition().size() == 0)
                    setTvRight1Text(getString(R.string.text_done));
                else
                    setTvRight1Text("(" + adapter.getSelectedPicPosition().size() + "/9)完成");
            }
        });
        recyclerView.setAdapter(adapter);
        new LoadLocalImageTask().execute();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_local_picture:
                showPicPopWin();
                break;
            case R.id.tv_look_up:
                if(adapter.getSelectedPicPosition().size() <= 0)
                    return;
                onLookUp();
                break;
        }
    }

    private void showPicPopWin() {

    }

    private void onLookUp() {
        List<Integer> listPos = adapter.getSelectedPicPosition();
        Collections.sort(listPos, new Comparator<Integer>() {
            @Override
            public int compare(Integer t1, Integer t2) {
                if(t1 < t2)
                    return -1;
                else if(t1 == t2)
                    return 0;
                else
                    return 1;
            }
        });
        ArrayList<Picture>listSelectedPic = new ArrayList<>();

        for(int i=0; i<listPos.size(); i++){
            listSelectedPic.add(listPic.get(listPos.get(i)));
        }
        Intent intent = new Intent(PhotoSelectActivity.this, BrowsePictureActivity.class);
        intent.putExtra("selectedPictures", listSelectedPic);
        startActivity(intent);

    }

    @Override
    protected void onTextRight1() {
        super.onTextRight1();

    }

    public void onEventMainThread(DefaultEvent event){
        int type = event.getIntType();
        if(type == DefaultEvent.CLOSE_ACTIVITY)
            this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 通过ContentResolver去获取本地所有PNG JPEG的图片，
     * 图片大小必须大于100kb，否则会找出很多很小的图标，
     * 并且按照图片时间先后顺序排序
     */
    class LoadLocalImageTask extends AsyncTask<Object, Object, Object> {

        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(listPic == null)
                listPic = new ArrayList<>();
            dialog = new ProgressDialog(PhotoSelectActivity.this);
            dialog.setMessage("加载中...");
            dialog.show();
        }

        @Override
        protected Object doInBackground(Object... objects) {
            ContentResolver contentResolver = PhotoSelectActivity.this.getContentResolver();
            String[] projection = {
                    MediaStore.Images.Media._ID
            };
            Uri ext_uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            String where = "(" + MediaStore.Images.Media.MIME_TYPE
                    + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?)"
                    + " and " + MediaStore.Images.Media.SIZE + ">=?";
            Cursor c = MediaStore.Images.Media.query(
                    contentResolver,
                    ext_uri,
                    projection,
                    where,
                    new String[] { "image/jpeg", "image/png", "102400" },
                    MediaStore.Images.Media.DATE_MODIFIED + " desc");

            int columnIndex = c.getColumnIndexOrThrow(MediaStore.Images.Media._ID);

            int i = 0;
            while (c.moveToNext() && i < c.getCount()) {
                long origId = c.getLong(columnIndex);
                Picture picture = new Picture();
                picture.uri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, origId + "").toString();
                listPic.add(picture);
                i++;
            }
            c.close();
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            if(dialog != null && dialog.isShowing()){
                dialog.dismiss();
            }
            adapter.notifyDataSetChanged();
        }
    }
}
