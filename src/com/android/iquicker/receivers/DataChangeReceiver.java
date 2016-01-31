package com.android.iquicker.receivers;

import java.lang.reflect.InvocationTargetException;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.android.iquicker.services.RefreshDataService;


public class DataChangeReceiver extends BroadcastReceiver {
	
	private static int minutesCount = 0;
	private static boolean mPreFullFlag = false;
    
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub

		minutesCount++;
		boolean isServiceRunning = false; 
		try {
            if (intent.getAction().equals(Intent.ACTION_TIME_TICK)) 
            { 
            	ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE); 
            	for (RunningServiceInfo service :manager.getRunningServices(Integer.MAX_VALUE)) 
            	{ 
            		if("com.android.floatquicker.services.RefreshDataService".equals(service.service.getClassName()))     
            		{ 
            			isServiceRunning = true;
            			break;
            		}
            	}
            	
            	if (!isServiceRunning) 
            	{
            		Intent i = new Intent(context, RefreshDataService.class); 
            		context.startService(i); 
            	}
            	else
            	{
            		
            	}
            	
            } 

		} catch (Exception e) {
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
