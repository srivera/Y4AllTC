package ec.com.yacare.y4all.activities;

import android.app.Activity;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.support.multidex.MultiDexApplication;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import org.apache.http.Header;

import java.io.Serializable;
import java.util.List;

import ec.com.yacare.y4all.activities.evento.EventosActivity;
import ec.com.yacare.y4all.activities.focos.ProgramacionActivity;
import ec.com.yacare.y4all.activities.principal.Y4HomeActivity;
import ec.com.yacare.y4all.activities.socket.MonitorIOActivity;
import ec.com.yacare.y4all.lib.dto.Cuenta;
import ec.com.yacare.y4all.lib.dto.Equipo;
import io.xlink.wifi.pipe.Constant;
import io.xlink.wifi.pipe.bean.Device;
import io.xlink.wifi.pipe.http.HttpManage;
import io.xlink.wifi.pipe.manage.DeviceManage;
import io.xlink.wifi.pipe.util.CrashHandler;
import io.xlink.wifi.pipe.util.SharedPreferencesUtil;
import io.xlink.wifi.pipe.util.XlinkUtils;
import io.xlink.wifi.sdk.XDevice;
import io.xlink.wifi.sdk.XlinkAgent;
import io.xlink.wifi.sdk.XlinkCode;
import io.xlink.wifi.sdk.bean.EventNotify;
import io.xlink.wifi.sdk.listener.XlinkNetListener;
import io.xlink.wifi.sdk.util.MyLog;

public class DatosAplicacion extends MultiDexApplication  implements XlinkNetListener{

	private Equipo equipoSeleccionado;
	
	private Cuenta cuenta;
	
	private String token;
	
	private String regId;

	
	private Y4HomeActivity y4HomeActivity;

	private MonitorIOActivity monitorIOActivity;

	private EventosActivity eventosActivity;

//	private Dispositivo dispositivoChatActual;
//
//	private Dispositivo dispositivoOrigen;

	private ProgramacionActivity programacionActivity;

