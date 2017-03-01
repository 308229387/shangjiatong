package com.android.gmacs.view.emoji;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.merchantplatform.R;
import com.android.gmacs.gif.GifImageView;
import com.common.gmacs.utils.GmacsEnvi;
import com.common.gmacs.utils.GmacsUtils;

import java.io.IOException;
import java.util.List;

public class GifAdapter extends BaseAdapter {

    private List<GifEmoji> data;

    private LayoutInflater inflater;
    private Context mContext;
    private int size = 0;
    private EmojiGifLayoutBuilder emojiGifLayoutBuilder;

    public GifAdapter(Context context, List<GifEmoji> list, EmojiGifLayoutBuilder emojiGifLayoutBuilder) {
        mContext = context;
        this.inflater = LayoutInflater.from(context);
        this.data = list;
        this.size = list.size();
        this.emojiGifLayoutBuilder = emojiGifLayoutBuilder;
    }

    @Override
    public int getCount() {
        return this.size;
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        final GifEmoji emoji = data.get(position);
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.gmacs_item_emoji_gif, null);
            viewHolder.iv_face = (ImageView) convertView.findViewById(R.id.item_iv_face);
            viewHolder.gif_name = (TextView) convertView.findViewById(R.id.gif_name);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.iv_face.setTag(emoji);
        viewHolder.gif_name.setText(emoji.gifName);
        try {
            viewHolder.iv_face.setImageBitmap(BitmapFactory.decodeStream(GmacsEnvi.appContext.getAssets().open(emoji.pngPath)));
        } catch (IOException e) {
            viewHolder.iv_face.setImageResource(0);
            e.printStackTrace();
        }

        convertView.setOnLongClickListener(new OnLongClickListener() {
            public boolean onLongClick(View v) {
                //显示popuwindow
                showPopu(v, emoji);
                return false;
            }
        });
        convertView.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                    //关闭popuwindow
                    if (popu != null) {
                        popu.dismiss();
                    }
                }
                return false;
            }
        });
        final View view = convertView;
        convertView.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (emojiGifLayoutBuilder != null) {
                    emojiGifLayoutBuilder.onItemClick(null, view, position, (long) position);
                }
            }
        });
        return convertView;
    }


    class ViewHolder {

        public ImageView iv_face;
        private TextView gif_name;
    }


    private PopupWindow popu;
    private GifImageView gifView;

    public void showPopu(View v, GifEmoji gif) {
        if (popu == null) {
            View popuView = LayoutInflater.from(mContext).inflate(R.layout.gmacs_popu_gif, null);
            gifView = (GifImageView) popuView.findViewById(R.id.gif_view);
            popu = new PopupWindow(popuView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            popu.setFocusable(true);
            popu.setOutsideTouchable(true);
            popu.setBackgroundDrawable(new BitmapDrawable());
        }
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        gifView.setGifImageByAssetName(gif.gifPath);
        int x = location[0];
        if (x < GmacsUtils.dipToPixel(13)) {
            x = GmacsUtils.dipToPixel(13);
        }
        WindowManager wm = (WindowManager) GmacsEnvi.appContext.getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        int rightX = width - GmacsUtils.dipToPixel(13) - GmacsUtils.dipToPixel(124);
        if (x > rightX) {
            x = rightX;
        }
        popu.showAtLocation(v, Gravity.NO_GRAVITY, x, location[1] - GmacsUtils.dipToPixel(130));
    }

}