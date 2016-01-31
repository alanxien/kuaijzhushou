/* 
 * @Title:  CrashHandler.java 
 * @Copyright:  XXX Co., Ltd. Copyright YYYY-YYYY,  All rights reserved 
 * @Description:  TODO<请描述此文件是做什么的> 
 * @author:  xie.xin
 * @data:  2015-12-31 上午2:33:16 
 * @version:  V1.0 
 */
package com.android.iquicker.common;

import java.lang.Thread.UncaughtExceptionHandler;

import android.content.Context;
import android.util.Log;

/**
 * TODO<请描述这个类是干什么的>
 * 
 * @author xie.xin
 * @data: 2015-12-31 上午2:33:16
 * @version: V1.0
 */
public class CrashHandler implements UncaughtExceptionHandler {

	private static CrashHandler instance; // 单例引用，这里我们做成单例的，因为我们一个应用程序里面只需要一个UncaughtExceptionHandler实例

	private CrashHandler() {
	}

	public synchronized static CrashHandler getInstance() { // 同步方法，以免单例多线程环境下出现异常
		if (instance == null) {
			instance = new CrashHandler();
		}
		return instance;
	}

	public void init(Context ctx) { // 初始化，把当前对象设置成UncaughtExceptionHandler处理器
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) { // 当有未处理的异常发生时，就会来到这里。。
		String threadName = thread.getName();
		if (!"sub1".equals(threadName)) {
			Log.e("CrashHandler", "未处理异常");
			ex.printStackTrace();
		}
	}

}
