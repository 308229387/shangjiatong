package com.android.gmacs.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.merchantplatform.R;
import com.android.gmacs.album.AlbumViewPager;
import com.android.gmacs.view.GmacsDialog;
import com.android.gmacs.view.IMImageMsgView;
import com.android.gmacs.view.photoview.PhotoView;
import com.android.gmacs.view.photoview.PhotoViewAttacher;
import com.common.gmacs.core.ChannelManager;
import com.common.gmacs.core.GmacsConstant;
import com.common.gmacs.downloader.RequestManager;
import com.common.gmacs.downloader.VolleyError;
import com.common.gmacs.downloader.image.ImageLoader;
import com.common.gmacs.downloader.image.ImageRequest;
import com.common.gmacs.utils.BitmapUtil;
import com.common.gmacs.utils.FileUtil;
import com.common.gmacs.utils.GmacsEnvi;
import com.common.gmacs.utils.ImageUtil;
import com.common.gmacs.utils.StringUtil;
import com.common.gmacs.utils.ToastUtil;
import com.xxganji.gmacs.Client;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.android.gmacs.msg.view.IMImageMsgView.ImgResize;
import static com.android.gmacs.msg.view.IMImageMsgView.MinResize;

/**
 * 图片类
 */
public class GmacsImageActivity extends BaseActivity {

    private AlbumViewPager mImageViewPager;
    private ArrayList<IMImageMsgView.ImageInfoWrapper> mImageInfoList;
    private RelativeLayout mLayout;
    private int mCurrentIndex, mEnterIndex;
    private final int animationDuration = 200;
    private boolean firstShown = true;
    private int bitmapVisibleWidth;
    private int bitmapVisibleHeight;

    @Override
    protected void initView() {
        mLayout = (RelativeLayout) findViewById(R.id.activity_image_layout);
    }

    @Override
    protected void initData() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissionOnNeed(Manifest.permission.READ_EXTERNAL_STORAGE,
                    GmacsConstant.REQUEST_CODE_READ_EXTERNAL_STORAGE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gmacs_activity_image);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
        requestWindowNoTitle(true);
        if (!getIntentData()) {
            finish();
            return;
        }

        initImageView();

