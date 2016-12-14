package com.merchantplatform.model;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.android.gmacs.fragment.ConversationListFragment;
import com.merchantplatform.R;
import com.merchantplatform.activity.HomepageActivity;
import com.merchantplatform.fragment.BaseFragment;
import com.merchantplatform.fragment.Fragment1;
import com.merchantplatform.fragment.CallMessageFragment;
import com.merchantplatform.fragment.Fragment3;
import com.merchantplatform.fragment.PersonalCenterFragment;
import com.ui.HomepageBottomButton;

/**
 * Created by SongYongmeng on 2016/11/24.
 */

public class HomepageModel extends BaseModel implements View.OnClickListener {
    private HomepageActivity context;
    private HomepageBottomButton bottomButton1, bottomButton2, bottomButton3, bottomButton4;
    private BaseFragment fragment1, fragment2, fragment3, fragment4;
    private ConversationListFragment conversationListFragment;
    private Fragment mFragment;

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

    private void selectThis(HomepageBottomButton v) {
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
        conversationListFragment = new ConversationListFragment();
        fragment2 = new CallMessageFragment();
        fragment3 = new Fragment3();
        fragment4 = new PersonalCenterFragment();
        mFragment = conversationListFragment;
    }

    public void createFragmentManagerAndShow() {
        FragmentManager fragmentManager = context.getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.main_fragment, mFragment).commit();
    }

    private void switchFragment(Fragment fragment) {
        if (mFragment != fragment)
            isNotShowing(fragment);
    }

    private void isNotShowing(Fragment fragment) {
        judgeFragmentAdded(fragment);
        mFragment = fragment;
    }

    private void judgeFragmentAdded(Fragment fragment) {
        if (!fragment.isAdded())
            context.getSupportFragmentManager().beginTransaction().hide(mFragment)
                    .add(R.id.main_fragment, fragment).commit();
        else
            context.getSupportFragmentManager().beginTransaction().hide(mFragment).show(fragment).commit();
    }

    private void dealWithClick(HomepageBottomButton button, Fragment fragment) {
        selectThis(button);
        switchFragment(fragment);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.homepage_bottom_button1:
                dealWithClick(bottomButton1, conversationListFragment);
                bottomButton1.setNum(20);
                break;
            case R.id.homepage_bottom_button2:
                dealWithClick(bottomButton2, fragment2);
                bottomButton2.setShowRedHot();
                break;
            case R.id.homepage_bottom_button3:
                dealWithClick(bottomButton3, fragment3);
                break;
            case R.id.homepage_bottom_button4:
                dealWithClick(bottomButton4, fragment4);
                break;
            default:
                break;
        }
    }

}
