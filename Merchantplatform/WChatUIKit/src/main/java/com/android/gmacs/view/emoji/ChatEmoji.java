package com.android.gmacs.view.emoji;


/**
 * 表情对象实体
 */
public class ChatEmoji {

    /**
     * 表情资源图片对应的ID
     */
    private int id;

    /**
     * 表情资源对应的文字描述
     */
    private String character;

    /**
     * 表情资源图片对应的ID
     */
    public int getId() {
        return id;
    }

    /**
     * 表情资源图片对应的ID
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * 表情资源对应的文字描述
     */
    public String getCharacter() {
        return character;
    }

    /**
     * 表情资源对应的文字描述
     */
    public void setCharacter(String character) {
        this.character = character;
    }

}
