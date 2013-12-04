package com.evme.logger.dispatchers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.net.Uri;

import com.evme.logger.Log;
import com.evme.logger.reports.Report;

public class EmailReportDispatcher implements ReportDispatcher {

	@Override
	public void dispatch(Report report) {

		// TODO - send report to email/s. take from constructor of this class
		String email = "roman@everything.me";

		// generate report
		List<File> attachments = report.create();
		ArrayList<Uri> uris = new ArrayList<Uri>();
		for (File file : attachments) {
			Uri uri = Uri.fromFile(file);
			uris.add(uri);
		}
		
		// TODO - do it without android intent:
		// http://stackoverflow.com/questions/2020088/sending-email-in-android-using-javamail-api-without-using-the-default-built-in-a
		Intent intent = new Intent(android.content.Intent.ACTION_SEND_MULTIPLE);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_SUBJECT, "Log report");
		intent.putExtra(Intent.EXTRA_TEXT, "Log report");
		intent.putExtra(Intent.EXTRA_EMAIL, new String[] { email });
		intent.putExtra(Intent.EXTRA_STREAM, uris);
		Intent createChooser = Intent.createChooser(intent, "Send Log Report");
		createChooser.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Log.context.startActivity(createChooser);
	}

}
