package com.merchantplatform.model;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.callback.DialogCallback;
import com.merchantplatform.R;
import com.merchantplatform.activity.HomepageActivity;
import com.merchantplatform.activity.MobileValidateActivity;
import com.merchantplatform.bean.BindMobileResponse;
import com.merchantplatform.bean.GetCodeResponse;
import com.okhttputils.OkHttpUtils;
import com.Utils.TitleBar;
import com.utils.PageSwitchUtils;
import com.utils.StringUtil;
import com.utils.ToastUtils;
import com.utils.Urls;
import com.utils.UserUtils;

import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by 58 on 2016/12/14.
 */

public class MobileValidateActivityModel extends BaseModel implements View.OnClickListener{

   private MobileValidateActivity context;

    private TitleBar tb_validate_title;
    private EditText validate_mobile ,validate_code;
    private Button validate_getcode,commit;

    private static final String FAILURE = "1";//异常
    private  static final String SUCCESS = "0";//成功
    private int countDownTime;

    private Handler handler = new Handler();
    public MobileValidateActivityModel(MobileValidateActivity context){
        this.context = context;
    }

    public void initView(){
        tb_validate_title = (TitleBar) context.findViewById(R.id.tb_validate_title);
        validate_mobile = (EditText) context.findViewById(R.id.validate_mobile);
        validate_code = (EditText) context.findViewById(R.id.validate_code);
        validate_getcode = (Button) context.findViewById(R.id.validate_getcode);
        commit = (Button) context.findViewById(R.id.commit);
    }

    public void initData(){
        //设置透明状态栏
        tb_validate_title.setImmersive(true);
        //设置背景颜色
        tb_validate_title.setBackgroundColor(Color.WHITE);
        //设置左侧图标
        tb_validate_title.setLeftImageResource(R.mipmap.title_back);
        //设置左侧点击事件
        tb_validate_title.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.onBackPressed();
            }
        });
        //设置标题
        tb_validate_title.setTitle("手机验证");
        //设置主标题颜色
        tb_validate_title.setTitleColor(Color.BLACK);

    }

    public void setListener(){
        validate_getcode.setOnClickListener(this);
        commit.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
       switch(v.getId()){
           case R.id.validate_getcode:
               getValidateCode();
               break;
           case R.id.commit:
               commit();
               break;
       }
    }

    private void getValidateCode(){
        String mobiletext = validate_mobile.getText().toString();
        if (StringUtil.isMobileNO(mobiletext)) {
            getCode(mobiletext);
        } else {
            ToastUtils.makeImgAndTextToast(context, "请输入正确的手机号", R.mipmap.validate_error, Toast.LENGTH_SHORT).show();
        }
    }

    private void getCode(String phone){
        OkHttpUtils.get(Urls.GET_VALIDATE_CODE)
                .params("phone",phone)
                .execute(new getCodeCallback(context));
    }

    private void countdown(){
        countDownTime = 60;
        handler.postDelayed(runnable, 0);
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            countDownTime--;
            ColorStateList color;
            if(countDownTime>=0){
                validate_getcode.setClickable(false);
                validate_getcode.setText(countDownTime+"s后重新发送");
                color = context.getResources().getColorStateList(R.color.whitedark);
                validate_getcode.setTextColor(color);
                handler.postDelayed(this, 1000);
            }
            else{
                validate_getcode.setClickable(true);
                color = context.getResources().getColorStateList(R.color.red);
                validate_getcode.setTextColor(color);
                validate_getcode.setText("重新获取");
                handler.removeCallbacks(runnable);
            }
        }
    };

    private void commit(){
        String mobiletext = validate_mobile.getText().toString();
        String codetext = validate_code.getText().toString();
        if (!StringUtil.isMobileNO(mobiletext)) {
            ToastUtils.makeImgAndTextToast(context, "请输入正确的手机号码", R.mipmap.validate_error, Toast.LENGTH_SHORT).show();
        }
        else if(!StringUtil.isCode(codetext)){
            ToastUtils.makeImgAndTextToast(context, "请输入正确的验证码", R.mipmap.validate_error, Toast.LENGTH_SHORT).show();
        }
        else{
            validate(mobiletext,codetext);
        }

    }

    private  void validate(String phone,String code){
        OkHttpUtils.get(Urls.VALIDATE)
                .params("phone",phone)
                .params("code",code)
                .execute(new validateCallback(context));
    }

    private void validateSuccess(){
        goToHomePage();
        finishSelf();
        setValidate();
    }

    private void goToHomePage(){
        PageSwitchUtils.goToActivity(context, HomepageActivity.class);
    }

    private void finishSelf(){
        context.finish();
    }

    private void setValidate(){
        UserUtils.hasValidate(context);
    }

    private class getCodeCallback extends DialogCallback<GetCodeResponse>{

        public getCodeCallback(Activity activity) {
            super(activity);
        }


        @Override
        public void onResponse(boolean isFromCache, GetCodeResponse getCodeResponse, Request request, @Nullable Response response) {
            String status = getCodeResponse.getData().getStatus();
            String message = getCodeResponse.getData().getMsg();
            if (!TextUtils.isEmpty(status) && !TextUtils.isEmpty(message)) {
                if (TextUtils.equals(status, SUCCESS)) {
                    ToastUtils.makeImgAndTextToast(context, context.getString(R.string.validate_code_already_send), R.mipmap.validate_done, Toast.LENGTH_SHORT).show();
                    countdown();
                } else if (TextUtils.equals(status, FAILURE)) {
                    ToastUtils.makeImgAndTextToast(context, message, R.mipmap.validate_done, Toast.LENGTH_SHORT).show();
                }
            }


        }
    }

    private class validateCallback extends DialogCallback<BindMobileResponse>{


        public validateCallback(Activity activity) {
            super(activity);
        }

        @Override
        public void onResponse(boolean isFromCache, BindMobileResponse bindMobileResponse, Request request, @Nullable Response response) {
            String status = bindMobileResponse.getData().getStatus();
            String message = bindMobileResponse.getData().getMsg();

            if(TextUtils.equals(status,SUCCESS)){
                validateSuccess();
            } else{
                if(!TextUtils.isEmpty(message))
                ToastUtils.makeImgAndTextToast(context, message, R.mipmap.validate_error, 0).show();
            }
        }
    }
}
