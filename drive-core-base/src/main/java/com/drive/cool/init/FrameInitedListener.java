package com.drive.cool.init;

/**
 * 
 * @author kevin
 *
 */
public interface FrameInitedListener {
	/**
	 * 在框架初始化完成后做额外的初始化操作
	 */
	public void init();
	
	public int getOrder();
}
