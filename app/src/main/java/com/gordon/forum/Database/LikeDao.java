package com.gordon.forum.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

public class LikeDao {
    private MyDatabaseHelper helper;
    private Context context;

    public LikeDao(Context context) {
        helper = new MyDatabaseHelper(context);
        this.context = context;
    }

    /**
     * 查询一条记录
     * @param userid
     * @param postid
     * @return
     */
    public boolean queryLike(String userid, int postid) {

        SQLiteDatabase db = helper.getWritableDatabase();

        int results = 0;
        Cursor cursor = null;
        try {
            cursor = db.query("tb_userlike", null, "userid=? and postid=?",
                    new String[]{userid, postid+""}, null, null, null);
            results = cursor.getCount();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return results != 0;
    }

    /**
     * 添加一条记录
     * @param userid
     * @param postid
     * @return
     */
    public long insertLike(String userid, int postid) {

        //列表中已存在该记录
        if(queryLike(userid, postid))
            return 0;

        //新记录
        SQLiteDatabase db = helper.getWritableDatabase();
        String sql = "insert into tb_userlike(userid, postid) values(?, ?)";
        long ret = 0;

        SQLiteStatement stat = db.compileStatement(sql);
        db.beginTransaction();
        try {

            stat.bindString(1, userid);
            stat.bindString(2, postid+"");

            ret = stat.executeInsert();
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
            db.close();
        }
        return ret;
    }

    /**
     * 删除一条记录
     * @param userid
     * @param postid
     * @return
     */
    public int deleteLike(String userid, int postid) {

        SQLiteDatabase db = helper.getWritableDatabase();

        int ret = 0;
        try {
            ret = db.delete("tb_userlike", "userid=? and postid=?", new String[]{userid, postid+""});
        }

        catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null) {
                db.close();
            }
        }

        return ret;
    }

}
