package com.emmanuel.wechatdemo.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.emmanuel.wechatdemo.R;
import com.emmanuel.wechatdemo.adapter.ContactsAdapter;
import com.emmanuel.wechatdemo.bean.User;
import com.emmanuel.wechatdemo.bean.Word;
import com.emmanuel.wechatdemo.util.DataFactory;
import com.emmanuel.wechatdemo.view.ContactsDividerItemDecoration;
import com.emmanuel.wechatdemo.view.IndexView;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by user on 2016/9/12.
 */
public class ContactsFragment extends Fragment implements View.OnClickListener{

    private static final String TAG = "ContactsFragment";

    private RecyclerView recyclerView;
    private IndexView indexView;
    private TextView tvWord;
    private List<User>listFriends;
    private ContactsAdapter adapter;
    private LinearLayoutManager layoutManager;
    private Word words[] = new Word[26]; //26个字母表

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        recyclerView = (RecyclerView)view.findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        tvWord = (TextView)view.findViewById(R.id.tv_word);
        indexView = (IndexView)view.findViewById(R.id.index_view);
        indexView.addOnIndexListener(new IndexView.OnIndexListener() {
            @Override
            public void onSelectedIndex(int index, String word) {
                tvWord.setText(word);
                if(index-1 >=0 && index-1 <26) {
                    int dex = words[index - 1].index;
                    layoutManager.scrollToPositionWithOffset(dex, 0);
                }
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

        initData();
    }

    private void initData() {
        listFriends = DataFactory.createFriends(80);
        Collections.sort(listFriends, new Comparator<User>() {
            @Override
            public int compare(User user, User t1) {
                return user.name.compareTo(t1.name);
            }
        });
        initWord();
        adapter = new ContactsAdapter(getContext());
        adapter.setDatas(listFriends);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new ContactsDividerItemDecoration(getContext(), words));
    }

    //初始化字母表
    private void initWord() {
        int in = 0;
        int k = 0;
        String word = "";
        for(int i=0; i<listFriends.size(); i++){
            String w1 = listFriends.get(i).name.substring(0,1);
            if(word.equals(w1)){
                continue;
            }
            word = w1;
            in = i;
            for(; k<words.length; k++){
                String w2 = IndexView.INDEX_KEY[k + 1];
                if(word.compareTo(w2) < 0)
                    break;
                words[k] = new Word();
                words[k].title = IndexView.INDEX_KEY[k + 1];
                words[k].index = in;
            }
        }
        for(; k<26; k++){
            words[k] = new Word();
            words[k].title = IndexView.INDEX_KEY[k+1];
            words[k].index = in;
        }
    }

    @Override
    public void onClick(View view) {

    }
}
