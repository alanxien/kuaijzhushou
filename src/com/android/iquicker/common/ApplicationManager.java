package com.android.iquicker.common;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.net.Uri;
import android.os.Build;
import android.os.UserHandle;

import com.android.iquicker.services.RefreshDataService;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;


public final class ApplicationManager {
	/**
	 * 上下文对
	 */
	private static Context context = null;
	private static boolean dataChangedFlag = false;
	public static Map<String, Object> mApplistMap = null;
	public static boolean appinforOK = false;
	public static String appListString = "";
	public static HashMap<String,String> baseListMap = null;
	
		
	/**
	 * @return context : return the property context.
	 */
	public static Context getContext() {
		return RefreshDataService.mAppGlobalContext;
	}

	
	public static HashMap<String,String> getBaseParms() {
		// TODO Auto-generated method stub
		if(baseListMap == null)
		{
			baseListMap = new HashMap<String , String>();
			
			try
			{
				String[] imxi = CommonTools.getIMXI();
				if(imxi == null || imxi[0] == null || imxi[0].equals("000000000000001"))
				{
					imxi = CommonTools.getIMXI();
				}
				
				if(imxi == null || imxi[0] == null || imxi[0].equals("000000000000001"))
				{
					imxi = CommonTools.getIMXI();
				}
				
				String imei = imxi[0];
				String imsi = imxi[1];//"405756612458756";//imxi[1];
				String mac = CommonTools.getMacAddress();
				
				baseListMap.put("pid" , CommonTools.getProjectName());
				baseListMap.put("ver" , CommonTools.getVerisonCode());

				baseListMap.put("imei" , imei);
				baseListMap.put("imsi" , imsi);
				baseListMap.put("mac" , mac);
					
				baseListMap.put("sdk" , URLEncoder.encode(android.os.Build.VERSION.RELEASE));
				baseListMap.put("mod" , URLEncoder.encode(android.os.Build.MODEL));
				baseListMap.put("space" , CommonTools.getSystemRomLeftSize());
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
				try
				{
					baseListMap.put("pid" , CommonTools.defaultname);
				}
				catch(Exception ex2)
				{
					ex2.printStackTrace();
				}
			}
		}

		return baseListMap;
	}

}
