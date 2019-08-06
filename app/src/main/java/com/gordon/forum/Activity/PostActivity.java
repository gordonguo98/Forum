package com.gordon.forum.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.biometrics.BiometricManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.gordon.forum.Adapter.MessageAdapter;
import com.gordon.forum.Database.LikeDao;
import com.gordon.forum.Fragment.CommentDialogFragment;
import com.gordon.forum.Model.Message;
import com.gordon.forum.Model.Post;
import com.gordon.forum.R;
import com.gordon.forum.Util.BitmapUtil;
import com.gordon.forum.Util.GlideImageLoader;
import com.gordon.forum.Util.UrlHelper;
import com.lzy.ninegrid.ImageInfo;
import com.lzy.ninegrid.NineGridView;
import com.lzy.ninegrid.preview.NineGridViewClickAdapter;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class PostActivity extends AppCompatActivity implements View.OnClickListener {

    Activity mContext;

    private String post_json;
    private String userId;
    private int postId;
    private Post post;
    private Bitmap profilePhotoBitmap;
    private List<Message> messagesList = new ArrayList<>();
    private MessageAdapter messageAdapter;

    private SwipeRefreshLayout refreshLayout;
    private CircleImageView profilePhoto;
    private TextView userName;
    private TextView createDate;
    private TextView content;
    private NineGridView nineGridView;
    private RecyclerView commentsRV;
    private ImageView like;

    private MyHandler myHandler;

    private static final int UPDATE_MESSAGE_LIST = 200;
    private static final int STOP_REFRESHING = 201;
    private static final int AFTER_SENDING = 202;
    private static final int POSTING_LIKE = 203;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        mContext = PostActivity.this;
        //设置状态栏，工具栏
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_post_toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(null != actionBar)
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        addMiddleTitle(this, "帖子详情", toolbar);

        Intent intent = getIntent();
        post_json = intent.getStringExtra("post");
        userId = intent.getStringExtra("user_id");

        Toast.makeText(this, "传递后json: " + post_json, Toast.LENGTH_LONG).show();

        post = JSON.parseObject(post_json, Post.class);

        postId = post.getPostId();

        TextView commentTextView = (TextView) findViewById(R.id.comment_textview);
        commentTextView.setOnClickListener(this);

        initImgLoadingOptions();
        initView();
        getMessages();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void addMiddleTitle(Context context, CharSequence title, Toolbar toolbar) {

        TextView textView = new TextView(context);
        textView.setText(title);
        textView.setTextColor(getResources().getColor(R.color.textColor));
        textView.setTextSize(20);

        Toolbar.LayoutParams params = new Toolbar.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        toolbar.addView(textView, params);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.comment_textview:
                CommentDialogFragment commentDialogFragment = new CommentDialogFragment();
                commentDialogFragment.setOnSendListener(new CommentDialogFragment.OnSendListener() {
                    @Override
                    public void OnSend(String content) {
                        sendMessage(content);
                    }
                });
                commentDialogFragment.show(getSupportFragmentManager(), "CommentDialogFragment");
                break;
            default:
                break;
        }
    }

    private void initView(){

        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.post_detail_refreshlayout) ;
        profilePhoto = (CircleImageView) findViewById(R.id.post_detail_profile_photo);
        userName = (TextView) findViewById(R.id.post_detail_user_name);
        createDate = (TextView) findViewById(R.id.post_detail_create_date);
        content = (TextView) findViewById(R.id.post_detail_content);
        nineGridView = (NineGridView) findViewById(R.id.post_detail_nine_grid);
        commentsRV = (RecyclerView) findViewById(R.id.post_detail_comments);
        like = (ImageView) findViewById(R.id.like_imageview);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getMessages();
            }
        });

        //profilePhoto.setImageBitmap(BitmapUtil.getProfilePhoto(post.getCreator().getProfile_photo()));
        BitmapUtil bitmapUtil = new BitmapUtil();
        Bitmap bitmap = bitmapUtil.getProfilePhoto("http://img.redocn.com/sheying/20150213/mulanweichangcaoyuanfengjing_3951976.jpg");
        Log.e("test", "initView: "+bitmap.toString());
        profilePhoto.setImageBitmap(bitmap);

        userName.setText(post.getCreator().getName());
        createDate.setText(post.getCreateTime());
        content.setText(post.getQuestion());

        //生成九宫格图片
        List<String> data = post.getContentImages();
        ArrayList<ImageInfo> imageInfos = new ArrayList<>();
        if (data.size() != 0) {  //有图片时
            for (String image : data) {
                ImageInfo info = new ImageInfo();
                info.setThumbnailUrl(image);
                info.setBigImageUrl(image);
                imageInfos.add(info);
            }
            nineGridView.setAdapter(new NineGridViewClickAdapter(mContext, imageInfos));
        } else {
            Log.e("test", "initView: 无图");
            nineGridView.setVisibility(View.GONE);
        }

        commentsRV.setLayoutManager(new LinearLayoutManager(mContext));
        messageAdapter = new MessageAdapter(mContext, messagesList);
        commentsRV.setAdapter(messageAdapter);
        commentsRV.setHasFixedSize(true);
        //commentsRV.setNestedScrollingEnabled(false);

        messageAdapter.setOnMessageClickListener(new MessageAdapter.OnMessageClickListener() {
            @Override
            public void onMessageClick(final int position) {
                if(messagesList.get(position).getSender().getPhone_num().equals(userId)){
                    //如果回复是自己发送的,则可以删除
                    final AlertDialog.Builder normalDialog = new AlertDialog.Builder(PostActivity.this);
                    normalDialog.setIcon(R.drawable.ic_delete_forever_black_24dp);
                    normalDialog.setTitle("删除评论");
                    normalDialog.setMessage("确认删除?");
                    normalDialog.setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //删除评论
                                    deleteMessage(messagesList.get(position).getMessageId(), position);
                                }
                            });
                    normalDialog.setNegativeButton("取消",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //取消删除
                                }
                            });
                    // 显示
                    normalDialog.show();
                }
            }
        });

        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LikeDao likeDao = new LikeDao(mContext);
                //已经赞过
                if(likeDao.queryLike(userId, postId)){
                    //取消赞
                    //TODO
                }

                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        OkHttpClient client = new OkHttpClient();
                        FormBody.Builder formBody = new FormBody.Builder();
                        formBody.add("method", "like");
                        formBody.add("post_id", postId + "");
                        final Request request = new Request.Builder()
                                .url(UrlHelper.URL_FOR_POSTING_LIKE)
                                .post(formBody.build())
                                .build();

                        client.newCall(request).enqueue(new Callback() {

                            @Override
                            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                                Log.e("test", "onFailure: 点赞失败");
                            }

                            @Override
                            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                                Log.e("test", "onResponse: 点赞成功");
                                android.os.Message message = new android.os.Message();
                                message.what = POSTING_LIKE;
                                myHandler.sendMessage(message);
                                LikeDao likeDao = new LikeDao(mContext);
                                //将点赞信息存入数据库
                                likeDao.insertLike(userId, postId);
                            }
                        });

                    }
                });

            }

        });

        myHandler = new MyHandler(this, refreshLayout, messageAdapter, like);
    }

    private void initImgLoadingOptions(){

        /** Picasso 加载 */
        class PicassoImageLoader implements NineGridView.ImageLoader {

            @Override
            public void onDisplayImage(Context context, ImageView imageView, String url) {
                Picasso.with(context).load(url)//
                        .placeholder(R.drawable.ic_default_image)//
                        .error(R.drawable.ic_default_image)//
                        .into(imageView);
            }

            @Override
            public Bitmap getCacheImage(String url) {
                return null;
            }
        }

        NineGridView.setImageLoader(new GlideImageLoader());
    }

    private void getMessages(){

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(UrlHelper.URL_FOR_GETTING_COMMENT + "?post_id=" + postId)
                        .get()
                        .build();
                client.newCall(request).enqueue(new Callback() {

                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        Log.e("getMessages: ", "onFailure: " + e.toString());
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        ResponseBody responseBody = response.body();
                        String body = null;
                        if(null != responseBody)
                            body = responseBody.string();
                        if(null == body)
                            Log.e("getMessages: ", "onResponse: responsebody is null!");
                        else {
                            Log.e("getMessages: ", "onResponse: responsebody is " + body);
                            try {
                                JSONObject jsonObject = JSON.parseObject(body);
                                int code = (int) jsonObject.get("code");
                                String msg = (String) jsonObject.get("msg");
                                if(code == 1000) {
                                    int message_num = (int) jsonObject.get("message_num");
                                    Log.i("test", "onResponse: " + code + " " + msg + " " + message_num);
                                    messagesList.clear();
                                    for (int i = 1; i <= message_num; i++) {
                                        JSONObject message_json = (JSONObject) jsonObject.get("message_" + i);
                                        Log.i("test", "onResponse: " + message_json);
                                        Message newMessage = JSON.toJavaObject(message_json, Message.class);
                                        Log.i("test", "onResponse: " + newMessage.getContent());
                                        BitmapUtil bitmapUtil = new BitmapUtil();
                                        newMessage.getSender().setProfile_photo_bitmap(bitmapUtil.getProfilePhoto("http://img.redocn.com/sheying/20150213/mulanweichangcaoyuanfengjing_3951976.jpg"));
                                        messagesList.add(newMessage);
                                        android.os.Message message = new android.os.Message();
                                        message.what = UPDATE_MESSAGE_LIST;
                                        myHandler.sendMessage(message);
                                        Log.i("test", "onResponse: " + newMessage.getSendTime());
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.e("test", e.toString());
                            }
                        }
                    }
                });
                android.os.Message message = new android.os.Message();
                message.what = STOP_REFRESHING;
                myHandler.sendMessage(message);
            }
        });
    }

    private void sendMessage(final String content){

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                OkHttpClient client = new OkHttpClient();
                FormBody.Builder formBody = new FormBody.Builder();
                formBody.add("email", userId);
                formBody.add("post_id", postId + "");
                formBody.add("content", content);
                final Request request = new Request.Builder()
                        .url(UrlHelper.URL_FOR_POSTING_COMMENT)
                        .post(formBody.build())
                        .build();

                client.newCall(request).enqueue(new Callback() {

                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        Log.e("test", "onFailure: 回复失败");
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        Log.e("test", "onResponse: 回复成功");
                        android.os.Message message = new android.os.Message();
                        message.what = AFTER_SENDING;
                        myHandler.sendMessage(message);
                    }
                });
            }
        });

    }

    private void deleteMessage(int messageId, int position){
        //TODO
    }

    static class MyHandler extends Handler{

        private PostActivity activity;
        private SwipeRefreshLayout refreshLayout;
        private MessageAdapter messageAdapter;
        private ImageView like;

        public MyHandler(PostActivity activity, SwipeRefreshLayout refreshLayout, MessageAdapter messageAdapter, ImageView like){
            this.activity = activity;
            this.refreshLayout = refreshLayout;
            this.messageAdapter = messageAdapter;
            this.like = like;
        }

        @Override
        public void handleMessage(@NonNull android.os.Message msg) {
            if(msg.what == UPDATE_MESSAGE_LIST){
                Log.e("test", "handleMessage: 信号送达");
                messageAdapter.notifyDataSetChanged();
            }
            switch(msg.what){
                case UPDATE_MESSAGE_LIST:
                    messageAdapter.notifyDataSetChanged();
                    break;
                case STOP_REFRESHING:
                    refreshLayout.setRefreshing(false);
                    break;
                case AFTER_SENDING:
                    activity.getMessages();
                    break;
                case POSTING_LIKE:
                    like.setImageResource(R.drawable.ic_favorite_black_24dp);
                    break;
            }
        }
    }

}
