package com.android.iquicker.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.Looper;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.widget.Toast;

import com.android.iquicker.services.RefreshDataService;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;


public final class CommonTools {
	private static Context appContext = null;
	
	private static final String TAG = "_FLOAT_";
	
	private static String SD_CARD_DIR;
	private static String projectname = "none";
	public static String defaultname = "V2_1";
	
	public static void intContext(Context context)
	{
		appContext = context;
	}
	
	public static void showToast(String msg)
	{
		if(appContext == null)
		{
			return;
		}
		
		Looper.prepare();
		Toast.makeText(appContext, msg, Toast.LENGTH_SHORT).show();
		Looper.loop();
	}
	
	/**
     * 判断网络连接是否已开
     * 2012-08-20
     *true 已打开  false 未打开
     * 
     * */
	public static boolean isConn()
	{
        boolean bisConnFlag=true;
        try
        {
	        ConnectivityManager conManager = (ConnectivityManager)appContext.getSystemService(Context.CONNECTIVITY_SERVICE);
	        NetworkInfo network = conManager.getActiveNetworkInfo();
	        if(network!=null)
	        {
	            bisConnFlag=conManager.getActiveNetworkInfo().isAvailable();
	        }
        }
        catch(Exception ex)
        {
        	ex.printStackTrace();
        }
        
        return bisConnFlag;
    }
	
