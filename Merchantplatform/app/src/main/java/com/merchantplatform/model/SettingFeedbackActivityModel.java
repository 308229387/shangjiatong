package com.merchantplatform.model;

import android.app.Activity;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;

import com.callback.DialogCallback;
import com.dataStore.AppPrefersUtil;
import com.merchantplatform.R;
import com.merchantplatform.activity.SettingFeedbackActivity;
import com.okhttputils.OkHttpUtils;
import com.ui.TitleBar;
import com.utils.KeyboardUtil;
import com.utils.StringUtil;
import com.utils.ToastUtils;
import com.utils.Urls;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by 58 on 2016/12/10.
 */

public class SettingFeedbackActivityModel extends BaseModel{
    private SettingFeedbackActivity context;

    private TitleBar tb_feedback_title;
    private EditText et_content,et_contact;

    private boolean mSaveFlag = true;//是否保存草稿标示 默认保存，发送成功不保存。

    public SettingFeedbackActivityModel(SettingFeedbackActivity context){
        this.context =context;
    }

    public void initView(){
        tb_feedback_title = (TitleBar) context.findViewById(R.id.tb_feedback_title);
        et_content = (EditText) context.findViewById(R.id.et_content);
        et_contact = (EditText) context.findViewById(R.id.et_contact);
    }

    public void initData(){
        initTitleData();
        initContentData();
    }

    private void initTitleData() {
        //设置透明状态栏
        tb_feedback_title.setImmersive(true);
        //设置背景颜色
        tb_feedback_title.setBackgroundColor(Color.parseColor("#64b4ff"));
        //设置左侧文案
        tb_feedback_title.setLeftText("返回");
        //设置左侧点击事件
        tb_feedback_title.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KeyboardUtil.hideSoftInput(context);
                context.onBackPressed();
            }
        });
        //设置标题
        tb_feedback_title.setTitle("意见反馈");
        //设置右侧文案
        tb_feedback_title.addAction(new TitleBar.TextAction("提交") {
            @Override
            public void performAction(View view) {
                gotoSubmit();
            }
        });
    }

     private void initContentData(){
         String hisContent = AppPrefersUtil.getInstance().getFeedBackContent();
         et_content.setText(hisContent);
         et_content.setSelection(hisContent.length());
     }

    private void gotoSubmit(){
        String content = et_content.getText().toString();
        String contact = et_contact.getText().toString();
        if (StringUtil.isEmpty(content)) {
            ToastUtils.showToast(context.getString(R.string.feedback_input_empty));
            return;
        }

        OkHttpUtils.get(Urls.PERSONAL_FEEDBACK)
                .params("","")
                .execute(new feedbackCallback(context));
    }

    public void destoryOperate(){
        try{
            if(mSaveFlag){
                String content = et_content.getText().toString();
                AppPrefersUtil.getInstance().saveFeedBackContent(content);
            }else{
                AppPrefersUtil.getInstance().saveFeedBackContent("");
            }
        }catch (Exception e){
            e.printStackTrace();

        }
    }

    private class feedbackCallback extends DialogCallback<String>{
        public feedbackCallback(Activity activity) {
            super(activity);
        }

        @Override
        public void onResponse(boolean isFromCache, String s, Request request, @Nullable Response response) {
            ToastUtils.showToast(context.getString(R.string.feedback_submit_success));
            if (tb_feedback_title != null) {
                mSaveFlag = false;
                context.finish();
            }
        }

        @Override
        public void onError(boolean isFromCache, Call call, @Nullable Response response, @Nullable Exception e) {
            if (e == null) {
                ToastUtils.showToast(context.getString(R.string.feedback_submit_error));
            } else {
                ToastUtils.showToast(e.getMessage());
            }

            context.finish();
        }
    }


}
