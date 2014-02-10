package me.everything.logger.tools.device;

import java.lang.reflect.Field;

import android.content.Context;
import android.os.Build;

public class DeviceTool {

	private static DeviceTool mInstance = null;
	private Context context;

	private DeviceTool(Context context) {
		this.context = context;
	}

	public static DeviceTool getIntance(Context context) {
		if (mInstance == null) {
			mInstance = new DeviceTool(context);
		}
		return mInstance;
	}

	/**
	 * Get package name
	 * 
	 * @return
	 */
	public String getPackageName() {
		return context.getPackageName();
	}

	/**
	 * Get device model. For example: Nexus 4
	 * 
	 * @return
	 */
	public String getDeviceModel() {
		return android.os.Build.MODEL;
	}

	/**
	 * Get android version.
	 * @return
	 */
	public String getAndroidVersion() {
		String version = "";

		int apiLevel;
		try {
			// This field has been added in Android 1.6
			final Field SDK_INT = Build.VERSION.class.getField("SDK_INT");
			apiLevel = SDK_INT.getInt(null);
		} catch (SecurityException e) {
			apiLevel = Integer.parseInt(Build.VERSION.SDK);
		} catch (NoSuchFieldException e) {
			apiLevel = Integer.parseInt(Build.VERSION.SDK);
		} catch (IllegalArgumentException e) {
			apiLevel = Integer.parseInt(Build.VERSION.SDK);
		} catch (IllegalAccessException e) {
			apiLevel = Integer.parseInt(Build.VERSION.SDK);
		}

		switch (apiLevel) {
		case 1:
			version = "1.0";
			break;
		case 2:
			version = "1.1";
			break;
		case 3:
			version = "1.5 Cupcake";
			break;
		case 4:
			version = "1.6 Donut";
			break;
		case 5:
			version = "2.0 Eclair";
			break;
		case 6:
			version = "2.0.1 Eclair";
			break;
		case 7:
			version = "2.1 Eclair";
			break;
		case 8:
			version = "2.2 Froyo";
			break;
		case 9:
			version = "2.3 Gingerbread";
			break;
		case 10:
			version = "2.3.3 Gingerbread";
			break;
		case 11:
			version = "3.0 Honeycomb";
			break;
		case 12:
			version = "3.1 Honeycomb";
			break;
		case 13:
			version = "3.2 Honeycomb";
			break;
		case 14:
			version = "4.0 Ice Cream Sandwich";
			break;
		case 15:
			version = "4.0.3 Ice Cream Sandwich";
			break;
		case 16:
			version = "4.1 Jellybean";
			break;
		case 17:
			version = "4.2 Jellybean";
			break;
		case 18:
			version = "4.3 Jellybean";
			break;
		case 19:
			version = "4.4 KitKat";
			break;
		case 10000:
			version = "Current Development Build";
			break;
		default:
			version = String.valueOf(apiLevel);
			break;
		}

		return version;
	}

	/**
	 * Get brand of the device. For example: google
	 * 
	 * @return
	 */
	public String getBrand() {
		return android.os.Build.BRAND;
	}

	/**
	 * Get product of the device. For example: occam
	 * 
	 * @return
	 */
	public String getProduct() {
		return android.os.Build.PRODUCT;
	}
	
}
