package com.emmanuel.wechatdemo.activity;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;

import com.emmanuel.wechatdemo.App;
import com.emmanuel.wechatdemo.R;
import com.emmanuel.wechatdemo.adapter.PictureAdapter;
import com.emmanuel.wechatdemo.bean.Picture;
import com.emmanuel.wechatdemo.bean.ShuoShuo;
import com.emmanuel.wechatdemo.event.ShuoShuoPushEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2016/9/12.
 */
public class PushShuoActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private GridLayoutManager layoutManager;
    private PictureAdapter adapter;

    private List<Picture> listPic;
    private EditText etShuo;

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_push_shuoshuo);

        setTitle(getString(R.string.title_push_shuo));
        setLeftBtnVisibility(View.VISIBLE);
        setTvRight1Text(getString(R.string.send));

        initViews();
        initData();
    }

    private void initViews() {
        etShuo = (EditText)findViewById(R.id.et_shuoshuo);
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        layoutManager = new GridLayoutManager(this, 5);
        recyclerView.setLayoutManager(layoutManager);

    }

    private void initData() {
        listPic = (ArrayList<Picture>)getIntent().getSerializableExtra("selectedPictures");
        if(listPic != null) {
            adapter = new PictureAdapter(this, listPic, null, 2);
            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    protected void onTextRight1() {
        super.onTextRight1();
        ShuoShuo shuoShuo = new ShuoShuo();
        shuoShuo.user = App.getInstance().getUser();
        shuoShuo.picList = listPic;
        shuoShuo.content = etShuo.getText().toString();
        EventBus.getDefault().post(new ShuoShuoPushEvent(shuoShuo));
        finish();
    }
}
