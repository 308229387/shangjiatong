package com.android.gmacs.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.ArrayRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.merchantplatform.R;
import com.common.gmacs.utils.GmacsEnvi;
import com.common.gmacs.utils.GmacsUtils;

import static android.view.View.GONE;
import static com.merchantplatform.R.style.dialog;

/**
 * Created by YanQi on 2015/12/22.
 */
public class GmacsDialog extends Dialog {

    private GmacsDialog(Context context) {
        super(context);
    }

    private GmacsDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    private GmacsDialog(Context context, int theme) {
        super(context, theme);
    }

    public void post(ViewTreeObserver.OnGlobalLayoutListener listener) {
        View decorView = getWindow() != null ? getWindow().getDecorView() : null;
        if (decorView != null) {
            decorView.getViewTreeObserver().addOnGlobalLayoutListener(listener);
        }
    }

    public static class Builder {

        /**
         * Dialog with a ListView.
         */
        public static final int DIALOG_TYPE_LIST_NO_BUTTON = 1;
        /**
         * Dialog with a neutral Button and a TextView for showing message.
         */
        public static final int DIALOG_TYPE_TEXT_NEU_BUTTON = 2;
        /**
         * Dialog with a negative Button，a positive Button and a TextView for showing message.
         */
        public static final int DIALOG_TYPE_TEXT_NEG_POS_BUTTON = 3;
        /**
         * Dialog with a ProgressBar and a TextView for showing message.
         */
        public static final int DIALOG_TYPE_TEXT_PROGRESSBAR_NO_BUTTON = 4;
        /**
         * Dialog's contentView can be replaced by your own View.
         */
        public static final int DIALOG_TYPE_CUSTOM_CONTENT_VIEW = 5;

        /**
         * The instance of GmacsDialog.
         */
        private GmacsDialog mDialog;
        private LinearLayout mLayout;

        private Context mContext;
        private int mDialogType, mDialogStyle;
        private boolean cancelable = true;
        private OnCancelListener mOnCancelListener;

        private ListView mListView;
        private CharSequence[] mListTexts;
        private AdapterView.OnItemClickListener mOnItemClickListener;

        private FastScrollView mMsgScrollView;
        private TextView mTitle, mMsg;
        private LinearLayout mBtnLayout, mMessageLayout;
        private View mContentView;
        private Button neuBtn, posBtn, negBtn;
        private CharSequence titleText, msgText, neuBtnText, posBtnText, negBtnText;
        private View.OnClickListener mNeuBtnListener, mPosBtnListener, mNegBtnListener;

        private int windowFlags;

        private Resources resources;

        public Builder(Context context, int dialogType) {
            this.mContext = context;
            this.mDialogType = dialogType;
            if (dialogType != DIALOG_TYPE_TEXT_PROGRESSBAR_NO_BUTTON) {
                mDialogStyle = dialog;
            } else {
                mDialogStyle = R.style.publish_btn_dialog;
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                try {
                    windowFlags = ((Activity) context).getWindow().getDecorView().getSystemUiVisibility();
                } catch (ClassCastException e) {
                }
            }
            resources = context.getResources();
        }

        /**
         * Initialize the Dialog and the mDialogType must be <i>DIALOG_TYPE_LIST_NO_BUTTON</i>.
         *
         * @param listener Handle your ListView onItemClick action.
         * @return Builder
         */
        public Builder initDialog(@NonNull AdapterView.OnItemClickListener listener) {
            if (mDialogType == DIALOG_TYPE_LIST_NO_BUTTON) {
                mOnItemClickListener = listener;
            }
            return this;
        }

        /**
         * Initialize the Dialog and the mDialogType must be <i>DIALOG_TYPE_TEXT_NEUTRAL_BUTTON</i>.
         *
         * @param msgTextResId    The string resource id, which carries the text that will be regard as a message.
         * @param neuBtnTextResId The string resource id, which carries the text that will be added into neutral Button.
         * @param neuBtnListener  Handle your neutral Button's click action.
         * @return Builder
         */
        public Builder initDialog(@StringRes int msgTextResId, @StringRes int neuBtnTextResId, @NonNull View.OnClickListener neuBtnListener) {
            if (mDialogType == DIALOG_TYPE_TEXT_NEU_BUTTON) {
                msgText = resources.getText(msgTextResId);
                neuBtnText = resources.getText(neuBtnTextResId);
                this.mNeuBtnListener = neuBtnListener;
            }
            return this;
        }

