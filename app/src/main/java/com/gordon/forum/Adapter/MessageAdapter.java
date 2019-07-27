package com.gordon.forum.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gordon.forum.Model.Message;
import com.gordon.forum.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context mContext;
    private List<Message> mList;

    public MessageAdapter(Context mContext, List<Message> list){
        this.mContext = mContext;
        mList = list;
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_post_message, parent, false);
        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MyViewHolder myViewHolder = (MyViewHolder) holder;
        myViewHolder.itemView.setTag(position);
        Message data = mList.get(position);
        myViewHolder.profilePhoto.setImageBitmap(data.getSender().getProfile_photo_bitmap());
        myViewHolder.userName.setText(data.getSender().getName());
        myViewHolder.createDate.setText(data.getSendTime());
        myViewHolder.content.setText(data.getContent());
        Log.e("test", "onBindViewHolder: 执行");
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        CircleImageView profilePhoto;
        TextView userName;
        TextView createDate;
        TextView content;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            profilePhoto = itemView.findViewById(R.id.message_profile_photo);
            userName = itemView.findViewById(R.id.message_user_name);
            createDate = itemView.findViewById(R.id.message_create_date);
            content = itemView.findViewById(R.id.message_content);
        }
    }
}
