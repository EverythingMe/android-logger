package com.evme.logger.dispatchers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.net.Uri;
import android.text.Html;

import com.evme.logger.Log;
import com.evme.logger.reports.Report;

public class EmailReportDispatcher implements ReportDispatcher {

	private final String[] emails;

	public EmailReportDispatcher(String... emails) {
		this.emails = emails;
	}

	@Override
	public void dispatch(Report report) {

		// generate report
		report.create();
		List<File> attachments = report.getFiles();
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
		intent.putExtra(Intent.EXTRA_SUBJECT, "Automatic Logger Report");
		// TODO - create content by summary of report generated data
		intent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(createContent(report)));
		intent.putExtra(Intent.EXTRA_EMAIL, emails);
		intent.putExtra(Intent.EXTRA_STREAM, uris);
		Intent createChooser = Intent.createChooser(intent, "Send Log Report");
		createChooser.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Log.context.startActivity(createChooser);
	}

	/**
	 * Crash Report
	 * 
	 * My Comment: When I opened the app, it just crashed.
	 * 
	 * Device info: - Manufacture: - Android api: - ...
	 * 
	 * Report configuration: - From time: 12:03PM - Log level: TRACE - Output
	 * format: simple - System events: 1. Battery 2. Screen
	 * 
	 * Logs attached: 1. App logs 2. System events logs
	 * 
	 * 
	 * @param report
	 * @return
	 */
	private String createContent(Report report) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("<h3>Crash Report</h3>");
		stringBuilder.append("<b>Report configuration:</b>");
		stringBuilder.append("<br>");
		stringBuilder.append("- From time: 23:30");
		stringBuilder.append("<br>");
		stringBuilder.append("- Log level: TRACE");
		stringBuilder.append("<br>");
		stringBuilder.append("- System events: ");
		stringBuilder.append("<br>");
		stringBuilder.append("&nbsp;&nbsp;&nbsp;&nbsp;1. Battery ");
		stringBuilder.append("<br>");
		stringBuilder.append("&nbsp;&nbsp;&nbsp;&nbsp;2. Screen ");
		stringBuilder.append("<br>");
		stringBuilder.append("<br>");
		stringBuilder.append("<b>Logs attached:</b>");
		stringBuilder.append("<br>");
		stringBuilder.append("  1. App logs ");
		stringBuilder.append("<br>");
		stringBuilder.append("  2. System events logs ");
		return stringBuilder.toString();
	}

}
