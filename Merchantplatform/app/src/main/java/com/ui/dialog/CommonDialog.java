package com.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.merchantplatform.R;
import com.utils.StringUtil;

/**
 * Created by 58 on 2016/12/19.
 */

public class CommonDialog extends Dialog implements View.OnClickListener {
    private  Context mContext;
    private TextView tv_content;
    private TextView tv_sure;
    private TextView tv_cancel;
    private View v_divider;
    private OnDialogClickListener listener;

    private String mContent;
    private String mBtnSureText;
    private String mBtnCancelText;
    private int mBtnSureVisible = View.VISIBLE;

    public CommonDialog(Context context) {
        super(context, R.style.DialogWithAnim);
        mContext = context;
    }

    public CommonDialog(Context context, int theme){
        super(context,theme);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
    }

    public void setContent(String content){
        mContent = content;
    }

    public void setBtnSureText(String btnOkText){
        mBtnSureText= btnOkText;
    }

    public void setBtnSureVisible(int visible){
        mBtnSureVisible = visible;
    }

    public void setBtnCancelText(String btnCancelText){
        mBtnCancelText = btnCancelText;
    }

    public void setOnDialogClickListener(OnDialogClickListener listener){
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initWindowAttrs();
        initViews();
    }

    private void initWindowAttrs(){
        Window dialogWindow = getWindow();
        dialogWindow.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        dialogWindow.setAttributes(lp);
    }

     private void initViews(){
         LayoutInflater inflater = LayoutInflater.from(mContext);
         View view = inflater.inflate(getLayoutId(),null);
         int width = mContext.getResources().getDisplayMetrics().widthPixels;
         int dialogWidth = (int)mContext.getResources().getFraction(
                 R.fraction.common_dialog_width,width,width);
         setContentView(view,new ViewGroup.LayoutParams(dialogWidth,
                 ViewGroup.LayoutParams.WRAP_CONTENT));
         tv_content = (TextView) findViewById(R.id.tv_content);
         tv_cancel = (TextView) findViewById(R.id.tv_cancel);
         tv_sure = (TextView) findViewById(R.id.tv_sure);
         v_divider = findViewById(R.id.v_divider);
         tv_cancel.setOnClickListener(this);
         tv_sure.setOnClickListener(this);
     }

    private int getLayoutId(){
        return R.layout.dialog_common;
    }

    @Override
    public void onClick(View v) {
       switch(v.getId()){
           case R.id.tv_sure:
               dismiss();
               if(listener != null){
                   listener.onDialogClickSure();
               }
               break;
           case R.id.tv_cancel:
               dismiss();
               if(listener != null){
                   listener.onDialogClickCancel();
               }
               break;
       }
    }

    @Override
    public void show() {
        super.show();
        if (!StringUtil.isEmpty(mContent)) {
            tv_content.setText(mContent);
        }
        if (View.VISIBLE == mBtnSureVisible) {
            if (!StringUtil.isEmpty(mBtnSureText)) {
                tv_sure.setText(mBtnSureText);
            }
        } else {
            v_divider.setVisibility(View.GONE);
            tv_sure.setVisibility(View.GONE);
        }
        if (tv_sure.getVisibility() == View.GONE && tv_cancel.getVisibility() == View.VISIBLE) {
            tv_cancel.setBackgroundResource(R.drawable.dialog_white_single_btn_selector);
        }
        if (!StringUtil.isEmpty(mBtnCancelText)) {
            tv_cancel.setText(mBtnCancelText);
        }
    }

    public interface OnDialogClickListener{
        void onDialogClickSure();
        void onDialogClickCancel();
    }
}
