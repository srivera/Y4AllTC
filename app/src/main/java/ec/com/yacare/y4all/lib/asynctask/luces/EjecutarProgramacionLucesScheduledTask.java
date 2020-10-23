package ec.com.yacare.y4all.lib.asynctask.luces;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.apache.http.Header;

import java.util.Map;

import ec.com.yacare.y4all.activities.DatosAplicacion;
import ec.com.yacare.y4all.lib.asynctask.io.ComandoIOFocoScheduledTask;
import ec.com.yacare.y4all.lib.focos.ComandoFoco;
import ec.com.yacare.y4all.lib.resources.YACSmartProperties;
import io.xlink.wifi.pipe.Constant;
import io.xlink.wifi.pipe.bean.Device;
import io.xlink.wifi.pipe.http.HttpManageSync;
import io.xlink.wifi.pipe.manage.CmdManage;
import io.xlink.wifi.pipe.manage.DeviceManage;
import io.xlink.wifi.pipe.util.SharedPreferencesUtil;
import io.xlink.wifi.sdk.XDevice;
import io.xlink.wifi.sdk.XlinkAgent;
import io.xlink.wifi.sdk.XlinkCode;
import io.xlink.wifi.sdk.listener.ConnectDeviceListener;
import io.xlink.wifi.sdk.listener.GetSubscribeKeyListener;
import io.xlink.wifi.sdk.listener.ScanDeviceListener;
import io.xlink.wifi.sdk.listener.SubscribeDeviceListener;


public class EjecutarProgramacionLucesScheduledTask extends Thread {


	Context context;
	String zona;
	String idRouter;
	public Device device;
	String comandoPendiente;


	public EjecutarProgramacionLucesScheduledTask(Context context, String zona, String idRouter, String comandoPendiente) {
		super();
		this.idRouter = idRouter;
		this.context = context;
		this.zona = zona;
		this.comandoPendiente = comandoPendiente;
	}


	@Override
	public void run() {
		Log.i("PUSH LUCES", "INGRESAR ");
		if (!XlinkAgent.getInstance().isConnectedLocal()) {
			XlinkAgent.getInstance().start();
		}
		if (!XlinkAgent.getInstance().isConnectedOuterNet()) {
			XlinkAgent.getInstance().login(DatosAplicacion.getApp().getAppid(), DatosAplicacion.getApp().getAuth());
		}

		SharedPreferences sharedPrefsLuces = PreferenceManager.getDefaultSharedPreferences(context);
		String registerEmail = sharedPrefsLuces.getString("prefRegisterEmail", "0");
		if(registerEmail.equals("0")) {
			loginUser("8438387D9364@futlight.com", SharedPreferencesUtil.queryValue(Constant.SAVE_PASSWORD_ID));
		}else{
			loginUser(SharedPreferencesUtil.queryValue(Constant.SAVE_EMAIL_ID), SharedPreferencesUtil.queryValue(Constant.SAVE_PASSWORD_ID));
		}

		//loginUser(SharedPreferencesUtil.queryValue(Constant.SAVE_EMAIL_ID), SharedPreferencesUtil.queryValue(Constant.SAVE_PASSWORD_ID));


	}


	public void loginUser(final String user, final String pwd) {
		HttpManageSync.getInstance().login(user, pwd, new HttpManageSync.ResultCallbackSync<Map<String, Object>>() {
			@Override
			public void onError(Header[] headers, HttpManageSync.ErrorSync error) {
				Log.i("PUSH LUCES", "ERROR LOGIN ");
			}

			@Override
			public void onSuccess(int code, Map<String, Object> response) {
				String authKey = (String) response.get("authorize");
				String accessToken = (String) response.get("access_token");
				int appid = ((Double) response.get("user_id")).intValue();
				SharedPreferencesUtil.keepShared("appId", appid);
				SharedPreferencesUtil.keepShared("authKey", authKey);
				DatosAplicacion.getApp().setAccessToken(accessToken);
				DatosAplicacion.getApp().setAppid(appid);
				DatosAplicacion.getApp().setAuth(authKey);
				DatosAplicacion.getApp().auth = true;
				//if (!XlinkAgent.getInstance().isConnectedLocal()) {
					XlinkAgent.getInstance().start();
				//}
				//if (!XlinkAgent.getInstance().isConnectedOuterNet()) {
					XlinkAgent.getInstance().login(DatosAplicacion.getApp().getAppid(), DatosAplicacion.getApp().getAuth());
				//}

				for(Device device1 : DeviceManage.getInstance().getDevices()){
					if(device1.getMacAddress().equals(((DatosAplicacion)context).getEquipoSeleccionado().getNumeroSerie())){
						((DatosAplicacion)context).getEquipoSeleccionado().setDevice(device1);
						device = device1;
						Log.i("PUSH LUCES", "login ok 0");
					}
				}
				Log.i("PUSH LUCES", "login ok ");
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				int ret = XlinkAgent.getInstance().scanDeviceByProductId(
						Constant.PRODUCTID, scanListener);
				Log.i("PUSH LUCES", "login ok " + ret);
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				/*
				ret = XlinkAgent.getInstance().scanDeviceByProductId(
						Constant.PRODUCTID, scanListener);
				Log.i("PUSH LUCES", "login ok " + ret);
				try {
					Thread.sleep(8000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				ret = XlinkAgent.getInstance().scanDeviceByProductId(
						Constant.PRODUCTID, scanListener);
				try {
					Thread.sleep(8000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Log.i("PUSH LUCES", "login ok " + ret);*/

				/*if(ret == -10){
					try {
						Thread.sleep(8000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				//	ret = XlinkAgent.getInstance().scanDeviceByProductId(
				//			Constant.PRODUCTID, scanListener);
				//	Log.i("PUSH LUCES", "login ok 2" + ret);
					connectDevice();
					/*try {
						Thread.sleep(8000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					connectDevice();
				}else {*/
					connectDevice();
				//}

			}
		});
	}

