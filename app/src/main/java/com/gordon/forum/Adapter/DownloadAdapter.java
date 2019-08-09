package com.gordon.forum.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.gordon.forum.Model.DownloadInfo;
import com.gordon.forum.R;
import com.gordon.forum.Util.DownloadManager;

import java.util.ArrayList;
import java.util.List;

public class DownloadAdapter extends BaseAdapter {

    private List<DownloadInfo> mdata = new ArrayList<>();
    private LayoutInflater inflater;
    private OnDownloadCacelListener onDownloadCacelListener;

    public DownloadAdapter(Context context, List<DownloadInfo> mdata) {
        this.mdata = mdata;
        inflater = LayoutInflater.from(context);
    }

    /**
     * 更新下载进度
     * @param info
     */
    public void updateProgress(DownloadInfo info){
        for (int i = 0; i < mdata.size(); i++){
            if (mdata.get(i).getUrl().equals(info.getUrl())){
                mdata.set(i,info);
                notifyDataSetChanged();
                Log.e("测试", "updateProgress: 调用");
                break;
            }
        }
    }

    @Override
    public int getCount() {
        return mdata.size();
    }

    @Override
    public Object getItem(int i) {
        return mdata.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View convertView, final ViewGroup viewGroup) {
        final DownloadAdapter.UploadHolder hv;
        if (null==convertView){
            hv = new DownloadAdapter.UploadHolder();
            convertView = inflater.inflate(R.layout.item_download_layout,null,false);
            hv.main_progress = convertView.findViewById(R.id.main_progress);
            hv.main_btn_startandpause = convertView.findViewById(R.id.main_btn_startandpause);
            hv.main_btn_cancel = convertView.findViewById(R.id.main_btn_cancel);
            convertView.setTag(hv);
        }else {
            hv = (DownloadAdapter.UploadHolder) convertView.getTag();
        }
        final DownloadInfo info = mdata.get(i);
        if (DownloadInfo.DOWNLOAD_CANCEL.equals(info.getDownloadStatus())){
            hv.main_progress.setProgress(0);
        }else if (DownloadInfo.DOWNLOAD_OVER.equals(info.getDownloadStatus())){
            hv.main_progress.setProgress(hv.main_progress.getMax());
        }else {
            if (info.getTotal() == 0){
                hv.main_progress.setProgress(0);
            }else {
                double d = info.getProgress() * hv.main_progress.getMax() / (double)info.getTotal();
                hv.main_progress.setProgress((int) d);
            }
        }
        hv.main_btn_startandpause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mdata.get(i).isClickStart()) {
                    ((ImageButton) view).setImageResource(R.drawable.ic_pause_black_24dp);
                    DownloadManager.getInstance().download(info.getUrl());
                    mdata.get(i).setClickStart(false);
                }
                else{
                    ((ImageButton) view).setImageResource(R.drawable.ic_play_arrow_black_24dp);
                    DownloadManager.getInstance().pauseDownload(info.getUrl());
                    mdata.get(i).setClickStart(true);
                }
            }
        });

        hv.main_btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DownloadManager.getInstance().cancelDownload(info);
                hv.main_btn_startandpause.setImageResource(R.drawable.ic_play_arrow_black_24dp);
            }
        });

        return convertView;
    }

    public class UploadHolder{

        private ProgressBar main_progress;
        private ImageButton main_btn_startandpause;
        private ImageButton main_btn_cancel;
        private boolean clickStart;

    }

    public void setOnDownloadCacelListener(OnDownloadCacelListener onDownloadCacelListener){
        this.onDownloadCacelListener = onDownloadCacelListener;
    }

    public interface OnDownloadCacelListener{
        public void onDownloadCancel(int position);
    }
}
