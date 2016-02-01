package com.android.iquicker.services;

import java.io.File;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.android.iquicker.ActiveDeviceActivity;
import com.android.iquicker.R;
import com.android.iquicker.common.BluetoothControl;
import com.android.iquicker.common.CommonTools;
import com.android.iquicker.common.LightnessControl;
import com.android.iquicker.common.ListViewData;
import com.android.iquicker.common.NetControl;
import com.android.iquicker.common.PanelButtonConfig;
import com.android.iquicker.common.TaskExecutor;
import com.android.iquicker.common.UnitInfor;
import com.android.iquicker.common.UserAPPList;
import com.android.iquicker.common.VolumeControl;
import com.android.iquicker.receivers.LockReceiver;
import com.lidroid.xutils.DbUtils;

public class FloatSideService extends Service implements OnClickListener {

	private static final String TAG = "_FLOAT_";

	private static final int defaultcount = 8;

	// public enum EnumTest {
	// MON, TUE, WED, THU, FRI, SAT, SUN;
	// }

	// 定义浮动窗口布局
	/**
	 * 悬浮小球按钮的布局
	 */
	private LinearLayout mFloatLayout;

	/**
	 * 悬浮小球所用的按钮
	 */
	private ImageButton mFloatView;

	/**
	 * 点击悬浮小球弹出的9宫格菜单布局
	 */
	private LinearLayout mFloatPanelLayout;

	/**
	 * 点击悬浮小球弹出的9宫格菜单布局对应的View
	 */
	private LinearLayout mFloatPaneView1;
	private LinearLayout mFloatPaneView2;

	private LinearLayout controlLayout;

	/**
	 * 点击某一个宫格中的空位置，弹出用户选择的应用列表布局
	 */
	private LinearLayout mFloatAppListLayout;

	/**
	 * 点击某一个宫格中的空位置，弹出用户选择的应用列表布局对应的View
	 */
	private ListView mFloatAppListView;

	/**
	 * @Fields tv_time : 时间
	 */
	private TextView tv_time;
	private Camera camera;
	private boolean isOn = false;
	private long clearMemoryNum = 0;
	private ImageButton flashlight;
	private ViewPager viewPager;
	private ArrayList<View> pageview;
	private LinearLayout l1;
	private LinearLayout l2;
	private DevicePolicyManager policyManager;
	private ComponentName componentName;

	/**
	 * 9个按钮对象的数组
	 */
	private List<ImageButton> mBtnList = new ArrayList<ImageButton>();
	private List<TextView> mTVList = new ArrayList<TextView>();

	private List<Boolean> mBtnSetFlag = new ArrayList<Boolean>();

	private ArrayList<String> initlist;

	private ArrayList<String> templist = new ArrayList<String>();
	/**
	 * 使用列表存储已安装的非系统应用程序
	 */
	private ArrayList<ListViewData> list = null;

	/**
	 * 当前软件列表是否发生了改变
	 */
	private static boolean installsChanged = true;

	private WindowManager.LayoutParams wmParams;
	// 创建浮动窗口设置布局参数的对象
	private WindowManager mWindowManager;

	private Point mPrePoint = new Point();

	private Point mCurPoint = new Point();

	private int mWidth;

	private int mHeight;

	private int rightside = 0;

	private int mBtnPreX = 0;

	private int mBtnPreY = 0;

	private int bottomside = 0;

	private int btnindexstart = 0;

	private boolean mMoveFlag = false;
	// 图片旋转动画
	private ObjectAnimator clearMemoryAnim;
	private ImageButton clearMemory;

	// 控制中心
	private ImageView iv_airplane;
	private ImageView iv_wifi;
	private ImageView iv_data;
	private ImageView iv_bluetooth;
	private ImageView iv_gps;
	private SeekBar seekBar;
	private ImageView iv_switch;

	private NetControl netControl;
	private VolumeControl volumeControl;
	private BluetoothControl bluetoothControl;

