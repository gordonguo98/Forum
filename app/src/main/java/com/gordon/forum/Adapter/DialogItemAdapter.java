package com.gordon.forum.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gordon.forum.R;

import java.util.List;

public class DialogItemAdapter extends BaseAdapter {

    private Context mContext;
    private List<String> fileList;
    private LayoutInflater inflater;
    private OnDialogItemClickListener onDialogItemClickListener;

    public DialogItemAdapter(Context context, List<String> fileList){
        mContext = context;
        this.fileList = fileList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return fileList != null?fileList.size():0;
    }

    @Override
    public Object getItem(int i) {
        return fileList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View convertView, ViewGroup viewGroup) {
        MyViewHolder hv;
        if (null==convertView){
            hv = new MyViewHolder();
            convertView = inflater.inflate(R.layout.adapter_dialog_view,null,false);
            hv.tvName = convertView.findViewById(R.id.adapter_dialog_tv);
            hv.ivDelete = convertView.findViewById(R.id.adapter_dialog_iv);
            hv.ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(null != onDialogItemClickListener){
                        onDialogItemClickListener.onDialogItemClick(i);
                    }
                }
            });
            convertView.setTag(hv);
        }else {
            hv = (MyViewHolder) convertView.getTag();
        }
        hv.tvName.setText(fileList.get(i));
        return convertView;
    }

    public void setOnDialogItemClickListener(OnDialogItemClickListener onDialogItemClickListener){
        this.onDialogItemClickListener = onDialogItemClickListener;
    }

    public static class MyViewHolder {
        TextView tvName;
        ImageView ivDelete;
    }

    public interface OnDialogItemClickListener{
        public void onDialogItemClick(int position);
    }
}
