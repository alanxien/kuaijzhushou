/* 
 * @Title:  LockReceiver.java 
 * @Copyright:  XXX Co., Ltd. Copyright YYYY-YYYY,  All rights reserved 
 * @Description:  TODO<请描述此文件是做什么的> 
 * @author:  xie.xin
 * @data:  2016-2-1 下午9:06:22 
 * @version:  V1.0 
 */
package com.android.iquicker.receivers;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;

/** 
 * TODO<请描述这个类是干什么的> 
 * @author  xie.xin 
 * @data:  2016-2-1 下午9:06:22 
 * @version:  V1.0 
 */
public class LockReceiver extends DeviceAdminReceiver{ 
	   
	   
 @Override 
 public void onReceive(Context context, Intent intent) { 
     super.onReceive(context, intent); 
     System.out.println("onreceiver"); 
 } 

 @Override 
 public void onEnabled(Context context, Intent intent) { 
     System.out.println("激活使用"); 
     super.onEnabled(context, intent); 
 } 

 @Override 
 public void onDisabled(Context context, Intent intent) { 
     System.out.println("取消激活"); 
     super.onDisabled(context, intent); 
 } 
}
