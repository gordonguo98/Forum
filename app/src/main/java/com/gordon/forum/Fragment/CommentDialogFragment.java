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

import com.gordon.forum.Model.ImageItem;
import com.gordon.forum.R;

import java.util.ArrayList;
import java.util.List;

/*
 *作者: showzeng
 *链接: https://showzeng.itscoder.com/android/2017/08/11/the-imitation-of-the-international-weibo-comment-box.html
 *
 * Modified by Gordon
 * Date: 2019年 08月 09日
 * Time: 19:40
 */

public class CommentDialogFragment extends DialogFragment implements View.OnClickListener{

    private Dialog mDialog;
    private EditText commentEditText;
    private ImageView photoButton;
    private ImageView attachmentButton;
    private ImageView sendButton;

    private OnSendListener onSendListener;
    private OnPhotoListener onPhotoListener;
    private OnAttachmentListener onAttachmentListener;

    private List<ImageItem> contentImages = new ArrayList<>();
    private List<String> contentFiles = new ArrayList<>();

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
        attachmentButton = (ImageView) mDialog.findViewById(R.id.image_btn_attachment);
        sendButton = (ImageView) mDialog.findViewById(R.id.image_btn_comment_send);

        photoButton.setOnClickListener(this);
        attachmentButton.setOnClickListener(this);
        sendButton.setOnClickListener(this);

        return mDialog;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_btn_photo:
                Toast.makeText(getActivity(), "Choose photos Activity", Toast.LENGTH_SHORT).show();
                if(null != onPhotoListener)
                    onPhotoListener.OnPhoto(contentImages);
                break;
            case R.id.image_btn_attachment:
                Toast.makeText(getActivity(), "Choose Files Activity", Toast.LENGTH_SHORT).show();
                if(null != onAttachmentListener)
                    onAttachmentListener.OnAttachment(contentFiles);
                break;
            case R.id.image_btn_comment_send:
                Toast.makeText(getActivity(), commentEditText.getText().toString(), Toast.LENGTH_SHORT).show();
                if(null != onSendListener)
                    onSendListener.OnSend(commentEditText.getText().toString(), contentImages, contentFiles);
                dismiss();
                break;
            default:
                break;
        }
    }

    public void setOnSendListener(OnSendListener onSendListener) {
        this.onSendListener = onSendListener;
    }

    public void setOnPhotoListener(OnPhotoListener onPhotoListener) {
        this.onPhotoListener = onPhotoListener;
    }

    public void setOnAttachmentListener(OnAttachmentListener onAttachmentListener) {
        this.onAttachmentListener = onAttachmentListener;
    }

    public interface OnSendListener{
        public void OnSend(String content, List<ImageItem> contentImages, List<String> contentFiles);
    }

    public interface OnPhotoListener{
        public void OnPhoto(List<ImageItem> contentImages);
    }

    public interface OnAttachmentListener{
        public void OnAttachment(List<String> contentFiles);
    }
}