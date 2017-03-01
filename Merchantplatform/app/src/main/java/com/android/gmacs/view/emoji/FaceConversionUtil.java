package com.android.gmacs.view.emoji;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;

import com.merchantplatform.R;
import com.common.gmacs.parse.emoji.IEmojiParser;
import com.common.gmacs.utils.GmacsEnvi;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * 表情轉換工具
 */
public class FaceConversionUtil implements IEmojiParser {
	private static LinkedHashMap<String, Integer> emojiMap = new LinkedHashMap<>();
	/** 保存于内存中的表情集合 */
	private static List<ChatEmoji> emojis = new ArrayList<>();

    /** 表情分页的结果集合 */
	static List<List<ChatEmoji>> emojiLists = new ArrayList<>();
	
	private static FaceConversionUtil mFaceConversionUtil;
	static {
		emojiMap.put("[微笑]",R.drawable.smiley_001);
		emojiMap.put("[撇嘴]",R.drawable.smiley_016);
		emojiMap.put("[色]",R.drawable.smiley_011);
		emojiMap.put("[发呆]",R.drawable.smiley_036);
		emojiMap.put("[得意]",R.drawable.smiley_037);
		emojiMap.put("[流泪]",R.drawable.smiley_006);
		emojiMap.put("[害羞]",R.drawable.smiley_029);

		emojiMap.put("[闭嘴]",R.drawable.smiley_030);
		emojiMap.put("[睡觉]",R.drawable.smiley_022);
		emojiMap.put("[大哭]",R.drawable.smiley_003);
		emojiMap.put("[尴尬]",R.drawable.smiley_032);
		emojiMap.put("[发怒]",R.drawable.smiley_004);
		emojiMap.put("[调皮]",R.drawable.smiley_026);
		emojiMap.put("[呲牙]",R.drawable.smiley_008);

		emojiMap.put("[惊讶]",R.drawable.smiley_069);
		emojiMap.put("[难过]",R.drawable.smiley_045);
		emojiMap.put("[酷]",R.drawable.smiley_040);
		emojiMap.put("[冷汗]",R.drawable.smiley_077);
		emojiMap.put("[抓狂]",R.drawable.smiley_014);
		emojiMap.put("[吐]",R.drawable.smiley_028);


		emojiMap.put("[偷笑]", R.drawable.smiley_002);
		emojiMap.put("[愉快]",R.drawable.smiley_053);
		emojiMap.put("[白眼]",R.drawable.smiley_038);
		emojiMap.put("[傲慢]",R.drawable.smiley_010);
		emojiMap.put("[饥饿]",R.drawable.smiley_079);
		emojiMap.put("[困]",R.drawable.smiley_023);
		emojiMap.put("[惊恐]",R.drawable.smiley_020);

		emojiMap.put("[擦汗]",R.drawable.smiley_080);
		emojiMap.put("[憨笑]",R.drawable.smiley_005);
		emojiMap.put("[悠闲]",R.drawable.smiley_081);
		emojiMap.put("[奋斗]",R.drawable.smiley_046);
		emojiMap.put("[咒骂]",R.drawable.smiley_042);
		emojiMap.put("[疑问]",R.drawable.smiley_025);
		emojiMap.put("[嘘]",R.drawable.smiley_035);

		emojiMap.put("[晕]",R.drawable.smiley_009);
		emojiMap.put("[疯了]",R.drawable.smiley_082);
		emojiMap.put("[衰]",R.drawable.smiley_024);
		emojiMap.put("[骷髅]",R.drawable.smiley_083);
		emojiMap.put("[敲打]",R.drawable.smiley_027);
		emojiMap.put("[再见]",R.drawable.smiley_078);


		emojiMap.put("[流汗]",R.drawable.smiley_007);
		emojiMap.put("[抠鼻]",R.drawable.smiley_015);
		emojiMap.put("[鼓掌]",R.drawable.smiley_017);
		emojiMap.put("[糗大了]",R.drawable.smiley_039);
		emojiMap.put("[坏笑]",R.drawable.smiley_018);
		emojiMap.put("[左哼哼]",R.drawable.smiley_034);
		emojiMap.put("[右哼哼]",R.drawable.smiley_033);

		emojiMap.put("[哈欠]",R.drawable.smiley_041);
		emojiMap.put("[鄙视]",R.drawable.smiley_019);
		emojiMap.put("[委屈]",R.drawable.smiley_021);
		emojiMap.put("[快哭了]",R.drawable.smiley_012);
		emojiMap.put("[阴险]",R.drawable.smiley_031);
		emojiMap.put("[亲亲]",R.drawable.smiley_013);
		emojiMap.put("[惊吓]",R.drawable.smiley_044);

		emojiMap.put("[可怜]",R.drawable.smiley_043);
		emojiMap.put("[菜刀]",R.drawable.smiley_084);
		emojiMap.put("[西瓜]",R.drawable.smiley_074);
		emojiMap.put("[啤酒]",R.drawable.smiley_075);
		emojiMap.put("[篮球]",R.drawable.smiley_070);
		emojiMap.put("[乒乓]",R.drawable.smiley_072);


		emojiMap.put("[咖啡]",R.drawable.smiley_049);
		emojiMap.put("[米饭]",R.drawable.smiley_073);
		emojiMap.put("[猪头]",R.drawable.smiley_047);
		emojiMap.put("[玫瑰]",R.drawable.smiley_051);
		emojiMap.put("[凋谢]",R.drawable.smiley_052);
		emojiMap.put("[示爱]",R.drawable.smiley_056);
		emojiMap.put("[爱心]",R.drawable.smiley_054);

		emojiMap.put("[心碎]",R.drawable.smiley_055);
		emojiMap.put("[蛋糕]",R.drawable.smiley_059);
		emojiMap.put("[闪电]",R.drawable.smiley_060);
		emojiMap.put("[炸弹]",R.drawable.smiley_048);
		emojiMap.put("[刀]",R.drawable.smiley_068);
		emojiMap.put("[足球]",R.drawable.smiley_071);
		emojiMap.put("[瓢虫]",R.drawable.smiley_085);

		emojiMap.put("[便便]",R.drawable.smiley_076);
		emojiMap.put("[月亮]",R.drawable.smiley_058);
		emojiMap.put("[太阳]",R.drawable.smiley_057);
		emojiMap.put("[礼品]",R.drawable.smiley_050);
		emojiMap.put("[拥抱]",R.drawable.smiley_086);
		emojiMap.put("[强]",R.drawable.smiley_063);


		emojiMap.put("[弱]",R.drawable.smiley_064);
		emojiMap.put("[握手]",R.drawable.smiley_067);
		emojiMap.put("[胜利]",R.drawable.smiley_065);
		emojiMap.put("[抱拳]",R.drawable.smiley_066);
		emojiMap.put("[勾引]",R.drawable.smiley_062);
		emojiMap.put("[拳头]",R.drawable.smiley_087);
		emojiMap.put("[差劲]",R.drawable.smiley_088);

		emojiMap.put("[爱你]",R.drawable.smiley_089);
		emojiMap.put("[不]",R.drawable.smiley_090);
		emojiMap.put("[OK]",R.drawable.smiley_061);

        for (Map.Entry<String, Integer> entry : emojiMap.entrySet()) {
            ChatEmoji emojiEntry = new ChatEmoji();
            emojiEntry.setId(entry.getValue());
            emojiEntry.setCharacter(entry.getKey());
            emojis.add(emojiEntry);
        }

        int pageCount = emojis.size() / 20;
        if (emojis.size() % 20 != 0) {
            pageCount++;
        }

        for (int i = 0; i < pageCount; i++) {
            emojiLists.add(getData(i));
        }
    }
	
	
	

