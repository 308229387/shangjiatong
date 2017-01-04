package com.merchantplatform.model;


import android.app.Activity;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.Utils.TitleBar;
import com.callback.DialogCallback;
import com.log.LogUmengAgent;
import com.log.LogUmengEnum;
import com.merchantplatform.R;
import com.merchantplatform.activity.MobileBindChangeActivity;
import com.merchantplatform.bean.TempResponse;
import com.merchantplatform.bean.UpdateMobileResponse;
import com.okhttputils.OkHttpUtils;
import com.ui.dialog.CommonDialog;
import com.utils.StringUtil;
import com.utils.ToastUtils;
import com.utils.Urls;
import com.utils.UserUtils;

import okhttp3.Request;
import okhttp3.Response;


/**
 * Created by 58 on 2016/12/9.
 */

public class MobileBindChangeModel extends BaseModel implements View.OnClickListener{
    private MobileBindChangeActivity context;

    private TitleBar tb_Change_title;
    private EditText et_now_bind_mobile, et_now_validate_code, et_new_validate_mobile;
    private TextView tv_new_mobile_alert;
    private Button btn_getCode, btn_submit;

    private  static final String SUCCESS = "0";//成功

    private String mobile;

    private boolean newPhoneSatisfy;
    private boolean validateCodeSatisfy;
    int time = 60;

    private CommonDialog mConfirmDialog ;

    public MobileBindChangeModel(MobileBindChangeActivity context){
        this.context = context;
    }

    public void initView(){
        tb_Change_title = (TitleBar) context.findViewById(R.id.tb_Change_title);
        et_now_bind_mobile = (EditText) context.findViewById(R.id.now_bind_mobile);
        et_now_validate_code = (EditText) context.findViewById(R.id.now_validate_code);
        et_new_validate_mobile = (EditText) context.findViewById(R.id.new_validate_mobile);
        btn_getCode = (Button) context.findViewById(R.id.btn_getCode);
        tv_new_mobile_alert = (TextView) context.findViewById(R.id.tv_new_mobile_alert);
        btn_submit = (Button) context.findViewById(R.id.btn_submit);
    }

    public void initData(){
        initTitleData();
        setNowBindMobile();
        setSubmitState();
    }