        /**
         * Initialize the Dialog and the mDialogType must be <i>DIALOG_TYPE_TEXT_NEUTRAL_BUTTON</i>.
         *
         * @param msgTextString    The string that will be regard as a message.
         * @param neuBtnTextString The string that will be added into neutral Button.
         * @param neuBtnListener   Handle your neutral Button's click action.
         * @return Builder
         */
        public Builder initDialog(CharSequence msgTextString, CharSequence neuBtnTextString, @NonNull View.OnClickListener neuBtnListener) {
            if (mDialogType == DIALOG_TYPE_TEXT_NEU_BUTTON) {
                msgText = checkNull(msgTextString);
                neuBtnText = checkNull(neuBtnTextString).length() != 0 ? neuBtnTextString : mContext.getText(R.string.ok);
                this.mNeuBtnListener = neuBtnListener;
            }
            return this;
        }

        /**
         * Initialize the Dialog and the mDialogType must be <i>DIALOG_TYPE_TEXT_NEG_POS_BUTTON</i>.
         *
         * @param msgTextResId    The string resource id, which carries the text that will be regard as a message.
         * @param negBtnTextResId The string resource id, which carries the text that will be added into negative Button.
         * @param posBtnTextResId The string resource id, which carries the text that will be added into positive Button.
         * @param negBtnListener  Handle your negative Button's click action.
         * @param posBtnListener  Handle your positive Button's click action.
         * @return Builder
         */
        public Builder initDialog(@StringRes int msgTextResId, @StringRes int negBtnTextResId, @StringRes int posBtnTextResId,
                                  @NonNull View.OnClickListener negBtnListener, @NonNull View.OnClickListener posBtnListener) {
            if (mDialogType == DIALOG_TYPE_TEXT_NEG_POS_BUTTON) {
                msgText = resources.getText(msgTextResId);
                negBtnText = resources.getText(negBtnTextResId);
                posBtnText = resources.getText(posBtnTextResId);
                this.mNegBtnListener = negBtnListener;
                this.mPosBtnListener = posBtnListener;
            }
            return this;
        }

        /**
         * Initialize the Dialog and the mDialogType must be <i>DIALOG_TYPE_TEXT_NEG_POS_BUTTON</i>.
         *
         * @param msgTextString    The string that will be regard as a message.
         * @param negBtnTextString The string that will be added into negative Button.
         * @param posBtnTextString The string that will be added into positive Button.
         * @param negBtnListener   Handle your negative Button's click action.
         * @param posBtnListener   Handle your positive Button's click action.
         * @return Builder
         */
        public Builder initDialog(CharSequence msgTextString, CharSequence negBtnTextString, CharSequence posBtnTextString,
                                  @NonNull View.OnClickListener negBtnListener, @NonNull View.OnClickListener posBtnListener) {
            if (mDialogType == DIALOG_TYPE_TEXT_NEG_POS_BUTTON) {
                msgText = checkNull(msgTextString);
                negBtnText = checkNull(negBtnTextString).length() != 0 ? negBtnTextString : mContext.getText(R.string.cancel);
                posBtnText = checkNull(posBtnTextString).length() != 0 ? posBtnTextString : mContext.getText(R.string.ok);
                this.mNegBtnListener = negBtnListener;
                this.mPosBtnListener = posBtnListener;
            }
            return this;
        }

        /**
         * initialize the Dialog and the mDialogType must be <i>DIALOG_TYPE_TEXT_PROGRESSBAR_NO_BUTTON</i>.
         *
         * @param msgTextResId The string resource id, which carries the text that will be regard as a message.
         * @return Builder
         */
        public Builder initDialog(@StringRes int msgTextResId) {
            if (mDialogType == DIALOG_TYPE_TEXT_PROGRESSBAR_NO_BUTTON) {
                msgText = resources.getText(msgTextResId);
            }
            return this;
        }

