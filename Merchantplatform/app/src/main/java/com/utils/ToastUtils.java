package com.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.merchantplatform.R;
import com.merchantplatform.application.HyApplication;

/**
 * Created by SongYongmeng on 2016/11/24.
 */

public class ToastUtils extends Toast {
    /**
     * Construct an empty Toast object.  You must call {@link #setView} before you
     * can call {@link #show}.
     *
     * @param context The context to use.  Usually your {@link Application}
     *                or {@link Activity} object.
     */
    public ToastUtils(Context context) {
        super(context);
    }

    public static void showToast(int message) {
        Toast.makeText(HyApplication.getApplication(), message + "", Toast.LENGTH_SHORT).show();
    }

    public static void showToast(String message) {
        Toast.makeText(HyApplication.getApplication(), message, Toast.LENGTH_SHORT).show();
    }

    /**
     * 生成一个图文并存的Toast
     * @param context  // 上下文对象
     * @param res_id   // 要显示的图片的资源id
     * @param content  // 要显示的文字
     * @param duration // 显示时间
     * @return
     */
    @SuppressWarnings("deprecation")
    public static ToastUtils makeImgAndTextToast(Context context, String content, int res_id, int duration) {
        ToastUtils result = new ToastUtils(context);
        LayoutInflater inflate = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflate.inflate(R.layout.toast_view, null);
        ImageView iv_toast_res   = (ImageView) v.findViewById(R.id.iv_toast_res);
        TextView tv_toast_content  = (TextView) v.findViewById(R.id.tv_toast_content);
        iv_toast_res.setImageResource(res_id);
        tv_toast_content.setText(content);
        result.setView(v);
        result.setGravity(Gravity.CENTER_VERTICAL, 0, 0);  // setGravity方法用于设置位置，此处为垂直居中
        result.setDuration(duration);
        return result;
    }
}
