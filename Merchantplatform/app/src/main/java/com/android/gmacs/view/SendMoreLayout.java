package com.android.gmacs.view;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.merchantplatform.R;
import com.android.gmacs.activity.BaseActivity;
import com.android.gmacs.activity.GmacsChatActivity;
import com.android.gmacs.activity.GmacsMapActivity;
import com.android.gmacs.adapter.ViewPagerAdapter;
import com.android.gmacs.album.AlbumConstant;
import com.android.gmacs.album.GmacsAlbumActivity;
import com.android.gmacs.album.GmacsPhotoBrowseActivity;
import com.android.gmacs.album.ImageUrlArrayListWrapper;
import com.common.gmacs.core.GmacsConstant;
import com.common.gmacs.utils.BitmapUtil;
import com.common.gmacs.utils.FileUtil;
import com.common.gmacs.utils.GLog;
import com.common.gmacs.utils.GmacsUtils;
import com.common.gmacs.utils.ToastUtil;
import com.log.LogUmengAgent;
import com.log.LogUmengEnum;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by YanQi on 2015/12/12.
 */
public class SendMoreLayout extends LinearLayout implements ViewPager.OnPageChangeListener {

    public final static int REQUEST_GALLERY_CODE = 301;     // 图库请求码
    public final static int REQUEST_TAKE_PHOTO_CODE = 302;  // 拍照返回请求码
    public final static int REQUEST_LOCATION_CODE = 303;    // 位置请求码
    public final static int REQUEST_CAMERA_CODE = 3021;    // 拍照返回并预览后的发送请求码
    //    public final static int REQUEST_WRIGHT_SETTINGS_CODE = 304;//设置写权限请求码
    public final static String DEFAULT_BTN_TEXT_IMAGE = "图片";
    public final static String DEFAULT_BTN_TEXT_CAMERA = "拍照";
    public final static String DEFAULT_BTN_TEXT_LOCATION = "位置";

    // Default button settings.
    private int[] btnImgResIds = new int[]{R.drawable.gmacs_ic_send_image,
            R.drawable.gmacs_ic_send_camera, R.drawable.gmacs_ic_send_location};
    private String[] btnTexts = new String[]{DEFAULT_BTN_TEXT_IMAGE,
            DEFAULT_BTN_TEXT_CAMERA, DEFAULT_BTN_TEXT_LOCATION};

    private GmacsChatActivity gmacsChatActivity;
    private ViewPager mViewPager;
    private ViewPagerAdapter mPagerAdapter;
    private List<View> mViewList;
    private LinearLayout mDotLayout;
    private ArrayList<ImageView> mDotList;
    private SendMoreAdapter mViewAdapter;
    private OnMoreItemClickListener mcListener;
    private static String picturePath;
    private int pageCount = 3;
    private int currentPage = 1;

    public SendMoreLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SendMoreLayout(Context context) {
        super(context);
    }

    public SendMoreLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ArrayList<SendMoreAdapterDataStruct> data = new ArrayList<>();
        addBtnContent(data, 0, btnTexts.length);
        mViewPager = (ViewPager) findViewById(R.id.send_more_viewpager);
        mDotLayout = (LinearLayout) findViewById(R.id.send_more_view_dot);

