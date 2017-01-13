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
import android.widget.Button;
import android.widget.TextView;

import com.merchantplatform.R;

/**
 * Created by 58 on 2017/1/12.
 */

public class UpdateDialog extends Dialog implements View.OnClickListener {
    private Context mContext;
    private TextView tv_content;
    private Button btnOk;
    private Button btnCancel;
    private View.OnClickListener mOkListener;
    private View.OnClickListener mCancelListener;

    private String mIsForceUpdate;

    public UpdateDialog(Context context) {
        super(context, R.style.DialogWithAnim);
        mContext = context;
        setCancelable(false);
        setCanceledOnTouchOutside(false);
    }

    public void setForceUpdateFlag(String flag) {
        mIsForceUpdate = flag;
    }

    public void setOkOnClickListener(View.OnClickListener listener){
        mOkListener = listener;
    }

    public void setCancelOnClickListener(View.OnClickListener listener){
        mCancelListener = listener;
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
        int dialogWidth = (int) mContext.getResources().getFraction(
                R.fraction.common_dialog_width, width, width);
        setContentView(view,new ViewGroup.LayoutParams(dialogWidth,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        btnOk = (Button) view.findViewById(R.id.btn_ok);
        btnCancel = (Button) view.findViewById(R.id.btn_cancel);
        tv_content =  (TextView) findViewById(R.id.tv_content);
        btnOk.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
    }

    private int getLayoutId(){
        return R.layout.dialog_update;
    }

    @Override
    public void onClick(View v) {
       switch (v.getId()){
           case R.id.btn_ok:
               if(mOkListener != null){
                   mOkListener.onClick(v);
               }
               dismiss();
               break;
           case R.id.btn_cancel:
               if (mCancelListener != null){
                   mCancelListener.onClick(v);
               }
               dismiss();
               break;
       }
    }

    @Override
    public void show() {
        super.show();
        if ("1".equals(mIsForceUpdate)) {
            btnCancel.setVisibility(View.GONE);
//            btnOk.setBackgroundResource(R.drawable.dialog_red_single_btn_selector);
        } else {
            btnCancel.setVisibility(View.VISIBLE);
        }
    }
}
