package com.merchantplatform.model;

import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.merchantplatform.R;
import com.merchantplatform.activity.MobileBindChangeActivity;
import com.ui.TitleBar;
import com.utils.Constant;
import com.utils.ToastUtils;
import com.utils.UserUtils;

/**
 * Created by 58 on 2016/12/9.
 */

public class MobileBindChangeModel extends BaseModel implements View.OnClickListener{
    private MobileBindChangeActivity context;

    private TitleBar tb_Change_title;
    private EditText et_now_bind_mobile, et_now_validate_code, et_new_validate_mobile;
    private Button btn_getCode, btn_submit;

    private String mobile;

    private boolean newPhoneSatisfy;
    private boolean validateCodeSatisfy;
    int time = 60;

    public MobileBindChangeModel(MobileBindChangeActivity context){
        this.context = context;
    }

    public void initView(){
        tb_Change_title = (TitleBar) context.findViewById(R.id.tb_Change_title);
        et_now_bind_mobile = (EditText) context.findViewById(R.id.now_bind_mobile);
        et_now_validate_code = (EditText) context.findViewById(R.id.now_validate_code);
        et_new_validate_mobile = (EditText) context.findViewById(R.id.new_validate_mobile);
        btn_getCode = (Button) context.findViewById(R.id.btn_getCode);
        btn_submit = (Button) context.findViewById(R.id.btn_submit);
    }

    public void initData(){
        initTitleData();
        getMobile();
        setNowBindMobile();
    }

    private void initTitleData() {
        //设置透明状态栏
        tb_Change_title.setImmersive(true);
        //设置左侧文案
        tb_Change_title.setLeftText("返回");
        //设置标题
        tb_Change_title.setTitle("设置");
        //设置左侧点击事件
        tb_Change_title.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.onBackPressed();
            }
        });
    }

    private void getMobile(){
        mobile = context.getIntent().getStringExtra(Constant.MOBILE);
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
            int length = s.length();
            if(length == 11){
                newPhoneSatisfy = true;
            }else{
                newPhoneSatisfy = false;
            }
            checkSubmitEnabled(validateCodeSatisfy,newPhoneSatisfy);
        }
    };

    private void checkSubmitEnabled(boolean validateCodeSatisfy, boolean newPhoneSatisfy) {
        if(validateCodeSatisfy && newPhoneSatisfy){
            btn_submit.setEnabled(true);
            btn_submit.setTextColor(Color.parseColor("#FFFFFF"));
            btn_submit.setBackgroundResource(R.drawable.submit_btn_bg);
        }else{
            btn_submit.setEnabled(false);
            btn_submit.setTextColor(Color.parseColor("#FDC9C0"));
            btn_submit.setBackgroundResource(R.drawable.submit_bg_gray);
        }
    }

    private void setSubmitState(){
        btn_submit.setEnabled(false);
        btn_submit.setTextColor(Color.parseColor("#FDC9C0"));
        btn_submit.setBackgroundResource(R.drawable.submit_bg_gray);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_getCode://获取验证码
                break;
            case R.id.btn_submit://提交
                submit();
                break;
        }
    }

    private void submit(){
        if(TextUtils.equals(et_now_bind_mobile.getText().toString(),et_new_validate_mobile.getText().toString())){
            ToastUtils.makeImgAndTextToast(context, "两次修改的手机号不能一致", R.mipmap.validate_error, 0).show();
            return ;
        }else{

        }
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


}
