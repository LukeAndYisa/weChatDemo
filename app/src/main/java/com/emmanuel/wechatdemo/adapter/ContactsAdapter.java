package com.emmanuel.wechatdemo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.emmanuel.wechatdemo.R;
import com.emmanuel.wechatdemo.bean.User;
import com.emmanuel.wechatdemo.util.ImageLoadUtil;
import com.nostra13.universalimageloader.core.ImageLoader;


/**
 * Created by user on 2016/9/13.
 */
public class ContactsAdapter extends BaseRecycleViewAdapter {

    private Context context;

    public ContactsAdapter(Context context){
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_contacts, parent, false);
        ContactsViewHolder viewHolder = new ContactsViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        User user = (User)datas.get(position);
        ContactsViewHolder viewHolder = (ContactsViewHolder)holder;
        ImageLoader.getInstance().displayImage(user.photoUrl, viewHolder.ivPhoto, ImageLoadUtil.getOptions2());
        viewHolder.tvName.setText(user.name);
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    class ContactsViewHolder extends RecyclerView.ViewHolder{

        public ImageView ivPhoto;
        public TextView tvName;

        public ContactsViewHolder(View itemView) {
            super(itemView);
            ivPhoto = (ImageView)itemView.findViewById(R.id.iv_photo);
            tvName = (TextView)itemView.findViewById(R.id.tv_name);
        }
    }
}
