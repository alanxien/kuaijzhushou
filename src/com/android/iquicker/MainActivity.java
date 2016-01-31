package com.android.iquicker;


import java.util.ArrayList;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.android.iquicker.common.CommonTools;
import com.android.iquicker.services.FloatSideService;
import com.android.iquicker.services.RefreshDataService;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;


public class MainActivity extends Activity {

	private static int FLAG_CLOSE = 0;
	
	private static int FLAG_OPEN = 1;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        if(RefreshDataService.mAppGlobalContext == null)
        {
        	RefreshDataService.mAppGlobalContext = this.getApplicationContext();
        }
        
        CommonTools.intContext(RefreshDataService.mAppGlobalContext);

        try
        {
	        //AnalyticsConfig.setAppkey(MainActivity.this, "5639b8e167e58ea8c60001d8");
	        //AnalyticsConfig.setChannel(CommonTools.getProjectName());
	        
	        UmengUpdateAgent.update(MainActivity.this);
        }
        catch(Exception ex)
        {
        	ex.printStackTrace();
        }
        
        Intent intentFloat = new Intent(MainActivity.this, FloatSideService.class);  
        startService(intentFloat);
        
        //用户知晓按钮
        Button btnOK = (Button)findViewById(R.id.buttonOK);
        btnOK.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				//TaskExecutor.DoExecuteGetTask();
				finish();
			}
        	
        });
        
        //用户关闭悬浮窗
        Button btnClose = (Button)findViewById(R.id.buttonClose);
        btnClose.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				CommonTools.SetShowFlag(FLAG_CLOSE);
				Intent intentFloat = new Intent(MainActivity.this, FloatSideService.class);  
		        stopService(intentFloat);
			}
        	
        });
        
        //用户重新开启悬浮窗
        Button btnRestart = (Button)findViewById(R.id.buttonReOpen);
        btnRestart.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				CommonTools.SetShowFlag(FLAG_OPEN);
				Intent intentFloat = new Intent(MainActivity.this, FloatSideService.class);  
		        startService(intentFloat); 
			}
        });

		Intent intentRefresh = new Intent(MainActivity.this, RefreshDataService.class);  
        startService(intentRefresh); 
        
        new Thread(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub		        
		        if(CommonTools.IsShowFlagON())
		        {
			   	    Intent intentFloat = new Intent(MainActivity.this, FloatSideService.class);  
			        startService(intentFloat);
		        }
			}
        }).start();
                   
    }
    
	@Override
	protected void onStart() {
		super.onStart();
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub       
        super.onStop();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		finish();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
	}
	
	public static boolean isServiceWorked(Context context, String serviceName) {
		ActivityManager myManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		ArrayList<RunningServiceInfo> runningService = (ArrayList<RunningServiceInfo>) myManager
				.getRunningServices(Integer.MAX_VALUE);
		for (int i = 0; i < runningService.size(); i++) {
			if (runningService.get(i).service.getClassName().toString()
					.equals(serviceName)) {
				return true;
			}
		}
		return false;
	}
}

