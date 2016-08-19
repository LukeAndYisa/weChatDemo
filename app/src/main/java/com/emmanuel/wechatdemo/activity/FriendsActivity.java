package com.emmanuel.wechatdemo.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;

import com.emmanuel.wechatdemo.R;
import com.emmanuel.wechatdemo.adapter.FriendsAdapter;
import com.emmanuel.wechatdemo.bean.Comment;
import com.emmanuel.wechatdemo.bean.ShuoShuo;
import com.emmanuel.wechatdemo.util.DataFactory;
import com.emmanuel.wechatdemo.view.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class FriendsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FriendsAdapter friendsAdapter;
    private List<ShuoShuo>listSS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        initView();
        updata();
    }

    private void initView() {
        recyclerView = (RecyclerView)findViewById(R.id.recyclerview_friends);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this ,  LinearLayoutManager.VERTICAL));
        friendsAdapter = new FriendsAdapter(this);
        recyclerView.setAdapter(friendsAdapter);
    }

    private void updata(){
        listSS = new ArrayList<>();
        listSS.addAll(DataFactory.createComment(10));//随机产生10条说说
        friendsAdapter.setDatas(listSS);
    }
}
