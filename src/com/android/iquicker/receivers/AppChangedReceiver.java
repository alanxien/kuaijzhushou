package com.android.iquicker.receivers;

import java.lang.reflect.InvocationTargetException;

import com.android.iquicker.services.FloatSideService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AppChangedReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		try {
			FloatSideService.DataChanged();
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
