package com.android.iquicker.common;

import android.content.Context;
import android.media.AudioManager;

/**
 * @author xin.xie
 * 音量控制
 */
public class VolumeControl {

	private AudioManager audioManager;
	
	public VolumeControl(Context context){
		audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
	}
	
	/**
	 * @author xin.xie
	 * @return
	 * 获取系统最大音量
	 */
	public int getMaxVolume(){
		return audioManager.getStreamMaxVolume(AudioManager.STREAM_SYSTEM);
	}
	
	/**
	 * @author xin.xie
	 * @return
	 * 获取系统当前音量
	 */
	public int getCurrentVolume(){
		return audioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);
	}
	
	/**
	 * @author xin.xie
	 * @param v
	 * 设置音量大小
	 */
	public void setVolume(int volume){
		audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, volume, 0);
	}
}
