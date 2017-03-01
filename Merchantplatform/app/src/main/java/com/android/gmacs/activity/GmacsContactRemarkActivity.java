package com.android.gmacs.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.merchantplatform.R;
import com.android.gmacs.event.RemarkEvent;
import com.android.gmacs.logic.ContactLogic;
import com.common.gmacs.core.ClientManager;
import com.common.gmacs.core.GmacsConstant;
import com.common.gmacs.parse.contact.Remark;
import com.common.gmacs.utils.ToastUtil;
import com.xxganji.gmacs.proto.CommonPB;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zhangxiaoshuang on 2015/12/24.联系人添加备注
 */
public class GmacsContactRemarkActivity extends BaseActivity {

    private EditText mEtRemarkName, mEtRemarkPhone, mEtRemarkInfo;
    private String userId;
    private int userSource;
    private Remark remark;

    @Override
    protected void initView() {
        setTitle(getText(R.string.remark));
        mTitleBar.setRightText(getText(R.string.save));
        mEtRemarkName = (EditText) findViewById(R.id.et_remark_name);
        mEtRemarkPhone = (EditText) findViewById(R.id.et_remark_phone);
        mEtRemarkInfo = (EditText) findViewById(R.id.et_remark_info);
        userId = getIntent().getStringExtra(GmacsConstant.EXTRA_USER_ID);
        userSource = getIntent().getIntExtra(GmacsConstant.EXTRA_USER_SOURCE, 0);
        remark = getIntent().getParcelableExtra(GmacsConstant.EXTRA_REMARK);
        if (TextUtils.isEmpty(userId)) {
            finish();
            return;
        }
        mEtRemarkName.setFilters(new InputFilter[]{new LengthFilter(30)});
        mEtRemarkPhone.setFilters(new InputFilter[]{new LengthFilter(20)});
        mEtRemarkInfo.setFilters(new InputFilter[]{new LengthFilter(100)});
        if (remark != null) {
            mEtRemarkName.setText(remark.remark_name);
            mEtRemarkPhone.setText(remark.remark_telephone);
            mEtRemarkInfo.setText(remark.remark_info);
            try {
                mEtRemarkName.setSelection(remark.remark_name == null ? 0 : remark.remark_name.length());
                mEtRemarkPhone.setSelection(remark.remark_telephone == null ? 0 : remark.remark_telephone.length());
                mEtRemarkInfo.setSelection(remark.remark_info == null ? 0 : remark.remark_info.length());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mTitleBar.setRightTextListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ClientManager.getInstance().getConnectionStatus() == CommonPB.ConnectionStatus.STATUS_CONNECTED_VALUE) {
                    if (getBytesLength(mEtRemarkPhone) > 20
                            || getBytesLength(mEtRemarkName) > 30
                            || getBytesLength(mEtRemarkInfo) > 100) {
                        ToastUtil.showToast(getText(R.string.remark_length_overflow));
                        return;
                    }
                    if (remark == null) {
                        remark = new Remark();
                    }
                    remark.remark_telephone = mEtRemarkPhone.getText().toString().trim();
                    if (isPhoneNumValid(remark.remark_telephone)) {
                        remark.remark_name = mEtRemarkName.getText().toString().trim();
                        remark.remark_info = mEtRemarkInfo.getText().toString().trim();
                        ContactLogic.getInstance().remark(userId, userSource, remark.remark_name, remark);
                        InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        manager.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
                    } else {
                        ToastUtil.showToast(getText(R.string.remark_phone_wrong));
                    }
                } else {
                    ToastUtil.showToast(getText(R.string.connection_error_or_kickedoff));
                }
            }
        });
    }

    private int getBytesLength(TextView textView) {
        return textView.getText().toString().getBytes().length;
    }

    /**
     * 有效号段信息取自 http://www.shoujihaoma.cn/PhoneNums.aspx
     *
     * @param number
     * @return
     */
    private boolean isPhoneNumValid(String number) {
        if (!TextUtils.isEmpty(number)) {
            Pattern mobilePhonePattern = Pattern.compile(
                    "((\\+\\d{2})?13[0-9][-| ]?\\d{4}[-| ]?\\d{4})|" +
                            "((\\+\\d{2})?14[5|7][-| ]?\\d{4}[-| ]?\\d{4})|" +
                            "((\\+\\d{2})?15[^4][-| ]?\\d{4}[-| ]?\\d{4})|" +
                            "((\\+\\d{2})?17[6|7|8][-| ]?\\d{4}[-| ]?\\d{4})|" +
                            "((\\+\\d{2})?18[0-9][-| ]?\\d{4}[-| ]?\\d{4})|" +
                            "((\\+\\d{2})?170[-| ]?[0,5,9]\\d{3}[-| ]?\\d{4})"
            );
            Matcher isMobilePhoneNumber = mobilePhonePattern.matcher(number);

            Pattern telephoneNumberPattern = Pattern.compile("((0[0-9]{2,3}[-| ]?)?[2-9][0-9]{6,7}(\\-[0-9]{1,4})?)");
            Matcher isTelephoneNumber = telephoneNumberPattern.matcher(number);

            return isMobilePhoneNumber.matches() || isTelephoneNumber.matches();
        }
        return true;
    }

    private class LengthFilter implements InputFilter {

        final int maxLength; // 最大字节数

        LengthFilter(int maxLength) {
            this.maxLength = maxLength;
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {
            int destCount = dest.toString().getBytes().length;
            int sourceCount = source.toString().getBytes().length;
            if (destCount + sourceCount > maxLength) {
                ToastUtil.showToast(getText(R.string.remark_length_overflow));
                return "";
            } else {
                return source;
            }
        }
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gmacs_activity_contact_remark);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRemark(RemarkEvent event) {
        finish();
    }
}
