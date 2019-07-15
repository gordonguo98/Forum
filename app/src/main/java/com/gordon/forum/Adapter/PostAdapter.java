package com.gordon.forum.Adapter;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<Post> mList;
    private static final String SERVER_ADDRESS_IMG = "192.168.1.0:8080";//服务器地址

    public PostAdapter(Context mContext, List<Post> list) {
        this.mContext = mContext;
        mList = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View item = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.activity_forum_post, viewGroup, false);
        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        MyViewHolder holder = (MyViewHolder) viewHolder;
        Post data = mList.get(i);
        holder.profilePhoto.setImageBitmap(data.getCreator().getProfile_photo());
        holder.userName.setText(data.getCreator().getName());
        holder.createDate.setText(data.getCreateTime().toString());
        holder.content.setText(data.getQuestion());
        //生成九宫格图片
        ArrayList<ImageInfo> imageInfos = new ArrayList<>();
        List<String> images = new ArrayList<>();
        if (!TextUtils.isEmpty(data.getContentImages())) {  //有图片时
            try {
                JSONArray jSONArray = new JSONArray(data.getContentImages());
                for (int j = 0; j < jSONArray.length(); j++) {
                    JSONObject temp = new JSONObject(jSONArray.getString(j));
                    images.add(SERVER_ADDRESS_IMG + temp.getString("path"));
                }
                for (String image : images) {
                    ImageInfo info = new ImageInfo();
                    info.setThumbnailUrl(image);
                    info.setBigImageUrl(image);
                    imageInfos.add(info);
                }
                holder.nineGridView.setAdapter(new NineGridViewClickAdapter(mContext, imageInfos));
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
        ImageView profilePhoto;
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

}