	private ScanDeviceListener scanListener = new ScanDeviceListener() {

		@Override
		public void onGotDeviceByScan(XDevice device2) {
			//XlinkUtils.shortTips("扫描到设备:" + device.getMacAddress());
			final Device dev = new Device(device2);
			Log.i("PUSH LUCES", "scan listener 1");
			if (device2.getAccessKey() > 0) {
				dev.setAccessKey(device2.getAccessKey());
				Log.i("PUSH LUCES", "scan listener 2");
			}
			DeviceManage.getInstance().addDevice(dev);

			for(Device device1 : DeviceManage.getInstance().getDevices()){
				Log.i("PUSH LUCES", "scan listener 3");
				if(device1.getMacAddress().equals(idRouter)){
					device =device1;
					((DatosAplicacion)context).getEquipoSeleccionado().setDevice(device1);
					Log.i("PUSH LUCES", "scan listener ");
				}
			}
		}
	};

	int intentos = 0;
	public void connectDevice() {

		if (device != null && device.getXDevice() != null) {
			Log.i("PUSH LUCES", "connectDevice 1 ");
			if (device != null && device.getXDevice() != null && device.getXDevice().getVersion() >= 3 && device.getXDevice().getSubKey() <= 0) {
				XlinkAgent.getInstance().getInstance().getDeviceSubscribeKey(device.getXDevice(), device.getXDevice().getAccessKey(), new GetSubscribeKeyListener() {
					@Override
					public void onGetSubscribekey(XDevice xdevice, int code, int subKey) {
						device.getXDevice().setSubKey(subKey);
						DeviceManage.getInstance().updateDevice(device);
						Log.i("PUSH LUCES", "connectDevice 1-1 ");
					}
				});
			}
			if (device != null && !device.isSubscribe()) {
				XlinkAgent.getInstance().subscribeDevice(device.getXDevice(), device.getXDevice().getSubKey(), new SubscribeDeviceListener() {
					@Override
					public void onSubscribeDevice(XDevice xdevice, int code) {
						if (code == XlinkCode.SUCCEED) {
							device.setSubscribe(true);
							Log.i("PUSH LUCES", "connectDevice 1-2 ");
						}
					}
				});
			}

			intentos++;
			int ret = XlinkAgent.getInstance().connectDevice(device.getXDevice(), device.getXDevice().getAccessKey(), device.getXDevice().getSubKey(), connectDeviceListener);

			try {
				Thread.sleep(8000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			Log.i("PUSH LUCES ", "connectDevice ret " + ret);
			if (ret < 0) {
				//Debe hacer por socket
				Log.i("PUSH LUCES", "connectDevice 2 ");
				String comandoSocket;
				String[] com = comandoPendiente.split(";");
				int deviceId = device.getXDevice().getDeviceId();
				if (device.getXDevice().getDeviceId() == 0) {
					SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
					deviceId = sharedPrefs.getInt(idRouter, 0);
				}
				if (deviceId != 0) {
					if (comandoPendiente != null && !comandoPendiente.equals("")) {
						if (comandoPendiente.startsWith(YACSmartProperties.COM_APAGAR_LUZ_WIFI)) {
							//apagar
							comandoSocket = YACSmartProperties.COM_APAGAR_LUZ_WIFI + ";" + deviceId + ";" + idRouter + ";ANDROID;" + com[0] + ";";
						} else {
							//encender
							comandoSocket = YACSmartProperties.COM_ENCENDER_LUZ_WIFI + ";" + deviceId + ";" + idRouter + ";ANDROID;" + com[0] + ";";
						}
						ComandoIOFocoScheduledTask comandoIOFocoScheduledTask = new ComandoIOFocoScheduledTask(comandoSocket);
						comandoIOFocoScheduledTask.start();

					}
				}
			}else{
				//Ejecutar comando
				Log.i("PUSH LUCES", "connectDevice 3 ");
				String comandoSocket = comandoPendiente + ";" + "encender" + ";" + "ANDROID" + ";" + idRouter + ";" + zona + ";"+ idRouter + ";" ;
				ComandoFoco comandoFoco = new ComandoFoco(comandoSocket, context);
				comandoFoco.start();
			}
		}
	}

	Boolean mobile = false;
	private ConnectDeviceListener connectDeviceListener = new ConnectDeviceListener() {

		@Override
		public void onConnectDevice(XDevice xDevice, int result) {
			String pass;
			String tips;

//			if(Connectivity.isConnectedMobile(getApplicationContext())) {
//				txtEstado.setText("esta conexión le permite SOLO\n encender o apagar las luces.");
//				nombreZonaT.setText("Internet");
//				mobile = true;
//			}else{
//				txtEstado.setText("esta conexión le permite SOLO\n encender o apagar las luces.");

//			}

			Log.i("PUSH LUCES", "estado " + result);
			String comandoSocket = comandoPendiente + ";" + "encender" + ";" + "ANDROID" + ";" + idRouter + ";" + zona + ";"+ idRouter + ";" ;
			Log.i("PUSH LUCES", "connectDeviceListener 1 " + comandoSocket);
			switch (result) {
				case XlinkCode.DEVICE_STATE_LOCAL_LINK:
					if(CmdManage.sesionFocos == null) {
						pass = Integer.toHexString(xDevice.getSessionId());
						CmdManage.sesionFocos = hexStringToByteArray(pass);

					}

					ComandoFoco comandoFoco1 = new ComandoFoco(comandoSocket, context);
					comandoFoco1.start();
					Log.i("PUSH LUCES", "connectDeviceListener 1.1 " + CmdManage.sesionFocos);
					DeviceManage.getInstance().updateDevice(xDevice);
					XlinkAgent.getInstance().sendProbe(xDevice);
					break;
				case XlinkCode.DEVICE_STATE_OUTER_LINK:

					if(CmdManage.sesionFocos == null) {
						pass = Integer.toHexString(xDevice.getSessionId());
						CmdManage.sesionFocos = hexStringToByteArray(pass);

						Log.i("PUSH LUCES", "connectDeviceListener 1.2 " + CmdManage.sesionFocos);
					}
					ComandoFoco comandoFoco = new ComandoFoco(comandoSocket, context);
					comandoFoco.start();
					DeviceManage.getInstance().updateDevice(xDevice);
					DeviceManage.getInstance().addDevice(xDevice);
					break;
				case XlinkCode.CONNECT_DEVICE_INVALID_KEY:
					if(intentos < 3){
						connectDevice();
					}else{
					}
					Log.i("PUSH LUCES", "connectDeviceListener 1.3 ");
					break;
				case XlinkCode.CONNECT_DEVICE_OFFLINE:
					if(intentos < 3){
						connectDevice();
					}else{

					}
					Log.i("PUSH LUCES", "connectDeviceListener 1.4 ");
					break;
				case XlinkCode.CONNECT_DEVICE_TIMEOUT:
					if(intentos < 3){
						connectDevice();
					}else{

					}
					Log.i("PUSH LUCES", "connectDeviceListener 1.5 ");
					break;

				case XlinkCode.CONNECT_DEVICE_SERVER_ERROR:
					if(intentos < 3){
						connectDevice();
					}else{

					}
					Log.i("PUSH LUCES", "connectDeviceListener 1.6 ");
					break;
				case XlinkCode.CONNECT_DEVICE_OFFLINE_NO_LOGIN:
					if(intentos < 3){
						connectDevice();
					}else{

					}
					Log.i("PUSH LUCES", "connectDeviceListener 1.7 ");
					break;
				default:
					//	XlinkUtils.shortTips("Otro error:" + result);
					break;
			}
			intentos++;
			Log.i("PUSH LUCES", "connectDeviceListener 2 ");

		}
	};

	public static byte[] hexStringToByteArray(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
					+ Character.digit(s.charAt(i+1), 16));
		}
		return data;
	}
}
