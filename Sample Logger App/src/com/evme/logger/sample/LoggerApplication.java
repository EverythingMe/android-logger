package com.evme.logger.sample;

import android.app.Application;

import com.evme.logger.Log;
import com.evme.logger.LogConfiguration;
import com.evme.logger.formaters.JsonLogEntryFormatter;
import com.evme.logger.receivers.BatteryReceiver;
import com.evme.logger.receivers.ScreenReceiver;

public class LoggerApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		LogConfiguration logConfiguration = new LogConfiguration.Builder(this)
			.addSystemReceiver(new BatteryReceiver())
			.addSystemReceiver(new ScreenReceiver())
			.setLogPriority(Thread.MIN_PRIORITY)
			.setLogQueueListMaxSize(5)
			.setLogEntryFormatter(new JsonLogEntryFormatter())
			.build();
		
		Log.setConfiguration(logConfiguration);
		Log.start();

	}

}
