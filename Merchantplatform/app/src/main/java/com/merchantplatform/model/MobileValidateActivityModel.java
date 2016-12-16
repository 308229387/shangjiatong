package com.merchantplatform.model;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.callback.DialogCallback;
import com.merchantplatform.R;
import com.merchantplatform.activity.MobileValidateActivity;
import com.okhttputils.OkHttpUtils;
import com.Utils.TitleBar;
import com.utils.StringUtil;
import com.utils.ToastUtils;
import com.utils.Urls;

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

    private void getCode(String mobile){
        OkHttpUtils.get(Urls.GET_VALIDE_CODE)
                .params("","")
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
            validate();
        }

    }

    private  void validate(){
        OkHttpUtils.get(Urls.VALIDATE)
                .params("","")
                .params("","")
                .execute(new validateCallback(context));
    }

    private void validateSuccess(){
        context.finish();
    }

    private class getCodeCallback extends DialogCallback<String>{

        public getCodeCallback(Activity activity) {
            super(activity);
        }

        @Override
        public void onResponse(boolean isFromCache, String s, Request request, @Nullable Response response) {

        }
    }

    private class validateCallback extends DialogCallback<String>{


        public validateCallback(Activity activity) {
            super(activity);
        }

        @Override
        public void onResponse(boolean isFromCache, String s, Request request, @Nullable Response response) {

        }
    }
}
