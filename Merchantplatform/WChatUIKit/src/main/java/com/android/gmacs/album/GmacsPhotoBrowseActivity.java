package com.android.gmacs.album;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wchatuikit.R;
import com.android.gmacs.activity.BaseActivity;
import com.android.gmacs.view.photoview.PhotoView;
import com.android.gmacs.view.photoview.PhotoViewAttacher;
import com.common.gmacs.downloader.RequestManager;
import com.common.gmacs.downloader.VolleyError;
import com.common.gmacs.downloader.image.ImageLoader;
import com.common.gmacs.downloader.image.ImageRequest;
import com.common.gmacs.utils.GmacsEnvi;
import com.common.gmacs.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 照片浏览Activity
 */
public class GmacsPhotoBrowseActivity extends BaseActivity {

    private ArrayList<String> mSelectedDataList = new ArrayList<>();
    private ArrayList<String> mDataList = new ArrayList<>();
    private int currentIndex;

    private ImageView mRawCheck;
    private TextView mRawSizeText;
    private ImageView mSelectCheck;
    private TextView mSelectText;
    private String func = AlbumConstant.FUNC_UPDATE;
    private boolean fromCamera;

    // 拍照后预览, 取消发送
    private boolean isCameraJobCanceled = true;
    private boolean isPreviewMode;

    private int mMaxCount;
    private ViewPager mViewPager;
    private RelativeLayout mBottomContainer;

