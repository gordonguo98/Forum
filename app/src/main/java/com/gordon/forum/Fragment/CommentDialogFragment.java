package com.gordon.forum.Fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.gordon.forum.R;

/*
 *作者: showzeng
 *链接: https://showzeng.itscoder.com/android/2017/08/11/the-imitation-of-the-international-weibo-comment-box.html
 */

public class CommentDialogFragment extends DialogFragment implements View.OnClickListener{

    private Dialog mDialog;
    private EditText commentEditText;
    private ImageView photoButton;
    private ImageView sendButton;

    private OnSendListener onSendListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // 自定义 style BottomDialog
        mDialog = new Dialog(getActivity(), R.style.BottomDialog);

        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.dialog_fragment_comment_layout);

        // 外部点击设置为可以取消
        mDialog.setCanceledOnTouchOutside(true);

        Window window = mDialog.getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();

        // 布局属性位于整个窗口底部
        layoutParams.gravity = Gravity.BOTTOM;

        // 布局属性宽度填充满整个窗口宽度，默认是有 margin 值的
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(layoutParams);

        commentEditText = (EditText) mDialog.findViewById(R.id.edit_comment);
        photoButton = (ImageView) mDialog.findViewById(R.id.image_btn_photo);
        sendButton = (ImageView) mDialog.findViewById(R.id.image_btn_comment_send);

        photoButton.setOnClickListener(this);
        sendButton.setOnClickListener(this);

        return mDialog;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_btn_photo:
                Toast.makeText(getActivity(), "Pick photo Activity", Toast.LENGTH_SHORT).show();
                break;
            case R.id.image_btn_comment_send:
                Toast.makeText(getActivity(), commentEditText.getText().toString(), Toast.LENGTH_SHORT).show();
                if(null != onSendListener)
                    onSendListener.OnSend(commentEditText.getText().toString());
                dismiss();
                break;
            default:
                break;
        }
    }

    public void setOnSendListener(OnSendListener onSendListener) {
        this.onSendListener = onSendListener;
    }

    public interface OnSendListener{
        public void OnSend(String content);
    }
}