	/** 保存于内存中的表情HashMap */
//	private HashMap<String, String> emojiMap = new HashMap<String, String>();


	private FaceConversionUtil() {

	}

	public static FaceConversionUtil getInstace() {
		if (mFaceConversionUtil == null) {
			mFaceConversionUtil = new FaceConversionUtil();
		}
		return mFaceConversionUtil;
	}

	/**
	 * 得到一个SpanableString对象，通过传入的字符串,并进行正则判断
	 * 
	 * @param str
	 * @return
	 */
    @Override
	public SpannableString getExpressionString(String str,int px) {
		if(str==null){
            return new SpannableString("");
		}
        return dealExpression(str,px);
	}
	
	public void replaceAllEmojis(Context context, Spannable spannable){
        int length = spannable.length();
        ImageSpan[] oldSpans = spannable.getSpans(0, length, ImageSpan.class);
        for (ImageSpan oldSpan : oldSpans) {
            spannable.removeSpan(oldSpan);
        }
		String spannableString = spannable.toString();
		EmojiMatcher emojiMatcher = new EmojiMatcher(spannableString);
		int[] indexs;
		while ((indexs = emojiMatcher.find())!=null) {
			String key =spannableString.substring(indexs[0], indexs[1]+1);
			// 返回第一个字符的索引的文本匹配整个正则表达式,ture 则继续递归
			Integer resId = emojiMap.get(key);
			if(resId==null){
				continue;
			}
			if (resId != 0) {
				float den = context.getResources().getDisplayMetrics().density;
				int bound = (int) (24 * den + 0.5f);
				Drawable drawable = context.getResources().getDrawable(resId);
				drawable.setBounds(0, 0, bound, bound);
				ImageSpan imageSpan = new ImageSpan(drawable);
				// 将该图片替换字符串中规定的位置中
				spannable.setSpan(imageSpan, indexs[0], indexs[1]+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		}
	}


	/**
	 * 对spanableString进行正则判断，如果符合要求，则以表情图片代替
	 * 
	 * @param spannableString
	 */
	private SpannableString dealExpression(String spannableString,int px) {
		EmojiMatcher emojiMatcher = new EmojiMatcher(spannableString);
		SpannableString spannable = new SpannableString(spannableString);
		int[] subIndexes;
        while ((subIndexes = emojiMatcher.find()) != null) {
            String key = spannableString.substring(subIndexes[0], subIndexes[1] + 1);
            // 返回第一个字符的索引的文本匹配整个正则表达式,true 则继续递归
            Integer resId = emojiMap.get(key);
            if (resId == null) {
                continue;
            }
            if (resId != 0) {
                float den = GmacsEnvi.appContext.getResources().getDisplayMetrics().density;
                int bound = (int) (px * den + 0.5f);
                Drawable drawable;
                try {
                    drawable = GmacsEnvi.appContext.getResources().getDrawable(resId);
                } catch (Resources.NotFoundException e) {
                    continue;
                }

                drawable.setBounds(0, 0, bound, bound);
                ImageSpan imageSpan = new ImageSpan(drawable);
                // 将该图片替换字符串中规定的位置中
                spannable.setSpan(imageSpan, subIndexes[0], subIndexes[1] + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return spannable;
	}
	public class EmojiMatcher{
		CharSequence str;
		int len;
		int index = 0;
		public EmojiMatcher(CharSequence str){
			this.str = str;
			this.len = str.length();
		}
		
		
		public int[] find() {
			int start = -1;
			int end = -1;
			for(;index<len;index++){
				char c = str.charAt(index);
				if(c=='['){
					start = index;
				}else if(c == ']'){
					if(start!=-1){
						end = index;
						int[] indexs = new int[2];
						indexs[0] = start;
						indexs[1] = end;
						return indexs;
					}
					
				}
			}
			return null;
		}
		
		
	}
	
	
	/**
	 * 获取分页数据
	 * 
	 * @param page
	 * @return
	 */
	private static List<ChatEmoji> getData(int page) {
		/* 每一页表情的个数 */
        int pageSize = 20;
        int startIndex = page * pageSize;
		int endIndex = startIndex + pageSize;

		if (endIndex > emojis.size()) {
			endIndex = emojis.size();
		}
		// 不这么写，会在viewpager加载中报集合操作异常，我也不知道为什么
		List<ChatEmoji> list = new ArrayList<ChatEmoji>();
		list.addAll(emojis.subList(startIndex, endIndex));
		if (list.size() < pageSize) {
			for (int i = list.size(); i < pageSize; i++) {
				ChatEmoji object = new ChatEmoji();
				list.add(object);
			}
		}
		if (list.size() == pageSize) {
			ChatEmoji object = new ChatEmoji();
			object.setId(R.drawable.gmacs_btn_del_emoji);
			list.add(object);
		}
		return list;
	}
}