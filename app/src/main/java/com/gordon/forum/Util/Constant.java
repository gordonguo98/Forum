package com.gordon.forum.Util;

import android.os.Environment;

import java.io.File;

/**
 * Created by zs
 * Date：2018年 09月 12日
 * Time：13:54
 * —————————————————————————————————————
 * About:
 * —————————————————————————————————————
 * 修改：2019年 08月 08日
 * by Gordon
 */
public class Constant {

    /**
     * 下载路径
     */
    public final static String FILE_PATH = Environment.getExternalStorageDirectory()
            .getAbsolutePath()+"/Forum/Download/";

    public static String getFilePath(){
        File file = new File(FILE_PATH);
        if(!file.exists()){
            try{
                file.mkdirs();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return FILE_PATH;
    }

    /**
     * 删除文件
     *
     * @param fileName
     * @return
     */
    public static boolean deleteFile(String fileName) {
        boolean status;
        SecurityManager checker = new SecurityManager();
        File file = new File(FILE_PATH + fileName);
        if (file.exists()){
            checker.checkDelete(file.toString());
            if (file.isFile()) {
                try {
                    file.delete();
                    status = true;
                } catch (SecurityException se) {
                    se.printStackTrace();
                    status = false;
                }
            } else
                status = false;
        }else
            status = false;
        return status;
    }

}