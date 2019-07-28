package com.gordon.forum.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.gordon.forum.Adapter.MultiImageAdapter;
import com.gordon.forum.Model.ImageItem;
import com.gordon.forum.R;
import com.gordon.forum.Util.GlideImageLoader;
import com.gordon.forum.Util.MyGlideEngine;
import com.gordon.forum.Util.UrlHelper;
import com.lzy.ninegrid.NineGridView;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NewPostActivity extends AppCompatActivity {

    private String courseId;
    private String userId;

    private static final int REQUEST_CODE_CHOOSE = 3;

    private ArrayList<ImageItem> images = new ArrayList<>();
    //adapter是核心，添加按钮的处理删除的处理都在里面，后面会说，别急
    private MultiImageAdapter adapter;

    private EditText editText;
    private GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        //设置状态栏，工具栏
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_new_post_toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(null != actionBar)
            actionBar.setDisplayShowTitleEnabled(false);
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        addMiddleTitle(this, "发帖", toolbar);

        Intent intent = getIntent();
        courseId = intent.getIntExtra("course_id", -1)+"";
        userId = intent.getStringExtra("email");

        editText = (EditText) findViewById(R.id.activity_new_post_editext);
        adapter = new MultiImageAdapter(this);

        gridView = (GridView) findViewById(R.id.activity_new_post_gridView);
        gridView.setAdapter(adapter);

        NineGridView.setImageLoader(new GlideImageLoader());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_new_post_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        if (item.getItemId() == R.id.action_publish_post) {
            publishPost();
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

    public void publishPost(){

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

        multiBuilder.addFormDataPart("method", "create");
        multiBuilder.addFormDataPart("email", userId);
        multiBuilder.addFormDataPart("course_id", courseId);
        multiBuilder.addFormDataPart("question", editText.getText().toString());
        multiBuilder.addFormDataPart("image_num", images.size()+"");

        MultipartBody requestBody = multiBuilder.build();

        OkHttpClient client = new OkHttpClient();
        final Request request = new Request.Builder()
                .url(UrlHelper.URL_FOR_POSTING_POST)
                .post(requestBody)
                .build();

        Log.e("test", "onResponse: 发送内容：" + requestBody.parts());

        client.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.e("test", "onFailure: 发帖失败");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Log.e("test", "onResponse: 发帖成功");
                Log.e("test", "onResponse: 返回内容：" + response.body().string());
            }
        });

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
        if (data != null && requestCode == REQUEST_CODE_CHOOSE) {

            List<Uri> mSelected = Matisse.obtainResult(data);
            // 可以同时插入多张图片
            for (Uri imageUri : mSelected) {
                String imagePath = getPath(NewPostActivity.this, imageUri);
                ImageItem imageItem = new ImageItem();
                imageItem.path = imagePath;
                images.add(imageItem);
            }

            //拿到图片数据后把images传过去
            adapter = new MultiImageAdapter(this,images);
            gridView.setAdapter(adapter);

        } else {
            Toast.makeText(this, "没有选择图片", Toast.LENGTH_SHORT).show();
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

}
