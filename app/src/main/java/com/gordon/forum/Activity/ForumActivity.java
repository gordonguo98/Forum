package com.gordon.forum.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gordon.forum.Adapter.PostAdapter;
import com.gordon.forum.Model.Post;
import com.gordon.forum.Model.User;
import com.gordon.forum.R;
import com.lzy.ninegrid.NineGridView;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ForumActivity extends AppCompatActivity {

    private String courseId;
    private String userId;
    private String courseName;
    private int postNum;

    List<Post> postList = new ArrayList<>();

    String url = "http://image.baidu.com/search/index?tn=baiduimage&ct=201326592&lm=-1&cl=2&ie="+
            "gb18030&word=%CD%BC%C6%AC%20%CD%B7%CF%F1&fr=ala&oriquery=%E5%9B%BE%E7%89%87%20%E5%"+
            "A4%B4%E5%83%8F&ala=1&alatpl=portait&pos=0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum);
        //设置状态栏，工具栏
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_forum_toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));

        Intent intent = getIntent();
        if(null != intent){
            courseId = intent.getStringExtra("courseId");
            userId = intent.getStringExtra("userId");
            //添加中间标题
            addMiddleTitle(this, courseId, toolbar);
        }
        //getData(url);
        initImgLoadingOptions();
        initPostList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_forum_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        if (item.getItemId() == R.id.action_new_post) {
            Intent intent = new Intent(ForumActivity.this, NewPostActivity.class);
            startActivity(intent);
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

    private void getData(String url){

        try {
            //从服务器获取数据
            OkHttpClient okHttpClient = new OkHttpClient.Builder().readTimeout(5, TimeUnit.SECONDS).build();
            final Request request = new Request.Builder()
                    .url(url)
                    .get()//默认就是GET请求，可以不写
                    .build();
            Call call = okHttpClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e("TEST", "onFailure: 请求失败");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Log.d("TEST", "onResponse: " + response.body().string());
                }
            });
            //等待请求线程，否则主线程结束无法看到请求结果
            Thread.currentThread().join(5000);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public boolean deletePost() {
        //此处处理删除帖子逻辑，由于需要后台服务，故暂时不实现
        return true;
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

        NineGridView.setImageLoader(new PicassoImageLoader());
    }

    private void initPostList(){

        //测试数据，正常情况下应通过网络请求获得
        createTestData();

        final SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_post_list);
        refreshLayout.setColorSchemeResources(R.color.colorPrimary);
        refreshLayout.setProgressBackgroundColorSchemeResource(android.R.color.white);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.post_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        final PostAdapter postAdapter = new PostAdapter(this, postList);
        postAdapter.setOnItemClickListener(new PostAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Toast.makeText(view.getContext(), "position: " + position, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(ForumActivity.this, PostActivity.class);
                intent.putExtra("postId", postList.get(position).getPostId());
                startActivity(intent);
            }
        });
        postAdapter.setOnItemLongClickListener(new PostAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, final int position) {
                //if(postList.get(position).getCreator().getUserId().equals(userId)){
                    //如果帖子是自己发布的，则可以删除
                //}

                //为测试，以下代码不考虑帖子是不是本人的
                final AlertDialog.Builder normalDialog = new AlertDialog.Builder(ForumActivity.this);
                normalDialog.setIcon(R.drawable.ic_delete_forever_black_24dp);
                normalDialog.setTitle("删除帖子");
                normalDialog.setMessage("确认删除?");
                normalDialog.setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //删除帖子
                                if(deletePost()) {
                                    postList.remove(position);
                                    postAdapter.notifyDataSetChanged();
                                }
                                else
                                    Toast.makeText(ForumActivity.this, "删除失败", Toast.LENGTH_LONG).show();
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
        });

        recyclerView.addItemDecoration(new SpacesItemDecoration(14));//设定间隔
        recyclerView.setAdapter(postAdapter);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //下拉刷新帖子列表
                //getData(url);
                postAdapter.notifyDataSetChanged();
                refreshLayout.setRefreshing(false);
            }
        });
    }

    private void createTestData(){
        List<Bitmap> profile_photos = new ArrayList<>();
        for(int i = 1; i <= 10; i++){
            profile_photos.add(Bitmap.createBitmap(BitmapFactory.decodeStream(getClass().getResourceAsStream("/res/drawable/p" + i + ".png"))));
        }
        List<User> users = new ArrayList<>();
        for(int i = 0; i < 10; i++){
            users.add(new User(i+"", profile_photos.get(i), "00000000", "张三", "北京大学", "软件工程", 2017));
        }
        for(int i = 0; i < 10; i++){
            postList.add(new Post(i+"", users.get(i), "请问今天布置了啥作业？", new Date(), null, 10, 10));
        }
    }

}

class SpacesItemDecoration extends RecyclerView.ItemDecoration {
    private int space;

    public SpacesItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {
        outRect.left = space;
        outRect.right = space;
        outRect.bottom = space;

        // Add top margin only for the first item to avoid double space between items
        if (parent.getChildLayoutPosition(view) == 0)
            outRect.top = space;
    }
}
