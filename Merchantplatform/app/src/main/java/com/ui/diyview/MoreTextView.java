package com.ui.diyview;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.merchantplatform.R;

/**
 * Created by linyueyang on 17/1/12.
 * <p>
 * 详情信息，展示更多View
 */

public class MoreTextView extends FrameLayout {

    private Context context;
    private TextView tv_description_content;
    private String content;
    private TextView tv_more;
    private View line;
    private int limiteCount = 6;//行数限制 两个都设置已line为准
    private LimiteTypeEnum limiteTypeEnum = LimiteTypeEnum.LINE_LIMITE;

    public MoreTextView(Context context) {
        super(context);
        this.context = context;
        initView();
    }

    public MoreTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initView();
    }

    public MoreTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView();
    }

    private void initView() {
        View view = LayoutInflater.from(context).inflate(R.layout.more_text_view, this, true);
        tv_description_content = (TextView) view.findViewById(R.id.tv_description_content);
        tv_more = (TextView) view.findViewById(R.id.tv_more);
        line = view.findViewById(R.id.line);

        tv_more.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                tv_more.setVisibility(View.GONE);
                if (limiteTypeEnum == LimiteTypeEnum.LINE_LIMITE) {
                    tv_description_content.setMaxLines(Integer.MAX_VALUE);
                } else {
                    tv_description_content.setText(content);
                }
            }
        });
    }

    public void setLimiteType(LimiteTypeEnum limiteTypeEnum, int limiteCount) {
        this.limiteTypeEnum = limiteTypeEnum;
        this.limiteCount = limiteCount;
    }

    public void setMoreText(String moreText) {
        tv_more.setText(moreText);
    }

    public void setContent(final String content) {
        this.content = content;

        tv_description_content.setText(content);
        //getLineCount()方法必须必须在Text加载完成之后才能获取
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (limiteTypeEnum == LimiteTypeEnum.LINE_LIMITE) {
                    if (tv_description_content.getLineCount() > limiteCount) {
                        line.setVisibility(View.VISIBLE);
                        tv_more.setVisibility(View.VISIBLE);
                        tv_description_content.setMaxLines(limiteCount);
                    } else {
                        tv_more.setVisibility(View.GONE);
                        line.setVisibility(View.GONE);
                    }
                } else {
                    if (content.length() > limiteCount) {
                        tv_more.setVisibility(View.VISIBLE);
                        line.setVisibility(View.VISIBLE);
                        tv_description_content.setText(content.substring(0, limiteCount));
                    } else {
                        tv_more.setVisibility(View.GONE);
                        line.setVisibility(View.GONE);
                    }
                }
            }
        }, 100);

    }

    public enum LimiteTypeEnum {
        TEXT_LIMITE,
        LINE_LIMITE;
    }

}
