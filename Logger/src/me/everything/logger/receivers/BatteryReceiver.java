package me.everything.logger.receivers;

import me.everything.logger.Log;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;


/**
 * Logs battery level and status changes
 * 
 * @author sromku
 */
public class BatteryReceiver implements SystemReceiver {

	private final static String NAME = "Battery";
	private boolean isRegistered = false;

	private BroadcastReceiver batteryReceiver;

	@Override
	public void register(Context context) {

		if (!isRegistered()) {
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
			isRegistered = true;
		}
	}

	@Override
	public void unregister(Context context) {
		if (isRegistered()) {
			context.unregisterReceiver(batteryReceiver);
			isRegistered = false;
		}
	}

	@Override
	public String getLoggerName() {
		return NAME;
	}

	@Override
	public boolean isRegistered() {
		return isRegistered;
	}

}