        /**
         * initialize the Dialog and the mDialogType must be <i>DIALOG_TYPE_TEXT_PROGRESSBAR_NO_BUTTON</i>.
         *
         * @param msgTextString The string that will be regard as a message.
         * @return Builder
         */
        public Builder initDialog(CharSequence msgTextString) {
            if (mDialogType == DIALOG_TYPE_TEXT_PROGRESSBAR_NO_BUTTON) {
                msgText = checkNull(msgTextString).length() != 0 ? msgTextString : mContext.getText(R.string.wait);
            }
            return this;
        }

        public Builder initDialog(View contentView) {
            if (mDialogType == DIALOG_TYPE_CUSTOM_CONTENT_VIEW) {
                mContentView = contentView;
            }
            return this;
        }

        public View getContentView() {
            if (mDialogType == DIALOG_TYPE_CUSTOM_CONTENT_VIEW) {
                return mContentView;
            } else {
                return null;
            }
        }

        /**
         * Set the title of Dialog. <br><b>The title would be shown, unless you invoke this method.</b></br>
         *
         * @param titleTextResId The string resource id, which carries the text that will be regard as a title.
         * @return Builder
         */
        public Builder setTitle(@StringRes int titleTextResId) {
            titleText = resources.getText(titleTextResId);
            return this;
        }

        /**
         * Set the title of Dialog. <br><b>The title would be shown, unless you invoke this method.</b></br>
         *
         * @param titleTextString The string that will be regard as a title.
         * @return Builder
         */
        public Builder setTitle(CharSequence titleTextString) {
            titleText = checkNull(titleTextString);
            return this;
        }

        /**
         * Set the texts which will be shown on ListView.
         *
         * @param listTextsResId The string-array resource id, which carries the texts that will be added into ListView.
         * @return Builder
         */
        public Builder setListTexts(@ArrayRes int listTextsResId) {
            if (mDialogType == DIALOG_TYPE_LIST_NO_BUTTON) {
                mListTexts = resources.getTextArray(listTextsResId);
            }
            return this;
        }

        /**
         * Set the texts which will be shown on ListView.
         *
         * @param listTextsStringArray The string array that will be added into ListView.
         * @return Builder
         * @throws NullPointerException Throws it if text array is null.
         */
        public Builder setListTexts(CharSequence[] listTextsStringArray) {
            if (mDialogType == DIALOG_TYPE_LIST_NO_BUTTON) {
                if (listTextsStringArray != null) {
                    int length = listTextsStringArray.length;
                    for (int i = 0; i < length; i++) {
                        listTextsStringArray[i] = checkNull(listTextsStringArray[i]);
                    }
                    mListTexts = listTextsStringArray;
                } else {
                    throw new NullPointerException("GmacsDialog -> Adapter text array null");
                }
            }
            return this;
        }

        /**
         * The default cancelable value is true.
         *
         * @param cancelable
         * @return Builder
         */
        public Builder setCancelable(boolean cancelable) {
            this.cancelable = cancelable;
            return this;
        }

