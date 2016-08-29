package com.emmanuel.wechatdemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.emmanuel.wechatdemo.R;

/**
 * Created by user on 2016/8/29.
 */
public class FindFragment extends Fragment implements View.OnClickListener{

    private LinearLayout layoutFriends;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_find, null);
        initView(view);
        return view;
    }

    private void initView(View view) {
        layoutFriends = (LinearLayout)view.findViewById(R.id.layout_friends);
        layoutFriends.setOnClickListener(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.layout_friends:
                Intent intent = new Intent(getActivity(), FriendsActivity.class);
                startActivity(intent);
                break;
        }
    }
}
