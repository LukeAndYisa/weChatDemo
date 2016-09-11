package com.emmanuel.wechatdemo.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.emmanuel.wechatdemo.R;
import com.emmanuel.wechatdemo.bean.Picture;
import com.emmanuel.wechatdemo.util.ImageLoadUtil;
import com.emmanuel.wechatdemo.view.ColorFilterImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 2016/9/9.
 */
public class PictureAdapter extends BaseRecycleViewAdapter {

    private static final String TAG = "PictureAdapter";
    private Context context;
    private final int MAX_PICTURE = 9; //最多选9张
    private List<Integer> listSelected = new ArrayList<>();
    private List<Picture>datas;
    private OnSelectedListener onSelectedListener;

    public PictureAdapter(Context context, List<Picture>datas, OnSelectedListener listener){
        this.context = context;
        this.datas = datas;
        this.onSelectedListener = listener;
    }

    @Override
    public PictureViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_picture, parent, false);
        PictureViewHolder viewHolder = new PictureViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final int p = position;
        final PictureViewHolder viewHolder = (PictureViewHolder)holder;
        final Picture picture = datas.get(position);
        ImageLoader.getInstance().displayImage(picture.uri, viewHolder.imageView, ImageLoadUtil.getOptions1());
        if(picture.isChecked){
            viewHolder.checkBox.setImageResource(R.mipmap.icon_checked);
            viewHolder.imageView.turnOff();
        } else {
            viewHolder.checkBox.setImageResource(R.mipmap.icon_unchecked);
            viewHolder.imageView.turnOn();
        }
        viewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(datas.get(p).isChecked){
                    viewHolder.checkBox.setImageResource(R.mipmap.icon_unchecked);
                    viewHolder.imageView.turnOn();
                    for(int i=0; i<listSelected.size(); i++){
                        int sPos = listSelected.get(i);
                        if(p == sPos){
                            listSelected.remove(i);
                            datas.get(p).isChecked = false;
                            break;
                        }
                    }
                    if(onSelectedListener != null)
                        onSelectedListener.onRemove(p);
                } else{
                    if(MAX_PICTURE <= listSelected.size())
                        return;
                    viewHolder.checkBox.setImageResource(R.mipmap.icon_checked);
                    viewHolder.imageView.turnOff();
                    listSelected.add(p);
                    datas.get(p).isChecked = true;
                    if(onSelectedListener != null)
                        onSelectedListener.onSelected(p);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    class PictureViewHolder extends RecyclerView.ViewHolder{

        public ImageView checkBox;
        public ColorFilterImageView imageView;

        public PictureViewHolder(View itemView) {
            super(itemView);
            checkBox = (ImageView)itemView.findViewById(R.id.checkbox);
            imageView = (ColorFilterImageView)itemView.findViewById(R.id.iv_picture);
            imageView.setCanTouchSwitch(false);
        }
    }

    //返回选择图片的序号，还未排序
    public List<Integer> getSelectedPicPosition(){
        return listSelected;
    }

    public interface OnSelectedListener{
        public void onSelected(int position);

        public void onRemove(int position);
    }
}
