package com.ui.sheet;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.merchantplatform.R;

import java.util.List;

/**
 * Created by 58 on 2016/12/13.
 */

public class NormalActionSheet extends ActionSheet {

    private ScrollView scrollView;
    private LinearLayout  linearLayout;
    private TextView  titleView;
    private View divider;
    private TextView  cancelView;
    private List<String> mValues;
    private boolean showTitle = false;
    private OnNormalItemClickListener listener;

    public OnNormalItemClickListener getListener() {
        return listener;
    }

    public NormalActionSheet setListener(OnNormalItemClickListener listener) {
        this.listener = listener;
        return this;
    }

    public NormalActionSheet(Context context) {
        super(context);
    }

    public NormalActionSheet builder() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.normal_actionsheet, null);
        view.setMinimumWidth(mDisplay.getWidth());
        scrollView = (ScrollView)view.findViewById(R.id.normal_scrollview);
        linearLayout = (LinearLayout)view.findViewById(R.id.normal_linearlayout);
        titleView = (TextView)view.findViewById(R.id.normal_title);
        divider = view.findViewById(R.id.normal_divider);
        cancelView = (TextView)view.findViewById(R.id.normal_cancel);
        cancelView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        mDialog.setContentView(view);
        return this;
    }

    public NormalActionSheet setItems(List<String> values) {
        mValues = values;
        return this;
    }

    public NormalActionSheet setTitle(String title) {
        showTitle = true;
        titleView.setVisibility(View.VISIBLE);
        titleView.setText(title);
        divider.setVisibility(View.VISIBLE);
        return this;
    }

    private void setSheetItems() {
        if(mValues != null && mValues.size() > 0) {
            int size = mValues.size();
            if(size >= 7) {
                LinearLayout.LayoutParams i = (LinearLayout.LayoutParams)scrollView.getLayoutParams();
                i.height = mDisplay.getHeight() / 2;
                scrollView.setLayoutParams(i);
            }

            for( int i = 1; i <= size; i++) {
                final int index = i;
                TextView textView = new TextView(mContext);
                textView.setText(mValues.get(i - 1));
                textView.setGravity(Gravity.CENTER);
                if(i == size) {
                    textView.setBackgroundResource(R.drawable.actionsheet_bottom_selector);
                } else {
                    textView.setBackgroundResource(R.drawable.actionsheet_middle_selector);
                }

                ColorStateList csl = mContext.getResources().getColorStateList(R.color.alertdialog_text);
                textView.setTextColor(csl);
                textView.setTextSize(17);
                // 高度
                float scale = mContext.getResources().getDisplayMetrics().density;
                int height = (int)(45 * scale + 0.5F);
                textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height));
                // 点击事件
                textView.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        if(listener != null) {
                            listener.onClick(mValues.get(index - 1));
                        }

                        mDialog.dismiss();
                    }
                });
                linearLayout.addView(textView);
            }

        }
    }

    public void show() {
        setSheetItems();
        super.show();
    }

    public interface OnNormalItemClickListener {
        void onClick(String item);
    }
}
