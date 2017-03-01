
package com.android.gmacs.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

import com.merchantplatform.R;
import com.common.gmacs.downloader.RequestManager;
import com.common.gmacs.downloader.VolleyError;
import com.common.gmacs.downloader.image.ImageLoader.ImageContainer;
import com.common.gmacs.downloader.image.ImageLoader.ImageListener;
import com.common.gmacs.downloader.image.ImageRequest;
import com.common.gmacs.utils.GmacsEnvi;

/**
 * Handles fetching an image from a URL as well as the life-cycle of the
 * associated request.
 */
public class NetworkImageView extends ImageView {

    public static final int IMG_RESIZE = GmacsEnvi.appContext.getResources().getDimensionPixelOffset(R.dimen.avatar_conversation_list);

    /** The URL of the network image to load */
    private String mUrl;

    /**
     * Resource ID of the image to be used as a placeholder until the network image is loaded.
     */
    private int mDefaultImageId;

    /**
     * Resource ID of the image to be used if the network response fails.
     */
    private int mErrorImageId;


    /** Current ImageContainer. (either in-flight or finished) */
    private ImageContainer mImageContainer;

    private int mDrawShape = ImageRequest.DRAW_SHAPE_RECT;
    private int mDecorate;
    private Drawable mSideLine;
    private int mWidth;
    private int mHeight;

    public NetworkImageView(Context context) {
        this(context, null);
    }

    public NetworkImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.NetworkImageView);
        String drawShape = typedArray.getString(R.styleable.NetworkImageView_drawShape);
        if (drawShape != null) {
            if (drawShape.equals(context.getString(R.string.drawShape_RoundRect))) {
                mDrawShape = ImageRequest.DRAW_SHAPE_ROUND_RECT;
            } else if (drawShape.equals(context.getString(R.string.drawShape_Circle))) {
                mDrawShape = ImageRequest.DRAW_SHAPE_CIRCLE;
            }
        }
        mDecorate = typedArray.getResourceId(R.styleable.NetworkImageView_drawDecorate, 0);
        mSideLine = typedArray.getDrawable(R.styleable.NetworkImageView_drawSideLine);
        typedArray.recycle();

    }

    public NetworkImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setImageUrl(String url) {
        mUrl = url;
        // The URL has potentially changed. See if we need to load it.
        loadImageIfNecessary(false);
    }

    public NetworkImageView setViewWidth(int width) {
        mWidth = width;
        return this;
    }
    public NetworkImageView setViewHeight(int height) {
        mHeight = height;
        return this;
    }

    /**
     * Sets the default image resource ID to be used for this view until the attempt to load it
     * completes.
     */
    public NetworkImageView setDefaultImageResId(int defaultImage) {
        mDefaultImageId = defaultImage;
        return this;
    }

    /**
     * Sets the error image resource ID to be used for this view in the event that the image
     * requested fails to load.
     */
    public NetworkImageView setErrorImageResId(int errorImage) {
        mErrorImageId = errorImage;
        return this;
    }

    /**
     * Loads the image for the view if it isn't already loaded.
     * @param isInLayoutPass True if this was invoked from a layout pass, false otherwise.
     */
    void loadImageIfNecessary(final boolean isInLayoutPass) {
        int width;
        int height;
        if (mHeight == 0 || mWidth == 0) {
            width = getWidth();
            height = getHeight();
        } else {
            width = mWidth;
            height = mHeight;
        }
        ScaleType scaleType = getScaleType();

        boolean wrapWidth = false, wrapHeight = false;
        if (getLayoutParams() != null) {
            wrapWidth = getLayoutParams().width == LayoutParams.WRAP_CONTENT;
            wrapHeight = getLayoutParams().height == LayoutParams.WRAP_CONTENT;
        }

        // if the view's bounds aren't known yet, and this is not a wrap-content/wrap-content
        // view, hold off on loading the image.
        boolean isFullyWrapContent = wrapWidth && wrapHeight;
        if (width == 0 && height == 0 && !isFullyWrapContent) {
            return;
        }

        // if the URL to be loaded in this view is empty, cancel any old requests and clear the
        // currently loaded image.
        if (TextUtils.isEmpty(mUrl)) {
            if (mImageContainer != null) {
                mImageContainer.cancelRequest();
                mImageContainer = null;
            }
            setDefaultImageOrNull();
            return;
        }

        // if there was an old request in this view, check if it needs to be canceled.
        if (mImageContainer != null && mImageContainer.getRequestUrl() != null) {
            if (mImageContainer.getRequestUrl().equals(mUrl)) {
                // if the request is from the same URL, return.
                return;
            } else {
                // if there is a pre-existing request, cancel it if it's fetching a different URL.
                mImageContainer.cancelRequest();
                setDefaultImageOrNull();
            }
        }

        // Calculate the max image width / height to use while ignoring WRAP_CONTENT dimens.
        int maxWidth = wrapWidth ? 0 : width;
        int maxHeight = wrapHeight ? 0 : height;

        // The pre-existing content of this view didn't match the current URL. Load the new image
        // from the network.

        // update the ImageContainer to be the new bitmap container.
        mImageContainer = RequestManager.getInstance().getImageLoader().get(mUrl,
                new ImageListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (mErrorImageId != 0) {
                            if (mDecorate != 0) {
                                setBackgroundResource(mErrorImageId);
                                setImageBitmap(null);
                            } else {
                                setImageResource(mErrorImageId);
                            }
                        }
                    }

                    @Override
                    public void onResponse(final ImageContainer response, boolean isImmediate) {
                        // If this was an immediate response that was delivered inside of a layout
                        // pass do not set the image immediately as it will trigger a requestLayout
                        // inside of a layout. Instead, defer setting the image by posting back to
                        // the main thread.
//                        if (isImmediate && isInLayoutPass) {
//                            post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    onResponse(response, false);
//                                }
//                            });
//                            return;
//                        }

                        if (response.getBitmap() != null) {
                            if (mDecorate != 0) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                    setBackground(null);
                                } else {
                                    setBackgroundDrawable(null);
                                }
                            }
                            setImageBitmap(response.getBitmap());
                        } else if (mDefaultImageId != 0) {
                            if (mDecorate != 0) {
                                setImageBitmap(null);
                                setBackgroundResource(mDefaultImageId);
                            } else {
                                setImageResource(mDefaultImageId);
                            }
                        }
                    }
                }, maxWidth, maxHeight, scaleType, mDrawShape, mDecorate);
    }

    private void setDefaultImageOrNull() {
        if(mDefaultImageId != 0) {
            if (mDecorate != 0) {
                setImageBitmap(null);
                setBackgroundResource(mDefaultImageId);
            } else {
                setImageResource(mDefaultImageId);
            }
        } else {
            setImageBitmap(null);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (mHeight == 0 || mWidth == 0) {
            loadImageIfNecessary(true);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        if (mImageContainer != null) {
            // If the view was bound to an image request, cancel it and clear
            // out the image from the view.
            mImageContainer.cancelRequest();
            setImageBitmap(null);
            // also clear out the container so we can reload the image if necessary.
            mImageContainer = null;
        }
        super.onDetachedFromWindow();
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mSideLine != null) {
            mSideLine.setBounds(0, 0, mWidth, mHeight);
            mSideLine.draw(canvas);
        }
    }
}
