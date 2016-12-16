package com.merchantplatform.model;

import android.graphics.Color;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.merchantplatform.R;
import com.merchantplatform.activity.AboutActivity;
import com.merchantplatform.activity.SystemMessageActivity;
import com.merchantplatform.adapter.SystemMessageAdapter;
import com.push.bean.PushSqlBean;
import com.Utils.TitleBar;
import com.utils.PageSwitchUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 58 on 2016/12/15.
 */

public class SystemMessageActivityModel extends BaseModel{

    private SystemMessageActivity context;

    private TitleBar tb_message_title;

    private ListView lv_message_center;

    private SystemMessageAdapter adapter;
    private List<PushSqlBean> beans = new ArrayList<PushSqlBean>();

    public SystemMessageActivityModel(SystemMessageActivity context){
        this.context = context;
    }

    public void initView(){
        tb_message_title = (TitleBar) context.findViewById(R.id.tb_message_title);
        lv_message_center = (ListView) context.findViewById(R.id.lv_message_center);
    }

    public void initData(){
        initTitleBar();
        initAdapter();
        initListData();
    }

    private  void initTitleBar(){
        //设置透明状态栏
        tb_message_title.setImmersive(true);
        //设置背景颜色
        tb_message_title.setBackgroundColor(Color.WHITE);
        //设置左侧图标
        tb_message_title.setLeftImageResource(R.mipmap.title_back);
        //设置左侧点击事件
        tb_message_title.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.onBackPressed();
            }
        });
        //设置标题
        tb_message_title.setTitle("系统消息");
        //设置主标题颜色
        tb_message_title.setTitleColor(Color.BLACK);
    }

  private void initAdapter(){
      adapter = new SystemMessageAdapter(context);
      lv_message_center.setAdapter(adapter);
  }

    private void initListData(){
        PushSqlBean bean1 = new PushSqlBean();
        bean1.setMsgCate(100);
        beans.add(bean1);
        PushSqlBean bean2 = new PushSqlBean();
        bean2.setMsgCate(101);
        beans.add(bean2);
        adapter.setDataSources(beans);
    }

    public void setListener(){
        lv_message_center.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PushSqlBean bean = beans.get(position);
                int msgCate = bean.getMsgCate();
                if(msgCate == 101){
                    PageSwitchUtils.goToActivity(context, AboutActivity.class);
                }
            }
        });
    }

}
