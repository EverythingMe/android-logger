package me.everything.logger.activities;

import me.everything.logger.Log;
import me.everything.logger.Log.OnSendListener;
import me.everything.logger.R;
import android.app.Activity;
import android.os.Bundle;

public class SendLogActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.send_log_activity);
		
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
