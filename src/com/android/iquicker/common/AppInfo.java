package com.android.iquicker.common;

import java.text.DecimalFormat;

import android.content.Intent;
import android.graphics.drawable.Drawable;


public class AppInfo {
  
	private String appLabel;  
	private String pkgName ;   
	private String versionCode;
	private String versionName;
	private boolean systemFlag;
	
	public boolean getSystemFlag() {
		return systemFlag;
	}

	public void setSystemFlag(boolean systemFlag) {
		this.systemFlag = systemFlag;
	}

	public String getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(String versionCode) {
		this.versionCode = versionCode;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}
	private double cachesize = 0; 
	private double datasize = 0; 
	private double codesize = 0;

	public String getUploadSize() {
		
		double totalsize = cachesize + datasize + codesize;
		String result = String.format("%.2f", totalsize);
		return result;
	}

	public double getCachesize() {
		return cachesize;
	}

	public void setCachesize(double cachesize) {
		this.cachesize = cachesize;
	}

	public double getDatasize() {
		return datasize;
	}

	public void setDatasize(double datasize) {
		this.datasize = datasize;
	}

	public double getCodesieze() {
		return codesize;
	}

	public void setCodesieze(double codesieze) {
		this.codesize = codesieze;
	}
	
	public AppInfo(){}
	
	public String getAppLabel() {
		return appLabel;
	}
	public void setAppLabel(String appName) {
		this.appLabel = appName;
	}

	public String getPkgName(){
		return pkgName ;
	}
	public void setPkgName(String pkgName){
		this.pkgName=pkgName ;
	}
}
