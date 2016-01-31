package com.android.iquicker.common;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;
import android.view.WindowManager;
import android.widget.Toast;

/**
 * @author xin.xie
 * 调节屏幕亮度
 */
public class LightnessControl {

	/**
	 * @author xin.xie
	 * @param act
	 * @return
	 * 判断是否开启了自动亮度调节
	 */
	public static boolean isAutoBrightness(Context act) {
		boolean automicBrightness = false;
		ContentResolver aContentResolver = act.getContentResolver();
		try {
			automicBrightness = Settings.System.getInt(aContentResolver,
					Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
		} catch (Exception e) {
			Toast.makeText(act, "无法获取亮度", Toast.LENGTH_SHORT).show();
		}
		return automicBrightness;
	}

	/**
	 * @author xin.xie
	 * @param act
	 * @param value
	 * 改变亮度0-255
	 */
	public static void SetLightness(Context act, int value) {
		android.provider.Settings.System.putInt(act.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, value);
	}

	/**
	 * @author xin.xie
	 * @param act
	 * @return
	 * 获取亮度
	 */
	public static int GetLightness(Context act) {
		return Settings.System.getInt(act.getContentResolver(),
				Settings.System.SCREEN_BRIGHTNESS, -1);
	}

	/**
	 * @author xin.xie
	 * @param activity
	 * 停止自动亮度调节
	 */
	public static void stopAutoBrightness(Context activity) {
		Settings.System.putInt(activity.getContentResolver(),
				Settings.System.SCREEN_BRIGHTNESS_MODE,
				Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
	}

	/**
	 * @author xin.xie
	 * @param activity
	 * 开启亮度自动调节
	 */
	public static void startAutoBrightness(Context activity) {
		Settings.System.putInt(activity.getContentResolver(),
				Settings.System.SCREEN_BRIGHTNESS_MODE,
				Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
	}
}
