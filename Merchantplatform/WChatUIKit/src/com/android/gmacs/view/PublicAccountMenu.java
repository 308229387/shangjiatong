package com.android.gmacs.view;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.android.gmacs.R;
import com.android.gmacs.activity.GmacsChatActivity;
import com.android.gmacs.activity.GmacsWebViewActivity;
import com.common.gmacs.parse.pubcontact.PAFunctionConfig;
import com.common.gmacs.utils.GmacsUiUtil;
import com.common.gmacs.utils.GmacsUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by YanQi on 2016/1/20.
 */
public class PublicAccountMenu extends LinearLayout implements View.OnClickListener {

    private GmacsChatActivity gmacsChatActivity;

    private LayoutInflater layoutInflater;
    private Context context;
    private LinearLayout mLlMenu;
    private ImageView mIvKeyboard;

    private PopupWindow popupWindow = new PopupWindow();

    private PAFunctionConfig config;

    public PublicAccountMenu(Context context) {
        super(context);
        this.context = context;
    }

    public PublicAccountMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PublicAccountMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Adding the public account function config data to this View.
     *
     * @param context
     * @param config
     * @return If config is null object or no content in it, returns true. Otherwise, false instead.
     */
    public boolean setConfig(Context context, PAFunctionConfig config) {
        this.context = context;
        this.config = config;
        layoutInflater = LayoutInflater.from(context);
        initMenu();
        return config == null || config.getPrimaryTitle().size() == 0;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        gmacsChatActivity = (GmacsChatActivity) getContext();
        mIvKeyboard = (ImageView) findViewById(R.id.iv_public_account_keyboard_up);
        mLlMenu = (LinearLayout) findViewById(R.id.ll_public_account_menu);
        mIvKeyboard.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gmacsChatActivity.sendMsgLayout.isShown()) {
                    gmacsChatActivity.sendMsgLayout.setVisibility(GONE);
                    setVisibility(VISIBLE);
                } else {
                    gmacsChatActivity.sendMsgLayout.setVisibility(VISIBLE);
                    setVisibility(GONE);
                }
            }
        });
    }

    private void initMenu() {
        if (config != null) {
            mLlMenu.removeAllViews();
            int primaryItemCount = config.getPrimaryTitle().size();

            for (int i = 0; i < primaryItemCount; i++) {
                LinearLayout layout = (LinearLayout) layoutInflater.inflate(R.layout.gmacs_public_account_menu_item, null);
                Button btn = (Button) layout.findViewById(R.id.btn_public_account_menu_item);
                ImageView iv = (ImageView) layout.findViewById(R.id.spinner_public_account_menu_item);
                btn.setOnClickListener(this);

                HashMap<Integer, ArrayList<Integer>> secondaryContentIndex = config.getSecondaryContentIndex();
                if (secondaryContentIndex.get(i) != null &&
                        secondaryContentIndex.get(i).size() != 0 &&
                        secondaryContentIndex.get(i).get(0) != -1) {
                    btn.setTag(i);
                    iv.setVisibility(VISIBLE);
                } else {
                    btn.setTag(config.getPrimaryUrl().get(i));
                    btn.setTag(R.id.btn_public_account_menu_item, config.getPrimaryTitle().get(i));
                    iv.setVisibility(GONE);
                }
                btn.setText(config.getPrimaryTitle().get(i));

                mLlMenu.addView(layout, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1));
            }
        }
    }

    @Override
    public void onClick(final View v) {
        Object tag = v.getTag();
        if (tag != null) {
            if (tag instanceof Integer) { // Tag is Button's index.

                // 避免同时按下多个tab，弹出多个PopupWindow
//                synchronized (popupWindow) {
                LinearLayout layout = (LinearLayout) layoutInflater.inflate(R.layout.gmacs_public_account_menuitem, null);

                // Getting the maximum text and calculating its width -- maxTextWidth on display.
                int maxLength = 0, maxIndex = 0, secondaryTitlesSize = config.getSecondaryTitle().size();
                for (int i = 0; i < secondaryTitlesSize; i++) {
                    int secondaryTitleLength = config.getSecondaryTitle().get(i).length();
                    if (maxLength < secondaryTitleLength) {
                        maxIndex = i;
                        maxLength = secondaryTitleLength;
                    }
                }
                int maxTextWidth = (int) Math.ceil(((TextView) layoutInflater.inflate(
                        R.layout.gmacs_public_account_menuitem_layout, null).findViewById(
                        R.id.tv_public_account_menuitem)).getPaint().measureText(config.
                        getSecondaryTitle().get(config.getSecondaryContentIndex().
                        get(tag).get(maxIndex))) + GmacsUtils.dipToPixel(20));

                popupWindow.setContentView(layout);

                // Compare maxTextWidth with Button's width. Using Button's width if lower.
                if (maxTextWidth <= v.getWidth()) {
                    popupWindow.setWidth(v.getWidth());
                } else {
                    popupWindow.setWidth(maxTextWidth);
                }
                popupWindow.setHeight(LayoutParams.WRAP_CONTENT);
                popupWindow.setFocusable(true);
                popupWindow.setTouchable(true);
                popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.gmacs_bg_tab_bottom_normal));
                popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        WindowManager.LayoutParams layoutParams = gmacsChatActivity.getWindow().getAttributes();
                        layoutParams.alpha = 1f;
                        gmacsChatActivity.getWindow().setAttributes(layoutParams);
                    }
                });
                ListView lv = (ListView) layout.findViewById(R.id.lv_public_account_menuitem_layout);
                lv.setAdapter(new ListViewAdapter((Integer) tag));
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        try {
                            popupWindow.dismiss();
                            Intent intent = new Intent(context, Class.forName(GmacsUiUtil.getBrowserClassName()));
                            intent.putExtra(GmacsWebViewActivity.EXTRA_TITLE, config.getSecondaryTitle().
                                    get(config.getSecondaryContentIndex().get(v.getTag()).get(position)));
                            intent.putExtra(GmacsWebViewActivity.EXTRA_URL, config.getSecondaryUrl().
                                    get(config.getSecondaryContentIndex().get(v.getTag()).get(position)));
                            context.startActivity(intent);
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                });

                // 解决有NavigationBar时PopupWindow错位问题
                // 单个item的总宽度 = 按钮 + 分割线 = v.getWidth() + GmacsUtils.dipToPixel(0.5f)
                // 对齐高度 + 1是为了不遮挡Dock上边缘的divider
                if (hasNavigationBar(context)) {
                    popupWindow.showAtLocation(v, Gravity.RIGHT | Gravity.BOTTOM, (config.getPrimaryTitle().size() - 1 - (Integer) tag) * (v.getWidth() + GmacsUtils.dipToPixel(0.5f)),
                            v.getHeight() + getResources().getDimensionPixelSize(getResources().getIdentifier("navigation_bar_height", "dimen", "android")) + 1);
                } else {
                    popupWindow.showAtLocation(v, Gravity.RIGHT | Gravity.BOTTOM, (config.getPrimaryTitle().size() - 1 - (Integer) tag) * (v.getWidth() + GmacsUtils.dipToPixel(0.5f)), v.getHeight() + 1);
                }
                WindowManager.LayoutParams layoutParams = gmacsChatActivity.getWindow().getAttributes();
                layoutParams.alpha = 0.975f;
                gmacsChatActivity.getWindow().setAttributes(layoutParams);