	private Boolean porteroInstalado = false;

	
	public Equipo getEquipoSeleccionado() {
		return equipoSeleccionado;
	}
	public void setEquipoSeleccionado(Equipo equipoSeleccionado) {
		this.equipoSeleccionado = equipoSeleccionado;
	}
	public Cuenta getCuenta() {
		return cuenta;
	}
	public void setCuenta(Cuenta cuenta) {
		this.cuenta = cuenta;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getRegId() {
		return regId;
	}
	public void setRegId(String regId) {
		this.regId = regId;
	}

	public Y4HomeActivity getY4HomeActivity() {
		return y4HomeActivity;
	}

	public void setY4HomeActivity(Y4HomeActivity y4HomeActivity) {
		this.y4HomeActivity = y4HomeActivity;
	}


//	public Dispositivo getDispositivoChatActual() {
//		return dispositivoChatActual;
//	}
//
//	public void setDispositivoChatActual(Dispositivo dispositivoChatActual) {
//		this.dispositivoChatActual = dispositivoChatActual;
//	}
//
//	public Dispositivo getDispositivoOrigen() {
//		return dispositivoOrigen;
//	}
//
//	public void setDispositivoOrigen(Dispositivo dispositivoOrigen) {
//		this.dispositivoOrigen = dispositivoOrigen;
//	}

//	public ChatActivity getChatActivity() {
//		return chatActivity;
//	}
//
//	public void setChatActivity(ChatActivity chatActivity) {
//		this.chatActivity = chatActivity;
//	}

	public MonitorIOActivity getMonitorIOActivity() {
		return monitorIOActivity;
	}

	public void setMonitorIOActivity(MonitorIOActivity monitorIOActivity) {
		this.monitorIOActivity = monitorIOActivity;
	}

//	public AdministrarZonasActivity getAdministrarZonasActivity() {
//		return administrarZonasActivity;
//	}
//
//	public void setAdministrarZonasActivity(AdministrarZonasActivity administrarZonasActivity) {
//		this.administrarZonasActivity = administrarZonasActivity;
//	}

//	public AdministrarRoutersActivity getAdministrarRoutersActivity() {
//		return administrarRoutersActivity;
//	}
//
//	public void setAdministrarRoutersActivity(AdministrarRoutersActivity administrarRoutersActivity) {
//		this.administrarRoutersActivity = administrarRoutersActivity;
//	}

	public ProgramacionActivity getProgramacionActivity() {
		return programacionActivity;
	}

	public void setProgramacionActivity(ProgramacionActivity programacionActivity) {
		this.programacionActivity = programacionActivity;
	}

	public Boolean getPorteroInstalado() {
		return porteroInstalado;
	}

	public void setPorteroInstalado(Boolean porteroInstalado) {
		this.porteroInstalado = porteroInstalado;
	}

	public EventosActivity getEventosActivity() {
		return eventosActivity;
	}

	public void setEventosActivity(EventosActivity eventosActivity) {
		this.eventosActivity = eventosActivity;
	}



	private static final String TAG = "MyApp";
	private static DatosAplicacion application;

	/**
	 * 首选项设置
	 */
	public static SharedPreferences sharedPreferences;
	// 判断程序是否正常启动
	public boolean auth;

	@Override
	public void onCreate() {
		super.onCreate();
		application = this;
		auth = false;
		Log.e(TAG, "onCreate()");
		// bug收集
		CrashHandler.init(this);
		// 初始化sdk
		XlinkAgent.init(this);
//        XlinkAgent.setCMServer("cm.iotbull.com", 23778);
//        XlinkAgent.setCMServer("42.121.122.23", 23778);//测试服务器地址
//        XlinkAgent.setCMServer("114.55.119.222",23778);
		XlinkAgent.setCMServer("cm2.xlink.cn", 23778);//正式平台地址
		XlinkAgent.getInstance().addXlinkListener(this);
		//优先内网连接(谨慎使用,如果优先内网,则外网会在内网连接成功或者失败,或者超时后再进行连接,可能会比较慢)
//        XlinkAgent.getInstance().setPreInnerServiceMode(true);
		// 首选项 用于存储用户
		sharedPreferences = getSharedPreferences("XlinkOfficiaDemo", Context.MODE_PRIVATE);
		appid = SharedPreferencesUtil.queryIntValue(Constant.SAVE_appId);
		authKey = SharedPreferencesUtil.queryValue(Constant.SAVE_authKey, "");
		String prodctid = SharedPreferencesUtil.queryValue(Constant.SAVE_PRODUCTID);
		String compayId = SharedPreferencesUtil.queryValue(Constant.SAVE_COMPANY_ID);

		if (!TextUtils.isEmpty(prodctid)) {
			Constant.PRODUCTID = prodctid.replace(" ", "");
		}
		if (!TextUtils.isEmpty(compayId)) {
			HttpManage.COMPANY_ID = compayId.replace(" ", "");
		}
		// if (prodctid.equals("")) {
		// SharedPreferencesUtil.keepShared("pid", Constant.PRODUCTID);
		// } else if (prodctid.length() > 30) {
		// Constant.PRODUCTID = prodctid;
		// }
		// Constant.PRODUCTID= Constant.PRODUCTID.trim();
		// Constant.PRODUCTID=Constant.PRODUCTID.replace(" ", "");
		initHandler();
		for (Device device : DeviceManage.getInstance().getDevices()) {// 向sdk初始化设备
			MyLog.e(TAG, "init device:" + device.getMacAddress());
			XlinkAgent.getInstance().initDevice(device.getXDevice());
		}

		// 获取当前软件包版本号和版本名称
		try {
			PackageInfo pinfo = getPackageManager().getPackageInfo(
					getPackageName(), 0);
			versionCode = pinfo.versionCode;
			versionName = pinfo.versionName;
			packageName = pinfo.packageName;

		} catch (PackageManager.NameNotFoundException e) {
		}

		HttpManage.COMPANY_ID="1007d2ada2c7a200";
		SharedPreferencesUtil.keepShared(Constant.SAVE_COMPANY_ID, "1007d2ada2c7a200");
	//	SharedPreferencesUtil.keepShared(Constant.SAVE_EMAIL_ID, "");
		SharedPreferencesUtil.keepShared(Constant.SAVE_PRODUCTID, "985b157bf6fc43368a63467ea3b19d0d");

		//Luces internet
		String userName = SharedPreferencesUtil.queryValue(Constant.SAVE_EMAIL_ID);
		if (userName == null || userName.equals("")) {
			userName = getLocalMac();
			Log.d("debug", "wifi mac = " + getLocalMac());
			if (userName == null || userName.equals("020000000000")) {
				userName = "";
				for (int i = 0; i < 10; i++) {
					userName = new StringBuilder(String.valueOf(userName)).append(String.valueOf((int) (Math.random() * 10.0d))).toString();
				}
				Log.d("debug", "wifi mac Rand = " + userName);
			}
			SharedPreferencesUtil.keepShared(Constant.SAVE_EMAIL_ID, new StringBuilder(String.valueOf(userName)).append("@futlight.com").toString());
			SharedPreferencesUtil.keepShared(Constant.SAVE_PASSWORD_ID, "123456789");
			DatosAplicacion.getApp().auth = true;
			appid = SharedPreferencesUtil.queryIntValue("appId");
			authKey = SharedPreferencesUtil.queryValue("authKey", "");
			//if (!isHaveAppid()) {
				registerUser();

			//}
		} else {
			Log.d("debug", "wifi mac old = " + userName);
		}
	}

	public void registerUser() {
//		tm = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
//		id = XlinkUtils.MD5(tm.getDeviceId());
		registerUserByMail(SharedPreferencesUtil.queryValue(Constant.SAVE_EMAIL_ID), "Tablet", SharedPreferencesUtil.queryValue(Constant.SAVE_PASSWORD_ID));
	}

	public void registerUserByMail(final String uid, final String name, final String pwd) {
		HttpManage.getInstance().registerUserByMail(uid, name, pwd, new HttpManage.ResultCallback<String>() {
			@Override
			public void onError(Header[] headers, HttpManage.Error error) {
				//loginUser(uid, pwd);

			}

			@Override
			public void onSuccess(int code, String response) {
				//loginUser(uid, pwd);
			}
		});
	}

	public boolean isHaveAppid() {
		if (appid == 0 || authKey.equals("")) {
			return false;
		}
		return true;
	}
	public String getLocalMac() {
		String mac = "";
		return ((WifiManager) getSystemService(Context.WIFI_SERVICE)).getConnectionInfo().getMacAddress().replace(":", "");
	}


	public String versionName;
	public int versionCode;
	public String packageName;
	private static Handler mainHandler = null;
	private String accessToken;

	public static void initHandler() {
		mainHandler = new Handler();
	}

	/**
	 * 执行在主线程任务
	 *
	 * @param runnable
	 */
	public static void postToMainThread(Runnable runnable) {
		mainHandler.post(runnable);
	}

	public static DatosAplicacion getApp() {
		return application;
	}

	// 全局登录的 appId 和auth
	public int appid;
	public String authKey;

	public void setAppid(int id) {
		appid = id;
	}

	public void setAuth(String auth) {
		this.authKey = auth;
	}

	public int getAppid() {
		return appid;
	}


	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getAccessToken() {
		return accessToken;
	}


	public String getAuth() {
		return authKey;
	}

	// 当前的activity
	private Activity currentActivity;

	public Activity getCurrentActivity() {
		return currentActivity;
	}

	public void setCurrentActivity(Activity currentActivity) {
		this.currentActivity = currentActivity;
	}

	// xlink 回调的onStart
	@Override
	public void onStart(int code) {
		// TODO Auto-generated method stub
		Log.e(TAG, "onStart code" + code);
		sendBroad(Constant.BROADCAST_ON_START, code);
	}

	// 回调登录xlink状态
	@Override
	public void onLogin(int code) {
		// TODO Auto-generated method stub
		Log.e(TAG, "login code" + code);
		sendBroad(Constant.BROADCAST_ON_LOGIN, code);
		if (code == XlinkCode.SUCCEED) {
			//XlinkUtils.shortTips("云端网络已可用");
		} else if (code == XlinkCode.CLOUD_CONNECT_NO_NETWORK
				|| XlinkUtils.isConnected()) {
			// XlinkUtils.shortTips("网络不可用，请检查网络连接");

		} else {
			//XlinkUtils.shortTips("连接到服务器失败，请检查网络连接");
		}
	}

	@Override
	public void onLocalDisconnect(int code) {
		if (code == XlinkCode.LOCAL_SERVICE_KILL) {
			// 这里是xlink服务被异常终结了（第三方清理软件，或者进入应用管理被强制停止应用/服务）
			// 永不结束的service
			// 除非调用 XlinkAgent.getInstance().stop（）;
			XlinkAgent.getInstance().start();
		}
	//	XlinkUtils.shortTips("本地网络已经断开");
	}


	@Override
	public void onDisconnect(int code) {
		if (code == XlinkCode.CLOUD_SERVICE_KILL) {
			// 这里是服务被异常终结了（第三方清理软件，或者进入应用管理被强制停止服务）
			if (appid != 0 && !TextUtils.isEmpty(authKey)) {
				XlinkAgent.getInstance().login(appid, authKey);
			}
		}
		//XlinkUtils.shortTips("正在修复云端连接");
	}

	/**
	 * 收到 局域网设备推送的pipe数据
	 */
	@Override
	public void onRecvPipeData(short messageId,XDevice xdevice,  byte[] data) {
		// TODO Auto-generated method stub
		Log.e(TAG, "onRecvPipeData::device:" + xdevice.toString() + "data:"
				+ data);
		Device device = DeviceManage.getInstance().getDevice(
				xdevice.getMacAddress());
		if (device != null) {
			// 发送广播，那个activity需要该数据可以监听广播，并获取数据，然后进行响应的处理
			sendPipeBroad(Constant.BROADCAST_RECVPIPE, device, data);
			// TimerManage.getInstance().parseByte(device,data);
		}
	}

	/**
	 * 收到设备通过云端服务器推送的pipe数据
	 */
	@Override
	public void onRecvPipeSyncData(short messageId,XDevice xdevice,  byte[] data) {
		// TODO Auto-generated method stub
		Log.e(TAG, "onRecvPipeSyncData::device:" + xdevice.toString() + "data:"
				+ data);
		Device device = DeviceManage.getInstance().getDevice(
				xdevice.getMacAddress());
		if (device != null) {
			// 发送广播，那个activity需要该数据可以监听广播，并获取数据，然后进行响应的处理
			// TimerManage.getInstance().parseByte(device,data);
			sendPipeBroad(Constant.BROADCAST_RECVPIPE_SYNC, device, data);
		}
	}

	/**
	 */
	public void sendBroad(String action, int code) {
		Intent intent = new Intent(action);
		intent.putExtra(Constant.STATUS, code);
		DatosAplicacion.this.sendBroadcast(intent);
	}

	/**
	 */
	public void sendPipeBroad(String action, Device device, byte[] data) {
		Intent intent = new Intent(action);
		intent.putExtra(Constant.DEVICE_MAC, device.getMacAddress());
		if (data != null) {
			intent.putExtra(Constant.DATA, data);
		}
		DatosAplicacion.this.sendBroadcast(intent);
	}

	/**
	 * 设备状态改变：掉线/重连/在线
	 */
	@Override
	public void onDeviceStateChanged(XDevice xdevice, int state) {
		// TODO Auto-generated method stub

		Log.e(TAG, "onDeviceStateChanged::" + xdevice.getMacAddress()
				+ " state:" + state);
		Device device = DeviceManage.getInstance().getDevice(
				xdevice.getMacAddress());
		if (device != null) {
			device.setxDevice(xdevice);
			Intent intent = new Intent(Constant.BROADCAST_DEVICE_CHANGED);
			intent.putExtra(Constant.DEVICE_MAC, device.getMacAddress());
			intent.putExtra(Constant.STATUS, state);
			DatosAplicacion.getApp().sendBroadcast(intent);
		}

	}

	@Override
	public void onDataPointUpdate(XDevice xDevice, List<io.xlink.wifi.sdk.bean.DataPoint> dataPionts, int i) {
				Log.e(TAG,"onDataPointUpdate:"+dataPionts.toString());

		Device device = DeviceManage.getInstance().getDevice(xDevice.getMacAddress());
		if (device != null) {
			Intent intent = new Intent(Constant.BROADCAST_DATAPOINT_RECV);
			intent.putExtra(Constant.DEVICE_MAC, device.getMacAddress());
			if (dataPionts != null) {
				intent.putExtra(Constant.DATA, (Serializable) dataPionts);
			}
			DatosAplicacion.this.sendBroadcast(intent);
		}
	}

//	@Override
//	public void onDataPointUpdate(XDevice xDevice, List<DataPoint> dataPionts, int channel) {
//		Log.e(TAG,"onDataPointUpdate:"+dataPionts.toString());
//
//		Device device = DeviceManage.getInstance().getDevice(xDevice.getMacAddress());
//		if (device != null) {
//			Intent intent = new Intent(Constant.BROADCAST_DATAPOINT_RECV);
//			intent.putExtra(Constant.DEVICE_MAC, device.getMacAddress());
//			if (dataPionts != null) {
//				intent.putExtra(Constant.DATA, (Serializable) dataPionts);
//			}
//			DatosAplicacion.this.sendBroadcast(intent);
//		}
//	}

	@Override
	public void onEventNotify(EventNotify eventNotify) {
		String str = "EventNotify{" +
				"notyfyFlags=" + eventNotify.notyfyFlags +
				", formId=" + eventNotify.formId +
				", messageId=" + eventNotify.messageId +
				", messageType=" + eventNotify.messageType +
				", notifyData=" +new String(eventNotify.notifyData) +
				'}';

		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(DatosAplicacion.getApp());

		mBuilder.setWhen(System.currentTimeMillis())// 通知产生的时间，会在通知信息里显示
				.setPriority(Notification.PRIORITY_DEFAULT)// 设置该通知优先级
				.setDefaults(Notification.DEFAULT_SOUND)
				.setOngoing(false)//不是正在进行的   true为正在进行  效果和.flag一样
				.setSmallIcon(R.drawable.ic_launcher)
				.setContentText(str);
//		Notification notify = mBuilder.build();
//		Intent intent=new Intent(this, NotifyEventInfoActivity.class);
//		intent.putExtra(NotifyEventInfoActivity.NOTIFY_BUNDLE,eventNotify);
//		PendingIntent pendingIntent=PendingIntent.getActivities(getApplicationContext(),0,new Intent[]{intent},0);
//		notify.contentIntent=pendingIntent;
//
//		notify.flags = Notification.FLAG_AUTO_CANCEL;
//		NotificationManager mNotificationManager = (NotificationManager) MyApp.getApp().getSystemService(Context.NOTIFICATION_SERVICE);
//		mNotificationManager.notify(new Random().nextInt(), notify);
//		XlinkUtils.longTips(str);
	}

}
