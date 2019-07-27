package com.gordon.forum.Util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class BitmapUtil implements Runnable{

    private String url;
    private Bitmap profilePhotoBitmap;

    public BitmapUtil(){}

    public Bitmap getProfilePhoto(String url){

        this.url = url;
        profilePhotoBitmap = null;

        Thread downloadImg = new Thread(this);
        downloadImg.start();
        while(downloadImg.getState() != Thread.State.TERMINATED);
        //缩放
        if(null != profilePhotoBitmap) {
            int width = profilePhotoBitmap.getWidth();
            int height = profilePhotoBitmap.getHeight();
            int newWidth = 32;
            int newHeight = 32;
            float scaleWidth = ((float) newWidth) / width;
            float scaleHeight = ((float) newHeight) / height;
            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeight);
            return Bitmap.createBitmap(profilePhotoBitmap, 0, 0, width, height, matrix, true);
        }
        return null;
    }

    @Override
    public void run() {
        try {
            URL mUrl = new URL(url);
            URLConnection conn = mUrl.openConnection();
            conn.connect();
            InputStream in;
            in = conn.getInputStream();
            profilePhotoBitmap = BitmapFactory.decodeStream(in);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("test", "getProfilePhoto: 错误！");
        }
    }
}
