package com.emmanuel.wechatdemo.activity;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.emmanuel.wechatdemo.R;
import com.emmanuel.wechatdemo.adapter.FriendsAdapter;
import com.emmanuel.wechatdemo.bean.ShuoShuo;
import com.emmanuel.wechatdemo.util.DataFactory;
import com.emmanuel.wechatdemo.view.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class FriendsActivity extends AppCompatActivity {

    private static final String TAG = "FriendsActivity";
    private RecyclerView recyclerView;
    private FriendsAdapter friendsAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<ShuoShuo>listSS;

    private boolean isLoadMore = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        initView();
        updata();
    }

    private void initView() {
        recyclerView = (RecyclerView)findViewById(R.id.recyclerview_friends);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this ,  LinearLayoutManager.VERTICAL));
        friendsAdapter = new FriendsAdapter(this);
        recyclerView.setAdapter(friendsAdapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int lastVisibleItemPosition = ((LinearLayoutManager)layoutManager).findLastVisibleItemPosition();
                if(lastVisibleItemPosition >= friendsAdapter.getItemCount()-1  && !isLoadMore){
                    isLoadMore = true;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            loadMore();
                        }
                    }, 3000);
                }
            }
        });
    }

    private void updata(){
        listSS = new ArrayList<>();
        listSS.addAll(DataFactory.createComment(10));//随机产生10条说说
        friendsAdapter.setDatas(listSS);
    }

    private void loadMore(){
        listSS.addAll(DataFactory.createComment(10));//随机产生10条说说
        friendsAdapter.notifyDataSetChanged();
        isLoadMore = false;
    }
}
