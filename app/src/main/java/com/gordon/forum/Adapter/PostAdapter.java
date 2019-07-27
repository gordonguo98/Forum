package com.gordon.forum.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gordon.forum.Model.Post;
import com.gordon.forum.R;
import com.lzy.ninegrid.ImageInfo;
import com.lzy.ninegrid.NineGridView;
import com.lzy.ninegrid.preview.NineGridViewClickAdapter;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener, View.OnLongClickListener{

    private Context mContext;
    private List<Post> mList;

    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(View view, int position);
    }

    public PostAdapter(Context mContext, List<Post> list) {
        this.mContext = mContext;
        mList = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View item = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_forum_post, viewGroup, false);
        item.setOnClickListener(this);
        item.setOnLongClickListener(this);
        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        MyViewHolder holder = (MyViewHolder) viewHolder;
        viewHolder.itemView.setTag(i);
        Post data = mList.get(i);
        holder.profilePhoto.setImageBitmap(data.getCreator().getProfile_photo_bitmap());
        holder.userName.setText(data.getCreator().getName());
        holder.createDate.setText(data.getCreateTime());
        holder.content.setText(data.getQuestion());
        //生成九宫格图片
        ArrayList<ImageInfo> imageInfos = new ArrayList<>();
        if (data.getContentImages().size() != 0) {  //有图片时
            for (String image : data.getContentImages()) {
                ImageInfo info = new ImageInfo();
                info.setThumbnailUrl(image);
                info.setBigImageUrl(image);
                imageInfos.add(info);
            }
            holder.nineGridView.setAdapter(new NineGridViewClickAdapter(mContext, imageInfos));
        } else {
            Log.e("TEST", "onBindViewHolder: 无图");
            holder.nineGridView.setVisibility(View.GONE);
        }

        holder.reply.setImageResource(R.drawable.ic_mode_comment_black_24dp);
        holder.replyNum.setText((data.getReplyNum()+""));
        holder.like.setImageResource(R.drawable.ic_favorite_border_black_24dp);
        holder.likeNum.setText((data.getLikeNum()+""));

    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        CircleImageView profilePhoto;
        TextView userName;
        TextView createDate;
        TextView content;
        NineGridView nineGridView;
        ImageView reply;
        ImageView like;
        TextView replyNum;
        TextView likeNum;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            profilePhoto = itemView.findViewById(R.id.post_profile_photo);
            userName = itemView.findViewById(R.id.post_user_name);
            createDate = itemView.findViewById(R.id.post_create_date);
            content = itemView.findViewById(R.id.post_content);
            reply = itemView.findViewById(R.id.post_reply);
            replyNum = itemView.findViewById(R.id.post_reply_num);
            like = itemView.findViewById(R.id.post_like);
            likeNum = itemView.findViewById(R.id.post_like_num);
            nineGridView = itemView.findViewById(R.id.post_nine_grid);
        }
    }

    @Override
    public void onClick(View view) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(view, (int) view.getTag());//注意这里使用getTag方法获取position
        }
    }

    @Override
    public boolean onLongClick(View view) {
        if (mOnItemLongClickListener != null) {
            mOnItemLongClickListener.onItemLongClick(view, (int) view.getTag());//注意这里使用getTag方法获取position
        }
        return true;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener){
        this.mOnItemLongClickListener = listener;
    }

}
