package com.evme.logger.dispatchers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.net.Uri;
import android.text.Html;

import com.evme.logger.helpers.Constants;
import com.evme.logger.helpers.Utils;
import com.evme.logger.receivers.SystemReceiver;
import com.evme.logger.reports.Report;
import com.evme.logger.reports.Report.ReportType;
import com.evme.logger.tools.date.DateTool;

public class EmailReportDispatcher implements ReportDispatcher {

	private final String[] emails;

	public EmailReportDispatcher(String... emails) {
		this.emails = emails;
	}

	@Override
	public void dispatch(Report report) {

		// generate report
		report.create();

		// get generated files
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
		report.getLogConfiguration().getContext().startActivity(createChooser);
	}

	/**
	 * Create content of the email
	 * 
	 * @param report
	 * @return
	 */
	private String createContent(Report report) {

		StringBuilder stringBuilder = new StringBuilder();

		// header
		if (report.getReportType().equals(ReportType.CRASH)) {
			stringBuilder.append("<u><b>Crash Report</b></u>");
			stringBuilder.append("<br>");
			stringBuilder.append("<br>");
		}

		// device info
		if (report.getDeviceInfos().size() > 0) {
			stringBuilder.append("<b>Device info:</b>");
			stringBuilder.append("<br>");
			for (String info : report.getDeviceInfos()) {
				stringBuilder.append(info);
				stringBuilder.append("<br>");
			}
		}
		stringBuilder.append("<br>");

		// configuration
		stringBuilder.append("<b>Report configuration:</b>");
		stringBuilder.append("<br>");

		// from time
		stringBuilder.append("- From time: ");
		String fromTime = DateTool.getString(DateTool.getDate(report.getLogsFilter().getFromTime()), Constants.DATE_FORMAT);
		stringBuilder.append(fromTime);
		stringBuilder.append("<br>");

		// log level
		stringBuilder.append("- Log level: ");
		String logLevel = Utils.getLogLevelName(report.getLogsFilter().getLogLevel());
		stringBuilder.append(logLevel);
		stringBuilder.append("<br>");
		stringBuilder.append("<br>");

		// system events
		List<SystemReceiver> systemEvents = report.getLogConfiguration().getSystemReceivers();
		if (systemEvents.size() > 0) {

			stringBuilder.append("<b>System events:</b>");
			stringBuilder.append("<br>");
			int i = 1;
			for (SystemReceiver systemReceiver : systemEvents) {
				stringBuilder.append("&nbsp;&nbsp;&nbsp;&nbsp;" + i + ". " + systemReceiver.getLoggerName());
				stringBuilder.append("<br>");
				i++;
			}
		}
		stringBuilder.append("<br>");

		// set attached logs
		List<File> files = report.getFiles();
		if (files.size() > 0) {

			stringBuilder.append("<b>Logs attached:</b>");
			stringBuilder.append("<br>");
			int i = 1;
			for (File file : files) {
				stringBuilder.append(i + ". " + file.getName());
				stringBuilder.append("<br>");
				i++;
			}
		}
		stringBuilder.append("<br>");

		// set last log
		String lastLog = report.getLastLog();
		if (lastLog != null) {

			stringBuilder.append("<b>Last log:</b>");
			stringBuilder.append("<br>");

			String[] splitted = lastLog.split(Constants.EXCEPTION_STRING_SPLITTER);
			int i = 0;
			for (String str : splitted) {
				if (i >= 1) {
					stringBuilder.append(" at ");
				}
				stringBuilder.append(str);
				stringBuilder.append("<br>");
				i++;
			}

		}

		return stringBuilder.toString();
	}

}
