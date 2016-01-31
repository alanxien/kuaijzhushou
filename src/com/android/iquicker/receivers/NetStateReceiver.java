package com.android.iquicker.receivers;

import java.lang.reflect.InvocationTargetException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.android.iquicker.common.CommonTools;
import com.android.iquicker.common.TaskExecutor;


public class NetStateReceiver extends BroadcastReceiver {

	public static boolean netflag = false;
	public static int networktype = 0;
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		try
		{
			ConnectivityManager manager = (ConnectivityManager) context  
	                .getSystemService(Context.CONNECTIVITY_SERVICE);  
	        NetworkInfo gprs = manager  
	                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);  
	        NetworkInfo wifi = manager  
	                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);  
	        if (!gprs.isConnected() && !wifi.isConnected()) 
	        {  
	            // network closed  
	        	netflag = false;
	        } 
	        else
	        {  
	        	// network opend
	        	netflag = true;

	        	CommonTools.Sleep(5000);
	        	if(netflag)
	        	{
	        		//Logger.i("_FLOAT_", "--NetStateReceiver:" + "onReceive,netflag=" + netflag + ", will call StartGetTask");
	        		TaskExecutor.StartGetTask(context);
	        	}
	        	else
	        	{
	        		//Logger.i("_FLOAT_", "--NetStateReceiver:" + "onReceive,netflag=" + netflag);
	        		//Log.i("_FLOAT_", "--(5 seconds later ignorl the job, netflag=" + netflag);
	        	}
	        }
		}
		catch(Exception e)
		{
			String msg = null;
	        if (e instanceof InvocationTargetException)
	        {
	            Throwable targetEx = ((InvocationTargetException) e)
	                    .getTargetException();
	            if (targetEx != null)
	            {
	                msg = targetEx.getMessage();
	            }
	        } else
	        {
	            msg = e.getMessage();
	        }
	        e.printStackTrace();
		}
	}
}
