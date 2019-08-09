package com.gordon.forum.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
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
import com.gordon.forum.Adapter.DownloadAdapter;
import com.gordon.forum.Adapter.MessageAdapter;
import com.gordon.forum.Adapter.MultiImageAdapter;
import com.gordon.forum.Database.LikeDao;
import com.gordon.forum.Fragment.CommentDialogFragment;
import com.gordon.forum.Model.DownloadInfo;
import com.gordon.forum.Model.ImageItem;
import com.gordon.forum.Model.Message;
import com.gordon.forum.Model.Post;
import com.gordon.forum.R;
import com.gordon.forum.Util.BitmapUtil;
import com.gordon.forum.Util.Constant;
import com.gordon.forum.Util.GlideImageLoader;
import com.gordon.forum.Util.MyGlideEngine;
import com.gordon.forum.Util.UrlHelper;
import com.lzy.ninegrid.ImageInfo;
import com.lzy.ninegrid.NineGridView;
import com.lzy.ninegrid.preview.NineGridViewClickAdapter;
import com.squareup.picasso.Picasso;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import ru.bartwell.exfilepicker.ExFilePicker;
import ru.bartwell.exfilepicker.data.ExFilePickerResult;

public class PostActivity extends AppCompatActivity implements View.OnClickListener {

    Activity mContext;

    private String post_json;
    private String userId;
    private int postId;
    private Post post;
    private Bitmap profilePhotoBitmap;
    private List<Message> messagesList = new ArrayList<>();
    private MessageAdapter messageAdapter;
    private DownloadAdapter downloadAdapter;
    private List<DownloadInfo> downloadInfos = new ArrayList<>();
    private List<ImageItem> contentImages = new ArrayList<>();//评论的图片
    private List<String> contentFiles = new ArrayList<>();//评论的附件
    private List<String> fileName = new ArrayList<>();

    private SwipeRefreshLayout refreshLayout;
    private CircleImageView profilePhoto;
    private TextView userName;
    private TextView createDate;
    private TextView content;
    private NineGridView nineGridView;
    private ImageView attachmentIV;
    private RecyclerView commentsRV;
    private ImageView like;

    private MyHandler myHandler;

    private static final int REQUEST_CODE_CHOOSE = 3;
    private static final int UPDATE_MESSAGE_LIST = 200;
    private static final int STOP_REFRESHING = 201;
    private static final int AFTER_SENDING = 202;
    private static final int POSTING_LIKE = 203;
    private static final int DELETING_LIKE = 204;
    private static final int DELETING_MESSAGE = 205;
    private static final int REQUEST_FOR_RETURN = 103;
    private static final int EX_FILE_PICKER_RESULT = 104;

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

        post = JSON.parseObject(post_json, Post.class);

        postId = post.getPostId();

        TextView commentTextView = (TextView) findViewById(R.id.comment_textview);
        commentTextView.setOnClickListener(this);

