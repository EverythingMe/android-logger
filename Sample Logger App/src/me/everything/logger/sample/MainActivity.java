package me.everything.logger.sample;

import me.everything.logger.Log;
import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {

	private final static String TAG = MainActivity.class.getName();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Log.v(TAG, "Trace message 1");

		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				Log.i(TAG, "Info message in other thread");
				// Integer.valueOf("aa");
			}
		}, "My thread");
		thread.setPriority(Thread.MAX_PRIORITY);
		thread.start();

		Log.d(TAG, "Debug message 2");
		Log.i(TAG, "{} message {} with paramters {}", "CUSTOM", 1111, 3);
		Log.i(TAG, "Info message 3");
		Log.w(TAG, "Warning message {}", 4);
		Log.e(TAG, "Error message 5");
		Log.e(TAG, "Error message 6", new RuntimeException("Something strange"));

		// Integer.valueOf("aa");

		Log.d(TAG, "Debug message 7");
//		throw new RuntimeException("Something strange");

		Log.send();
	}
	
	@Override
	protected void onDestroy() {
		Log.stop();
		super.onDestroy();
	}
	
}
