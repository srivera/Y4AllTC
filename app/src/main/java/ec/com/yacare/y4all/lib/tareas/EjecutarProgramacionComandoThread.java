package ec.com.yacare.y4all.lib.tareas;

import android.app.Activity;

import java.net.DatagramPacket;

import ec.com.yacare.y4all.activities.DatosAplicacion;
import ec.com.yacare.y4all.activities.respuesta.AdministrarRespuestasActivity;
import ec.com.yacare.y4all.activities.respuesta.NuevaRespuestaActivity;
import ec.com.yacare.y4all.activities.socket.MonitorIOActivity;
import ec.com.yacare.y4all.lib.dto.Respuesta;

public class EjecutarProgramacionComandoThread extends Thread  {

	private Activity activity;
	private String comando;
	private Respuesta respuesta;
	private MonitorIOActivity monitorActivity;
	private AdministrarRespuestasActivity administrarRespuestasActivity;

	private String ipEquipoInternet;
	private Integer puertoComandoEnviar;
	private NuevaRespuestaActivity nuevaRespuestaActivity;

	private DatosAplicacion datosAplicacion;

	public EjecutarProgramacionComandoThread(Activity activity, String comando, Respuesta respuesta, MonitorIOActivity monitorActivity, AdministrarRespuestasActivity administrarRespuestasActivity, String ipEquipoInternet,
											 Integer puertoComandoEnviar, NuevaRespuestaActivity nuevaRespuestaActivity) {
		super();
		this.activity = activity;
		this.comando = comando;
		this.respuesta = respuesta;
		this.monitorActivity = monitorActivity;
		this.administrarRespuestasActivity = administrarRespuestasActivity;
		this.ipEquipoInternet = ipEquipoInternet;
		this.puertoComandoEnviar = puertoComandoEnviar;
		this.nuevaRespuestaActivity = nuevaRespuestaActivity;
	}

	String comandoInicial;
	boolean vaciar = false;

	byte[] video;
	DatagramPacket receivePacketA;