    @Override
    protected void initView() {
        requestNotFitSystemWindow();
        resetContentContainerMargin();
        mTitleBar.setBackListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mTitleBar.setBackgroundColor(getResources().getColor(R.color.dark_212121));
        if (mStatusBar != null) {
            mStatusBar.setBackgroundColor(getResources().getColor(R.color.dark_212121));
        }
        mViewPager = (AlbumViewPager) findViewById(R.id.view_pager);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                currentIndex = position;
                mTitleBar.setTitle((position + 1) + "/" + mDataList.size());
                if (mSelectedDataList.contains(mDataList.get(position))) {
                    mSelectCheck.setTag(true);
                    mSelectCheck.setImageResource(R.drawable.gmacs_btn_checkbox_checked);
                } else {
                    mSelectCheck.setTag(false);
                    mSelectCheck.setImageResource(R.drawable.gmacs_btn_checkbox_unchecked);
                }
                notifyCountInfo();
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }

        });

        mTitleBar.mRightTextView.setVisibility(View.VISIBLE);
        mTitleBar.mRightTextView.setText(R.string.send);
        mTitleBar.mRightTextView.setBackgroundResource(R.drawable.gmacs_blue_btn);
        mTitleBar.mRightTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSelectedDataList.size() == 0) {
                    mSelectedDataList.add(mDataList.get(currentIndex));
                }
                func = AlbumConstant.FUNC_OK;
                isCameraJobCanceled = false;
                finish();
            }
        });

        mBottomContainer = (RelativeLayout) findViewById(R.id.bottom_layout);
        mRawCheck = (ImageView) mBottomContainer.findViewById(R.id.raw);
        mRawCheck.setTag(false);
        mRawCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((boolean) mRawCheck.getTag()) {
                    // TODO: 图片大小
//                    mRawSizeText.setText();
                    mRawCheck.setTag(false);
                    mRawCheck.setImageResource(R.drawable.gmacs_btn_radio_unselected);
                } else {
                    mRawCheck.setTag(true);
                    mRawCheck.setImageResource(R.drawable.gmacs_btn_radio_selected);
                }
            }
        });
        mRawSizeText = (TextView) mBottomContainer.findViewById(R.id.raw_size);
        mSelectCheck = (ImageView) mBottomContainer.findViewById(R.id.select);
        mSelectCheck.setTag(false);
        mSelectCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri = mDataList.get(currentIndex);
                if ((boolean) mSelectCheck.getTag()) {
                    mSelectCheck.setTag(false);
                    mSelectCheck.setImageResource(R.drawable.gmacs_btn_checkbox_unchecked);
                    mSelectedDataList.remove(uri);
                } else {
                    if (mSelectedDataList.size() < mMaxCount) {
                        mSelectCheck.setTag(true);
                        mSelectCheck.setImageResource(R.drawable.gmacs_btn_checkbox_checked);
                        mSelectedDataList.add(uri);
                    } else {
                        ToastUtil.showToast(String.format(getString(R.string.reach_upload_max), mMaxCount));
                        return;
                    }
                }
                notifyCountInfo();
            }
        });
        mSelectText = (TextView) findViewById(R.id.select_text);
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        mMaxCount = intent.getIntExtra(AlbumConstant.EXTRA_PHOTO_MAX_COUNT, 10);
        boolean isPreview = intent.getBooleanExtra(AlbumConstant.IS_PREVIEW, false);
        ImageUrlArrayListWrapper wrapper = intent.getParcelableExtra(AlbumConstant.KEY_SELECTED_IMG_DATA);
        ArrayList<String> dataList = wrapper.mList;
        if (dataList != null) {
            mSelectedDataList.addAll(dataList);
        } else {
            finish();
            return;
        }
        if (intent.getBooleanExtra(AlbumConstant.RAW, false)) {
            mRawCheck.setTag(true);
            mRawCheck.setImageResource(R.drawable.gmacs_btn_radio_selected);
        } else {
            mRawCheck.setTag(false);
            mRawCheck.setImageResource(R.drawable.gmacs_btn_radio_unselected);
        }

        if (isPreview) {
            mDataList.addAll(mSelectedDataList);
            fromCamera = intent.getBooleanExtra(AlbumConstant.FROM_CAMERA, false);
            if (fromCamera) {
                mSelectCheck.setVisibility(View.GONE);
                mSelectText.setVisibility(View.GONE);
                mTitleBar.mTitleView.setVisibility(View.GONE);
            }
        } else {
            String mDirPath = intent.getStringExtra(AlbumConstant.DIR_PATH);
            if (TextUtils.isEmpty(mDirPath)) {
                finish();
                return;
            }

            GmacsImageDao.ImgDir imgDir = GmacsImageDao.instance(this).getImgListByDir(mDirPath);
            if (imgDir == null) {
                finish();
                return;
            } else {
                mDataList.addAll(imgDir.dataList);
                currentIndex = intent.getIntExtra(AlbumConstant.KEY_IMG_POSITION, 0);
            }
        }

        mViewPager.setAdapter(new AlbumPagerAdapter(mDataList));
        if (currentIndex != 0) {
            mViewPager.setCurrentItem(currentIndex);
        } else if (fromCamera) {
            mTitleBar.mRightTextView.setText(getText(R.string.send));
        } else {
            mTitleBar.setTitle("1/" + mDataList.size());
            if (mSelectedDataList.contains(mDataList.get(0))) {
                mSelectCheck.setTag(true);
                mSelectCheck.setImageResource(R.drawable.gmacs_btn_checkbox_checked);
            } else {
                mSelectCheck.setTag(false);
                mSelectCheck.setImageResource(R.drawable.gmacs_btn_checkbox_unchecked);
            }
            notifyCountInfo();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gmacs_activity_photo_browse);
    }

    private class AlbumPagerAdapter extends PagerAdapter implements PhotoViewAttacher.OnPhotoTapListener {

        private List<String> dataList = new ArrayList<>();

        AlbumPagerAdapter(List<String> dataList) {
            super();
            this.dataList = dataList;
        }

        @Override
        public int getCount() {
            return dataList.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            final PhotoView photoView = new PhotoView(container.getContext());
            photoView.setPadding(2, 0, 2, 0);
            RequestManager.getInstance().getNoL1CacheImageLoader().get(dataList.get(position),
                    new ImageLoader.ImageListener() {
                        @Override
                        public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                            if (response.getBitmap() != null) {
                                photoView.setImageBitmap(response.getBitmap());
                            }
                        }

                        @Override
                        public void onErrorResponse(VolleyError error) {
                        }
                    }, GmacsEnvi.getGLMaxTextureSize(), GmacsEnvi.getGLMaxTextureSize(),
                    ImageView.ScaleType.CENTER_INSIDE, ImageRequest.DRAW_SHAPE_RECT, 0);

            // Now just onEvent PhotoView to ViewPager and return it
            container.addView(photoView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            photoView.setOnPhotoTapListener(this);
            return photoView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void onPhotoTap(View view, float x, float y) {
            switchPreviewMode();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onBackPressed();
            return true;
        }
        return false;
    }

    private void notifyCountInfo() {
        int count = mSelectedDataList.size();
        if (count == 0) {
            mTitleBar.mRightTextView.setText(R.string.send);
        } else {
            mTitleBar.mRightTextView.setText(String.format(getString(R.string.send_count), count, 10));
        }
    }

    @Override
    public void finish() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
        myOnBack(func);
        super.finish();
    }

    public void myOnBack(String func) {
        ToastUtil.cancelToast();
        Intent data = new Intent();
        data.putExtra(AlbumConstant.KEY_IMG_POSITION, currentIndex);
        data.putExtra(AlbumConstant.FUNC, func);
        data.putExtra(AlbumConstant.RAW, (boolean) mRawCheck.getTag());
        data.putExtra(AlbumConstant.KEY_SELECTED_IMG_DATA, new ImageUrlArrayListWrapper(mSelectedDataList));
        if (fromCamera && isCameraJobCanceled) {
            setResult(RESULT_CANCELED);
        } else {
            setResult(RESULT_OK, data);
        }
    }

    private void switchPreviewMode() {
        if (!isPreviewMode) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            }
            hideTitleBar();
            mBottomContainer.setVisibility(View.GONE);
            isPreviewMode = true;
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            }
            showTitleBar();
            mBottomContainer.setVisibility(View.VISIBLE);
            isPreviewMode = false;
        }
    }

}