        EventBus.getDefault().register(this);
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
                    public void OnSend(String content, List<ImageItem> contentImages, List<String> contentFiles) {
                        sendMessage(content, contentImages, contentFiles);
                    }
                });
                commentDialogFragment.setOnPhotoListener(new CommentDialogFragment.OnPhotoListener() {
                    @Override
                    public void OnPhoto(List<ImageItem> contentImages) {
                        //TODO
                        pickMessagePhoto(contentImages);
                    }
                });
                commentDialogFragment.setOnAttachmentListener(new CommentDialogFragment.OnAttachmentListener() {
                    @Override
                    public void OnAttachment(List<String> contentFiles) {
                        //TODO
                        pickMessageAttachment(contentFiles);
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
        attachmentIV = (ImageView) findViewById(R.id.post_detail_attachment);
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


        if(post.getContentFiles().size() == 0)
            attachmentIV.setImageResource(R.drawable.ic_attachment_24dp);
        else{
            attachmentIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getAttachment(post.getContentFiles());
                }
            });
        }

        commentsRV.setLayoutManager(new LinearLayoutManager(mContext));
        messageAdapter = new MessageAdapter(mContext, messagesList);
        messageAdapter.setOnMessageAttachmentListener(new MessageAdapter.OnMessageAttachmentListener() {
            @Override
            public void onMessageAttachment(List<String> contentFiles) {
                getAttachment(contentFiles);
            }
        });

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

        if(new LikeDao(mContext).queryLike(userId, postId))
            like.setImageResource(R.drawable.ic_favorite_black_24dp);
        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LikeDao likeDao = new LikeDao(mContext);
                //已经赞过
                if(likeDao.queryLike(userId, postId)){
                    //取消赞
                    //TODO
                    OkHttpClient client = new OkHttpClient();
                    FormBody.Builder formBody = new FormBody.Builder();
                    formBody.add("method", "unlike");
                    formBody.add("post_id", postId + "");
                    final Request request = new Request.Builder()
                            .url(UrlHelper.URL_FOR_DELETING_LIKE)
                            .post(formBody.build())
                            .build();

                    client.newCall(request).enqueue(new Callback() {

                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            Log.e("test", "onFailure: 取消赞失败");
                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            Log.e("test", "onResponse: 取消赞成功");
                            android.os.Message message = new android.os.Message();
                            message.what = DELETING_LIKE;
                            myHandler.sendMessage(message);
                            LikeDao likeDao = new LikeDao(mContext);
                            //将点赞信息从数据库中删除
                            long ret = likeDao.deleteLike(userId, postId);
                            Log.e("test", "onResponse: ret of delete " + ret);
                            Log.e("test", "onResponse: body of response:  " + response.body().string());
                        }
                    });
                    return;
                }

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
                        long ret = likeDao.insertLike(userId, postId);
                        Log.e("test", "onResponse: ret of insert " + ret);
                    }
                });

            }

        });

        myHandler = new MyHandler(this, refreshLayout, messageAdapter, like);
    }

    /**
     * 初始化图片加载器
     */
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

    /**
     * 获取评论列表
     */
    private void getMessages(){

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

    /**
     * 发送评论
     * @param content
     */
    private void sendMessage(final String content, List<ImageItem> images, List<String> files){

        MediaType MEDIA_TYPE;
        MultipartBody.Builder multiBuilder = new MultipartBody.Builder();
        multiBuilder.setType(MultipartBody.FORM);

        for (int i = 1; i <= images.size(); i++) {

            File file = new File(images.get(i - 1).path);
            Log.e("test", "onResponse: 发送图片名称：" + file.getName());
            MEDIA_TYPE = file.getName().endsWith("png") ? MediaType.parse("image/png") : MediaType.parse("image/jpeg");
            RequestBody fileBody = MultipartBody.create(MEDIA_TYPE, file);
            multiBuilder.addFormDataPart("image_" + i, file.getName(), fileBody);
        }

        for (int i = 1; i <= files.size(); i++) {

            File file = new File(files.get(i -1 ));
            Log.e("test", "onResponse: 发送文件名称：" + file.getName());

            MEDIA_TYPE = MediaType.parse("*");
            RequestBody fileBody = MultipartBody.create(MEDIA_TYPE, file);
            multiBuilder.addFormDataPart("file_" + i, file.getName(), fileBody);
        }

        multiBuilder.addFormDataPart("method", "create");
        multiBuilder.addFormDataPart("email", userId);
        multiBuilder.addFormDataPart("post_id", postId + "");
        multiBuilder.addFormDataPart("content", content);
        multiBuilder.addFormDataPart("image_num", images.size()+"");
        multiBuilder.addFormDataPart("file_num", files.size()+"");

        MultipartBody requestBody = multiBuilder.build();

        OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(UrlHelper.URL_FOR_POSTING_COMMENT)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("test", "onFailure: 回复失败");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Log.e("test", "onResponse: 回复成功"+response.body().string());
                android.os.Message message = new android.os.Message();
                message.what = AFTER_SENDING;
                myHandler.sendMessage(message);
            }
        });

    }

    /**
     * 删除评论
     * @param messageId
     * @param position
     */
    private void deleteMessage(int messageId, final int position){
        //TODO
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder formBody = new FormBody.Builder();
        formBody.add("method", "delete");
        formBody.add("email", userId);
        formBody.add("message_id", messageId+"");
        final Request request = new Request.Builder()
                .url(UrlHelper.URL_FOR_DELETING_COMMENT)
                .post(formBody.build())
                .build();

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("test", "onFailure: 删除评论失败");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Log.e("test", "onResponse: 删除评论成功");
                android.os.Message message = new android.os.Message();
                message.what = DELETING_MESSAGE;
                messagesList.remove(position);
                myHandler.sendMessage(message);
            }
        });
    }

    /**
     * 下载附件对话框
     */
    private void getAttachment(final List<String> fileUrls){
        //TODO
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("下载附件")
                .setIcon(R.drawable.ic_file_download_black_24dp)
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //确认下载
                        downloadAttachment(fileUrls);
                    }
                })
                .setMessage("有" + fileUrls.size() + "个附件，确认下载？").create();
        dialog.show();
    }

    /**
     * 下载附件
     */
    private void downloadAttachment(List<String> fileUrls){
        //TODO
        downloadInfos.clear();
        for(String fileurl: fileUrls)
            downloadInfos.add(new DownloadInfo(fileurl));
        downloadAdapter = new DownloadAdapter(PostActivity.this, downloadInfos);
        downloadAdapter.setOnDownloadCacelListener(new DownloadAdapter.OnDownloadCacelListener() {
            @Override
            public void onDownloadCancel(int position) {
                downloadInfos.remove(position);
                downloadAdapter.notifyDataSetChanged();
            }
        });

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("下载文件")
                .setIcon(R.drawable.ic_file_upload_black_24dp)
                .setAdapter(downloadAdapter, null).create();
        dialog.show();
    }

    /**
     * Eventbus subscribe
     * @param info
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void update(DownloadInfo info){
        if (DownloadInfo.DOWNLOAD.equals(info.getDownloadStatus())){

            Log.e("下载", "update: 调用");
            downloadAdapter.updateProgress(info);

        }else if (DownloadInfo.DOWNLOAD_OVER.equals(info.getDownloadStatus())){

            Log.e("下载结束", "update: 调用");
            downloadAdapter.updateProgress(info);
            Toast.makeText(this, "文件\""+info.getFileName()+"\"已下载到目录"+ Constant.FILE_PATH, Toast.LENGTH_SHORT).show();

        }else if (DownloadInfo.DOWNLOAD_PAUSE.equals(info.getDownloadStatus())){

            Toast.makeText(this,"下载暂停",Toast.LENGTH_SHORT).show();

        }else if (DownloadInfo.DOWNLOAD_CANCEL.equals(info.getDownloadStatus())){

            downloadAdapter.updateProgress(info);
            Toast.makeText(this,"下载取消，已删除下载文件",Toast.LENGTH_LONG).show();

        }else if (DownloadInfo.DOWNLOAD_ERROR.equals(info.getDownloadStatus())){

            Toast.makeText(this,"下载出错",Toast.LENGTH_SHORT).show();
        }
    }

    public void chooseImages(int limitation){

        Matisse.from(this)
                .choose(MimeType.of(MimeType.JPEG, MimeType.PNG))//照片视频全部显示MimeType.allOf()
                .countable(true)//true:选中后显示数字;false:选中后显示对号
                .maxSelectable(limitation)//最大选择数量为9
                //.addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))//图片显示表格的大小
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)//图像选择和预览活动所需的方向
                .thumbnailScale(0.85f)//缩放比例
                .theme(R.style.Matisse_Zhihu)//主题  暗色主题 R.style.Matisse_Dracula
                .imageEngine(new MyGlideEngine())//图片加载方式，Glide4需要自定义实现
                .capture(true) //是否提供拍照功能，兼容7.0系统需要下面的配置
                //参数1 true表示拍照存储在共有目录，false表示存储在私有目录；参数2与 AndroidManifest中authorities值相同，用于适配7.0系统 必须设置
                .captureStrategy(new CaptureStrategy(true, "com.gordon.forum.fileprovider"))//存储到哪里
                .forResult(REQUEST_CODE_CHOOSE);//请求码
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE) {

            if(null != data) {
                List<Uri> mSelected = Matisse.obtainResult(data);
                // 可以同时插入多张图片
                for (Uri imageUri : mSelected) {
                    String imagePath = getPath(PostActivity.this, imageUri);
                    ImageItem imageItem = new ImageItem();
                    imageItem.path = imagePath;
                    contentImages.add(imageItem);
                }
            }else{
                Toast.makeText(this, "没有选择图片", Toast.LENGTH_SHORT).show();
            }

        } else if(requestCode == EX_FILE_PICKER_RESULT){
            ExFilePickerResult result = ExFilePickerResult.getFromIntent(data);
            if (result != null && result.getCount() > 0) {
                // Here is object contains selected files names and path
                Log.e("test", "onActivityResult: "
                        + "\n" + result.getPath()
                        + "\n" + result.getNames());
                boolean hasFolder = false;
                fileName.addAll(result.getNames());
                for(String name: fileName){
                    if(name.contains("."))
                        contentFiles.add(result.getPath() + name);
                    else
                        hasFolder = true;
                }
                if(hasFolder)
                    Toast.makeText(PostActivity.this,
                            "不支持文件夹", Toast.LENGTH_SHORT).show();
                }
        }
    }

    private String getPath(Context context, Uri uri) {
        String path = null;
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        if (cursor == null) {
            return null;
        }
        if (cursor.moveToFirst()) {
            try {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        return path;
    }

    private void pickMessagePhoto(List<ImageItem> contentImages){
        //TODO
        this.contentImages = contentImages;
        chooseImages(1);
    }

    private void pickMessageAttachment(List<String> contentFiles){
        //TODO
        this.contentFiles = contentFiles;
        chooseAttachment();
    }

    private void chooseAttachment(){
        ExFilePicker exFilePicker = new ExFilePicker();
        exFilePicker.start(this, EX_FILE_PICKER_RESULT);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
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
                case DELETING_LIKE:
                    like.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                    break;
                case DELETING_MESSAGE:
                    messageAdapter.notifyDataSetChanged();
            }
        }
    }

}
