package io.xlink.wifi.pipe.util;

import android.content.SharedPreferences.Editor;

import ec.com.yacare.y4all.activities.DatosAplicacion;


public class SharedPreferencesUtil {

    public static void keepShared(String key, String value) {
	Editor editor = DatosAplicacion.sharedPreferences.edit();
	editor.putString(key, value);
	editor.commit();
    }

    public static void keepShared(String key, Integer value) {
	Editor editor = DatosAplicacion.sharedPreferences.edit();
	editor.putInt(key, value);
	editor.commit();
    }

    public static void keepShared(String key, long value) {
	Editor editor = DatosAplicacion.sharedPreferences.edit();
	editor.putLong(key, value);
	editor.commit();
    }

    public static void keepShared(String key, int value) {
	Editor editor = DatosAplicacion.sharedPreferences.edit();
	editor.putInt(key, value);
	editor.commit();
    }

    /**
     * ������ѡ������
     * 
     * @param key
     * @param value
     */
    public static void keepShared(String key, boolean value) {
	Editor editor = DatosAplicacion.sharedPreferences.edit();
	editor.putBoolean(key, value);
	editor.commit();
    }

    /**
     * ��ѯָ��key û�з���null
     * 
     * @param key
     * @return
     */
    public static String queryValue(String key, String defvalue) {
	String value = DatosAplicacion.sharedPreferences.getString(key, defvalue);
	// if ("".equals(value)) {
	// return "";
	// }

	return value;
    }

    /**
     * ��ѯָ��key û�з���null
     * 
     * @param key
     * @return
     */
    public static String queryValue(String key) {
	String value = DatosAplicacion.sharedPreferences.getString(key, "");
	if ("".equals(value)) {
	    return "";
	}

	return value;
    }

    public static Integer queryIntValue(String key) {
	int value = DatosAplicacion.sharedPreferences.getInt(key, 0);

	return value;
    }

    /**
     * 
     * @param key
     * @return
     */
    public static boolean queryBooleanValue(String key) {
	return DatosAplicacion.sharedPreferences.getBoolean(key, false);
    }

    /**
     * 
     * @param key
     * @return
     */
    public static long queryLongValue(String key) {
	return DatosAplicacion.sharedPreferences.getLong(key, 0);
    }

    /**
     * �������
     * 
     * @return
     */
    public static boolean deleteAllValue() {

	return DatosAplicacion.sharedPreferences.edit().clear().commit();
    }

    /**
     * ����Key�����
     * 
     * @param key
     */
    public static void deleteValue(String key) {
        DatosAplicacion.sharedPreferences.edit().remove(key).commit();
    }
}
