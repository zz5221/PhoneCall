package com.pbg.uitl;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesTool {
	public static final String KEY_PHONE_NUM = "KEY_PHONE_NUM";
	public static final String KEY_SECURITY_CODE = "KEY_SECURITY_CODE";
	
	//Version
	public static final String KEY_VERSION_INFO = "KEY_VERSION_INFO";
	
	public static void clearAllPreferences(Context context){
		if(context==null)return;
		SharedPreferences sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
		if(sharedPreferences==null)return;
		SharedPreferences.Editor editor = sharedPreferences.edit();
		if(editor==null)return;
		editor.clear();
		editor.commit();
	}
	
	public static String getString(Context context, String key) {
		if(context==null)return null;
		SharedPreferences sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
		if(sharedPreferences!=null)
			return sharedPreferences.getString(key, null);
		return null;
	}
	public static void setString(Context context, String key, String value){
		if(context==null)return;
		SharedPreferences sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
		if(sharedPreferences==null)return;
		SharedPreferences.Editor editor = sharedPreferences.edit();
		if(editor!=null){
			editor.putString(key, value);
			editor.commit();
		}
	}
	public static void removeString(Context context, String key){
		if(context==null)return;
		SharedPreferences sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
		if(sharedPreferences==null)return;
		SharedPreferences.Editor editor = sharedPreferences.edit();
		if(editor!=null){
			editor.remove(key);
			editor.commit();
		}
	}
	
	public static float getFloat(Context context, String key) {
		if(context==null)return -1;
		SharedPreferences sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
		if(sharedPreferences!=null)
			return sharedPreferences.getFloat(key, -1.0f);
		return -1;
	}
	public static void setFloat(Context context, String key, float value){
		if(context==null)return;
		SharedPreferences sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
		if(sharedPreferences==null)return;
		SharedPreferences.Editor editor = sharedPreferences.edit();
		if(editor!=null){
			editor.putFloat(key, value);
			editor.commit();
		}
	}
	public static void removeFloat(Context context, String key){
		if(context==null)return;
		SharedPreferences sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
		if(sharedPreferences==null)return;
		SharedPreferences.Editor editor = sharedPreferences.edit();
		if(editor!=null){
			editor.remove(key);
			editor.commit();
		}
	}
	
	public static int getInt(Context context, String key) {
		if(context==null)return -1;
		SharedPreferences sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
		if(sharedPreferences!=null)
			return sharedPreferences.getInt(key, -1);
		return -1;
	}
	public static void setInt(Context context, String key, int value){
		if(context==null)return;
		SharedPreferences sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
		if(sharedPreferences==null)return;
		SharedPreferences.Editor editor = sharedPreferences.edit();
		if(editor!=null){
			editor.putInt(key, value);
			editor.commit();
		}
	}
	public static void removeInt(Context context, String key){
		if(context==null)return;
		SharedPreferences sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
		if(sharedPreferences==null)return;
		SharedPreferences.Editor editor = sharedPreferences.edit();
		if(editor!=null){
			editor.remove(key);
			editor.commit();
		}
	}
	
	public static long getLong(Context context, String key) {
		if(context==null)return -1;
		SharedPreferences sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
		if(sharedPreferences!=null)
			return sharedPreferences.getLong(key, -1);
		return -1;
	}
	public static void setLong(Context context, String key, long value){
		if(context==null)return;
		SharedPreferences sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
		if(sharedPreferences==null)return;
		SharedPreferences.Editor editor = sharedPreferences.edit();
		if(editor!=null){
			editor.putLong(key, value);
			editor.commit();
		}
	}
	public static void removeLong(Context context, String key){
		if(context==null)return;
		SharedPreferences sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
		if(sharedPreferences==null)return;
		SharedPreferences.Editor editor = sharedPreferences.edit();
		if(editor!=null){
			editor.remove(key);
			editor.commit();
		}
	}
	
	public static boolean getBoolean(Context context, String key) {
		if(context==null)return false;
		SharedPreferences sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
		if(sharedPreferences!=null)
			return sharedPreferences.getBoolean(key, false);
		return false;
	}
	public static void setBoolean(Context context, String key, boolean value){
		if(context==null)return;
		SharedPreferences sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
		if(sharedPreferences==null)return;
		SharedPreferences.Editor editor = sharedPreferences.edit();
		if(editor!=null){
			editor.putBoolean(key, value);
			editor.commit();
		}
	}
	public static void removeBoolean(Context context, String key){
		if(context==null)return;
		SharedPreferences sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
		if(sharedPreferences==null)return;
		SharedPreferences.Editor editor = sharedPreferences.edit();
		if(editor!=null){
			editor.remove(key);
			editor.commit();
		}
	}
}
