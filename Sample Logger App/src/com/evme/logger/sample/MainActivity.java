package com.evme.logger.sample;

import android.app.Activity;
import android.os.Bundle;

import com.evme.logger.Log;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Log.t(this, "Trace message 1");

		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				Log.i(this, "Info message in other thread");
			}
		}, "My thread");
		thread.setPriority(Thread.MAX_PRIORITY);
		thread.start();

		Log.d(this, "Debug message 2");
		Log.i(this, "{} message {} with paramters {}", "CUSTOM", 1111, 3);
		Log.i(this, "Info message 3");
		Log.w(this, "Warning message {}", 4);
		Log.e(this, "Error message 5");
		Log.e(this, "Error message 6", new RuntimeException("Something strange"));

		Integer.valueOf("aa");

		Log.d(this, "Debug message 7");
	}

}
