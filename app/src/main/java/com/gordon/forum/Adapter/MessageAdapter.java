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

import com.gordon.forum.Fragment.CommentDialogFragment;
import com.gordon.forum.Model.Message;
import com.gordon.forum.R;
import com.lzy.ninegrid.ImageInfo;
import com.lzy.ninegrid.NineGridView;
import com.lzy.ninegrid.preview.NineGridViewClickAdapter;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context mContext;
    private List<Message> mList;
    private OnMessageClickListener onMessageClickListener;
    private OnMessageAttachmentListener onMessageAttachmentListener;

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
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        MyViewHolder myViewHolder = (MyViewHolder) holder;
        myViewHolder.itemView.setTag(position);
        myViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(null != onMessageClickListener)
                    onMessageClickListener.onMessageClick(position);
                return true;
            }
        });
        final Message data = mList.get(position);
        myViewHolder.profilePhoto.setImageBitmap(data.getSender().getProfile_photo_bitmap());
        myViewHolder.userName.setText(data.getSender().getName());
        myViewHolder.createDate.setText(data.getSendTime());
        myViewHolder.content.setText(data.getContent());
        //生成九宫格图片
        ArrayList<ImageInfo> imageInfos = new ArrayList<>();
        if (data.getContentImages().size() != 0) {  //有图片时
            for (String image : data.getContentImages()) {
                ImageInfo info = new ImageInfo();
                info.setThumbnailUrl(image);
                info.setBigImageUrl(image);
                imageInfos.add(info);
            }
            myViewHolder.contentImages.setAdapter(new NineGridViewClickAdapter(mContext, imageInfos));
        } else {
            Log.e("TEST", "onBindViewHolder: 无图");
            myViewHolder.contentImages.setVisibility(View.GONE);
        }
        if(data.getContentFiles().size() == 0)
            myViewHolder.attachment.setImageResource(R.drawable.ic_attachment_24dp);
        else
            myViewHolder.attachment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(null != onMessageAttachmentListener)
                        onMessageAttachmentListener.onMessageAttachment(data.getContentFiles());
                }
            });
        Log.e("test", "onBindViewHolder: 执行");
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        CircleImageView profilePhoto;
        TextView userName;
        TextView createDate;
        TextView content;
        NineGridView contentImages;
        ImageView attachment;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            profilePhoto = itemView.findViewById(R.id.message_profile_photo);
            userName = itemView.findViewById(R.id.message_user_name);
            createDate = itemView.findViewById(R.id.message_create_date);
            content = itemView.findViewById(R.id.message_content);
            contentImages = itemView.findViewById(R.id.message_images);
            attachment = itemView.findViewById(R.id.message_attachment);
        }
    }

    public void setOnMessageClickListener(OnMessageClickListener onMessageClickListener){
        this.onMessageClickListener = onMessageClickListener;
    }

    public void setOnMessageAttachmentListener(OnMessageAttachmentListener onMessageAttachmentListener){
        this.onMessageAttachmentListener = onMessageAttachmentListener;
    }

    public interface OnMessageClickListener{
        public void onMessageClick(int position);
    }

    public interface OnMessageAttachmentListener{
        public void onMessageAttachment(List<String> contentFiles);
    }
}
