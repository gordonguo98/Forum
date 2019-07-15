package com.gordon.forum.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.gordon.forum.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //设置状态栏
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));

        final EditText courseIdText = (EditText) findViewById(R.id.courseID_text);
        final EditText userIdText = (EditText) findViewById(R.id.userID_text);
        Button enterBtn = (Button) findViewById(R.id.enter_btn);
        enterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(courseIdText.getText() != null && userIdText.getText() != null){
                    Intent intent = new Intent(MainActivity.this, ForumActivity.class);
                    intent.putExtra("courseId", courseIdText.getText().toString());
                    intent.putExtra("userId", userIdText.getText().toString());
                    startActivity(intent);
                }
            }
        });
    }
}
