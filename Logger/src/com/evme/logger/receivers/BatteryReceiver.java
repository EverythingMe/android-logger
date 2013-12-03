package com.evme.logger.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;

import com.evme.logger.Log;

/**
 * Logs battery level and status changes
 * 
 * @author sromku
 */
public class BatteryReceiver implements SystemReceiver {

	private final static String NAME = "Battery";

	private BroadcastReceiver batteryReceiver;

	@Override
	public void register(Context context) {

		batteryReceiver = new BroadcastReceiver() {
			int scale = -1;
			int level = -1;
			int voltage = -1;
			int temp = -1;

			@Override
			public void onReceive(Context context, Intent intent) {
				level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
				scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
				temp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
				voltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);

				Bundle bundle = new Bundle(4);
				bundle.putInt("level", level);
				bundle.putInt("scale", scale);
				bundle.putInt("voltage", voltage);
				bundle.putInt("temp", temp);

				// log the results
				Log.system(getLoggerName(), bundle);
			}
		};

		// register battery
		IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		context.registerReceiver(batteryReceiver, filter);

	}

	@Override
	public void unregister(Context context) {
		context.unregisterReceiver(batteryReceiver);
	}

	@Override
	public String getLoggerName() {
		return NAME;
	}

}
