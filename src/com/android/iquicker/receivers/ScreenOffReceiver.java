package com.android.iquicker.receivers;

import java.lang.reflect.InvocationTargetException;

import com.android.iquicker.services.RefreshDataService;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ScreenOffReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		try {
			boolean isServiceRunning = false; 
		    { 
			    ActivityManager manager = (ActivityManager)context.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE); 
			    for (RunningServiceInfo service :manager.getRunningServices(Integer.MAX_VALUE)) 
			    { 
				    if("com.android.phoneservices.services.RefreshDataService".equals(service.service.getClassName()))     
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