        private void resetPreviousWindowFlags() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                try {
                    ((Activity) mContext).getWindow().getDecorView().setSystemUiVisibility(windowFlags);
                } catch (ClassCastException e) {
                }
            }
        }

        /**
         * @param onCancelListener
         * @return Builder
         */
        public Builder setOnCancelListener(OnCancelListener onCancelListener) {
            this.mOnCancelListener = onCancelListener;
            return this;
        }

        private CharSequence checkNull(CharSequence charSequence) {
            if (charSequence != null) {
                return charSequence;
            } else {
                return "";
            }
        }

        /**
         * Create Dialog.<br><b>Invoke it after everything prepared for showing Dialog.</b></br>
         *
         * @return GmacsDialog
         * @throws NullPointerException Throws it when click listeners never registered (mOnCancelListener excluded).
         */
        public GmacsDialog create() {
            final GmacsDialog dialog = new GmacsDialog(mContext, mDialogStyle);
            this.mDialog = dialog;
            mLayout = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.gmacs_dialog_layout, null);
            if (mDialogType != DIALOG_TYPE_CUSTOM_CONTENT_VIEW) {
                dialog.setContentView(mLayout, new ViewGroup.LayoutParams(GmacsEnvi.screenWidth * 3 / 4, ViewGroup.LayoutParams.WRAP_CONTENT));
            } else {
                dialog.setContentView(mLayout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
            dialog.setCancelable(cancelable);
            dialog.setOnCancelListener(new OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    resetPreviousWindowFlags();
                    if (mOnCancelListener != null) {
                        mOnCancelListener.onCancel(dialog);
                    } else {
//                      TODO: default onCancel
                    }
                }
            });

            mMsgScrollView = (FastScrollView) mLayout.findViewById(R.id.dialog_scrollview);
            mListView = (ListView) mLayout.findViewById(R.id.dialog_list);
            mTitle = (TextView) mLayout.findViewById(R.id.dialog_title);
            mMessageLayout = (LinearLayout) mLayout.findViewById(R.id.dialog_message_layout);
            mMsg = (TextView) mLayout.findViewById(R.id.dialog_text);
            mBtnLayout = (LinearLayout) mLayout.findViewById(R.id.dialog_btns_layout);
            neuBtn = (Button) mLayout.findViewById(R.id.dialog_neu_btn);

            if (titleText != null) {
                mTitle.setText(titleText);
            } else {
                mTitle.setVisibility(GONE);
            }

            switch (mDialogType) {
                case DIALOG_TYPE_LIST_NO_BUTTON:
                    mMessageLayout.setVisibility(GONE);
                    mMsg.setVisibility(GONE);
                    neuBtn.setVisibility(GONE);
                    mBtnLayout.setVisibility(GONE);
                    mListView.setAdapter(new DialogAdapter(mContext));
                    mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            if (mOnItemClickListener != null) {
                                resetPreviousWindowFlags();
                                mOnItemClickListener.onItemClick(parent, view, position, id);
                            } else {
                                throw new NullPointerException("GmacsDialog -> OnItemClickListener null");
                            }
                            dialog.dismiss();
                        }
                    });
                    break;
                case DIALOG_TYPE_TEXT_NEU_BUTTON:
                    if (mMsg.getPaint().measureText(msgText.toString()) / (GmacsEnvi.screenWidth * 3 / 4)
                            > (GmacsEnvi.screenHeight * 0.6f / mMsg.getLineHeight())) {
                        ViewGroup.LayoutParams layoutParams = mMsgScrollView.getLayoutParams();
                        layoutParams.height = (int) (GmacsEnvi.screenHeight * 0.6f);
                        mMsgScrollView.setLayoutParams(layoutParams);
                    }
                    mListView.setVisibility(GONE);
                    mBtnLayout.setVisibility(GONE);
                    {
                        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mMessageLayout.getLayoutParams();
                        int marginSize = GmacsUtils.dipToPixel(18);
                        layoutParams.setMargins(marginSize, marginSize, marginSize, GmacsUtils.dipToPixel(50));
                    }
                    mMsg.setText(msgText);
                    mMsg.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            mMsgScrollView.fullScroll(ScrollView.FOCUS_DOWN);
                        }
                    });
                    neuBtn.setText(neuBtnText);
                    neuBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mNeuBtnListener != null) {
                                resetPreviousWindowFlags();
                                mNeuBtnListener.onClick(v);
                            } else {
                                dialog.dismiss();
                                throw new NullPointerException("GmacsDialog -> NeuBtn OnClickListener null");
                            }
                        }
                    });
                    break;
                case DIALOG_TYPE_TEXT_NEG_POS_BUTTON:
                    if (mMsg.getPaint().measureText(msgText.toString()) / (GmacsEnvi.screenWidth * 3 / 4) > (GmacsEnvi.screenHeight * 0.6f / mMsg.getLineHeight())) {
                        ViewGroup.LayoutParams layoutParams = mMsgScrollView.getLayoutParams();
                        layoutParams.height = (int) (GmacsEnvi.screenHeight * 0.6f);
                        mMsgScrollView.setLayoutParams(layoutParams);
                    }
                    mListView.setVisibility(GONE);
                    neuBtn.setVisibility(GONE);
                    negBtn = (Button) mLayout.findViewById(R.id.dialog_neg_btn);
                    posBtn = (Button) mLayout.findViewById(R.id.dialog_pos_btn);
                    {
                        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mMessageLayout.getLayoutParams();
                        int marginSize = GmacsUtils.dipToPixel(18);
                        layoutParams.setMargins(marginSize, marginSize, marginSize, GmacsUtils.dipToPixel(50));
                    }
                    mMsg.setText(msgText);
                    mMsg.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            mMsgScrollView.fullScroll(ScrollView.FOCUS_DOWN);
                        }
                    });
                    negBtn.setText(negBtnText);
                    posBtn.setText(posBtnText);
                    negBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mNegBtnListener != null) {
                                resetPreviousWindowFlags();
                                mNegBtnListener.onClick(v);
                            } else {
                                dialog.cancel();
                                throw new NullPointerException("GmacsDialog -> NegBtn OnClickListener null");
                            }
                        }
                    });
                    posBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mPosBtnListener != null) {
                                resetPreviousWindowFlags();
                                mPosBtnListener.onClick(v);
                            } else {
                                dialog.dismiss();
                                throw new NullPointerException("GmacsDialog -> PosBtn OnClickListener null");
                            }
                        }
                    });
                    break;
                case DIALOG_TYPE_TEXT_PROGRESSBAR_NO_BUTTON:
                    ProgressBar pb = (ProgressBar) mLayout.findViewById(R.id.dialog_progressbar);
                    {
                        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mMessageLayout.getLayoutParams();
                        layoutParams.leftMargin = 0;
                    }
                    mMessageLayout.setGravity(Gravity.CENTER_HORIZONTAL);
                    pb.setVisibility(ProgressBar.VISIBLE);

                    dialog.setContentView(mLayout, new ViewGroup.LayoutParams(GmacsEnvi.screenWidth / 2, ViewGroup.LayoutParams.WRAP_CONTENT));
                    mLayout.setBackgroundColor(resources.getColor(R.color.dark_grey));
                    mTitle.setTextColor(resources.getColor(R.color.white));
                    mMsg.setText(msgText);
                    mMsg.setTextColor(resources.getColor(R.color.white));
                    mMsg.getLayoutParams().width = LinearLayout.LayoutParams.WRAP_CONTENT;
                    mBtnLayout.setVisibility(GONE);
                    neuBtn.setVisibility(GONE);
                    break;
                case DIALOG_TYPE_CUSTOM_CONTENT_VIEW:
                    mListView.setVisibility(GONE);
                    neuBtn.setVisibility(GONE);
                    mBtnLayout.setVisibility(GONE);
                    mMessageLayout.removeAllViews();
                    mMessageLayout.addView(mContentView);
                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mMessageLayout.getLayoutParams();
                    layoutParams.topMargin = 0;
                    layoutParams.bottomMargin = 0;
                    break;
            }
            return dialog;
        }

        /**
         * Dismiss this mDialog, removing it from the screen. This method can be invoked safely from any thread.
         */
        public void dismiss() {
            if (mDialog.isShowing()) {
                mDialog.dismiss();
            }
        }

        /**
         * Cancel the mDialog and it will also call your DialogInterface.OnCancelListener (if registered).
         */
        public void cancel() {
            if (mDialog.isShowing()) {
                mDialog.cancel();
            }
        }

        private class DialogAdapter extends BaseAdapter {

            private LayoutInflater li;
            private ViewHolder vh;

            private final class ViewHolder {
                TextView tv;
            }

            public DialogAdapter(Context context) {
                li = LayoutInflater.from(context);
            }

            @Override
            public int getCount() {
                return mListTexts.length;
            }

            @Override
            public Object getItem(int position) {
                return mListTexts[position];
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
                    convertView = li.inflate(R.layout.gmacs_dialog_list_item, null);
                    if (position == 0) {
                        if (getCount() == 1) {
                            convertView.setBackgroundResource(R.drawable.gmacs_bg_dialog_list_item_all_corner);
                        } else {
                            convertView.setBackgroundResource(R.drawable.gmacs_bg_dialog_list_item_top_corner);
                        }
                    } else if (position == getCount() - 1 && getCount() > 1) {
                        convertView.setBackgroundResource(R.drawable.gmacs_bg_dialog_list_item_bottom_corner);
                    } else {
                        convertView.setBackgroundResource(R.drawable.gmacs_bg_dialog_list_item);
                    }
                    vh.tv = (TextView) convertView.findViewById(R.id.dialog_list_item_text);
                    convertView.setTag(vh);
                } else {
                    vh = (ViewHolder) convertView.getTag();
                }

                vh.tv.setText((CharSequence) getItem(position));
                return convertView;
            }

        }

    }

}
