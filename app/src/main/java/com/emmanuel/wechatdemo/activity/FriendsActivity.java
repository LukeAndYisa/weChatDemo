package com.emmanuel.wechatdemo.activity;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.emmanuel.wechatdemo.App;
import com.emmanuel.wechatdemo.R;
import com.emmanuel.wechatdemo.adapter.FriendsAdapter;
import com.emmanuel.wechatdemo.bean.Comment;
import com.emmanuel.wechatdemo.bean.ShuoShuo;
import com.emmanuel.wechatdemo.bean.User;
import com.emmanuel.wechatdemo.event.ShuoShuoPushEvent;
import com.emmanuel.wechatdemo.util.Constants;
import com.emmanuel.wechatdemo.util.DataFactory;
import com.emmanuel.wechatdemo.util.LogUtil;
import com.emmanuel.wechatdemo.util.SoftKeyBoardUtil;
import com.emmanuel.wechatdemo.view.DividerItemDecoration;
import com.emmanuel.wechatdemo.view.PushShuoDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class FriendsActivity extends BaseActivity implements View.OnClickListener{

    private static final String TAG = "FriendsActivity";
    private boolean keyboardShowFlag = false;

    private RelativeLayout rootLayout;
    private RecyclerView recyclerView;
    private FriendsAdapter friendsAdapter;
    private LinearLayoutManager layoutManager;
    private List<ShuoShuo>listSS;

    private boolean isLoadMore = false;
    private ImageView ivMore;
    private EditText etInput;
    private LinearLayout layoutInput;
    private TextView tvSend, tvVoice;

    private int commentPosition = -1; //评论的说说id
    private User toUser; //给谁评论

    private int heightRootLayout = -1; //未出现软键盘时候的高度


    //监听软键盘的弹出和消失
    private ViewTreeObserver.OnGlobalLayoutListener keyboardLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            Rect rect = new Rect();
            getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
//            int statusBarHeight = rect.top; //状态栏高度
            int curHeightRootLayout = rootLayout.getHeight();//当前rootlayout
//            int heightDiff = rootLayout.getRootView().getHeight() - rootLayout.getHeight();
//            int contentViewTop = getWindow().findViewById(Window.ID_ANDROID_CONTENT).getHeight();
//            LogUtil.logD(TAG, "rootLayout.getRootView().getHeight() = " + rootLayout.getRootView().getHeight());
            LogUtil.logD(TAG, "rootLayout.getHeight() = " + rootLayout.getHeight());
//            LogUtil.logD(TAG, "heightDiff = " + heightDiff);
//            LogUtil.logD(TAG, "contentViewTop = " + contentViewTop);
//            LogUtil.logD(TAG, "statusBarHeight = " + statusBarHeight);
            LogUtil.logD(TAG, "heightRootLayout = " + heightRootLayout);

            if(heightRootLayout == -1)
                heightRootLayout = rootLayout.getHeight();

            if(curHeightRootLayout >= heightRootLayout){
                layoutInput.setVisibility(View.GONE);
            } else {
                layoutInput.setVisibility(View.VISIBLE);
                etInput.setFocusable(true);
                etInput.setFocusableInTouchMode(true);
                etInput.requestFocus();
            }

        }
    };

    private Handler myHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case Constants.HANDLER_FLAG_SHOW_EDITTEXT:
                    onComment(msg);
                    break;
            }
        }
    };

    private void onComment(Message msg) {
        Bundle data = msg.getData();
        commentPosition = data.getInt(Constants.BUNDLE_KEY_SHUOSHUO_POS);
        toUser = (User) data.get(Constants.BUNDLE_KEY_TO_USER);
        layoutInput.setVisibility(View.VISIBLE);
        etInput.setFocusableInTouchMode(true);
        etInput.setFocusable(true);
        etInput.requestFocus();
        String hint = "回复";
        if(toUser == null){
            hint = hint + listSS.get(commentPosition).user.name;
        } else {
            hint = hint + toUser.name;
        }
        etInput.setHint(hint);
        SoftKeyBoardUtil.showSoftKeyBoard(etInput);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_friends);
        EventBus.getDefault().register(this);

        setTitle(getString(R.string.title_friends));
        setLeftBtnVisibility(View.VISIBLE);
        setRightRes(R.mipmap.icon_camera);

        initViews();
        updata();
    }

    private void initViews() {
        rootLayout = (RelativeLayout)findViewById(R.id.layout_root);
        rootLayout.getViewTreeObserver().addOnGlobalLayoutListener(keyboardLayoutListener);
        recyclerView = (RecyclerView)findViewById(R.id.recyclerview_friends);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this,  LinearLayoutManager.VERTICAL));
        friendsAdapter = new FriendsAdapter(this, myHandler);
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
        tvSend.setOnClickListener(this);
    }

    private void updata(){
        listSS = new ArrayList<>();
        listSS.addAll(DataFactory.createShuoShuo(10));//随机产生10条说说
        friendsAdapter.setDatas(listSS);
    }

    private void loadMore(){
        listSS.addAll(DataFactory.createShuoShuo(10));//随机产生10条说说
        friendsAdapter.notifyDataSetChanged();
        isLoadMore = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_send:
                String content = etInput.getText().toString().trim();
                if(commentPosition >=0 && commentPosition < listSS.size() && content != null && content.length() > 0){
                    Comment comment = new Comment();
                    comment.content = content;
                    comment.fromUser = App.getInstance().getUser();
                    if(toUser == null)
                        comment.toUser = listSS.get(commentPosition).user;
                    else
                        comment.toUser = toUser;
                    listSS.get(commentPosition).commentList.add(comment);
                    friendsAdapter.notifyDataSetChanged();
                }
                SoftKeyBoardUtil.hideSoftKeyBoard(etInput);
                etInput.setText("");
                layoutInput.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    protected void onBtnRight1() {
        super.onBtnRight1();
        final PushShuoDialog pushShuoDialog = new PushShuoDialog();
        pushShuoDialog.setItemClickLitener(new PushShuoDialog.ItemClickLitener() {
            @Override
            public void onClickItem(int position) {
                if(position == 1){
                    Intent intent = new Intent(FriendsActivity.this, PhotoSelectActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(FriendsActivity.this, VideoRecordActivity.class);
                    startActivity(intent);
                }
                pushShuoDialog.dismiss();
            }
        });
        pushShuoDialog.show(getSupportFragmentManager(), "PushShuoDialog");

    }

    @Subscribe
    public void onEventMainThread(ShuoShuoPushEvent event) {
        ShuoShuo newShuoshuo = event.getMySS();
        listSS.add(0, newShuoshuo);
        friendsAdapter.notifyDataSetChanged();
    }

}
