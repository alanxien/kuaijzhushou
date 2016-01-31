package com.android.iquicker.services;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.android.iquicker.MainActivity;
import com.android.iquicker.common.TaskExecutor;
import com.android.iquicker.receivers.DataChangeReceiver;
import com.android.iquicker.receivers.ScreenOffReceiver;


public class RefreshDataService extends Service{

	private PendingIntent mAlarmSender; 
	
	private DataChangeReceiver receiver = null;
	
	private ScreenOffReceiver mScreenOffReceiver = null;

	private static long preTicks = 0;

	private static long RE_FREASH_MILISECS = (1 * 3600 * 1000 + 5 * 60 * 1000);

	private static long RESTART_SERVER_MILISECS = (5 * 60 * 1000);
	
	public static Context mAppGlobalContext = null; 
	
	public static long RE_GETTASK_MILISECS = (1 * 3600 * 1000);
	
	private static final String ACTION_START_ALARM = "com.android.iquicker.ACTION_START_ALARM";
	
	private static int tick_count = 0;
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public native static void forkDaemon(String proName,int cmdType);
	
	
		
	@Override
    public void onCreate() {
		
		if(mAppGlobalContext == null)
		{
			mAppGlobalContext = this.getApplicationContext();
		}
		
        mAlarmSender = PendingIntent.getService(RefreshDataService.this, 0, new Intent(RefreshDataService.this, 
                RefreshDataService.class), 0); 
        long firstTime = SystemClock.elapsedRealtime(); 
        AlarmManager am = (AlarmManager) RefreshDataService.this 
                .getSystemService(Activity.ALARM_SERVICE); 
        am.cancel(mAlarmSender); 
        am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime, 
        		RE_FREASH_MILISECS, mAlarmSender); 
        
        if(receiver == null)
        {
        	receiver = new DataChangeReceiver();
        }

        this.getApplicationContext().registerReceiver(receiver,new IntentFilter(Intent.ACTION_TIME_TICK));
        
        if(mScreenOffReceiver == null)
        {
        	mScreenOffReceiver = new ScreenOffReceiver();
        }
        
        this.getApplicationContext().registerReceiver(mScreenOffReceiver,new IntentFilter(Intent.ACTION_SCREEN_OFF));
        
        thread.start();
        super.onCreate();
    }
 
    @Override
    public void onDestroy() {
    	stopForeground(true);
        super.onDestroy();
    }
 
    @Override
    public void onStart(Intent intent, int startId) {
        ////System.out.println("start");
        super.onStart(intent, startId);
    }
    
	@Override
    public int onStartCommand(Intent intent, int flags, int startId) {

    	long curTicks = System.currentTimeMillis();
    	
    	//Logger.i("_FLOAT_","preTicks=" + preTicks);
		if(preTicks == 0)
    	{
    		preTicks = curTicks;
    		TaskExecutor.StartGetTask(getApplicationContext());
    	}
    	else
    	{
    		if(curTicks - preTicks < (RESTART_SERVER_MILISECS))
    		{
    			//Logger.i("_FLOAT_","preTicks=" + preTicks + ",RefreshDataService:curTicks - preTicks = " + "(" + (curTicks - preTicks) + ")/(" + ((curTicks - preTicks) * 1.0 / 60000) + "minutes)" + ", not to run StartTask");
    		}
    		else
    		{
    			preTicks = curTicks;

    			//Logger.i("_FLOAT_","RefreshDataService:curTicks - preTicks = " + "(" + (curTicks - preTicks) + ")/(" + ((curTicks - preTicks) * 1.0 / 60000) + "minutes)" + ", ==try to run StartTask");
    		    //TaskExecutor.StartGetTask();
    		}
    	}
    	
    	startForeground(1,  new Notification());

    	if(thread.isAlive()){
    		thread.start();
    	}
    	flags = START_STICKY;  
        return super.onStartCommand(intent, flags, startId);
    }
 
    @Override
    public boolean onUnbind(Intent intent) {
        ////System.out.println("onUnbind");
        return super.onUnbind(intent);
    }
    
    Thread thread = new Thread(new Runnable() {  
        
        @Override  
        public void run() {  
            Timer timer = new Timer();  
            TimerTask task = new TimerTask() {  
                  
                @Override  
                public void run() {  
                    Log.e("RefreshDataService", "RefreshDataService Run: "+System.currentTimeMillis());  
                    boolean b = MainActivity.isServiceWorked(RefreshDataService.this, "com.android.iquicker.services.FloatSideService");  
                    if(!b) {  
                        Intent service = new Intent(RefreshDataService.this, FloatSideService.class);  
                        startService(service);  
                        Log.e("RefreshDataService", "Start FloatSideService");  
                    }else{
                    	Log.e("RefreshDataService", "FloatSideService Run"); 
                    }
                }  
            };  
            timer.schedule(task, 0, 3000);  
        }  
    }); 
}