    private void initTitleData() {
        //设置透明状态栏
        tb_Change_title.setImmersive(true);
        //设置背景颜色
        tb_Change_title.setBackgroundColor(Color.WHITE);
        //设置左侧图标
        tb_Change_title.setLeftImageResource(R.mipmap.title_back);
        //设置标题
        tb_Change_title.setTitle("更改手机绑定");
        //设置标题颜色
        tb_Change_title.setTitleColor(Color.BLACK);
        //设置左侧点击事件
        tb_Change_title.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.onBackPressed();
            }
        });
        //设置标题栏下划线
        tb_Change_title.setDividerColor(Color.parseColor("#DFE0E1"));
    }


    private void setNowBindMobile(){
        if (TextUtils.isEmpty(mobile)) {
            mobile = UserUtils.getMobile();
            et_now_bind_mobile.setText(mobile);
        } else {
            et_now_bind_mobile.setText(mobile);
        }
    }


    public void setListener(){
        et_now_validate_code.addTextChangedListener(codeChangeListener);
        btn_getCode.setOnClickListener(this);
        et_new_validate_mobile.addTextChangedListener(newMobileChangeListener);
        btn_submit.setOnClickListener(this);
    }

    private TextWatcher codeChangeListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            int length = s.length();
            if (length > 0) {
                validateCodeSatisfy = true;
            } else {
                validateCodeSatisfy = false;
            }
            checkSubmitEnabled(validateCodeSatisfy, newPhoneSatisfy);
        }
    };

    private TextWatcher newMobileChangeListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String phone = s.toString();

            if(!TextUtils.isEmpty(phone) && StringUtil.isMobileNO(phone)){
                newPhoneSatisfy = true;
                tv_new_mobile_alert.setVisibility(View.GONE);
            }else{
                newPhoneSatisfy = false;
                tv_new_mobile_alert.setVisibility(View.VISIBLE);

            }
            checkSubmitEnabled(validateCodeSatisfy,newPhoneSatisfy);
        }
    };

    private void checkSubmitEnabled(boolean validateCodeSatisfy, boolean newPhoneSatisfy) {
        if(validateCodeSatisfy && newPhoneSatisfy){
            btn_submit.setEnabled(true);
            btn_submit.setTextColor(Color.parseColor("#FDC9C0"));
            btn_submit.setBackgroundResource(R.drawable.submit_btn_bg);
        }else{
            btn_submit.setEnabled(false);
            btn_submit.setTextColor(Color.parseColor("#FFFFFF"));
            btn_submit.setBackgroundResource(R.drawable.submit_bg_gray);
        }
    }

    private void setSubmitState(){
        btn_submit.setEnabled(false);
        btn_submit.setTextColor(Color.parseColor("#FFFFFF"));
        btn_submit.setBackgroundResource(R.drawable.submit_bg_gray);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_getCode://获取验证码
                LogUmengAgent.ins().log(LogUmengEnum.LOG_SHEZHIXQY_HQYZHM);
                getCode(et_now_bind_mobile.getText().toString());
                break;
            case R.id.btn_submit://提交
                LogUmengAgent.ins().log(LogUmengEnum.LOG_SHEZHIXQY_SJHMGGTJ);
                submit();
                break;
        }
    }

    private void getCode(String phone){
        OkHttpUtils.get(Urls.GET_VALIDATE_CODE)
                .params("phone",phone)
                .execute(new getCodeCallback(context));
    }

    private void submit(){
        if(TextUtils.equals(et_now_bind_mobile.getText().toString(),et_new_validate_mobile.getText().toString())){
            ToastUtils.makeImgAndTextToast(context, "两次修改的手机号不能一致", R.mipmap.validate_error, 0).show();
            return ;
        }else{
            initConfirmChangeDialog();
        }
    }

    public void initConfirmChangeDialog() {

        if (mConfirmDialog != null && mConfirmDialog.isShowing()) {
            mConfirmDialog.dismiss();
        }
        mConfirmDialog = new CommonDialog(context);
        mConfirmDialog.setCancelable(false);
        mConfirmDialog.setCanceledOnTouchOutside(false);
        mConfirmDialog.setContent("确定要把手机号修改为:"+et_new_validate_mobile.getText().toString());
        mConfirmDialog.setOnDialogClickListener(new CommonDialog.OnDialogClickListener() {
            @Override
            public void onDialogClickSure() {
                mConfirmDialog.dismiss();
                updateMobile(et_now_bind_mobile.getText().toString(), et_new_validate_mobile.getText().toString(), et_now_validate_code.getText().toString());
            }

            @Override
            public void onDialogClickCancel() {
                mConfirmDialog.dismiss();
            }

        });
        mConfirmDialog.show();
    }

    private void updateMobile(String mobile,String newPhone,String code ){
        OkHttpUtils.get(Urls.VALIDATE)
                .params("oldPhone", mobile)
                .params("phone", newPhone)
                .params("code", code)
                .execute(new updateMobileCallback(context));
    }

    private void startCountDown(){
        time = 60;
        handler.sendEmptyMessageDelayed(0, 1000);
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            time--;
            if(time>0){
                btn_getCode.setTextColor(context.getResources().getColor(R.color.whitedark));
                btn_getCode.setClickable(false);
                btn_getCode.setText(time + "秒后重发");
                handler.sendEmptyMessageDelayed(0,1000*1);
            }else{
                btn_getCode.setTextColor(context.getResources().getColor(R.color.red));
                btn_getCode.setClickable(true);
                btn_getCode.setText("重新发送");
            }

        }
    };


    private class getCodeCallback extends DialogCallback<TempResponse>{

        public getCodeCallback(Activity activity) {
            super(activity);
        }


        @Override
        public void onResponse(boolean isFromCache, TempResponse tempResponse, Request request, @Nullable Response response) {
            if (tempResponse != null) {
                String status = tempResponse.getStatus();
                String message = tempResponse.getMsg();
                if (!TextUtils.isEmpty(status) && !TextUtils.isEmpty(message)) {
                    if (TextUtils.equals(status, SUCCESS)) {
                        ToastUtils.makeImgAndTextToast(context, context.getString(R.string.validate_code_already_send), R.mipmap.validate_done, Toast.LENGTH_SHORT).show();
                        startCountDown(); //  开始倒计时
                    } else {
                        ToastUtils.showToast(message);
                    }
                }
            }
        }
    }

    private class updateMobileCallback extends DialogCallback<TempResponse> {


        public updateMobileCallback(Activity activity) {
            super(activity);
        }

        @Override
        public void onResponse(boolean isFromCache, TempResponse tempResponse, Request request, @Nullable Response response) {
        if(tempResponse != null){
            String status = tempResponse.getStatus();
            String message = tempResponse.getMsg();
            if (!TextUtils.isEmpty(status) && !TextUtils.isEmpty(message)) {
                if (TextUtils.equals(status, SUCCESS)) {//成功
                    ToastUtils.makeImgAndTextToast(context, message, R.mipmap.validate_done, Toast.LENGTH_SHORT).show();
                    context.onBackPressed();
                } else {
                    ToastUtils.showToast(message);
                }
            }
        }

        }

    }


}
