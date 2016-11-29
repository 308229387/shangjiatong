package com.merchantplatform.model;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

/**
 * Created by 58 on 2016/11/29.
 */

public class NewsActivityModel extends  BaseModel {
    private Activity context;

    public NewsActivityModel(Activity context){
        this.context = context;
    }

    public void initData(){
        Intent intent = context.getIntent();
        intent.getStringExtra("key1");
        if (intent != null) {
            Log.d("PushUtils","NewsActivity onCreate getType:" + intent.getType()/*intent.getStringExtra("key1")*/);
        }else {
            Log.d("PushUtils","NewsActivity onCreate intent is null" );
        }
    }
}
