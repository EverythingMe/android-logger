package com.evme.logger.reports;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.evme.logger.helpers.Constants;
import com.evme.logger.tools.date.DateTool;
import com.evme.logger.tools.storage.ExternalStorageTool;
import com.evme.logger.tools.storage.StorageTool;

public class ReportGenerator {

	// TODO - take it from configuration
	private ExternalStorageTool mExternalStorageTool = StorageTool.getExternalStorageTool();

	@SuppressWarnings("unchecked")
	public void generate(Report report) {

		List<File> results = new ArrayList<File>();

		// generate and set folder name
		Date date = Calendar.getInstance().getTime();
		String reportFolderName = DateTool.getString(date, "dd-MM HH:mm:ss");
		report.setName(reportFolderName);

		// create report folders DEBUG/REPORT/dd-MM HH:mm:ss/
		createFolder(Constants.REPORTS);
		createFolder(Constants.REPORTS + File.separator + reportFolderName);

		// get files content
		// TODO - read from today log
		String appLogs = mExternalStorageTool.readTextFile(Constants.APP, Constants.APP_LOGS);
		String receiverLogs = mExternalStorageTool.readTextFile(Constants.RECEIVERS, Constants.RECEIVER_LOGS);

		String[] applogsArray = appLogs.split("(\\[#\\])");
		String[] receiverLogsArray = receiverLogs.split("(\\[#\\])");

		// filter by date
		long fromDate = report.getLogsFilter().getStartTime();
		List<String> appLogsFiltered = filterByDate(applogsArray, fromDate);
		List<String> receiverLogsList = filterByDate(receiverLogsArray, fromDate);

		// create new report empty files
		createFile(Constants.REPORTS + File.separator + reportFolderName, Constants.APP_LOGS);
		createFile(Constants.REPORTS + File.separator + reportFolderName, Constants.RECEIVER_LOGS);

		// append filtered data to report files
		appendFile(Constants.REPORTS + File.separator + reportFolderName, Constants.APP_LOGS, appLogsFiltered);
		appendFile(Constants.REPORTS + File.separator + reportFolderName, Constants.RECEIVER_LOGS, receiverLogsList);

		// merge
		List<String> mergedLogs = merge(appLogsFiltered, receiverLogsList);
		createFile(Constants.REPORTS + File.separator + reportFolderName, Constants.MERGED_LOGS);
		appendFile(Constants.REPORTS + File.separator + reportFolderName, Constants.MERGED_LOGS, mergedLogs);

		// append device info

		// get all files from report folder
		results.add(mExternalStorageTool.getFile(Constants.REPORTS + File.separator + reportFolderName, Constants.APP_LOGS));
		results.add(mExternalStorageTool.getFile(Constants.REPORTS + File.separator + reportFolderName, Constants.RECEIVER_LOGS));
		results.add(mExternalStorageTool.getFile(Constants.REPORTS + File.separator + reportFolderName, Constants.MERGED_LOGS));
		report.setFiles(results);
	}

	private Comparator<String> comparator = new Comparator<String>() {

		@Override
		public int compare(String str1, String str2) {

			Long timeFirst = getLogTime(str1);
			Long timeSecond = getLogTime(str2);

			timeFirst = timeFirst == null ? 0 : timeFirst;
			timeSecond = timeSecond == null ? 0 : timeSecond;

			return (int) (timeFirst - timeSecond);
		}
	};

	private List<String> merge(List<String>... logs) {
		List<String> list = new ArrayList<String>(logs[0]);
		for (int i = 1; i < logs.length; i++) {
			list.addAll(logs[i]);
		}
		Collections.sort(list, comparator);
		return list;
	}

	private void appendFile(String folderName, String fileName, List<String> logs) {
		for (String log : logs) {
			mExternalStorageTool.appendFile(folderName, fileName, log);
		}
	}

	private void createFolder(String folder) {
		if (!mExternalStorageTool.isDirectoryExists(folder)) {
			mExternalStorageTool.createDirectory(folder);
		}
	}

	private void createFile(String folder, String name) {
		if (!mExternalStorageTool.isFileExist(folder, name)) {
			mExternalStorageTool.createFile(folder, name, "");
		}
	}

	private List<String> filterByDate(String[] raws, long fromDate) {
		List<String> filteredAppLogsArray = new ArrayList<String>();
		for (String appLog : raws) {

			Long time = getLogTime(appLog);
			if (time != null && time >= fromDate) {
				filteredAppLogsArray.add(appLog);
			}

		}
		return filteredAppLogsArray;
	}

	private Long getLogTime(String log) {
		final Pattern pattern = Pattern.compile(Constants.DATE_FORMAT_REGEX);
		final Matcher matcher = pattern.matcher(log);
		if (matcher.find()) {
			String dateStr = matcher.group();
			try {
				long time = DateTool.getDate(dateStr, Constants.DATE_FORMAT).getTime();
				return time;
			} catch (ParseException e) {
				// TODO - log this
				e.printStackTrace();
			}
		}
		return null;
	}

}