	@Override
	public void run() {

//		DatosAplicacion.getApp().setAppid(appid);
//		DatosAplicacion.getApp().setAuth(authKey);
//
//
//		if (!XlinkAgent.getInstance().isConnectedLocal()) {
//			XlinkAgent.getInstance().start();
//		}
//		if (!XlinkAgent.getInstance().isConnectedOuterNet()) {
//			XlinkAgent.getInstance().login(DatosAplicacion.getApp().getAppid(), DatosAplicacion.getApp().getAuth());
//		}
//
//
//		initWidget();
//
//		loginUser(SharedPreferencesUtil.queryValue(Constant.SAVE_EMAIL_ID), SharedPreferencesUtil.queryValue(Constant.SAVE_PASSWORD_ID));
//	}
//
//	public void initWidget() {
//		for(Device device1 : DeviceManage.getInstance().getDevices()){
//			if(device1.getMacAddress().equals(equipoSeleccionado.getNumeroSerie())){
//				equipoSeleccionado.setDevice(device1);
//			}
//		}
//	}
//
//
//	public void loginUser(final String user, final String pwd) {
//		HttpManage.getInstance().login(user, pwd, new HttpManage.ResultCallback<Map<String, Object>>() {
//			@Override
//			public void onError(Header[] headers, HttpManage.Error error) {
//			}
//
//			@Override
//			public void onSuccess(int code, Map<String, Object> response) {
//				String authKey = (String) response.get("authorize");
//				String accessToken = (String) response.get("access_token");
//				int appid = ((Double) response.get("user_id")).intValue();
//				SharedPreferencesUtil.keepShared("appId", appid);
//				SharedPreferencesUtil.keepShared("authKey", authKey);
//				DatosAplicacion.getApp().setAccessToken(accessToken);
//				DatosAplicacion.getApp().setAppid(appid);
//				DatosAplicacion.getApp().setAuth(authKey);
//				DatosAplicacion.getApp().auth = true;
//				if (!XlinkAgent.getInstance().isConnectedLocal()) {
//					XlinkAgent.getInstance().start();
//				}
//				if (!XlinkAgent.getInstance().isConnectedOuterNet()) {
//					XlinkAgent.getInstance().login(DatosAplicacion.getApp().getAppid(), DatosAplicacion.getApp().getAuth());
//				}
//
//				int ret = XlinkAgent.getInstance().scanDeviceByProductId(
//						Constant.PRODUCTID, scanListener);
//				//if (ret < 0) {
//
//				connectDevice();
//
////					switch (ret) {
////						case XlinkCode.NO_CONNECT_SERVER:
////							//XlinkUtils.shortTips("No se ha podido conectar");
////							if (XlinkUtils.isWifi()) {
////								XlinkAgent.getInstance().start();
////							}
////							ret = XlinkAgent.getInstance().scanDeviceByProductId(
////									Constant.PRODUCTID, scanListener);
////							Log.d("resp", "resp " + ret);
////							break;
////						case XlinkCode.NETWORD_UNAVAILABLE:
////						//	XlinkUtils.shortTips("No tiene red");
////							break;
////						default:
////						//	XlinkUtils.shortTips("Error:" + ret);
////							break;
////					}
//				return;
//				//} else {
//
//				//}
//
//
//
//			}
//		});
//	}
//
//
//
//	private ScanDeviceListener scanListener = new ScanDeviceListener() {
//
//		@Override
//		public void onGotDeviceByScan(XDevice device2) {
//			//XlinkUtils.shortTips("扫描到设备:" + device.getMacAddress());
//			final Device dev = new Device(device2);
//			if (device2.getAccessKey() > 0) {
//				dev.setAccessKey(device2.getAccessKey());
//			}
//			DeviceManage.getInstance().addDevice(dev);
//
//			for(Device device1 : DeviceManage.getInstance().getDevices()){
//				if(device1.getMacAddress().equals(equipoSeleccionado.getNumeroSerie())){
//					device =device1;
//
//					equipoSeleccionado.setDevice(device1);
//					XlinkAgent.getInstance().initDevice(device1.getXDevice());
//					if(device1.getXDevice().getDeviceId() != 0){
//						SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//						SharedPreferences.Editor editor = sharedPrefs.edit();
//						editor.putInt(equipoSeleccionado.getNumeroSerie(), device1.getXDevice().getDeviceId());
//						editor.apply();
//						editor.commit();
//					}
//				}
//			}
//		}
//	};
//
//
//
//	@Override
//	protected void onDestroy() {
//		super.onDestroy();
//	}
//
//
//	public void connectDevice() {
//		if (isOnline) {
//			return;
//		}
//		if (device != null && device.getXDevice() != null) {
//			if (device != null && device.getXDevice() != null && device.getXDevice().getVersion() >= 3 && device.getXDevice().getSubKey() <= 0) {
//				XlinkAgent.getInstance().getInstance().getDeviceSubscribeKey(device.getXDevice(), device.getXDevice().getAccessKey(), new GetSubscribeKeyListener() {
//					@Override
//					public void onGetSubscribekey(XDevice xdevice, int code, int subKey) {
//						device.getXDevice().setSubKey(subKey);
//						DeviceManage.getInstance().updateDevice(device);
//					}
//				});
//			}
//			if (device != null && !device.isSubscribe()) {
//				XlinkAgent.getInstance().subscribeDevice(device.getXDevice(), device.getXDevice().getSubKey(), new SubscribeDeviceListener() {
//					@Override
//					public void onSubscribeDevice(XDevice xdevice, int code) {
//						if (code == XlinkCode.SUCCEED) {
//							device.setSubscribe(true);
//						}
//					}
//				});
//			}
//
//			int ret = XlinkAgent.getInstance().connectDevice(device.getXDevice(), device.getXDevice().getAccessKey(), device.getXDevice().getSubKey(), connectDeviceListener);
//			Log.d("estado focos ret", "ret " + ret);
//			if (ret < 0) {
//				//Debe hacer por socket
//
//				String comandoSocket;
//				String[] com = comandoPendiente.split(";");
//				int deviceId = device.getXDevice().getDeviceId();
//				if (device.getXDevice().getDeviceId() == 0) {
//					SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//					deviceId = sharedPrefs.getInt(equipoSeleccionado.getNumeroSerie(), 0);
//				}
//				if (deviceId != 0) {
//					nombreZonaT.setText("internet ");
//					txtEstado.setText("Internet");
//					if (comandoPendiente != null && !comandoPendiente.equals("")) {
//						if (comandoPendiente.startsWith(YACSmartProperties.COM_APAGAR_LUZ_WIFI)) {
//							//apagar
//							comandoSocket = YACSmartProperties.COM_APAGAR_LUZ_WIFI + ";" + deviceId + ";" + equipoSeleccionado.getNumeroSerie() + ";ANDROID;" + com[4] + ";";
//						} else {
//							//encender
//							comandoSocket = YACSmartProperties.COM_ENCENDER_LUZ_WIFI + ";" + deviceId + ";" + equipoSeleccionado.getNumeroSerie() + ";ANDROID;" + com[4] + ";";
//						}
//						ComandoIOFocoScheduledTask comandoIOFocoScheduledTask = new ComandoIOFocoScheduledTask(comandoSocket);
//						comandoIOFocoScheduledTask.start();
//					}
//				}
//
//
//				//[NSString stringWithFormat:@"%@;%d;%@;%@;%@;%d;%@;",LUZ_INTERNET_LUZ_COLOR,device.deviceID,idEquipo,nombreDispositivo,fila.zona.numeroZona,[fila.zona.numeroZona containsString:@"R"],color];
//
//				//		}else{
//				//			if (comandoPendiente != null && !comandoPendiente.equals("")) {
//				//				ComandoFoco comandoFoco = new ComandoFoco(comandoPendiente, getApplicationContext());
//				//				comandoFoco.start();
//				//			}
//			}
//		}
//	}
//
//	Boolean mobile = false;
//	private ConnectDeviceListener connectDeviceListener = new ConnectDeviceListener() {
//
//		@Override
//		public void onConnectDevice(XDevice xDevice, int result) {
//			String pass;
//			String tips;
//
////			if(Connectivity.isConnectedMobile(getApplicationContext())) {
////				txtEstado.setText("esta conexión le permite SOLO\n encender o apagar las luces.");
////				nombreZonaT.setText("Internet");
////				mobile = true;
////			}else{
////				txtEstado.setText("esta conexión le permite SOLO\n encender o apagar las luces.");
////				txtEstado.setText("Internet.");
////				nombreZonaT.setText("Internet.");
////			}
//			Log.d("estado focos", "estado " + result);
//			switch (result) {
//				case XlinkCode.DEVICE_STATE_LOCAL_LINK:
//					if(CmdManage.sesionFocos == null) {
//
//						pass = Integer.toHexString(xDevice.getSessionId());
//						CmdManage.sesionFocos = hexStringToByteArray(pass);
//						ComandoFoco comandoFoco = new ComandoFoco(comandoPendiente, getApplicationContext());
//						comandoFoco.start();
//					}
//					DeviceManage.getInstance().updateDevice(xDevice);
//					XlinkAgent.getInstance().sendProbe(xDevice);
//					break;
//				case XlinkCode.DEVICE_STATE_OUTER_LINK:
//					if(CmdManage.sesionFocos == null) {
//						pass = Integer.toHexString(xDevice.getSessionId());
//						CmdManage.sesionFocos = hexStringToByteArray(pass);
//						ComandoFoco comandoFoco = new ComandoFoco(comandoPendiente, getApplicationContext());
//						comandoFoco.start();
//					}
//					DeviceManage.getInstance().updateDevice(xDevice);
//					DeviceManage.getInstance().addDevice(xDevice);
//					break;
////				case XlinkCode.CONNECT_DEVICE_INVALID_KEY:
////					setDeviceStatus(false);
////					openDevicePassword();
////					Log.e(TAG, "Device:" + xDevice.getMacAddress() + "设备认证失败");
////					XlinkUtils.shortTips("设备认证失败");
////					break;
////				// 设备不在线
////				case XlinkCode.CONNECT_DEVICE_OFFLINE:
////					setDeviceStatus(false);
////					// Log.e(TAG, "Device:" + xDevice.getMacAddress() + "设备不在线");
////					XlinkUtils.shortTips("设备不在线");
////					Log("设备不在线");
////					break;
////
////				// 连接设备超时了，（设备未应答，或者服务器未应答）
////				case XlinkCode.CONNECT_DEVICE_TIMEOUT:
////					setDeviceStatus(false);
////					// Log.e(TAG, "Device:" + xDevice.getMacAddress() + "连接设备超时");
////					XlinkUtils.shortTips("连接设备超时");
////					break;
////
////				case XlinkCode.CONNECT_DEVICE_SERVER_ERROR:
////					setDeviceStatus(false);
////					XlinkUtils.shortTips("连接设备失败，服务器内部错误");
////
////					break;
////				case XlinkCode.CONNECT_DEVICE_OFFLINE_NO_LOGIN:
////					setDeviceStatus(false);
////					XlinkUtils.shortTips("连接设备失败，设备未在局域网内，且当前手机只有局域网环境");
////
////					break;
//				default:
//					//	XlinkUtils.shortTips("Otro error:" + result);
//					break;
//			}
//
//		}
//	};
//
//	public static byte[] hexStringToByteArray(String s) {
//		int len = s.length();
//		byte[] data = new byte[len / 2];
//		for (int i = 0; i < len; i += 2) {
//			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
//					+ Character.digit(s.charAt(i+1), 16));
//		}
//		return data;
	}

}