        GridView mEmptyView1, mEmptyView2;
        GridView mView = (GridView) LayoutInflater.from(getContext()).inflate(R.layout.gmacs_send_more_view, null);
        mView.setAdapter(mViewAdapter = new SendMoreAdapter(getContext(), data));
        mView.setOnItemClickListener(new OnItemClickListener());
        mViewPager.addView(mEmptyView1 = new GridView(getContext()));
        mViewPager.addView(mView);
        mViewPager.addView(mEmptyView2 = new GridView(getContext()));
        mViewList = new ArrayList<>();
        mViewList.add(mEmptyView1);
        mViewList.add(mView);
        mViewList.add(mEmptyView2);
        mViewPager.setAdapter(mPagerAdapter = new ViewPagerAdapter(mViewList));
        mViewPager.setCurrentItem(currentPage);
        mViewPager.setOnPageChangeListener(this);
    }

    public void setGmacsChatActivity(GmacsChatActivity gmacsChatActivity) {
        this.gmacsChatActivity = gmacsChatActivity;
    }

    /**
     * There are only 3 default btnImgResIds, <i>R.drawable.gmacs_ic_send_image</i>,
     * <i>R.drawable.gmacs_ic_send_camera</i> and <i>R.drawable.gmacs_ic_send_location</i>.
     * <br><b>We support more than eight buttons, there is a ViewPager will be shown if possible.</b></br>
     *
     * @param imgResIds The resource id of image of buttons.
     */
    public void setBtnImgResIds(int[] imgResIds) {
        btnImgResIds = imgResIds;
    }

    /**
     * There are only 3 default btnTexts, <i>DEFAULT_BTN_TEXT_IMAGE</i>,
     * <i>DEFAULT_BTN_TEXT_CAMERA</i> and <i>DEFAULT_BTN_TEXT_LOCATION</i>.
     * <br><b>We support more than eight buttons, there is a ViewPager will be shown if possible.</b></br>
     *
     * @param texts The unique texts of buttons, which can identify buttons definitely.
     */
    public void setBtnTexts(String[] texts) {
        btnTexts = texts;
    }

    /**
     * If true passed by param and the amount of items below 5, the SendMoreView height would be set as single line.
     * <br>Otherwise, double-line height as default value instead.
     *
     * @param isPreferred
     */
    public void showItemsSingleLinePreferred(boolean isPreferred) {
        //TODO
        if (isPreferred && btnImgResIds.length <= 4) {
            mViewPager.getLayoutParams().height = GmacsUtils.dipToPixel(209.25f / 2 + getResources().getDimension(R.dimen.size_b_b) / 2);
        }
    }

    /**
     * Add buttons' data for their adapter.
     *
     * @param data  The object which carries all the buttons' data.
     * @param start Add data starts from No.start button.
     * @param count Add data ends up to No.start+count button.
     */
    private void addBtnContent(ArrayList<SendMoreAdapterDataStruct> data, int start, int count) {
        for (int i = start; i < count; i++) {
            if (btnImgResIds[i] == R.drawable.gmacs_ic_send_image) {
                data.add(new SendMoreAdapterDataStruct(R.drawable.gmacs_ic_send_image, DEFAULT_BTN_TEXT_IMAGE));
            } else if (btnImgResIds[i] == R.drawable.gmacs_ic_send_camera) {
                data.add(new SendMoreAdapterDataStruct(R.drawable.gmacs_ic_send_camera, DEFAULT_BTN_TEXT_CAMERA));
            } else if (btnImgResIds[i] == R.drawable.gmacs_ic_send_location) {
                if (null == gmacsChatActivity || gmacsChatActivity.sendLocationEnable())
                    data.add(new SendMoreAdapterDataStruct(R.drawable.gmacs_ic_send_location, DEFAULT_BTN_TEXT_LOCATION));
            } else {
                data.add(new SendMoreAdapterDataStruct(btnImgResIds[i], btnTexts[i]));
            }
        }
    }

    /**
     * Notify the data of adapter.
     */
    public void notifyData() {
        if (btnImgResIds != null && btnTexts != null && btnImgResIds.length == btnTexts.length) {
            mViewAdapter.mData.clear();
            if (btnTexts.length <= 8) {
                addBtnContent(mViewAdapter.mData, 0, btnTexts.length);
            } else {
                mViewList.clear();
                mDotLayout.setVisibility(LinearLayout.VISIBLE);
                mDotList = new ArrayList<>();

                pageCount = 2 + (btnTexts.length % 8 == 0 ? ((int) (btnTexts.length / 8f)) : ((int) (btnTexts.length / 8f)) + 1);

                mViewList.add(new GridView(getContext()));
                for (int i = 0; i < pageCount; i++) {
                    if (i < pageCount - 2) {
                        GridView gv = (GridView) LayoutInflater.from(getContext()).inflate(R.layout.gmacs_send_more_view, null);
                        gv.setOnItemClickListener(new OnItemClickListener());

                        // Add SendMoreView in every page of mViewPager
                        mViewList.add(gv);
                        ArrayList<SendMoreAdapterDataStruct> data = new ArrayList<>();
                        for (int j = i * 8; j < (i + 1) * 8 && btnTexts.length - j > 0; j++) {
                            data.add(new SendMoreAdapterDataStruct(btnImgResIds[j], btnTexts[j]));
                        }
                        gv.setAdapter(new SendMoreAdapter(getContext(), data));
                    }

                    // Add dot button associated with every page of mViewPager
                    ImageView iv = new ImageView(getContext());
                    iv.setImageResource(R.drawable.gmacs_d1);
                    LayoutParams layoutParams = new LayoutParams(
                            new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    layoutParams.leftMargin = GmacsUtils.dipToPixel(5);
                    layoutParams.rightMargin = GmacsUtils.dipToPixel(5);
                    layoutParams.width = GmacsUtils.dipToPixel(5);
                    layoutParams.height = GmacsUtils.dipToPixel(5);
                    mDotLayout.addView(iv, layoutParams);
                    if (i == 0 || i == pageCount - 1) {
                        iv.setVisibility(View.GONE);
                    }
                    if (i == 1) {
                        iv.setImageResource(R.drawable.gmacs_d2);
                    }
                    mDotList.add(iv);
                }
                mViewList.add(new GridView(getContext()));
            }
            mPagerAdapter.notifyDataSetChanged();
            mViewAdapter.notifyDataSetChanged();
            mViewPager.setCurrentItem(currentPage);
        } else {
            GLog.e("SendMoreLayout", "The buttons' texts count is not equal to their imgResIds'.");
        }
    }

    /**
     * @return mSendMoreAdapter The adapter of SendMoreView which you can modify.
     */
    public SendMoreAdapter getSendMoreAdapter() {
        return mViewAdapter;
    }

    /**
     * 打开图片选择页面
     */
    private void openAlbumActivity() {
        LogUmengAgent.ins().log(LogUmengEnum.LOG_LIAOTIANXQY_XC);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean findMethod = true;
            try {
                ContextCompat.class.getMethod("checkSelfPermission", Context.class, String.class);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
                findMethod = false;
            }
            if (findMethod && ContextCompat.checkSelfPermission(getContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) getContext(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, GmacsConstant.REQUEST_CODE_READ_EXTERNAL_STORAGE);
                return;
            }
        }

        if (!FileUtil.sdcardAvailable()) {
            ToastUtil.showToast(gmacsChatActivity.getText(R.string.sdcard_not_exist));
        } else {
            Intent intent = new Intent(gmacsChatActivity, GmacsAlbumActivity.class);
            intent.putExtra(AlbumConstant.EXTRA_PHOTO_MAX_COUNT, 10);
            gmacsChatActivity.startActivityForResult(intent, REQUEST_GALLERY_CODE);
        }
    }

    /**
     * 打开拍照页面
     */
    public void openCameraActivity() {
        LogUmengAgent.ins().log(LogUmengEnum.LOG_LIAOTIANXQY_ZX);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean findMethod = true;
            try {
                ContextCompat.class.getMethod("checkSelfPermission", Context.class, String.class);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
                findMethod = false;
            }
            if (findMethod && ContextCompat.checkSelfPermission(getContext(),
                    Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) getContext(),
                        new String[]{Manifest.permission.CAMERA}, GmacsConstant.REQUEST_CODE_CAMERA);
                return;
            }
        }

        if (!FileUtil.sdcardAvailable()) { // 检测sd是否可用
            return;
        }

        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String name = DateFormat.format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA)) + ".jpg";

        File dir = new File(BitmapUtil.SAVE_IMAGE_FILE_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, name);
        picturePath = file.getAbsolutePath();

        Uri imageUri = Uri.fromFile(file);

        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        gmacsChatActivity.startActivityForResult(openCameraIntent, REQUEST_TAKE_PHOTO_CODE);
    }

    /**
     * 打开地图位置
     */
    public void openLocationActivity() {
        LogUmengAgent.ins().log(LogUmengEnum.LOG_LIAOTIANXQY_WZ);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean findMethod = true;
            try {
                ContextCompat.class.getMethod("checkSelfPermission", Context.class, String.class);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
                findMethod = false;
            }
            if (findMethod && ContextCompat.checkSelfPermission(getContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) getContext(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION},
                        GmacsConstant.REQUEST_CODE_ACCESS_LOCATION);
                return;
            }
        }

        Intent intent = new Intent(gmacsChatActivity, GmacsMapActivity.class);
        gmacsChatActivity.startActivityForResult(intent, REQUEST_LOCATION_CODE);
    }

    public void onActivityForResult(int requestCode, int resultCode, final Intent data) {
        if (resultCode != BaseActivity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_GALLERY_CODE) {
            GmacsUtils.getInstance().execute(new Runnable() {
                @Override
                public void run() {
                    if (data == null) {
                        return;
                    }
                    ImageUrlArrayListWrapper wrapper = data.getParcelableExtra(AlbumConstant.KEY_SELECTED_IMG_DATA);
                    ArrayList<String> dataList = wrapper.mList;
                    if (dataList == null) {
                        ToastUtil.showToast(getResources().getText(R.string.no_file_selected));
                        return;
                    }
                    for (int i = 0; i < dataList.size(); i++) {
                        String filePath = dataList.get(i);
                        String tmp = filePath.toLowerCase();
                        boolean isPictureFile = ((tmp.endsWith(".png") || tmp.endsWith(".bmp") || tmp.endsWith(".jpeg") || tmp.endsWith(".jpg")));
                        if (isPictureFile) {
                            gmacsChatActivity.sendImageMsg(filePath, data.getBooleanExtra(AlbumConstant.RAW, false));
                        } else {
                            ToastUtil.showToast(getResources().getText(R.string.file_format_not_support));
                        }
                    }
                }
            });
        } else if (requestCode == REQUEST_TAKE_PHOTO_CODE) {
            if (!TextUtils.isEmpty(picturePath)) {
                File file = new File(picturePath);
                if (file.exists()) {
                    Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    scanIntent.setData(Uri.fromFile(file));
                    gmacsChatActivity.sendBroadcast(scanIntent);

                    Intent intent = new Intent(gmacsChatActivity, GmacsPhotoBrowseActivity.class);
                    ArrayList<String> arrayList = new ArrayList<>();
                    arrayList.add(picturePath);
                    intent.putExtra(AlbumConstant.KEY_SELECTED_IMG_DATA, new ImageUrlArrayListWrapper(arrayList));
                    intent.putExtra(AlbumConstant.IS_PREVIEW, true);
                    intent.putExtra(AlbumConstant.FROM_CAMERA, true);
                    gmacsChatActivity.startActivityForResult(intent, REQUEST_CAMERA_CODE);
                }
            }
        } else if (requestCode == REQUEST_LOCATION_CODE) {
            if (data == null) {
                return;
            }
            double longitude = data.getDoubleExtra(IMLocationMsgView.LONGITUDE, -1f);
            double latitude = data.getDoubleExtra(IMLocationMsgView.LATITUDE, -1f);

            if (longitude == -1 && latitude == -1) {
                ToastUtil.showToast(getResources().getText(R.string.locate_failed));
            } else {
                gmacsChatActivity.sendLocationMsg(longitude, latitude, data.getStringExtra(IMLocationMsgView.ADDRESS));
            }
        } else if (requestCode == REQUEST_CAMERA_CODE) {
            GmacsUtils.getInstance().execute(new Runnable() {
                @Override
                public void run() {
                    gmacsChatActivity.sendImageMsg(picturePath, data.getBooleanExtra(AlbumConstant.RAW, false));
                }
            });
        }
    }

    public interface OnMoreItemClickListener {
        void onMoreItemClick(int position);
    }

    public void registerOnMoreItemClick(OnMoreItemClickListener onMoreItemClickListener) {
        mcListener = onMoreItemClickListener;
    }

    @Override
    public void onPageScrolled(int i, float v, int i1) {

    }

    @Override
    public void onPageSelected(int i) {
        if (i == 0) {
            mViewPager.setCurrentItem(i + 1);
        } else if (i == pageCount - 1) {
            mViewPager.setCurrentItem(i - 1);
        } else {
            currentPage = i;
            if (pageCount > 3) {
                for (int j = 1; j < pageCount - 1; j++) {
                    mDotList.get(j).setImageResource(R.drawable.gmacs_d1);
                }
                mDotList.get(i).setImageResource(R.drawable.gmacs_d2);
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int i) {

    }

    /**
     * The Listener binding on SendMsgLayout.mSendMoreView.
     * <br>You can place the default buttons at the position where you'd like to.
     * Maybe exchange position and etc, by changing the default buttons' positions and their tags at the same time.</br>
     */
    public class OnItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
            /**
             * Handle the default buttons click action.
             */
            if (btnImgResIds[position + (currentPage - 1) * 8] == R.drawable.gmacs_ic_send_image) {
                openAlbumActivity();
            } else if (btnImgResIds[position + (currentPage - 1) * 8] == R.drawable.gmacs_ic_send_camera) {
                openCameraActivity();
            } else if (btnImgResIds[position + (currentPage - 1) * 8] == R.drawable.gmacs_ic_send_location) {
                openLocationActivity();
            }
            /**
             * Handle the custom buttons click action.
             */
            else {
                if (mcListener != null) {
                    mcListener.onMoreItemClick(position + (currentPage - 1) * 8);
                }
            }
        }

    }

    /**
     * Associated with SendMoreView.
     */
    private class SendMoreAdapter extends BaseAdapter {

        private LayoutInflater li;
        public ArrayList<SendMoreAdapterDataStruct> mData;
        private ViewHolder vh;

        public SendMoreAdapter(Context context, ArrayList<SendMoreAdapterDataStruct> data) {
            li = LayoutInflater.from(context);
            mData = data;
        }

        private final class ViewHolder {
            ImageView btnImg;
            TextView btnText;
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Object getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            vh = null;
            if (view == null) {
                view = li.inflate(R.layout.gmacs_send_more_item_layout, null);
                vh = new ViewHolder();
                vh.btnImg = (ImageView) view.findViewById(R.id.send_more_item_img);
                vh.btnText = (TextView) view.findViewById(R.id.send_more_item_text);
                view.setTag(vh);
            } else {
                vh = (ViewHolder) view.getTag();
            }

            SendMoreAdapterDataStruct dataStruct = mData.get(position);
            vh.btnImg.setImageResource(dataStruct.btnImgResId);
            vh.btnText.setText(dataStruct.btnTextName);
            return view;
        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {
            if (observer != null) {
                super.unregisterDataSetObserver(observer);
            }
        }

    }

    public static class SendMoreAdapterDataStruct {

        public int btnImgResId;
        public String btnTextName;

        public SendMoreAdapterDataStruct(int btnImgResId, String btnTextName) {
            this.btnImgResId = btnImgResId;
            this.btnTextName = btnTextName;
        }

    }

}
