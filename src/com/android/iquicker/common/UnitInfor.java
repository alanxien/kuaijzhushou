package com.android.iquicker.common;

import android.graphics.drawable.Drawable;

public class UnitInfor {
	private String packageName;
	private String appName;

	private int indexOfSetting;
	private boolean systemFlag;
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public String getAppName() {
		return appName;
	}
	public void setAppName(String appName) {
		this.appName = appName;
	}

	public int getIndexOfSetting() {
		return indexOfSetting;
	}
	public void setIndexOfSetting(int indexOfSetting) {
		this.indexOfSetting = indexOfSetting;
	}
	public boolean isSystemFlag() {
		return systemFlag;
	}
	public void setSystemFlag(boolean systemFlag) {
		this.systemFlag = systemFlag;
	}
	
	
}
