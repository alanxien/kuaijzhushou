/* 
 * @Title:  BluetoothControl.java 
 * @Copyright:  XXX Co., Ltd. Copyright YYYY-YYYY,  All rights reserved 
 * @Description:  TODO<请描述此文件是做什么的> 
 * @author:  xie.xin
 * @data:  2015-12-31 上午1:14:58 
 * @version:  V1.0 
 */
package com.android.iquicker.common;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;

/** 
 * TODO<请描述这个类是干什么的> 
 * @author  xie.xin 
 * @data:  2015-12-31 上午1:14:58 
 * @version:  V1.0 
 */
public class BluetoothControl {

	BluetoothAdapter mBluetoothAdapter;
	Context context;
	
	public BluetoothControl(Context context){
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		this.context = context;
	}
	
	public boolean isBluetoothActive(){
		return mBluetoothAdapter.isEnabled();
	}
	
	public void startBluetooth(){
		if(!mBluetoothAdapter.isEnabled()){
			Intent mIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);  
			mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.context.startActivity(mIntent);
		}
	}
	
	public void stopBluetooth(){
		if(mBluetoothAdapter.isEnabled()){
			mBluetoothAdapter.disable();
		}
	}
}
