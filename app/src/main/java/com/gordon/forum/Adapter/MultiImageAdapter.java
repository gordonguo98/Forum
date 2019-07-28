package com.gordon.forum.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.gordon.forum.Activity.NewPostActivity;
import com.gordon.forum.Model.ImageItem;
import com.gordon.forum.R;

import java.util.ArrayList;

public class MultiImageAdapter extends BaseAdapter {

    private NewPostActivity activity;
    private LayoutInflater inflater;
    private ArrayList<ImageItem> mImages;

    //用来判断是否是刚刚进入，刚进入只显示添加按钮，也就是上面java代码中只传this的时候
    private boolean is = false;

    public MultiImageAdapter(NewPostActivity activity, ArrayList<ImageItem> images) {
        this.activity = activity;
        this.inflater = LayoutInflater.from(activity);
        this.mImages = images;
    }

    public MultiImageAdapter(NewPostActivity activity) {
        this.activity = activity;
        this.inflater = LayoutInflater.from(activity);
        is = true;//设置为true表示第一次初始化
    }

    @Override
    public int getCount() {
        if(!is){
            //这里判断数据如果有9张就size等于9,否则就+1，+1是为按钮留的位置
            return mImages.size()==9?mImages.size():mImages.size()+1;
        }
        //没有数据就是1，1是为按钮留的位置
        return 1;
    }

    @Override
    public Object getItem(int position) {
        return mImages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, final ViewGroup parent) {

        ViewHolder holder = null;
        if (null == view) {
            view = inflater.inflate(R.layout.item_util_image, null);
            holder = new ViewHolder();
            holder.ivIcon = (ImageView) view.findViewById(R.id.ivIcon);
            holder.ibAdd = (ImageButton) view.findViewById(R.id.ibAdd);
            holder.ibDelete = (ImageButton) view.findViewById(R.id.ibDelete);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        if(!is){
            //选了图片后会进入这里，先判断下position 是否等于size
            if(position == mImages.size()){
                //执行到这里就说明是最后一个位置，判断是否有9张图
                if(mImages.size() != 9){
                    //没有9张图就显示添加按钮
                    holder.ibAdd.setVisibility(View.VISIBLE);
                }else{
                    //有就隐藏
                    holder.ibAdd.setVisibility(View.GONE);
                    holder.ivIcon.setBackground(null);
                }
            }else{
                //还不是最后一个位置的时候执行这里
                //隐藏添加按钮，要设置图片嘛~
                holder.ibAdd.setVisibility(View.GONE);
                //根据条目位置设置图片
                ImageItem item = mImages.get(position);
                Glide.with(activity)
                        .load(item.path)
                        .into(holder.ivIcon);
            }
            //删除按钮的点击事件
            holder.ibDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //移除图片
                    mImages.remove(position);
                    //更新
                    notifyDataSetChanged();
                }
            });
        }else{
            //初次初始化的时候显示添加按钮
            holder.ibAdd.setVisibility(View.VISIBLE);
        }
        //添加按钮点击事件
        holder.ibAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断是否是初始化进入
                if(!is){
                    //到这里表示已经选过了，然后用9-size算出还剩几个图的位置
                    activity.chooseImages(9-mImages.size());
                }
                else
                    //未选过
                    activity.chooseImages(9);
            }
        });
        return view;
    }
    protected class ViewHolder {
        /** icon */
        protected ImageView ivIcon;
        /** 移除 */
        protected ImageButton ibDelete;
        /**添加 */
        protected ImageButton ibAdd;
    }

}
