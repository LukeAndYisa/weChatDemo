package com.emmanuel.wechatdemo.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.emmanuel.wechatdemo.R;
import com.emmanuel.wechatdemo.view.IndexView;

/**
 * Created by user on 2016/9/12.
 */
public class ContactsFragment extends Fragment implements View.OnClickListener{

    private RecyclerView recyclerView;
    private IndexView indexView;
    private TextView tvWord;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerView);
        tvWord = (TextView)view.findViewById(R.id.tv_word);
        indexView = (IndexView)view.findViewById(R.id.index_view);
        indexView.addOnIndexListener(new IndexView.OnIndexListener() {
            @Override
            public void onSelectedIndex(int index, String word) {
                tvWord.setText(word);
            }

            @Override
            public void onStart(int index, String word) {
                tvWord.setVisibility(View.VISIBLE);
                tvWord.setText(word);
            }

            @Override
            public void onEnd() {
                tvWord.setVisibility(View.GONE);
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onClick(View view) {

    }
}
