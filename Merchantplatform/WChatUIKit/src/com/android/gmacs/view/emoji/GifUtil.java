package com.android.gmacs.view.emoji;

import android.support.v4.util.LruCache;

import com.android.gmacs.gif.GifDrawable;
import com.common.gmacs.parse.gif.IGifParser;
import com.common.gmacs.utils.FileUtil;
import com.common.gmacs.utils.GmacsEnvi;
import com.common.gmacs.utils.GifDownLoad;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class GifUtil implements IGifParser {

    private volatile static GifUtil ourInstance;

    public static GifUtil getInstance() {
        if (null == ourInstance) {
            synchronized (GifUtil.class) {
                if (null == ourInstance) {
                    ourInstance = new GifUtil();
                }
            }
        }
        return ourInstance;
    }

    private GifUtil() {
    }

	public static final String GIF_PATH = "gif";// gif emoji path
	private static ArrayList<GifGroup> gifGoups;
	public static final int GIF_COUNT = 8;// 每页显示gif数量
	private static LruCache<String, GifDrawable> gifCache = new LruCache<String, GifDrawable>(16) {
		protected int sizeOf(String key, GifDrawable value) {
			return 1;// 目前按张数来存
		}
	};

    public static synchronized ArrayList<GifGroup> getGifGroups() {
		if (gifGoups == null) {
			try {
				gifGoups = new ArrayList<GifGroup>();
				String[] fileNames = GmacsEnvi.appContext.getAssets().list(GIF_PATH);
				for (int i = 0; i < fileNames.length; i++) {
					InputStream infoStream = GmacsEnvi.appContext.getAssets().open(GIF_PATH + "/" + fileNames[i] + "/info.json");
					String json = readJsonInfo(infoStream);
					GifGroup goup = parseGifGroup(json);
					gifGoups.add(goup);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return gifGoups;
	}

	public static String getGifPath(String serverId) {
		String path = null;
		ArrayList<GifGroup> gifGoups = getGifGroups();
		if (gifGoups == null) {
			return path;
		}
		if (serverId != null) {
			serverId = serverId.replace(".gif", "");
		}
		for (int i = 0, size = gifGoups.size(); i < size; i++) {
			GifGroup group = gifGoups.get(i);
			GifEmoji gif = group.emojiMap.get(serverId);
			if (gif != null) {
				return gif.gifPath;
			}
		}
		return path;
	}

	public static String getGifFromSd(String serverId) {
		String path = null;
		File gifDir = FileUtil.getCacheFile(GifDownLoad.GIF_DIR, serverId);
		if (gifDir.exists()) {
			path = gifDir.getAbsolutePath();
		}
		return path;
	}

	public static GifEmoji getGif(String serverId) {
		ArrayList<GifGroup> gifGoups = getGifGroups();
		if (gifGoups == null) {
			return null;
		}
		if (serverId != null) {
			serverId = serverId.replace(".gif", "");
		}
		for (int i = 0, size = gifGoups.size(); i < size; i++) {
			GifGroup group = gifGoups.get(i);
			GifEmoji gif = group.emojiMap.get(serverId);
			if (gif != null) {
				return gif;
			}
		}
		return null;
	}

	public static GifDrawable getGifDrawable(String serverId) {
		try {
			GifDrawable gifDrawable = gifCache.get(serverId);
			if (gifDrawable == null) {
				String path = getGifPath(serverId);
				if (path != null) {
					gifDrawable = new GifDrawable(GmacsEnvi.appContext.getAssets().open(path));
				} else {
					path = getGifFromSd(serverId);
					gifDrawable = new GifDrawable(new File(path));
				}
				gifCache.put(serverId, gifDrawable);
			}
			return gifDrawable;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static GifGroup parseGifGroup(String json) {
		try {
			GifGroup group = new GifGroup();
			group.emojiMap = new LinkedHashMap<String, GifEmoji>();
			group.emojiLists = new ArrayList<ArrayList<GifEmoji>>();
			JSONObject jsonObj = new JSONObject(json);
			group.id = jsonObj.optString("id");
			group.path = GIF_PATH + "/" + jsonObj.optString("path");
			group.icon = group.path + "/" + jsonObj.optString("icon");
			group.name = jsonObj.optString("name");
			JSONArray jarray = jsonObj.optJSONArray("gifs");
			int j = 1;
			ArrayList<GifEmoji> emojis = null;
			for (int i = 0, len = jarray.length(); i < len; i++) {
				JSONObject itemObj = jarray.optJSONObject(i);
				GifEmoji gif = new GifEmoji();
				gif.serverId = itemObj.optString("serverId");
				gif.gifName = itemObj.optString("gifName");
				gif.pngPath = group.path + "/" + itemObj.optString("pngPath");
				gif.gifPath = group.path + "/" + itemObj.optString("gifPath");
				group.emojiMap.put(gif.serverId, gif);
				// 分页计算
				if (j == 1) {
					emojis = new ArrayList<GifEmoji>();
				}
				emojis.add(gif);
				if (j == GIF_COUNT) {
					group.emojiLists.add(emojis);
					j = 1;
				} else {
					j++;
				}
			}
			return group;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String readJsonInfo(InputStream inputStream) {
		try {
			StringBuilder stringBuilder = new StringBuilder();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				stringBuilder.append(line);
			}
			bufferedReader.close();
			return stringBuilder.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

    @Override
    public String getGifNameById(String localId) {
        GifEmoji gif = GifUtil.getGif(localId);
        if (gif == null) {
            return "[动画]";
        } else {
            return "[" + gif.gifName + "]";
        }
    }
}
