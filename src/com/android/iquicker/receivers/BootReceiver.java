package com.android.iquicker.receivers;

import java.lang.reflect.InvocationTargetException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.android.iquicker.MainActivity;
import com.android.iquicker.common.CommonTools;
import com.android.iquicker.common.PanelButtonConfig;
import com.android.iquicker.services.FloatSideService;
import com.android.iquicker.services.RefreshDataService;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;


public class BootReceiver extends BroadcastReceiver {
	
    @Override 
    public void onReceive(Context context, Intent intent) { 
    	try {
    		RefreshDataService.mAppGlobalContext = context.getApplicationContext();
        	CommonTools.intContext(RefreshDataService.mAppGlobalContext);
        	
        	ResetDataBase();
        	
        	Intent refresh = new Intent(context, RefreshDataService.class);
            context.startService(refresh);
            
            if(CommonTools.IsShowFlagON())
            {
                Intent intentFloat = new Intent(context, FloatSideService.class);  
                context.startService(intentFloat);
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

	private void ResetDataBase() {
		// TODO Auto-generated method stub
		try
		{
			DbUtils db = DbUtils.create(RefreshDataService.mAppGlobalContext);
			
			PanelButtonConfig data = db.findFirst(Selector.from(PanelButtonConfig.class).where("id","=",1));
			if(data != null && data.getId() == 1)
			{
				//Logger.i("_FLOAT_", "ResetDataBase=" + data.getId() + ",pretick=" + data.getPretick());
				
				data.setPretick(0);
				
				db.update(data, "pretick");
			}
			
			db.close();
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
