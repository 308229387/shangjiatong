package com.android.gmacs.gif;

/**
 * gif动画效果的播放处理
 * 
 */
public interface GifAnimationListener {

	/**
	 * gif动画播放之前，在gif第一帧图片显示之前调用
	 * 
	 * @param loop
	 *            当前第几次循环
	 */
    void onGifAnimationPreStart(int loop);

	/**
	 * gif播放循环结束，最后一帧图片显示之后调用
	 * 
	 * @param loop
	 *            当前第几次循环
	 */
    void onGifAnimationFinished(int loop);

	/**
	 * view不显示或者是整个view被移除当前界面的时候调用
	 */
    void onGifAnimationTerminate();

}
