package com.gordon.forum.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 数据库版本号更新记录：
 *
 * 1：
 * create table tb_userlike
 *
 */

public class MyDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "forum.db";
    private static final int DB_VERSION = 1;

    public MyDatabaseHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    private void create_tb_userlike(SQLiteDatabase db) {
        db.execSQL("create table if not exists tb_userlike(" +
                "userid varchar, " +
                "postid integer, " +
                "primary key (userid, postid))");
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        //创建用户点赞帖子表
        create_tb_userlike(sqLiteDatabase);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        if (oldVersion >= newVersion)
            return;

        // 相邻版本间数据库的更新
        int version = oldVersion;

        if (version == 0) {
            create_tb_userlike(sqLiteDatabase);
            version = 1;
        }
    }
}
