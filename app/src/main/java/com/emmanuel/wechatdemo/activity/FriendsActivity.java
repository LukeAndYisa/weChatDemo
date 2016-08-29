package com.emmanuel.wechatdemo.activity;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.emmanuel.wechatdemo.App;
import com.emmanuel.wechatdemo.R;
import com.emmanuel.wechatdemo.adapter.FriendsAdapter;
import com.emmanuel.wechatdemo.adapter.RecycleViewItemListener;
import com.emmanuel.wechatdemo.bean.Comment;
import com.emmanuel.wechatdemo.bean.ShuoShuo;
import com.emmanuel.wechatdemo.util.DataFactory;
import com.emmanuel.wechatdemo.util.Helper;
import com.emmanuel.wechatdemo.util.UserUtil;
import com.emmanuel.wechatdemo.view.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class FriendsActivity extends BaseActivity implements View.OnClickListener{

    private static final String TAG = "FriendsActivity";
    private boolean keyboardShowFlag = false;

    private RecyclerView recyclerView;
    private FriendsAdapter friendsAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<ShuoShuo>listSS;

    private boolean isLoadMore = false;
    private ImageView ivMore;
    private EditText etInput;
    private LinearLayout layoutInput;
    private TextView tvSend, tvVoice;

    private int commentPosition = -1; //评论的说说id

    private RecycleViewItemListener listener = new RecycleViewItemListener() {
        @Override
        public void onItemClick(int position) {

        }

        @Override
        public void onCommentClick(int position) {
            layoutInput.setVisibility(View.VISIBLE);
            etInput.setFocusableInTouchMode(true);
            etInput.setFocusable(true);
            etInput.requestFocus();
            InputMethodManager inputManager = (InputMethodManager)etInput.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.showSoftInput(etInput, 0);
            commentPosition = position;
        }
    };

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_friends);
        initViews();
        updata();
    }

    private void initViews() {
        recyclerView = (RecyclerView)findViewById(R.id.recyclerview_friends);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,  LinearLayoutManager.VERTICAL));
        friendsAdapter = new FriendsAdapter(this);
        friendsAdapter.setItemListener(listener);
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
        initKeyBoard();
        addKeyBoardListener();
    }

    private void initKeyBoard() {
        ivMore = (ImageView)findViewById(R.id.iv_more);
        etInput = (EditText)findViewById(R.id.et_input);
        etInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String content = etInput.getText().toString();
                if(content != null && content.length() > 0){
                    tvSend.setVisibility(View.VISIBLE);
                    ivMore.setVisibility(View.GONE);
                }else {
                    tvSend.setVisibility(View.GONE);
                    ivMore.setVisibility(View.VISIBLE);
                }
            }
        });
        layoutInput = (LinearLayout)findViewById(R.id.layout_input);
        tvSend = (TextView)findViewById(R.id.tv_send);
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

    private ViewTreeObserver.OnGlobalLayoutListener layoutChangeListener = new ViewTreeObserver.OnGlobalLayoutListener() {

        @Override
        public void onGlobalLayout() {
            //判断窗口可见区域大小
            Rect r = new Rect();
            getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
            int screenHeight = Helper.getScreenHeight(FriendsActivity.this);
            //如果屏幕高度和Window可见区域高度差值大于整个屏幕高度的1/3，则表示软键盘显示中，否则软键盘为隐藏状态。
            int heightDifference = screenHeight - (r.bottom - r.top);
            boolean isKeyboardShowing = heightDifference > screenHeight/3;

            //如果之前软键盘状态为显示，现在为关闭，或者之前为关闭，现在为显示，则表示软键盘的状态发生了改变
            if ((keyboardShowFlag && !isKeyboardShowing) || (!keyboardShowFlag && isKeyboardShowing)) {
                if(keyboardShowFlag){
                    layoutInput.setVisibility(View.GONE);
                }
                keyboardShowFlag = isKeyboardShowing;
            }
        }

    };

    private void addKeyBoardListener(){
        //注册布局变化监听
        getWindow().getDecorView().getViewTreeObserver().addOnGlobalLayoutListener(layoutChangeListener);
    }

    private void removeKeyBoardListener(){
        //移除布局变化监听
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            getWindow().getDecorView().getViewTreeObserver().removeOnGlobalLayoutListener(layoutChangeListener);
        } else {
            getWindow().getDecorView().getViewTreeObserver().removeGlobalOnLayoutListener(layoutChangeListener);
        }
    }

    @Override
    protected void onDestroy() {
        removeKeyBoardListener();
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_send:
                String content = etInput.getText().toString().trim();
                if(commentPosition >=0 && commentPosition < listSS.size() && content != null && content.length() > 0){
                    Comment comment = new Comment();
                    comment.content = content;
                    comment.fromUser = App.getInstance().getUser();
                    listSS.get(commentPosition).commentList.add(new Comment());
                    friendsAdapter.notifyDataSetChanged();
                }
                break;
        }
    }
}
