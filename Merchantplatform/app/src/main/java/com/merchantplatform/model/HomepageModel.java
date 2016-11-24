package com.merchantplatform.model;

import android.view.View;

import com.merchantplatform.R;
import com.merchantplatform.activity.HomepageActivity;
import com.ui.HomepageBottomButton;

/**
 * Created by SongYongmeng on 2016/11/24.
 */
public class HomepageModel extends BaseModel implements View.OnClickListener {
    private HomepageActivity context;
    private HomepageBottomButton bottomButton1, bottomButton2, bottomButton3, bottomButton4;

    public HomepageModel(HomepageActivity context) {
        this.context = context;
    }

    public void init() {
        bottomButton1 = (HomepageBottomButton) context.findViewById(R.id.homepage_bottom_button1);
        bottomButton2 = (HomepageBottomButton) context.findViewById(R.id.homepage_bottom_button2);
        bottomButton3 = (HomepageBottomButton) context.findViewById(R.id.homepage_bottom_button3);
        bottomButton4 = (HomepageBottomButton) context.findViewById(R.id.homepage_bottom_button4);
    }

    public void setListener() {
        bottomButton1.setOnClickListener(this);
        bottomButton2.setOnClickListener(this);
        bottomButton3.setOnClickListener(this);
        bottomButton4.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.homepage_bottom_button1:
                clickThisView(bottomButton1);
                break;
            case R.id.homepage_bottom_button2:
                clickThisView(bottomButton2);
                break;
            case R.id.homepage_bottom_button3:
                clickThisView(bottomButton3);
                break;
            case R.id.homepage_bottom_button4:
                clickThisView(bottomButton4);
                break;
            default:
                break;
        }
    }

    private void clickThisView(HomepageBottomButton v) {
        registerState();
        v.dismissRedHot();
        if (!v.isSelected())
            v.setSelectedState();
    }

    private void registerState() {
        bottomButton1.registerState();
        bottomButton2.registerState();
        bottomButton3.registerState();
        bottomButton4.registerState();
    }
}
