package com.merchantplatform.model;

import android.support.v4.app.FragmentManager;
import android.view.View;

import com.merchantplatform.R;
import com.merchantplatform.activity.HomepageActivity;
import com.merchantplatform.fragment.BaseFragment;
import com.merchantplatform.fragment.Fragment1;
import com.merchantplatform.fragment.Fragment2;
import com.merchantplatform.fragment.Fragment3;
import com.merchantplatform.fragment.Fragment4;
import com.ui.HomepageBottomButton;

/**
 * Created by SongYongmeng on 2016/11/24.
 */
public class HomepageModel extends BaseModel implements View.OnClickListener {
    private HomepageActivity context;
    private HomepageBottomButton bottomButton1, bottomButton2, bottomButton3, bottomButton4;
    private BaseFragment fragment1, fragment2, fragment3, fragment4, mFragment;

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

    public void createFragment() {
        fragment1 = new Fragment1();
        fragment2 = new Fragment2();
        fragment3 = new Fragment3();
        fragment4 = new Fragment4();
        mFragment = fragment1;
    }

    public void createFragmentManager() {
        FragmentManager fragmentManager = context.getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.main_fragment, mFragment).commit();
    }

    private void switchFragment(BaseFragment fragment) {
        if (mFragment != fragment)
            isNotShowing(fragment);
    }

    private void isNotShowing(BaseFragment fragment) {
        jugeFragmentAdded(fragment);
        mFragment = fragment;
    }

    private void jugeFragmentAdded(BaseFragment fragment) {
        if (!fragment.isAdded())
            context.getSupportFragmentManager().beginTransaction().hide(mFragment)
                    .add(R.id.main_fragment, fragment).commit();
        else
            context.getSupportFragmentManager().beginTransaction().hide(mFragment).show(fragment).commit();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.homepage_bottom_button1:
                clickThisView(bottomButton1);
                switchFragment(fragment1);
                break;
            case R.id.homepage_bottom_button2:
                clickThisView(bottomButton2);
                switchFragment(fragment2);
                break;
            case R.id.homepage_bottom_button3:
                clickThisView(bottomButton3);
                switchFragment(fragment3);
                break;
            case R.id.homepage_bottom_button4:
                clickThisView(bottomButton4);
                switchFragment(fragment4);
                break;
            default:
                break;
        }
    }
}