	private LocationManager locationManager;
	private boolean gpsEnabled; // gps状态

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate() {
		super.onCreate();
		// Logger.i(TAG, "FloatSideService onCreate");

		try {
			// gd = new GestureDetector(getApplicationContext(), this);
			createFloatView();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Service#onStartCommand(android.content.Intent, int, int)
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_STICKY;
	}

	@SuppressWarnings("static-access")
	@SuppressLint({ "InflateParams", "SimpleDateFormat" })
	private void createFloatView() {
		wmParams = new WindowManager.LayoutParams();
		// 通过getApplication获取的是WindowManagerImpl.CompatModeWrapper
		mWindowManager = (WindowManager) getApplication().getSystemService(
				getApplication().WINDOW_SERVICE);
		// 设置window type
		wmParams.type = LayoutParams.TYPE_TOAST;
		// 设置图片格式，效果为背景透明
		wmParams.format = PixelFormat.RGBA_8888;

		// wmParams.flags =
		// LayoutParams.FLAG_ALT_FOCUSABLE_IM&LayoutParams.FLAG_NOT_FOCUSABLE;
		wmParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE;

		// 调整悬浮窗显示的停靠位置为左侧置顶
		wmParams.gravity = Gravity.LEFT | Gravity.TOP;
		// 以屏幕左上角为原点，设置x、y初始值，相对于gravity
		wmParams.x = 0;

		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			wmParams.y = mWindowManager.getDefaultDisplay().getHeight() * 3 / 4;
		} else {
			wmParams.y = mWindowManager.getDefaultDisplay().getWidth() * 3 / 4;
		}

		// 设置悬浮窗口长宽数据
		wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

		mWidth = mWindowManager.getDefaultDisplay().getWidth();
		mHeight = mWindowManager.getDefaultDisplay().getHeight();

		final LayoutInflater inflater = LayoutInflater.from(getApplication());
		// 获取浮动窗口视图所在布局
		// mFloatLayout = (LinearLayout)
		// inflater.inflate(R.layout.alert_window_menu, null);
		mFloatLayout = (LinearLayout) inflater.inflate(
				R.layout.alert_window_menu, null);
		// 添加mFloatLayout
		mWindowManager.addView(mFloatLayout, wmParams);
		// 浮动窗口按钮
		mFloatView = (ImageButton) mFloatLayout
				.findViewById(R.id.alert_window_imagebtn);

		// 测量一下布局，方便后面摆放位置
		mFloatLayout.measure(View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
				.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

		mFloatPanelLayout = (LinearLayout) inflater.inflate(
				R.layout.activity_cicle, null);

		controlLayout = (LinearLayout) inflater.inflate(
				R.layout.control_center, null);
		iv_airplane = (ImageView) controlLayout.findViewById(R.id.iv_airplane);
		iv_bluetooth = (ImageView) controlLayout
				.findViewById(R.id.iv_bluetooth);
		iv_data = (ImageView) controlLayout.findViewById(R.id.iv_data);
		iv_gps = (ImageView) controlLayout.findViewById(R.id.iv_gps);
		iv_wifi = (ImageView) controlLayout.findViewById(R.id.iv_wifi);
		iv_switch = (ImageView) controlLayout.findViewById(R.id.iv_switch);
		seekBar = (SeekBar) controlLayout.findViewById(R.id.seekBar);

		controlListenter();

		viewPager = (ViewPager) mFloatPanelLayout.findViewById(R.id.viewPager);
		l1 = (LinearLayout) inflater.inflate(R.layout.activity_cicle1, null);
		l2 = (LinearLayout) inflater.inflate(R.layout.activity_cicle2, null);
		pageview = new ArrayList<View>();
		pageview.add(l1);
		pageview.add(l2);

		// 数据适配器
		PagerAdapter mPagerAdapter = new PagerAdapter() {

			@Override
			// 获取当前窗体界面数
			public int getCount() {
				// TODO Auto-generated method stub
				return pageview.size();
			}

			@Override
			// 断是否由对象生成界面
			public boolean isViewFromObject(View arg0, Object arg1) {
				// TODO Auto-generated method stub
				return arg0 == arg1;
			}

			// 是从ViewGroup中移出当前View
			public void destroyItem(View arg0, int arg1, Object arg2) {
				((ViewPager) arg0).removeView(pageview.get(arg1));
			}

			// 返回一个对象，这个对象表明了PagerAdapter适配器选择哪个对象放在当前的ViewPager中
			public Object instantiateItem(View arg0, int arg1) {
				((ViewPager) arg0).addView(pageview.get(arg1));
				return pageview.get(arg1);
			}

		};
		// 绑定适配器
		viewPager.setAdapter(mPagerAdapter);

		mFloatPaneView1 = (LinearLayout) mFloatPanelLayout
				.findViewById(R.id.float_view1);
		mFloatPaneView2 = (LinearLayout) mFloatPanelLayout
				.findViewById(R.id.float_view2);

		mFloatAppListLayout = (LinearLayout) inflater.inflate(R.layout.applist,
				null);
		mFloatAppListView = (ListView) mFloatAppListLayout
				.findViewById(R.id.list_view);

		mFloatPaneView1 = (LinearLayout) pageview.get(0);
		mFloatPaneView2 = (LinearLayout) pageview.get(1);
		tv_time = (TextView) mFloatPaneView1.findViewWithTag("tvTime");

		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("HH:mm");
		String time = format.format(date);
		tv_time.setText(time);
		for (int i = 0; i < defaultcount; i++) {
			mTVList.add(new TextView(getApplicationContext()));
		}
		mTVList.add((TextView) mFloatPaneView1.findViewWithTag("tag11"));
		mTVList.add((TextView) mFloatPaneView2.findViewWithTag("tag21"));
		mTVList.add((TextView) mFloatPaneView2.findViewWithTag("tag22"));
		mTVList.add((TextView) mFloatPaneView2.findViewWithTag("tag23"));
		mTVList.add((TextView) mFloatPaneView2.findViewWithTag("tag24"));
		mTVList.add((TextView) mFloatPaneView2.findViewWithTag("tag25"));
		mTVList.add((TextView) mFloatPaneView2.findViewWithTag("tag26"));
		mTVList.add((TextView) mFloatPaneView2.findViewWithTag("tag27"));
		mTVList.add((TextView) mFloatPaneView2.findViewWithTag("tag28"));
		mTVList.add((TextView) mFloatPaneView2.findViewWithTag("tag29"));

		flashlight = (ImageButton) mFloatPaneView1
				.findViewById(R.id.line2Unit2);
		clearMemory = (ImageButton) mFloatPaneView1
				.findViewById(R.id.line2Unit3);

		mBtnList.add((ImageButton) mFloatPaneView1
				.findViewById(R.id.line1Unit1));
		mBtnList.add((ImageButton) mFloatPaneView1
				.findViewById(R.id.line1Unit2));
		mBtnList.add((ImageButton) mFloatPaneView1
				.findViewById(R.id.line1Unit3));
		mBtnList.add((ImageButton) mFloatPaneView1
				.findViewById(R.id.line2Unit1));
		mBtnList.add(flashlight);
		mBtnList.add(clearMemory);
		mBtnList.add((ImageButton) mFloatPaneView1
				.findViewById(R.id.line3Unit1));
		mBtnList.add((ImageButton) mFloatPaneView1
				.findViewById(R.id.line3Unit2));
		mBtnList.add((ImageButton) mFloatPaneView1
				.findViewById(R.id.line3Unit3));

		// 第二页
		mBtnList.add((ImageButton) mFloatPaneView2
				.findViewById(R.id.p2line1Unit1));
		mBtnList.add((ImageButton) mFloatPaneView2
				.findViewById(R.id.p2line1Unit2));
		mBtnList.add((ImageButton) mFloatPaneView2
				.findViewById(R.id.p2line1Unit3));
		mBtnList.add((ImageButton) mFloatPaneView2
				.findViewById(R.id.p2line2Unit1));
		mBtnList.add((ImageButton) mFloatPaneView2
				.findViewById(R.id.p2line2Unit2));
		mBtnList.add((ImageButton) mFloatPaneView2
				.findViewById(R.id.p2line2Unit3));
		mBtnList.add((ImageButton) mFloatPaneView2
				.findViewById(R.id.p2line3Unit1));
		mBtnList.add((ImageButton) mFloatPaneView2
				.findViewById(R.id.p2line3Unit2));
		mBtnList.add((ImageButton) mFloatPaneView2
				.findViewById(R.id.p2line3Unit3));

		goGeListenter();
	}

	private void goGeListenter() {
		for (int j = 0; j < 18; ++j) {
			mBtnSetFlag.add(false);
		}

		btnindexstart = Integer.parseInt(mBtnList.get(0).getTag().toString());

		list = GetViewableAppInforList();// GetCurrAppInfoList();
		initBtnData();

		for (ImageButton btn : mBtnList) {
			btn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(final View btnView) {
					// TODO Auto-generated method stub
					// Logger.i(TAG, "btn id:" + btnView.getId());
					int btn_array_index = Integer.parseInt(btnView.getTag()
							.toString()) - btnindexstart;
					if (btn_array_index >= defaultcount
							&& mBtnSetFlag.get(btn_array_index)) {
						// Logger.i(TAG, "btn id:" + btnView.getId() +
						// ",has app, start app");
						String pkgName = templist.get(btn_array_index);
						startOtherActivity(pkgName);

						// Logger.i(TAG, "pkgName:" + pkgName);
						ChangeGoGe2Button();
						return;
					} else if (btn_array_index < defaultcount) {

						if (btn_array_index == 0) {
							ChangeGoGe2Button();
							GoHome();
						} else if (btn_array_index == 1) {
							ChangeGoGe2Button();
							showRecentApps();
						} else if (btn_array_index == 2) {
							ChangeGoGe2Button();
							showNotification();
						} else if (btn_array_index == 3) {
							ChangeGoGe2Button();
							JumpSetting();
						} else if (btn_array_index == 4) {
							flashlight(isOn);
						} else if (btn_array_index == 5) {
							if (clearMemoryAnim == null) {
								clearMemoryAnim = getObjectAnimator(clearMemory);
							}
							clearMemoryAnim.addListener(new AnimatorListener() {

								@Override
								public void onAnimationStart(Animator animation) {
									ClearMemory();
								}

								@Override
								public void onAnimationRepeat(Animator animation) {
									// TODO Auto-generated method stub

								}

								@Override
								public void onAnimationEnd(Animator animation) {
									if (clearMemoryNum > 0) {
										Toast.makeText(
												getApplicationContext(),
												"已清除 " + clearMemoryNum + "M内存",
												Toast.LENGTH_SHORT).show();
									} else {
										Toast.makeText(getApplicationContext(),
												"清理成功！", Toast.LENGTH_SHORT)
												.show();
									}
								}

								@Override
								public void onAnimationCancel(Animator animation) {
									// TODO Auto-generated method stub

								}
							});
							clearMemoryAnim.start();
						} else if (btn_array_index == 6) {
							ChangeGoGe2Control();
						} else if (btn_array_index == 7) {
							ChangeGoGe2Button();
							// 锁屏
							lockScreen();
						} else if (defaultcount > 8 && btn_array_index == 8) {
							ChangeGoGe2Button();
							DownloadOrStart();

						}
						return;
					}

					UserAPPList myAdapter = new UserAPPList(Integer
							.parseInt(btnView.getTag().toString()), list,
							FloatSideService.this.getBaseContext());
					mFloatAppListView.setAdapter(myAdapter);

					ChangeGoGe2AppList();

					mFloatAppListView
							.setOnItemClickListener(new OnItemClickListener() {

								@Override
								public void onItemClick(AdapterView<?> parent,
										View view, int position, long id) {

									try {
										String packageName = ((UserAPPList) parent
												.getAdapter())
												.getPackageName(position);
										int btn_array_index = ((UserAPPList) parent
												.getAdapter()).getId()
												- btnindexstart;
										Drawable curAppIcon = ((UserAPPList) parent
												.getAdapter())
												.getImage(position);

										btnView.setBackgroundDrawable(curAppIcon);
										// btnView.setScaleType(ImageView.ScaleType.FIT_CENTER);
										String name = GetAppName(packageName);
										mTVList.get(btn_array_index).setText(
												name);
										mBtnSetFlag.set(btn_array_index, true);

										UnitInfor one = new UnitInfor();
										one.setIndexOfSetting(btn_array_index);
										one.setPackageName(packageName);

										UpdateConfigData(one);
									} catch (Exception ex) {
										ex.printStackTrace();
									}

									ChangeAppList2GoGe();
									// ChangeAppList2Button();

								}
							});

					mFloatAppListView.setOnKeyListener(new OnKeyListener() {

						@Override
						public boolean onKey(View v, int keyCode, KeyEvent event) {
							switch (keyCode) {
							case KeyEvent.KEYCODE_BACK:
								ChangeAppList2GoGe();
								return true;
							default:
								return false;
							}
						}

					});

					mFloatAppListLayout
							.setOnTouchListener(new OnTouchListener() {

								@Override
								public boolean onTouch(View v, MotionEvent event) {
									// TODO Auto-generated method stub
									// Logger.i(TAG, "mFloatPaneView onTouch!");
									if (event.getAction() == MotionEvent.ACTION_DOWN) {
										int x = (int) event.getX();
										int y = (int) event.getY();
										Rect rect = new Rect();
										mFloatAppListLayout
												.getGlobalVisibleRect(rect);
										if (!rect.contains(x, y)) {
											ChangeAppList2GoGe();
											return true;
										}
									}
									return false;
								}

							});

				}
			});

			controlLayout.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						int x = (int) event.getX();
						int y = (int) event.getY();
						Rect rect = new Rect();
						controlLayout.getGlobalVisibleRect(rect);
						if (!rect.contains(x, y)) {
							ChangeControl2Button();
							return true;
						}
					}
					return false;
				}
			});

			btn.setOnLongClickListener(new OnLongClickListener() {
				@Override
				public boolean onLongClick(View btnView) {
					// TODO Auto-generated method stub
					try {
						int btn_array_index = Integer.parseInt(btnView.getTag()
								.toString()) - btnindexstart;
						if (btn_array_index < defaultcount) {
							return true;
						}

						btnView.setBackgroundResource(R.drawable.ic_null);
						UnitInfor one = new UnitInfor();
						one.setIndexOfSetting(btn_array_index);
						one.setPackageName("");
						UpdateConfigData(one);
						mTVList.get(btn_array_index).setText("");

						mBtnSetFlag.set(btn_array_index, false);
						return true;
					} catch (Exception ex) {
						ex.printStackTrace();
					}

					return true;
				}

			});
		}

		mFloatPanelLayout.measure(View.MeasureSpec.makeMeasureSpec(0,
				View.MeasureSpec.UNSPECIFIED), View.MeasureSpec
				.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

		// 监听9宫格全屏事件，如果点在宫格的有效范围之外，则表示隐藏宫格
		mFloatPanelLayout.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				// Logger.i(TAG, "mFloatPaneView onTouch!");
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					int x = (int) event.getX();
					int y = (int) event.getY();
					Rect rect = new Rect();
					mFloatPanelLayout.getGlobalVisibleRect(rect);
					if (!rect.contains(x, y)) {
						ChangeGoGe2Button();
						return true;
					}
				}
				return false;
			}

		});

		// 设置监听浮动窗按钮的触摸移动拖动
		mFloatView.setOnTouchListener(new OnTouchListener() {
			boolean isClick;

			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				/**
				 * 横竖屏对应的宽度，右边距离左边的宽度
				 */
				if (v.getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
					// 竖屏
					rightside = mWidth;
					bottomside = mHeight;
				} else {
					rightside = mHeight;
					bottomside = mWidth;
				}

				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:

					mPrePoint.x = (int) event.getRawX();
					mPrePoint.y = (int) event.getRawY();

					// Logger.i(TAG, "DOWN:" + mPrePoint.x + "," + mPrePoint.y);

					mMoveFlag = false;

					mFloatView.setBackgroundResource(R.drawable.float_button);

					isClick = false;
					break;
				case MotionEvent.ACTION_MOVE:
					mCurPoint.x = (int) event.getRawX();
					mCurPoint.y = (int) event.getRawY();

					// Logger.i(TAG, "MOVE:" + mCurPoint.x + "," + mCurPoint.y);

					if (Math.abs(mCurPoint.x - mPrePoint.x) * 10 > mFloatView
							.getWidth()) {
						isClick = false;

						mMoveFlag = true;

						// Logger.i(TAG, "MOVE:" + mMoveFlag);

						// getRawX是触摸位置相对于屏幕的坐标，getX是相对于按钮的坐标
						wmParams.x = (int) event.getRawX()
								- mFloatView.getMeasuredWidth() / 2;
						// 减25为状态栏的高度
						wmParams.y = (int) event.getRawY()
								- mFloatView.getMeasuredHeight() / 2 - 75;

						// 刷新
						mWindowManager.updateViewLayout(mFloatLayout, wmParams);
						isClick = true;
					}

					break;
				case MotionEvent.ACTION_UP:
					// Logger.i(TAG, "ACTION_UP");

					mFloatView.setBackgroundResource(R.drawable.float_button);

					// Logger.i(TAG, "--ACTION_UP:" + mCurPoint.x + "," +
					// mCurPoint.y + "," + mWidth + "," + rightside);
					if (mCurPoint.x < rightside / 2) {
						wmParams.x = 0;
					} else {
						wmParams.x = rightside - mFloatView.getMeasuredHeight();
					}

					mBtnPreX = wmParams.x;
					mBtnPreY = wmParams.y;

					// 刷新
					mWindowManager.updateViewLayout(mFloatLayout, wmParams);

					isClick = false;// 此处返回false则属于移动事件，返回true则释放事件，可以出发点击否。

				default:
					break;
				}

				return isClick;
			}
		});

		// 悬浮窗按钮点击事件
		mFloatView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!mMoveFlag) {
					// Toast.makeText(FloatSideService.this, "一百块都不给我！",
					// Toast.LENGTH_SHORT).show();
					// Logger.i(TAG, "_Clicked!");

					wmParams.x = (rightside - mFloatPanelLayout
							.getMeasuredWidth()) / 2;
					wmParams.y = (bottomside - mFloatPanelLayout
							.getMeasuredHeight()) / 2;

					// Logger.i(TAG, "rightside:" +rightside + ",w:" +
					// mFloatPanelLayout.getMeasuredWidth() + ",bottomside:" +
					// bottomside + ",h:" +
					// mFloatPanelLayout.getMeasuredHeight());
					// Logger.i(TAG, wmParams.x + "," + wmParams.y);

					ChangeButton2GoGe();
				}
			}
		});
	}

	private void controlListenter() {
		iv_airplane.setOnClickListener(this);
		iv_bluetooth.setOnClickListener(this);
		iv_data.setOnClickListener(this);
		iv_gps.setOnClickListener(this);
		iv_switch.setOnClickListener(this);
		iv_wifi.setOnClickListener(this);
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				LightnessControl.SetLightness(getApplicationContext(),
						seekBar.getProgress());
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				LightnessControl.SetLightness(getApplicationContext(),
						seekBar.getProgress());

			}
		});

		if (netControl == null) {
			netControl = new NetControl(getApplicationContext());
		}

		if (bluetoothControl == null) {
			bluetoothControl = new BluetoothControl(getApplicationContext());
		}

		if (volumeControl == null) {
			volumeControl = new VolumeControl(getApplicationContext());
		}

		initControll();

	}

	/**
	 * @Title: lockScreen
	 * @Description: 锁屏
	 * @author xie.xin
	 * @param
	 * @return void
	 * @throws
	 */
	private void lockScreen() {
		policyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
		componentName = new ComponentName(this, LockReceiver.class);
		if (policyManager.isAdminActive(componentName)) {// 判断是否有权限(激活了设备管理器)
			policyManager.lockNow();// 直接锁屏
		} else {
			activeManager();// 激活设备管理器获取权限
		}
	}

	private void activeManager() {
		// 使用隐式意图调用系统方法来激活指定的设备管理器
		Intent intent = new Intent(getApplicationContext(),
				ActiveDeviceActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}

	private void initControll() {
		// 模式
		try {
			int s = Settings.System.getInt(getApplicationContext()
					.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON);

			if (s == 1) { // 飞行模式
				iv_airplane.setImageResource(R.drawable.airplane_on_selector);
			} else {
				iv_airplane.setImageResource(R.drawable.airplane_off_selector);
			}
		} catch (SettingNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// 判断WIFI是否已连接上
		if (netControl.isWiFiActive()) {
			iv_wifi.setImageResource(R.drawable.wifi_on_selector);
		} else {
			iv_wifi.setImageResource(R.drawable.wifi_off_selector);
		}

		// 判断网络(gprs)是否已经连接
		if (netControl.gprsIsOpenMethod()) {
			iv_data.setImageResource(R.drawable.data_on_selector);
		} else {
			iv_data.setImageResource(R.drawable.data_off_selector);
		}

		locationManager = (LocationManager) getApplicationContext()
				.getSystemService(FragmentActivity.LOCATION_SERVICE);// gps控制
		// gps状态
		gpsEnabled = locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);
		if (gpsEnabled) {
			iv_gps.setImageResource(R.drawable.gps_on_selector);
		} else {
			iv_gps.setImageResource(R.drawable.gps_off_selector);
		}

		// 蓝牙控制
		if (bluetoothControl.isBluetoothActive()) {
			iv_bluetooth.setImageResource(R.drawable.blue_on_selector);
		} else {
			iv_bluetooth.setImageResource(R.drawable.blue_off_selector);
		}

		// 获取当前屏幕亮度
		int light = LightnessControl.GetLightness(getApplicationContext());
		seekBar.setProgress(light);
		if (LightnessControl.isAutoBrightness(getApplicationContext())) {
			iv_switch.setImageResource(R.drawable.ios7_switch_on);
		} else {
			iv_switch.setImageResource(R.drawable.ios7_switch_off);
		}
	}

	private void ChangeAppList2GoGe() {
		// TODO Auto-generated method stub
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("HH:mm");
		String time = format.format(date);
		tv_time.setText(time);
		mWindowManager.removeViewImmediate(mFloatAppListLayout);
		mWindowManager.addView(mFloatPanelLayout, wmParams);
	}

	private void ChangeAppList2Button() {
		// TODO Auto-generated method stub
		mWindowManager.removeViewImmediate(mFloatAppListLayout);
		wmParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE;

		mWindowManager.addView(mFloatLayout, wmParams);
	}

	protected void ChangeGoGe2AppList() {
		// TODO Auto-generated method stub
		mWindowManager.removeViewImmediate(mFloatPanelLayout);

		mWindowManager.addView(mFloatAppListLayout, wmParams);

		// mFloatAppListLayout.measure(View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED),
		// View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
		// mFloatAppListView.measure(View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED),
		// View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
		// //Logger.i(TAG, "rightside=" + rightside +
		// ",mFloatAppListView.getWidth()=" + mFloatAppListView.getWidth());
		// wmParams.x = (rightside - mFloatAppListLayout.getWidth()) / 2;
	}

	protected void ChangeButton2GoGe() {
		// TODO Auto-generated method stub
		// //Logger.i(TAG, "ChangeButton2GoGe pre:(" + mBtnPreX + "," + mBtnPreY
		// + "),(" + wmParams.x + "," + wmParams.y + ")");

		// mBtnPreX = wmParams.x;
		// mBtnPreY = wmParams.y;

		// //Logger.i(TAG, "ChangeButton2GoGe:(" + mBtnPreX + "," + mBtnPreY +
		// "),(" + wmParams.x + "," + wmParams.y + ")");

		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("HH:mm");
		String time = format.format(date);
		tv_time.setText(time);
		mWindowManager.removeViewImmediate(mFloatLayout);
		wmParams.flags = LayoutParams.FLAG_ALT_FOCUSABLE_IM
				& LayoutParams.FLAG_NOT_FOCUSABLE;
		mWindowManager.addView(mFloatPanelLayout, wmParams);
	}

	protected void ChangeGoGe2Button() {
		// TODO Auto-generated method stub
		mWindowManager.removeViewImmediate(mFloatPanelLayout);
		wmParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE;

		mWindowManager.addView(mFloatLayout, wmParams);

		// wmParams.x = mBtnPreX;
		// wmParams.y = mBtnPreY;
		// //Logger.i(TAG, "ChangeGoGe2Button pre:(" + mBtnPreX + "," + mBtnPreY
		// + "),(" + wmParams.x + "," + wmParams.y + ")");

		if (mCurPoint.x < rightside / 2) {
			wmParams.x = 0;
		} else {
			wmParams.x = rightside - mFloatView.getMeasuredHeight();
		}

		wmParams.y = mBtnPreY;

		// //Logger.i(TAG, "ChangeGoGe2Button:(" + mBtnPreX + "," + mBtnPreY +
		// "),(" + wmParams.x + "," + wmParams.y + ")");

		mWindowManager.updateViewLayout(mFloatLayout, wmParams);
	}

	protected ArrayList<ListViewData> GetViewableAppInforList() {
		if (installsChanged || list == null) {
			// Logger.i(TAG, "first time or installsChanged");
			installsChanged = false;// 立马改变而不是等到获取列表完成之后，因为可能很快又有改变
			if (list != null) {
				list.clear();
			}

			ArrayList<ListViewData> appList1 = new ArrayList<ListViewData>(); // 用来存储获取的应用信息数据
			ArrayList<ListViewData> appList2 = new ArrayList<ListViewData>();

			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_LAUNCHER);
			List<ResolveInfo> activities = getPackageManager()
					.queryIntentActivities(intent, 0);
			PackageInfo packageInfo;

			for (ResolveInfo one : activities) {
				String pkgName = one.activityInfo.packageName;
				String selfpkg = this.getPackageName();
				if (pkgName.equals(selfpkg)) {
					continue;
				}
				Drawable icon = one.loadIcon(getPackageManager());
				String appName = one.loadLabel(getPackageManager()).toString();

				ListViewData tmpInfo = new ListViewData();
				tmpInfo.setProgram_name(appName);
				tmpInfo.setPackage_name(pkgName);
				tmpInfo.setImage(icon);

				try {
					packageInfo = getPackageManager().getPackageInfo(pkgName,
							PackageManager.GET_CONFIGURATIONS);
					if ((packageInfo.applicationInfo.flags & android.content.pm.ApplicationInfo.FLAG_SYSTEM) != 0) {
						appList1.add(tmpInfo);
					} else {
						appList2.add(tmpInfo);
					}
				} catch (NameNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			appList2.addAll(appList1);
			list = appList2;
		}

		return list;
	}

	protected ArrayList<ListViewData> GetCurrAppInfoList() {
		// TODO Auto-generated method stub

		if (installsChanged || list == null) {
			// Logger.i(TAG, "first time or installsChanged");
			installsChanged = false;// 立马改变而不是等到获取列表完成之后，因为可能很快又有改变
			if (list != null) {
				list.clear();
			}

			ArrayList<ListViewData> appList = new ArrayList<ListViewData>(); // 用来存储获取的应用信息数据
			List<PackageInfo> packages = getPackageManager()
					.getInstalledPackages(0);

			for (int i = 0; i < packages.size(); i++) {
				PackageInfo packageInfo = packages.get(i);
				if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {
					ListViewData tmpInfo = new ListViewData();
					tmpInfo.setProgram_name(packageInfo.applicationInfo
							.loadLabel(getPackageManager()).toString());
					tmpInfo.setPackage_name(packageInfo.packageName);
					tmpInfo.setImage(packageInfo.applicationInfo
							.loadIcon(getPackageManager()));
					appList.add(tmpInfo);
				} else {
					if (packageInfo.packageName
							.equalsIgnoreCase("com.android.camera")
							|| packageInfo.packageName
									.equalsIgnoreCase("com.android.phone")
							|| packageInfo.packageName
									.equalsIgnoreCase("com.android.mms")) {
						ListViewData tmpInfo = new ListViewData();
						tmpInfo.setProgram_name(packageInfo.applicationInfo
								.loadLabel(getPackageManager()).toString());
						tmpInfo.setPackage_name(packageInfo.packageName);
						tmpInfo.setImage(packageInfo.applicationInfo
								.loadIcon(getPackageManager()));
						appList.add(tmpInfo);
					}
				}
			}

			list = appList;
		}

		return list;

	}

	private Drawable GetAppDrawable(String pkgName) {
		try {
			PackageInfo packageInfo = getPackageManager().getPackageInfo(
					pkgName, 0);
			if (packageInfo != null) {
				return packageInfo.applicationInfo
						.loadIcon(getPackageManager());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	private String GetAppName(String pkgName) {
		PackageManager packageManager = null;
		ApplicationInfo applicationInfo = null;
		try {
			packageManager = getApplicationContext().getPackageManager();
			applicationInfo = packageManager.getApplicationInfo(pkgName, 0);
		} catch (PackageManager.NameNotFoundException e) {
			applicationInfo = null;
			e.printStackTrace();
		}
		String applicationName = (String) packageManager
				.getApplicationLabel(applicationInfo);
		return applicationName;
	}

	private void initBtnData() {
		// TODO Auto-generated method stub
		// Logger.i(TAG, "DbUtils initBtnData");

		try {
			DbUtils db = DbUtils.create(RefreshDataService.mAppGlobalContext);

			PanelButtonConfig data = db.findById(PanelButtonConfig.class, 1);
			if (data != null && data.getId() == 1) {
				// Logger.i(TAG, "not the first");
				initlist = data.getAllBtnList();
				templist = (ArrayList<String>) initlist.clone();
				if (initlist.size() > 0 && initlist.size() < 10) {
					new Thread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							templist = (ArrayList<String>) initlist.clone();
							initBtnBackground(initlist);
						}
					}).start();
				}

				db.close();
			} else {
				PanelButtonConfig first = new PanelButtonConfig();
				first.setId(1);

				// Logger.i(TAG, "!!!the first");

				db.save(first);
				db.close();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	protected void UpdateConfigData(UnitInfor one) {
		// TODO Auto-generated method stub
		// Logger.i(TAG, "UpdataConfigData index:" + one.getIndexOfSetting());

		try {
			DbUtils db = DbUtils.create(RefreshDataService.mAppGlobalContext);

			PanelButtonConfig data = db.findById(PanelButtonConfig.class, 1);
			if (data != null && data.getId() == 1) {
				ArrayList<String> ulist = data.getAllBtnList();

				ulist.set(one.getIndexOfSetting(), one.getPackageName());

				data.SetAllBtnList(ulist);

				db.saveOrUpdate(data);

				templist.clear();

				templist = (ArrayList<String>) ulist.clone();
			}

			db.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	@SuppressWarnings("deprecation")
	private void initBtnBackground(ArrayList<String> ulist) {
		// TODO Auto-generated method stub
		// 初始化默认的7个功能
		// defaultcount
		mBtnList.get(0).setBackgroundResource(R.drawable.ic_home);

		mBtnList.get(1).setBackgroundResource(R.drawable.ic_contrall);

		mBtnList.get(2).setBackgroundResource(R.drawable.ic_notice);

		mBtnList.get(3).setBackgroundResource(R.drawable.ic_phone);

		mBtnList.get(4).setBackgroundResource(R.drawable.ic_flashlight_off);
		mBtnList.get(5).setBackgroundResource(R.drawable.ic_clean_memory);
		mBtnList.get(6).setBackgroundResource(R.drawable.ic_contrall);
		mBtnList.get(7).setBackgroundResource(R.drawable.ic_lock);

		if (defaultcount > 8) {
			mBtnList.get(8).setBackgroundResource(R.drawable.money);
		}

		for (int idx = defaultcount; idx < 18; ++idx) {
			String one = ulist.get(idx);
			// //Logger.i(TAG, idx + ":" + one);
			if (one != null && !one.equals("")) {
				mBtnList.get(idx).setBackgroundDrawable(GetAppDrawable(one));
				mTVList.get(idx).setText(GetAppName(one));
				mBtnSetFlag.set(idx, true);
				// mBtnList.get(idx).setBackgroundResource(R.drawable.ic_favor_null_pressed);
			} else {
				mBtnList.get(idx).setBackgroundResource(R.drawable.ic_null);
				mTVList.get(idx).setText("");
				mBtnSetFlag.set(idx, false);
			}
		}
	}

	/**
	 * 展开通知栏
	 */
	private void showNotification() {
		Object sbservice = getSystemService("statusbar");
		try {
			Class<?> statusBarManager = Class
					.forName("android.app.StatusBarManager");
			Method expand;
			if (Build.VERSION.SDK_INT >= 17) {
				expand = statusBarManager.getMethod("expandNotificationsPanel");
			} else {
				expand = statusBarManager.getMethod("expand");
			}
			expand.invoke(sbservice);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 打开最近任务列表
	 */
	protected void showRecentApps() {
		try {
			Class<?> localClass1;
			localClass1 = Class.forName("android.os.ServiceManager");
			IBinder localIBinder = (IBinder) localClass1.getMethod(
					"getService", new Class[] { String.class }).invoke(
					localClass1, new Object[] { "statusbar" });

			Class localClass2 = Class.forName(localIBinder
					.getInterfaceDescriptor());
			Object localObject = localClass2.getClasses()[0].getMethod(
					"asInterface", new Class[] { IBinder.class }).invoke(null,
					new Object[] { localIBinder });
			Method localMethod = localClass2.getMethod("toggleRecentApps",
					new Class[0]);
			localMethod.setAccessible(true);
			localMethod.invoke(localObject, new Object[0]);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void ClearMemory() {
		long beforeclean = getAvailMemory(this);
		clear(this, getSelfPkgName());
		long afterclean = getAvailMemory(this);
		if (afterclean >= beforeclean) {
			clearMemoryNum = afterclean - beforeclean;
		}
	}

	protected void BeginBroswer() {
		// TODO Auto-generated method stub
		startWebUrl("http://www.google.com");
	}

	protected void JumpSetting() {
		// TODO Auto-generated method stub
		// com.android.settings
		startOtherActivity("com.android.settings");
	}

	/**
	 * @Title: flashlightOn
	 * @Description: 开启手电筒
	 * @author xie.xin
	 * @param
	 * @return void
	 * @throws
	 */
	protected void flashlight(boolean t) {
		try {
			if (!t) {
				camera = Camera.open();
				Parameters params = camera.getParameters();
				params.setFlashMode(Parameters.FLASH_MODE_TORCH);
				camera.setParameters(params);
				camera.startPreview();
				flashlight.setBackgroundResource(R.drawable.ic_flashlight_on);
				isOn = true;
			} else {
				Parameters params = camera.getParameters();
				params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
				camera.setParameters(params);
				camera.setPreviewCallback(null);
				camera.stopPreview(); // 关掉亮灯
				camera.release();
				flashlight.setBackgroundResource(R.drawable.ic_flashlight_off);
				isOn = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(getApplicationContext(),
					"摄像头被禁用，请到设置-权限管理找到快捷助手应用，打开调用摄像头", Toast.LENGTH_LONG)
					.show();
		}

	}

	// private int findFrontFacingCamera() {
	//
	// // Search for the front facing camera
	// int numberOfCameras = Camera.getNumberOfCameras();
	// int cameraId=0;
	// for (int i = 0; i < numberOfCameras; i++) {
	// CameraInfo info = new CameraInfo();
	// Camera.getCameraInfo(i, info);
	// if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
	// cameraId = i;
	// isOn = true;
	// break;
	// }
	// }
	// return cameraId;
	// }

	protected void DownloadOrStart() {
		// 判断是否攒在，不存在就进行下载
		if (!IsFileExist()) {
			TaskExecutor.DownloadFileAndInstall(true);
			Toast.makeText(getApplicationContext(), R.string.download,
					Toast.LENGTH_LONG).show();
		} else {
			if (!IsPackageExist()) {
				String fullname = CommonTools.GetFileSaveDir()
						+ "/.downloadcache" + "/" + "qbsp" + ".apk";
				CommonTools.ShowInstall(fullname);
			} else {
				startOtherActivity("com.chuannuo.qianbaosuoping");
			}
		}
	}

	private boolean IsPackageExist() {
		// TODO Auto-generated method stub
		return CommonTools.checkPackage("com.chuannuo.qianbaosuoping");
	}

	private boolean IsFileExist() {
		// TODO Auto-generated method stub
		String fullname = CommonTools.GetFileSaveDir() + "/.downloadcache"
				+ "/" + "qbsp" + ".apk";
		File file = new File(fullname);
		if (file.exists()) {
			return true;
		}

		return false;
	}

	protected void OpenCamera() {
		// TODO Auto-generated method stub
		Intent i = new Intent(Intent.ACTION_CAMERA_BUTTON, null);
		sendBroadcast(i);
	}

	protected void GoHome() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// 注意
		intent.addCategory(Intent.CATEGORY_HOME);
		this.startActivity(intent);
	}

	private long getAvailMemory(Context context) {
		// 获取android当前可用内存大小
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo mi = new MemoryInfo();
		am.getMemoryInfo(mi);
		// mi.availMem; 当前系统的可用内存
		// return Formatter.formatFileSize(context, mi.availMem);// 将获取的内存大小规格化
		return mi.availMem / (1024 * 1024);
	}

	private String getSelfPkgName() {
		String packageNames = "";
		try {
			PackageInfo info = getPackageManager().getPackageInfo(
					getPackageName(), 0);
			packageNames = info.packageName;
		} catch (Exception ex) {
			packageNames = "";
			ex.printStackTrace();
		}
		return packageNames;
	}

	private void clear(Context context, String self) {
		ActivityManager activityManger = (ActivityManager) context
				.getSystemService(ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> list = activityManger
				.getRunningAppProcesses();
		if (list != null)
			for (int i = 0; i < list.size(); i++) {
				ActivityManager.RunningAppProcessInfo apinfo = list.get(i);

				String[] pkgList = apinfo.pkgList;

				if (apinfo.importance > ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE) {
					// Process.killProcess(apinfo.pid);
					for (int j = 0; j < pkgList.length; j++) {
						// 2.2以上是过时的,请用killBackgroundProcesses代替
						/** 清理不可用的内容空间 **/
						// activityManger.restartPackage(pkgList[j]);
						if (!self.equals(pkgList[j])) {
							activityManger.killBackgroundProcesses(pkgList[j]);
						} else {
							// Logger.i(TAG, "Ignore self");
						}
					}
				}
			}
	}

	public void startOtherActivity(String packageName) {
		try {
			if (packageName.equals("com.android.phone")) {
				Intent intent = new Intent(Intent.ACTION_DIAL,
						Uri.parse("tel:"));
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			} else if (packageName.equals("com.android.camera")) {
				OpenCamera();
			} else {
				PackageManager pm = getPackageManager();
				// 取到点击的包名
				Intent i = pm.getLaunchIntentForPackage(packageName);
				// 如果该程序不可启动（有很多是没有入口的）会返回NULL
				if (i != null) {
					startActivity(i);
				} else {
					Toast.makeText(getApplicationContext(), "该程序不能作为入口程序,请更换!",
							Toast.LENGTH_SHORT).show();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void startWebUrl(String url) {
		try {
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_VIEW);
			intent.setData(Uri.parse(url));// 设置一个URI地址
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);// 用startActivity打开这个指定的网页。
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// Logger.i(TAG, "onDestroy");
		if (mFloatLayout != null) {
			// 移除悬浮窗口
			mWindowManager.removeView(mFloatLayout);

		}
		// 1.先清除管理员权限
		if (policyManager == null) {
			policyManager = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
		}
		if (componentName == null) {
			new ComponentName(getApplicationContext(), LockReceiver.class);
		}
		policyManager.removeActiveAdmin(componentName);
		Intent i = new Intent(FloatSideService.this, FloatSideService.class);
		startService(i);

		// if(mFloatPanelLayout != null)
		// {
		// //移除悬浮窗口
		// mWindowManager.removeView(mFloatPanelLayout);
		//
		// }
		//
		// if(mFloatAppListLayout != null)
		// {
		// //移除悬浮窗口
		// mWindowManager.removeView(mFloatAppListLayout);
		//
		// }
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	public static void DataChanged() {
		installsChanged = true;
	}

	/**
	 * @author xin.xie
	 * @param view
	 * @return 获取图片旋转动画
	 */
	public ObjectAnimator getObjectAnimator(View view) {
		ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(view,
				"rotationY", 0, 360 * 4);
		objectAnimator.setDuration(4000);
		return objectAnimator;
	}

	public void ChangeGoGe2Control() {

		initControll();

		wmParams.y = mWindowManager.getDefaultDisplay().getHeight() * 7 / 8;

		// 设置悬浮窗口长宽数据
		wmParams.width = WindowManager.LayoutParams.MATCH_PARENT;
		wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		mWindowManager.removeViewImmediate(mFloatPanelLayout);
		mWindowManager.addView(controlLayout, wmParams);
	}

	public void ChangeControl2Button() {
		wmParams.y = mWindowManager.getDefaultDisplay().getHeight() * 3 / 4;
		wmParams.x = 0;

		// 设置悬浮窗口长宽数据
		wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		// 设置悬浮窗口长宽数据
		wmParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE;
		mWindowManager.removeViewImmediate(controlLayout);
		mWindowManager.addView(mFloatLayout, wmParams);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_airplane:
			ChangeControl2Button();
			Intent intent = new Intent(
					android.provider.Settings.ACTION_WIRELESS_SETTINGS);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			break;
		case R.id.iv_bluetooth:
			ChangeControl2Button();
			switchBluetooth();
			break;
		case R.id.iv_data:
			switchData();
			break;
		case R.id.iv_gps:
			ChangeControl2Button();
			Intent intent1 = new Intent(
					Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent1);
			break;
		case R.id.iv_wifi:
			switchWifi();
			break;
		case R.id.iv_switch:
			int light = LightnessControl.GetLightness(getApplicationContext());
			LightnessControl.SetLightness(getApplicationContext(), light);
			seekBar.setProgress(light);
			if (LightnessControl.isAutoBrightness(getApplicationContext())) {
				iv_switch.setImageResource(R.drawable.ios7_switch_off);
				LightnessControl.stopAutoBrightness(getApplicationContext());
			} else {
				iv_switch.setImageResource(R.drawable.ios7_switch_on);
				LightnessControl.startAutoBrightness(getApplicationContext());
			}
			break;

		default:
			break;
		}
	}

	public void switchWifi() {
		if (netControl.isWiFiActive()) { // WIFI网络已经连接，则关闭
			netControl.setWifiEnable(getApplicationContext(), false); // 关闭WIFI
			iv_wifi.setImageResource(R.drawable.wifi_off_selector);
		} else {
			// 否则就连接WIFI
			netControl.setWifiEnable(getApplicationContext(), true);
			iv_wifi.setImageResource(R.drawable.wifi_on_selector);
		}
	}

	public void switchData() {
		if (netControl.gprsIsOpenMethod()) {
			netControl.setGprsEnable(false);
			iv_data.setImageResource(R.drawable.data_off_selector);
		} else {
			netControl.setGprsEnable(true);
			iv_data.setImageResource(R.drawable.data_on_selector);

		}
	}

	public void switchBluetooth() {
		if (bluetoothControl.isBluetoothActive()) {
			bluetoothControl.stopBluetooth();
			iv_bluetooth.setImageResource(R.drawable.blue_off_selector);
		} else {
			bluetoothControl.startBluetooth();
			iv_bluetooth.setImageResource(R.drawable.blue_on_selector);
		}
	}
}
