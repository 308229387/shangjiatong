package com.ui.diyview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.merchantplatform.R;

/**
 * Created by linyueyang on 17/1/19.
 */

public class EmptyView extends LinearLayout {

    private Context context;
    private ImageView imageView;
    private TextView textView;


    public EmptyView(Context context) {
        super(context);
        this.context = context;
        initView();
    }

    public EmptyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView();
    }

    public EmptyView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView();
    }

    private void initView() {
        View view = LayoutInflater.from(context).inflate(R.layout.empty_layout, this, true);
        imageView = (ImageView) view.findViewById(R.id.iv_empty);
        textView = (TextView) view.findViewById(R.id.tv_empty);
    }

    public void setImage(int res) {
        imageView.setImageResource(res);
    }

    public void setText(String text) {
        textView.setText(text);
    }


}
