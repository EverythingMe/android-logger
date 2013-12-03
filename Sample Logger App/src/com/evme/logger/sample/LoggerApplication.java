package com.evme.logger.sample;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.Thread.UncaughtExceptionHandler;

import android.app.Application;

import com.evme.logger.Log;
import com.evme.logger.LogConfiguration;
import com.evme.logger.cache.Cache;
import com.evme.logger.formatters.JsonLogEntryFormatter;
import com.evme.logger.receivers.BatteryReceiver;
import com.evme.logger.receivers.ScreenReceiver;
import com.evme.logger.tools.common.CommonTool;

public class LoggerApplication extends Application {

	// TEST CRASH
	private UncaughtExceptionHandler mHandler = new Thread.UncaughtExceptionHandler() {

		@Override
		public void uncaughtException(Thread t, Throwable e) {

			Log.e(this, "Crash", e);
			Log.flushMemory();

			Cache cache = Cache.getInstance();
			try {

				InputStream inputStream = getContentResolver().openInputStream(cache.getAppLogUri());
				String string = CommonTool.convertStreamToString(inputStream);
				android.util.Log.e("Sample", " --- App ---");
				android.util.Log.e("Sample", string);

				InputStream inputReceiverStream = getContentResolver().openInputStream(cache.getReceiverUri());
				String receiver = CommonTool.convertStreamToString(inputReceiverStream);
				android.util.Log.e("Sample", " --- Receiver ---");
				android.util.Log.e("Sample", receiver);

			} catch (FileNotFoundException e1) {

				android.util.Log.e("Logger", "general exception", e1);

			} finally {

				// clean cache
				cache.clean();
				// continue and show to the user the crash
				defaultUncaughtExceptionHandler.uncaughtException(t, e);

			}

		}
	};

	private UncaughtExceptionHandler defaultUncaughtExceptionHandler;

	@Override
	public void onCreate() {
		super.onCreate();

		LogConfiguration logConfiguration = new LogConfiguration.Builder(this)
			.addSystemReceiver(new BatteryReceiver())
			.addSystemReceiver(new ScreenReceiver())
			.setLogPriority(Thread.MIN_PRIORITY)
			.setLogQueueListMaxSize(2).setLogEntryFormatter(new JsonLogEntryFormatter()).build();

		Log.setConfiguration(logConfiguration);
		Log.start();

		defaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(mHandler);

	}

}
