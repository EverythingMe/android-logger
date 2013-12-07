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

import android.content.Context;

import com.evme.logger.cache.Cache;
import com.evme.logger.helpers.Constants;
import com.evme.logger.tools.date.DateTool;
import com.evme.logger.tools.device.DeviceTool;

/**
 * Report generator
 * 
 * @author sromku
 */
public class ReportGenerator {

	// cache instance
	private final Cache cache = Cache.getInstance();

	@SuppressWarnings("unchecked")
	public void generate(Report report) {

		// set log configuration to report
		report.setLogConfiguration(cache.getLogConfiguration());

		// initialize the files to be added to report
		List<File> results = new ArrayList<File>();

		// generate and set folder name
		Date date = Calendar.getInstance().getTime();
		String dirName = DateTool.getString(date, "dd-MM HH:mm:ss");
		String reportDir = Constants.DIR_REPORTS + File.separator + dirName;
		report.setName(dirName);

		// create report folders DEBUG/REPORT/dd-MM HH:mm:ss/
		cache.createFolder(Constants.DIR_REPORTS);
		cache.createFolder(reportDir);

		// get files content and split to rows of logs
		String appLogs = cache.getFileContent(Constants.DIR_APP, Constants.LOG_APP);
		String[] applogsArray = appLogs.split("(\\[#\\])");

		String receiverLogs = cache.getFileContent(Constants.DIR_RECEIVERS, Constants.LOG_RECEIVER);
		String[] receiverLogsArray = receiverLogs.split("(\\[#\\])");

		// filter by date
		long fromTime = report.getLogsFilter().getFromTime();
		List<String> appLogsFiltered = filterByDate(applogsArray, fromTime);
		List<String> receiverLogsList = filterByDate(receiverLogsArray, fromTime);

		// add last log
		if (appLogsFiltered.size() > 0) {
			report.setLastLog(appLogsFiltered.get(appLogsFiltered.size() - 1));
		}

		// create new report empty files
		// app logs
		if (appLogsFiltered.size() > 0) {
			cache.createFile(reportDir, Constants.LOG_APP);
			cache.appendFile(reportDir, Constants.LOG_APP, appLogsFiltered);
			results.add(cache.getFile(reportDir, Constants.LOG_APP));
		}

		// receiver logs
		if (receiverLogsList.size() > 0) {
			cache.createFile(reportDir, Constants.LOG_RECEIVER);
			cache.appendFile(reportDir, Constants.LOG_RECEIVER, receiverLogsList);
			results.add(cache.getFile(reportDir, Constants.LOG_RECEIVER));
		}

		// merge
		if (report.getMergeLogs()) {
			List<String> mergedLogs = merge(appLogsFiltered, receiverLogsList);
			cache.createFile(reportDir, Constants.REPORT_MERGE);
			cache.appendFile(reportDir, Constants.REPORT_MERGE, mergedLogs);
			results.add(cache.getFile(reportDir, Constants.REPORT_MERGE));
		}

		// append device info
		if (report.getIncludeDeviceInfo()) {
			List<String> deviceInfos = prepareDeviceInfo(cache.getLogConfiguration().getContext());
			report.setDeviceInfos(deviceInfos);
			cache.createFile(reportDir, Constants.DEVICE_INFO);
			cache.appendFile(reportDir, Constants.DEVICE_INFO, deviceInfos);
			results.add(cache.getFile(reportDir, Constants.DEVICE_INFO));
		}

		// set the results
		report.setFiles(results);
	}

	private List<String> prepareDeviceInfo(Context context) {
		
		List<String> infos = new ArrayList<String>();
		DeviceTool deviceTool = DeviceTool.getIntance(context);
		infos.add("Model: " + deviceTool.getDeviceModel());
		infos.add("Brand: " + deviceTool.getBrand());
		infos.add("Product: " + deviceTool.getProduct());
		infos.add("SDK: " + deviceTool.getAndroidVersion());
		return infos;
	}

	/**
	 * Merge all logs and sort by date.
	 * 
	 * @param logs
	 * @return Sorted merged logs
	 */
	private List<String> merge(List<String>... logs) {
		List<String> list = new ArrayList<String>(logs[0]);
		for (int i = 1; i < logs.length; i++) {
			list.addAll(logs[i]);
		}
		Collections.sort(list, new LogTimeComperator());
		return list;
	}

	/**
	 * Filter and return list of logs that start from passed date.
	 * 
	 * @param logs
	 *            The list of logs to filter
	 * @param fromDate
	 *            The date from which we will filter the logs
	 * @return The filtered logs
	 */
	private List<String> filterByDate(String[] logs, long fromDate) {
		List<String> filteredAppLogsArray = new ArrayList<String>();
		for (String appLog : logs) {

			Long time = getLogTime(appLog);
			if (time != null && time >= fromDate) {
				filteredAppLogsArray.add(appLog);
			}

		}
		return filteredAppLogsArray;
	}

	/**
	 * Get the time of the log
	 * 
	 * @param log
	 * @return
	 */
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

	/**
	 * Compare logs by date
	 * 
	 * @author sromku
	 */
	public class LogTimeComperator implements Comparator<String> {

		@Override
		public int compare(String log1, String str2) {

			Long timeFirst = getLogTime(log1);
			Long timeSecond = getLogTime(str2);

			timeFirst = timeFirst == null ? 0 : timeFirst;
			timeSecond = timeSecond == null ? 0 : timeSecond;

			return (int) (timeFirst - timeSecond);
		}
	}
}