//                }
            } else if (tag instanceof String) { // Tag is Url.
                try {
                    Intent intent = new Intent(context, Class.forName(GmacsUiUtil.getBrowserClassName()));
                    intent.putExtra(GmacsWebViewActivity.EXTRA_TITLE, (String) v.getTag(R.id.btn_public_account_menu_item));
                    intent.putExtra(GmacsWebViewActivity.EXTRA_URL, (String) tag);
                    context.startActivity(intent);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static private boolean hasNavigationBar(Context context) {
        boolean hasNavigationBar = false;
        Resources rs = context.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hasNavigationBar;
    }

    private class ListViewAdapter extends BaseAdapter {

        private LayoutInflater layoutInflater;
        private int menuIndex;
        private ViewHolder vh;

        ListViewAdapter(int menuIndex) {
            this.menuIndex = menuIndex;
            layoutInflater = LayoutInflater.from(context);
        }

        private final class ViewHolder {
            private TextView tv;
        }

        @Override
        public int getCount() {
            return config.getSecondaryContentIndex().get(menuIndex).size();
        }

        @Override
        public Object getItem(int position) {
            return new String[]{config.getSecondaryTitle().get(config.getSecondaryContentIndex().get(menuIndex).get(position)),
                    config.getSecondaryUrl().get(config.getSecondaryContentIndex().get(menuIndex).get(position))};
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            vh = null;
            if (convertView == null) {
                vh = new ViewHolder();
                convertView = layoutInflater.inflate(R.layout.gmacs_public_account_menuitem_layout, null);
                vh.tv = (TextView) convertView.findViewById(R.id.tv_public_account_menuitem);
                convertView.setTag(vh);
            } else {
                vh = (ViewHolder) convertView.getTag();
            }

            String[] item = (String[]) getItem(position);
            vh.tv.setText(item[0]);
            vh.tv.setTag(item[1]);
            return convertView;
        }

    }
}
