package com.android.gmacs.view.emoji;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 *
 *
 */
public class GifGroup {
    public String id;
    public String name;
    public String path;
    public String url;
    public String icon;
    public ArrayList<ArrayList<GifEmoji>> emojiLists;
    public LinkedHashMap<String, GifEmoji> emojiMap;

}