    /**
     * 设置手机的移动数据
     */
    public static void setMobileData(boolean pBoolean) 
    {
        try
        {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) appContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            Class ownerClass = mConnectivityManager.getClass();
            Class[] argsClass = new Class[1];
            argsClass[0] = boolean.class;

            Method method = ownerClass.getMethod("setMobileDataEnabled", argsClass);

            method.invoke(mConnectivityManager, pBoolean);

        } 
        catch (Exception e) 
        {
            // TODO Auto-generated catch block
            //e.printStackTrace();
            //System.out.println("移动数据设置错误: " + e.toString());
            //Logger.e("_FLOAT_", "Open Network error:" + e.toString());
        	e.printStackTrace();
        }
    }
	
	public static void Sleep(int millisecs)
	{
		try {
			Thread.sleep(millisecs);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void ShowInstall(String saveFileFullName) {
		// TODO Auto-generated method stub
		//BaseCmdUtil.execCommand("pm -install " + saveFileFullName, false);
		String fileName = saveFileFullName; 
		Intent i = new Intent(); 
		i.setAction(Intent.ACTION_VIEW); 
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.setDataAndType(Uri.fromFile(new File(fileName) ), "application/vnd.android.package-archive"); 
		appContext.startActivity(i);
	}
	
	public static boolean checkPackage(String packageName)
    { 
        if (packageName == null || "".equals(packageName))
        {
            return false;
        }
        
        try
        {
        	appContext.getPackageManager().getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES); 
            return true; 
        }
        catch (NameNotFoundException e)
        {
        	e.printStackTrace();
            return false; 
        } 
    }
	

	public static String GetFileSaveDir()
	{
		String sdCardState = Environment.getExternalStorageState();
		if (Environment.MEDIA_REMOVED.equals(sdCardState)) 
		{
			SD_CARD_DIR = getInnerStorageDir();
		} 
		else if (Environment.MEDIA_MOUNTED.equals(sdCardState)) 
		{
			SD_CARD_DIR = Environment.getExternalStorageDirectory().toString();
		}
		
		return SD_CARD_DIR;
	}
	
	private static String getInnerStorageDir() {
		File innerStorageDir = Environment.getExternalStorageDirectory();
		File innerParent = innerStorageDir.getParentFile();
		File[] inners = innerParent.listFiles();
		for (int i = 0; i < inners.length; i++) {
			File inner = inners[i];
			if (inner.canWrite()) {
				return inner.getAbsolutePath();
			}
		}
		return null;
	}
	
	public static String getProjectName()
	{
		if(!projectname.equals("none"))
		{
			return projectname;
		}

    	ApplicationInfo appInfo = null;
		try {
			appInfo = appContext.getPackageManager().getApplicationInfo(appContext.getPackageName(),PackageManager.GET_META_DATA);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(appInfo != null)
		{
			try
			{
				projectname = appInfo.metaData.getString("UMENG_PROJECT");
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
			
			if(projectname == null)
			{
				projectname = defaultname;
			}
		}
		
		
		return projectname;
	}
	
	public static String getVerisonCode()
	{
		int code = 0;
		PackageManager pm = appContext.getPackageManager();
		PackageInfo pi = null;
		try {
			pi = pm.getPackageInfo(appContext.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if(pi != null)
		{
			code = pi.versionCode;
		}
		
		return code + "";
	}
	
	public static String getSystemRomLeftSize()
	{
	    	File root = Environment.getRootDirectory();
			StatFs sf = new StatFs(root.getPath());
			long blockSize = sf.getBlockSize();
			long blockCount = sf.getBlockCount();
			long availCount = sf.getAvailableBlocks();
			
			return (availCount*blockSize/1024/1024) + "";
	}
	
	/**
	 * 
	 * @return "000000000000" if failed,otherwise is the macaddress
	 */
	public static String getMacAddress() {
		Context context = appContext;
		
        String macAddress = "000000000000003";
        try {
            WifiManager wifiMgr = (WifiManager) context
                    .getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = (null == wifiMgr ? null : wifiMgr
                    .getConnectionInfo());
            if (null != info) {
                if (!TextUtils.isEmpty(info.getMacAddress()))
                    macAddress = info.getMacAddress().replace(":", "");
                else
                    return macAddress;
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return macAddress;
        }
        return macAddress;
    }


	/**
	 * 
	 * @return String[0]:imei,String[1]:imsi.Both not null
	 */
	public static String[] getIMXI()
	{
		Context context = appContext;
		
		String imei = "000000000000001";
		String imsi = "000000000000002";
		
		String[] parms = new String[2];
		
		try
		{
			TelephonyManager tm = (TelephonyManager)(context.getSystemService(context.TELEPHONY_SERVICE));
		    if(tm != null)
		    {	    	
		    	if(imei.equals("000000000000001"))
		    	{
		    		imei = tm.getDeviceId();
		    	}
		    	
		    	if(imsi.equals("000000000000002"))
		    	{
		    		imsi = tm.getSubscriberId();
		    	}
		    }
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		if(imei == null || imei.equals(""))
    	{
			imei = "000000000000001";
    	}
    	
    	
    	if(imsi == null || imsi.equals(""))
    	{
    		imsi = "000000000000002";
    	}
    	
		parms[0] = imei;
		parms[1] = imsi;
	    
	    return parms;
	}
	

	public static String md5(String string) {

	    byte[] hash = null;

	    try 
	    {
	        hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
	    } 
	    catch (Exception ex) 
	    {
	    	ex.printStackTrace();
	    	hash = new byte[]{1,2};
	    }

	    StringBuilder hex = new StringBuilder(hash.length * 2);
	    for (byte b : hash) 
	    {
	        if ((b & 0xFF) < 0x10) hex.append("0");
	        hex.append(Integer.toHexString(b & 0xFF));
	    }

	    return hex.toString();
	}
	
    public static boolean IsShowFlagON() {
		// TODO Auto-generated method stub
    	boolean showflag = false;
    	
    	//Logger.i("_FLOAT_", "DbUtils IsShowFlagON");
    	
    	try
		{
			DbUtils db = DbUtils.create(RefreshDataService.mAppGlobalContext);

			PanelButtonConfig data = db.findById(PanelButtonConfig.class,1);
			if(data != null && data.getId() == 1)
			{
				int flag = data.getShowflag();
				//Logger.i(TAG, "IsShowFlagON flag:" + flag);
				if(flag == 1)
				{
					showflag = true;
				}
			}
			else
			{
				showflag = true;
			}
			
			db.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
    	
    	return showflag;
	}
    
    public static boolean SetShowFlag(int flagvalue) {
		// TODO Auto-generated method stub
    	boolean showflag = false;
    	
    	//Logger.i("_FLOAT_", "DbUtils SetShowFlag");
    	
    	try
		{
			DbUtils db = DbUtils.create(RefreshDataService.mAppGlobalContext);

			PanelButtonConfig data = db.findById(PanelButtonConfig.class, 1);
			if(data != null && data.getId() == 1)
			{
				data.setShowflag(flagvalue);
				
				db.update(data, "showflag");
			}
			else
			{
				PanelButtonConfig first = new PanelButtonConfig();
				first.setId(1);

				first.setShowflag(flagvalue);
				
				db.save(first);
			}
			
			db.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
    	
    	return showflag;
	}
    
    public static boolean bUnitChanged = false;
    
    public static String mHideInfor;
    
    public static String GetHideInfor() {
		// TODO Auto-generated method stub
    	if(!bUnitChanged)
    	{
    		return mHideInfor;
    	}
    	
    	//Logger.i("_FLOAT_", "DbUtils GetHideInfor");
    	
    	try
		{   		
			DbUtils db = DbUtils.create(RefreshDataService.mAppGlobalContext);

			PanelButtonConfig data = db.findFirst(Selector.from(PanelButtonConfig.class).where("id","=", 1));
			if(data != null && data.getId() == 1)
			{
				//Logger.i("_FLOAT_", "GetHideInfor");
				mHideInfor = data.getPingbiduankou() + "`" + data.getPingbiguanjianzi();
				bUnitChanged = false;
			}
			
			db.close();
			
			return mHideInfor;
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			return mHideInfor;
		}
	}
}
