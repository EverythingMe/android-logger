package me.everything.logger.activities;

import me.everything.logger.Log;
import me.everything.logger.Log.OnSendListener;
import android.app.Activity;
import android.os.Bundle;

public class SendLogActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		int identifier = getResources().getIdentifier("send_log_activity", "layout", getPackageName());
//		setContentView(R.layout.send_log_activity);
		setContentView(identifier);
		Log.send(new OnSendListener() {
			
			@Override
			public void onStart() {
			}
			
			@Override
			public void onFinish() {
				finish();
			}
		});
	}
}
