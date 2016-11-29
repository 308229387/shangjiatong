package com.merchantplatform.model;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.merchantplatform.R;
import com.merchantplatform.activity.NewsActivity;
import com.merchantplatform.application.HyApplication;
import com.push.WPushListener;
import com.utils.WPushInitUtils;
import com.wuba.wbpush.Push;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by 58 on 2016/11/28.
 */

public class PushActivityModel extends BaseModel implements
        DialogInterface.OnClickListener,
        View.OnClickListener,
        WPushListener{

    private Activity context;

    public TextView mLogView ;
    private Button mBindUserIDBtn ;
    private Button mBindAliasBtn ;
    private EditText mUserIDText ;
    private EditText mAliasText ;

    private MessageHandler mMessgeHandler;

    public  List<String> logList = new CopyOnWriteArrayList<>();

    private String mMessageID;

    private static final int BIND_USER_OK = 1;
    private static final int BIND_ALIAS_OK = 2;

    public PushActivityModel(Activity context){
        this.context = context;
    }

    public void initLayout(){
        mBindUserIDBtn = (Button)context.findViewById(R.id.bind_userid_button);
        mUserIDText = (EditText)context.findViewById(R.id.userid);
        mBindAliasBtn = (Button)context.findViewById(R.id.bind_alias_button);
        mAliasText = (EditText)context.findViewById(R.id.alias);
        mLogView = (TextView)context.findViewById(R.id.log);
    }

    public void initListener(){
        mBindUserIDBtn.setOnClickListener(this);
        mBindAliasBtn.setOnClickListener(this);
    }

    public void initHandler(){
        if (mMessgeHandler == null)
            mMessgeHandler = new MessageHandler(HyApplication.getApplication());

    }

    public void setPushListener(){
        new WPushInitUtils(context).setPushListener(this);

    }

    public void goToNewsActivity(){
        Intent intent = new Intent(context, NewsActivity.class);
        intent.setType("123");
        String uriString = intent.toUri(Intent.URI_INTENT_SCHEME);
        Log.d("PushUtils", "MainActivity onCreate uriString:" + uriString);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bind_userid_button:
                Push.getInstance().binderUserID(mUserIDText.getText().toString());
                break;
            case R.id.bind_alias_button:
                Push.getInstance().binderAlias(mAliasText.getText().toString());
                break;
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        Push.getInstance().reportPushMessageClicked(mMessageID);
    }
    @Override
    public void onDeviceIDAvalible(String deviceID) {
        String msgString = String.format("onDeviceIDAvalible :%s",deviceID);
        logList.add(msgString);
        Message msg = Message.obtain();
        msg.obj = msgString;
        mMessgeHandler.sendMessage(msg);
    }
    @Override
    public void onError(int errorCode, String errorString) {
        Toast.makeText(context, errorString, Toast.LENGTH_LONG).show();
        if (errorCode == BIND_USER_OK || errorCode == BIND_ALIAS_OK) {
            String msgString = null;
            if (errorCode == BIND_USER_OK) {
                msgString = String.format("bind user id sucess");
            }else {
                msgString = String.format("bind alias sucess");
            }
            logList.add(msgString);
            Message msg = Message.obtain();
            msg.obj = msgString;
            mMessgeHandler.sendMessage(msg);
        }
    }
    @Override
    public void onNotificationClicked(String messageId) {
        Toast.makeText(context, messageId, Toast.LENGTH_LONG).show();
    }
    @Override
    public void OnMessage(Push.PushMessage message) {
        String type = message.messageType == Push.MessageType.Notify ? "Notify" : "PassThrough";
        String msgString = null;
        if (message.messageInfos != null) {
            msgString = String.format("messgeID:%s messageType:%s messaegContent:%s pushType:%s",
                    message.messageID,type,message.messageContent,message.messageInfos.get("pushType"));
        } else {
            msgString = String.format("messgeID:%s messageType:%s messaegContent:%s",
                    message.messageID, type, message.messageContent);
        }
        logList.add(msgString);
        Message msg = Message.obtain();
        msg.obj = msgString;
        mMessgeHandler.sendMessage(msg);
        mMessageID = message.messageID;

        AlertDialog.Builder builder  = new AlertDialog.Builder(context);
        builder.setTitle("收到消息" ) ;
        builder.setMessage(msgString) ;
        builder.setPositiveButton("是",this );
        builder.setNegativeButton("否", null);
        builder.show();
    }

    private class MessageHandler extends Handler {

        private Context context;

        public MessageHandler(Context context){
            this.context = context;
        }

        @Override
        public void handleMessage(Message msg) {
            String s = (String) msg.obj;
            refreshLogInfo();
        }
    }

    public void refreshLogInfo(){
        String AllLog = "";
        for(String log : logList){
            AllLog = AllLog + log + "\n\n";
        }
        mLogView.setText(AllLog);
    }

    public void clearPushListener(){
        new WPushInitUtils(context).setPushListener(null);

    }


}
