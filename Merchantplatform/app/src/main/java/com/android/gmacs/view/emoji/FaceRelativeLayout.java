package com.android.gmacs.view.emoji;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.merchantplatform.R;
import com.android.gmacs.activity.GmacsChatActivity;
import com.common.gmacs.msg.data.IMGifMsg;
import com.common.gmacs.utils.GmacsUtils;

import java.io.IOException;
import java.util.ArrayList;

/**
 * 
 *
 */
public class FaceRelativeLayout extends RelativeLayout implements EmojiGifLayoutBuilder.OnGifClickListener {

	/** 表情区域 */
	private RelativeLayout view;
	private LinearLayout scrollView;
	private RelativeLayout contentView;
	private EmojiLayoutBuilder emojiLayoutBuilder;
	private GmacsChatActivity gmacsChatActivity;

    public FaceRelativeLayout(Context context) {
		super(context);
	}

	public FaceRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public FaceRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public void setMessageEditView(EditText messageEditText) {
		emojiLayoutBuilder.setMessageEditView(messageEditText);
	}

	public void setGmacsChatActivity(GmacsChatActivity gmacsChatActivity) {
		this.gmacsChatActivity = gmacsChatActivity;
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		ArrayList<GifGroup> groups = GifUtil.getGifGroups();
        HorizontalScrollView faceIndicator = (HorizontalScrollView) findViewById(R.id.face_indicator);
        view = (RelativeLayout) findViewById(R.id.FaceRelativeLayout);
		scrollView = (RadioGroup) findViewById(R.id.scroll_bar);
		contentView = (RelativeLayout) findViewById(R.id.face_layout);
		emojiLayoutBuilder = new EmojiLayoutBuilder(null);
		View staticEmogiLayout = emojiLayoutBuilder.getEmojiLayout();
		contentView.removeAllViews();
		contentView.addView(staticEmogiLayout, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		RadioButton radioStatic = new RadioButton(getContext());
		radioStatic.setButtonDrawable(R.color.transparent);
		radioStatic.setPadding(GmacsUtils.dipToPixel(10), GmacsUtils.dipToPixel(3), GmacsUtils.dipToPixel(10), GmacsUtils.dipToPixel(3));
		radioStatic.setBackgroundResource(R.drawable.gmacs_bg_tab_bottom_normal);
		radioStatic.setWidth(GmacsUtils.dipToPixel(62));
		radioStatic.setGravity(Gravity.CENTER);
		radioStatic.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.smiley_001, 0, 0);
		scrollView.addView(radioStatic);
		radioStatic.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				contentView.removeAllViews();
				contentView.addView(emojiLayoutBuilder.getEmojiLayout(), LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				emojiLayoutBuilder.getEmojiLayout().requestFocus();
			}
		});
		if (groups != null) {
			for (int i = 0; i < groups.size(); i++) {
				final GifGroup group = groups.get(i);
				RadioButton radioGif = new RadioButton(getContext());
				radioGif.setButtonDrawable(R.color.transparent);
				Drawable drawable = null;
				try {
					drawable = BitmapDrawable.createFromStream(getContext().getAssets().open(group.icon), "");
					drawable.setBounds(0, 0, GmacsUtils.dipToPixel(35), GmacsUtils.dipToPixel(35));
				} catch (IOException e) {
					e.printStackTrace();
				}
				radioGif.setWidth(GmacsUtils.dipToPixel(62));
				radioGif.setGravity(Gravity.CENTER);
				radioGif.setPadding(GmacsUtils.dipToPixel(10), GmacsUtils.dipToPixel(3), GmacsUtils.dipToPixel(10), GmacsUtils.dipToPixel(3));
				radioGif.setCompoundDrawables(null, drawable, null, null);
				radioGif.setBackgroundResource(R.drawable.gmacs_bg_tab_bottom_normal);
				scrollView.addView(radioGif);
				radioGif.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						EmojiGifLayoutBuilder gifbBuilder = new EmojiGifLayoutBuilder(group);
						gifbBuilder.setOnGifClickListener(FaceRelativeLayout.this);
						View gifLayout = gifbBuilder.getEmojiLayout();
						contentView.removeAllViews();
						contentView.addView(gifLayout, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
					}
				});
			}
            if (groups.size() == 0) {
                faceIndicator.setVisibility(GONE);
            }
        }

		radioStatic.setChecked(true);

	}

	/**
	 * 隐藏表情选择框
	 */
	public boolean faceViewShown() {
		// 隐藏表情选择框
        return view.getVisibility() == View.VISIBLE;
    }

	public void hidden() {
		view.setVisibility(View.GONE);
	}

	public void show() {
		view.setVisibility(View.VISIBLE);
	}

	public void onGifClick(GifEmoji gifEmoji) {
		if (gmacsChatActivity != null && gifEmoji != null) {
			IMGifMsg gifMsg = new IMGifMsg();
			gifMsg.gifId = gifEmoji.serverId;
			gmacsChatActivity.sendMsg(gifMsg);
		}
	}
	
	public void setOnlyStatic() {
		if (scrollView != null) {
			for (int i = 1; i < scrollView.getChildCount(); i++) {
				scrollView.getChildAt(i).setVisibility(View.GONE);
			}
		}
	}
}