        if (0 != mCurrentIndex) {
            mImageViewPager.setCurrentItem(mCurrentIndex);
        }
    }

    private boolean getIntentData() {
        Intent intent = getIntent();
        mImageInfoList = intent.getParcelableArrayListExtra(IMImageMsgView.IMAGE_INFO);
        mCurrentIndex = intent.getIntExtra(IMImageMsgView.IMAGE_INDEX, 0);
        mEnterIndex = mCurrentIndex;
        return true;
    }

    private void initImageView() {
        mImageViewPager = (AlbumViewPager) findViewById(R.id.vp_content_image);
        mImageViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int i) {
                mCurrentIndex = i;
                if (mTitleBar.getVisibility() == View.VISIBLE) {
                    mTitleBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        mImageViewPager.setAdapter(new ImageViewPagerAdapter(mImageInfoList));
    }

    private static final class ViewHolder {
        PhotoView photoView;
    }

    private class ImageViewPagerAdapter extends PagerAdapter {

        private List<IMImageMsgView.ImageInfoWrapper> dataList = new ArrayList<>();

        ImageViewPagerAdapter(List<IMImageMsgView.ImageInfoWrapper> dataList) {
            this.dataList = dataList;
        }

        @Override
        public int getCount() {
            return dataList.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            final View view = LayoutInflater.from(container.getContext()).inflate(R.layout.gmacs_adapter_image_pager_item, null);
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.photoView = (PhotoView) view.findViewById(R.id.imagePhotoView);
            view.setTag(viewHolder);
            final IMImageMsgView.ImageInfoWrapper imageInfo = dataList.get(position);
            String requestUrl;
            String tempUrl;
            int[] scaleSize = ImageUtil.getScaleSize(imageInfo.mWidth, imageInfo.mHeight, ImgResize, ImgResize, MinResize, MinResize);
            final int imageInCacheWidth = scaleSize[0];
            final int imageInCacheHeight = scaleSize[1];
            final int requestWidth = scaleSize[2];
            final int requestHeight = scaleSize[3];
            if (imageInfo.mUrl.startsWith("/")) {
                requestUrl = imageInfo.mUrl;
                tempUrl = imageInfo.mUrl;
            } else {
                requestUrl = imageInfo.mUrl;
                tempUrl = ImageUtil.makeUpUrl(imageInfo.mUrl, requestHeight, requestWidth);
            }
            final String finalTempUrl = tempUrl;
            ImageLoader.ImageContainer imageContainer =
                    RequestManager.getInstance().getNoL1CacheImageLoader().get(requestUrl
                    , new ImageLoader.ImageListener() {
                        @Override
                        public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                            Bitmap bitmap = response.getBitmap();
                            if (bitmap == null) {
                                RequestManager.getInstance().getImageLoader().get(finalTempUrl
                                        , new ImageLoader.ImageListener() {
                                            @Override
                                            public void onResponse(final ImageLoader.ImageContainer response, boolean isImmediate) {
                                                Bitmap bitmap1 = response.getBitmap();
                                                if (bitmap1 != null) {
                                                    if (position == mEnterIndex) {
                                                        animateEnter(bitmap1, viewHolder.photoView);
                                                    } else {
                                                        viewHolder.photoView.setImageBitmap(bitmap1);
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                if (position == mEnterIndex && firstShown) {
                                                    finish();
                                                }
                                            }
                                        }, imageInCacheWidth
                                        , imageInCacheHeight
                                        , ImageView.ScaleType.CENTER_CROP, ImageRequest.DRAW_SHAPE_RECT
                                        , 0);
                            } else {
                                viewHolder.photoView.setImageBitmap(bitmap);
                            }
                        }

                        @Override
                        public void onErrorResponse(VolleyError error) {
                        }
                    }, GmacsEnvi.getGLMaxTextureSize(), GmacsEnvi.getGLMaxTextureSize()
                    , ImageView.ScaleType.CENTER_INSIDE, ImageRequest.DRAW_SHAPE_RECT
                    , 0);
            container.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            viewHolder.photoView.setTag(imageContainer);
            viewHolder.photoView.setOnLongClickListener(mPhotoOnLongClickListener);
            viewHolder.photoView.setOnPhotoTapListener(mPhotoOnTapListener);
            viewHolder.photoView.setOnViewTapListener(mViewOnTapListener);
            return view;
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == o;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            //super.destroyItem(container, position, object);
            View view = (View) object;
            ViewHolder viewHolder = (ViewHolder) view.getTag();
            if (viewHolder.photoView != null) {
                ((ImageLoader.ImageContainer) viewHolder.photoView.getTag()).cancelRequest();
                viewHolder.photoView.setImageBitmap(null);
            }

            container.removeView((View) object);
            unbindDrawables((View) object);
        }
        private void unbindDrawables(View view) {
            if (view.getBackground() != null) {
                view.getBackground().setCallback(null);
            }
            if (view instanceof ViewGroup) {
                for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                    unbindDrawables(((ViewGroup) view).getChildAt(i));
                }
                ((ViewGroup) view).removeAllViews();
            }
        }
    }

    private void animateEnter(final Bitmap bitmap, final PhotoView photoView) {
        if (!firstShown) {
            photoView.setImageBitmap(bitmap);
            return;
        }
        firstShown = false;
        final Intent intent = getIntent();

        photoView.post(new Runnable() {
            @TargetApi(Build.VERSION_CODES.HONEYCOMB)
            @Override
            public void run() {
                bitmapVisibleWidth = photoView.getWidth();
                if (bitmap.getHeight() * photoView.getWidth() >= bitmap.getWidth() * photoView.getHeight()) {
                    bitmapVisibleHeight = photoView.getHeight();
                } else {
                    bitmapVisibleHeight = Math.round(bitmap.getHeight() * 1f / bitmap.getWidth() * photoView.getWidth());
                }

                int chatCardWidth = intent.getIntExtra("width", 0);
                int chatCardHeight = intent.getIntExtra("height", 0);
                int chatCardX = intent.getIntExtra("x", 0);
                int chatCardY = intent.getIntExtra("y", 0);

                mImageViewPager.setLayerType(View.LAYER_TYPE_HARDWARE, null);
                ObjectAnimator animatorScaleX = ObjectAnimator.ofFloat(mImageViewPager, "scaleX", chatCardWidth * 1f / bitmapVisibleWidth, 1);
                ObjectAnimator animatorScaleY = ObjectAnimator.ofFloat(mImageViewPager, "scaleY", chatCardHeight * 1f / bitmapVisibleHeight, 1);
                ObjectAnimator animatorTranslateX = ObjectAnimator.ofFloat(mImageViewPager, "translationX", -((GmacsEnvi.screenWidth - chatCardWidth) / 2 - chatCardX), 0);
                ObjectAnimator animatorTranslateY;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    animatorTranslateY = ObjectAnimator.ofFloat(mImageViewPager, "translationY", -((GmacsEnvi.screenHeight - chatCardHeight) / 2 - chatCardY), 0);
                } else {
                    animatorTranslateY = ObjectAnimator.ofFloat(mImageViewPager, "translationY", (chatCardHeight - GmacsEnvi.screenHeight - GmacsEnvi.statusBarHeight) / 2 + chatCardY, 0);
                }

                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.play(animatorScaleX)
                        .with(animatorScaleY)
                        .with(animatorTranslateX)
                        .with(animatorTranslateY);
                animatorSet.setDuration(animationDuration)
                        .addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                photoView.setImageBitmap(bitmap);
                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                mLayout.setBackgroundColor(Color.BLACK);
                                mImageViewPager.setLayerType(View.LAYER_TYPE_NONE, null);
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        });
                animatorSet.setInterpolator(new DecelerateInterpolator());
                animatorSet.start();
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void animateExit() {
        if (mCurrentIndex == mEnterIndex) {
            mLayout.setBackgroundColor(Color.TRANSPARENT);

            Intent intent = getIntent();
            int chatCardWidth = intent.getIntExtra("width", 0);
            int chatCardHeight = intent.getIntExtra("height", 0);
            int chatCardX = intent.getIntExtra("x", 0);
            int chatCardY = intent.getIntExtra("y", 0);

            mImageViewPager.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            ObjectAnimator animatorScaleX = ObjectAnimator.ofFloat(mImageViewPager, "scaleX", chatCardWidth * 1f / bitmapVisibleWidth);
            ObjectAnimator animatorScaleY = ObjectAnimator.ofFloat(mImageViewPager, "scaleY", chatCardHeight * 1f / bitmapVisibleHeight);
            ObjectAnimator animatorTranslateX = ObjectAnimator.ofFloat(mImageViewPager, "translationX", -((GmacsEnvi.screenWidth - chatCardWidth) / 2 - chatCardX));
            ObjectAnimator animatorTranslateY;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                animatorTranslateY = ObjectAnimator.ofFloat(mImageViewPager, "translationY", -((GmacsEnvi.screenHeight - chatCardHeight) / 2 - chatCardY));
            } else {
                animatorTranslateY = ObjectAnimator.ofFloat(mImageViewPager, "translationY", (chatCardHeight - GmacsEnvi.screenHeight - GmacsEnvi.statusBarHeight) / 2 + chatCardY);
            }
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.play(animatorScaleX)
                    .with(animatorScaleY)
                    .with(animatorTranslateX)
                    .with(animatorTranslateY);
            animatorSet.setDuration(animationDuration)
                    .addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mImageViewPager.setLayerType(View.LAYER_TYPE_NONE, null);
                            finish();
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
            animatorSet.setInterpolator(new DecelerateInterpolator());
            animatorSet.start();
        } else {
            finish();
            overridePendingTransition(0, R.anim.gmacs_anim_photo_exit);
        }
    }

    private View.OnLongClickListener mPhotoOnLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            final GmacsDialog.Builder dialog = new GmacsDialog.Builder(GmacsImageActivity.this, GmacsDialog.Builder.DIALOG_TYPE_LIST_NO_BUTTON);
            dialog.initDialog(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (position == 0) {
                        String url = mImageInfoList.get(mCurrentIndex).mUrl;
                        if (FileUtil.getSDCardFile("image/" + StringUtil.MD5(url) + ".jpg").exists()) {
                            ToastUtil.showToast(getResources().getString(R.string.picture_already_saved, ChannelManager.appName));
                        } else {
                            saveImageToLocal();
                        }
                        dialog.dismiss();
                    }
                }
            }).setListTexts(new String[]{getString(R.string.save_picture)}).create().show();
            return true;
        }
    };

    private PhotoViewAttacher.OnViewTapListener mViewOnTapListener = new PhotoViewAttacher.OnViewTapListener() {
        @Override
        public void onViewTap(View view, float x, float y) {
            animateExit();
        }
    };

    private PhotoViewAttacher.OnPhotoTapListener mPhotoOnTapListener = new PhotoViewAttacher.OnPhotoTapListener() {
        @Override
        public void onPhotoTap(View view, float x, float y) {
            animateExit();
        }
    };

    private void saveImageToLocal() {
        final String url = mImageInfoList.get(mCurrentIndex).mUrl;
        if (!url.startsWith("/")) {
            RequestManager.getInstance().getNoL1CacheImageLoader().get(url, new ImageLoader.ImageListener() {
                        @Override
                        public void onResponse(final ImageLoader.ImageContainer response, boolean isImmediate) {
                            if (response.getBitmap() != null) {
                                Client.getInstance().post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (saveImageToSdCard(response.getBitmap(), url)) {
                                            ToastUtil.showToast(getResources().getString(R.string.picture_save_ok, ChannelManager.appName));
                                        } else {
                                            ToastUtil.showToast(getResources().getString(R.string.picture_save_failed));
                                        }
                                    }
                                });
                            }
                        }

                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    }, GmacsEnvi.getGLMaxTextureSize(), GmacsEnvi.getGLMaxTextureSize()
                    , ImageView.ScaleType.CENTER_INSIDE, ImageRequest.DRAW_SHAPE_RECT
                    , 0);
        } else {
            ToastUtil.showToast(getResources().getString(R.string.picture_already_saved, ChannelManager.appName));
        }
    }

    /**
     * 保存图片到SD卡
     *
     * @param bitmap
     * @param url
     * @return
     */
    private boolean saveImageToSdCard(Bitmap bitmap, String url) {
        if (null == bitmap) {
            return false;
        }
        String filename = StringUtil.MD5(url);
        File captureFile;
        try {
            if (!FileUtil.sdcardAvailable()) {
                return false;
            }
            captureFile = new File(BitmapUtil.SAVE_IMAGE_FILE_DIR, filename + ".jpg");
            BufferedOutputStream bufferOs = new BufferedOutputStream(new FileOutputStream(captureFile));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bufferOs);
            bufferOs.flush();
            bufferOs.close();
            Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            scanIntent.setData(Uri.fromFile(captureFile));
            GmacsEnvi.appContext.sendBroadcast(scanIntent);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        animateExit();
    }

    @Override
    public void finish() {
        super.finish();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    }

}
