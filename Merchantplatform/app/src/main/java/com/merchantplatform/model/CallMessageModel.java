package com.merchantplatform.model;

import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.db.helper.CallDetailDaoOperate;
import com.db.helper.CallListDaoOperate;
import com.db.helper.DbManager;
import com.merchantplatform.R;
import com.merchantplatform.adapter.FragmentAdapter;
import com.merchantplatform.fragment.CallRecordFragment;
import com.merchantplatform.fragment.CallMessageFragment;
import com.utils.DateUtils;
import com.utils.DisplayUtils;

import org.greenrobot.greendao.query.WhereCondition;

import java.util.ArrayList;

/**
 * Created by SongYongmeng on 2016/11/24.
 */
public class CallMessageModel extends BaseModel {
    private CallMessageFragment context;
    private View view;
    private LinearLayout layout_call_head;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private ArrayList<String> titles;
    private ArrayList<Fragment> fragments;

    public CallMessageModel(CallMessageFragment context) {
        this.context = context;
    }

    public void createView(LayoutInflater inflater, ViewGroup container) {
        view = inflater.inflate(R.layout.fragment_call_message, container, false);
        layout_call_head = (LinearLayout) view.findViewById(R.id.layout_call_head);
        mTabLayout = (TabLayout) view.findViewById(R.id.tb_switch_callType);
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
    }

    public void createFragment() {
        fragments = new ArrayList<>();
        titles = new ArrayList<>();
        titles.add("未接来电");
        titles.add("通话记录");
        mTabLayout.addTab(mTabLayout.newTab().setText(titles.get(0)));
        mTabLayout.addTab(mTabLayout.newTab().setText(titles.get(1)));
        fragments.add(new CallRecordFragment());
        fragments.add(new CallRecordFragment());
    }

    public void setHeaderHeight() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            context.getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            int height = DisplayUtils.getStatusBarHeight(context.getActivity());
            int more = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, context.getResources().getDisplayMetrics());
            if (layout_call_head != null) {
                layout_call_head.setPadding(0, height + more, 0, 0);
            }
        }
    }

    public void setupViewPager() {
        FragmentAdapter fragmentAdapter = new FragmentAdapter(context.getChildFragmentManager(), fragments, titles);
        mViewPager.setAdapter(fragmentAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setTabsFromPagerAdapter(fragmentAdapter);
    }

    public void deleteLastMonthData() {
        DbManager.getInstance(context.getContext());
        String lastMonthDate = DateUtils.getMonthAgo(1);
        WhereCondition condition = new WhereCondition.StringCondition("date(CALL_TIME)<'" + lastMonthDate + "'");
        CallDetailDaoOperate.deleteByCondition(context.getContext(), condition);
        CallListDaoOperate.deleteByCondition(context.getContext(), condition);
    }

    public View getView() {
        return view;
    }
}