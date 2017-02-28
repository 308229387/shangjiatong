package com.merchantplatform.model;

import android.app.Activity;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.Utils.Urls;
import com.callback.DialogCallback;
import com.dataStore.AppPrefersUtil;
import com.log.LogUmengAgent;
import com.log.LogUmengEnum;
import com.merchantplatform.R;
import com.merchantplatform.activity.SettingFeedbackActivity;
import com.merchantplatform.bean.TempResponse;
import com.okhttputils.OkHttpUtils;
import com.Utils.TitleBar;
import com.ta.utdid2.android.utils.StringUtils;
import com.utils.KeyboardUtil;
import com.utils.StringUtil;
import com.utils.ToastUtils;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by 58 on 2016/12/10.
 */

public class SettingFeedbackActivityModel extends BaseModel{
    private SettingFeedbackActivity context;

    private TitleBar tb_feedback_title;
    private EditText et_content,et_contact;
    private ImageView iv_contact_delete;
    private TextView tv_contact_alert;


    private boolean mSaveFlag = true;//是否保存草稿标示 默认保存，发送成功不保存。

    public SettingFeedbackActivityModel(SettingFeedbackActivity context){
        this.context =context;
    }

    public void initView(){
        tb_feedback_title = (TitleBar) context.findViewById(R.id.tb_feedback_title);
        et_content = (EditText) context.findViewById(R.id.et_content);
        et_contact = (EditText) context.findViewById(R.id.et_contact);
        iv_contact_delete = (ImageView) context.findViewById(R.id.iv_contact_delete);
        tv_contact_alert = (TextView) context.findViewById(R.id.tv_contact_alert);
    }

    public void initData(){
        initTitleData();
        initContentData();
    }

    private void initTitleData() {
        //设置透明状态栏
        tb_feedback_title.setImmersive(true);
        //设置背景颜色
        tb_feedback_title.setBackgroundColor(Color.WHITE);
        //设置左侧图标
        tb_feedback_title.setLeftImageResource(R.mipmap.title_back);
        //设置左侧点击事件
        tb_feedback_title.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                KeyboardUtil.hideSoftInput(context);
                context.onBackPressed();
            }
        });
        //设置主标题颜色
        tb_feedback_title.setTitleColor(Color.BLACK);
        //设置标题
        tb_feedback_title.setTitle("意见反馈");
        //设置右侧文案颜色
        tb_feedback_title.setActionTextColor(Color.GRAY);
        //设置右侧文案
        tb_feedback_title.addAction(new TitleBar.TextAction("提交") {
            @Override
            public void performAction(View view) {
                LogUmengAgent.ins().log(LogUmengEnum.LOG_SHEZHIXQY_YJFKTJ);
                gotoSubmit();
            }
        });

        //设置标题栏下划线
        tb_feedback_title.setDividerColor(Color.parseColor("#DFE0E1"));
    }

     private void initContentData(){
         String hisContent = AppPrefersUtil.getInstance().getFeedBackContent();
         et_content.setText(hisContent);
         et_content.setSelection(hisContent.length());
         if(!StringUtils.isEmpty(hisContent)){
             tb_feedback_title.removeAllActions();
             tb_feedback_title.setActionTextColor(Color.RED);
             tb_feedback_title.addAction(new TitleBar.TextAction("提交") {
                 @Override
                 public void performAction(View view) {
                     gotoSubmit();
                 }
             });
         }
     }


    private void gotoSubmit(){
        String content = et_content.getText().toString();
        String contact = et_contact.getText().toString();
        if (StringUtil.isEmpty(content)) {
            ToastUtils.showToast(context.getString(R.string.feedback_input_empty));
            return;
        }

        OkHttpUtils.get(Urls.PERSONAL_FEEDBACK)
                .params("suggest",content)
                .params("phone",contact)
                .execute(new feedbackCallback(context));
    }

    public void setListener(){
        et_content.addTextChangedListener(contentChangeListener);
        et_contact.addTextChangedListener(contactChangeListener);
        iv_contact_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_contact.setText("");
            }
        });
    }

    private TextWatcher contentChangeListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            int length = s.length();
            if(length > 0){
                tb_feedback_title.removeAllActions();
                tb_feedback_title.setActionTextColor(Color.RED);
                tb_feedback_title.addAction(new TitleBar.TextAction("提交") {
                    @Override
                    public void performAction(View view) {
                        gotoSubmit();
                    }
                });
            }else{
                tb_feedback_title.removeAllActions();
                tb_feedback_title.setActionTextColor(Color.GRAY);
                tb_feedback_title.addAction(new TitleBar.TextAction("提交") {
                    @Override
                    public void performAction(View view) {
                        gotoSubmit();
                    }
                });
            }
        }
    };

    private TextWatcher contactChangeListener =  new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
           int length = s.length();
            if(length > 0){
                iv_contact_delete.setVisibility(View.VISIBLE);
            }else{
                iv_contact_delete.setVisibility(View.GONE);
            }

            String phone = s.toString();

            if(!TextUtils.isEmpty(phone) && !StringUtil.isMobileNO(phone)){
                tv_contact_alert.setVisibility(View.VISIBLE);
            }else{
                tv_contact_alert.setVisibility(View.GONE);
            }
        }
    };

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

    private class feedbackCallback extends DialogCallback<TempResponse>{
        public feedbackCallback(Activity activity) {
            super(activity);
        }

        @Override
        public void onResponse(boolean isFromCache, TempResponse tempResponse, Request request, @Nullable Response response) {
               if(tempResponse != null){
                   String message = tempResponse.getMsg();
                   if(!StringUtil.isEmpty(message)){
                       ToastUtils.showToast(message);
                       mSaveFlag = false;
                       context.finish();
                   }
               }
        }
    }


}
