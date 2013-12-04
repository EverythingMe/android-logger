package com.evme.logger.cache;

import java.io.File;
import java.util.List;

import android.os.Environment;

import com.evme.logger.Log;
import com.evme.logger.Log.LogEntry;
import com.evme.logger.LogConfiguration;
import com.evme.logger.formatters.LogEntryFormatter;
import com.evme.logger.tools.storage.ExternalStorageTool;
import com.evme.logger.tools.storage.StorageTool;

public class Cache {

	private static final String ROOT = "Evme";
	private static final String DEBUG = ROOT + File.separator + "Debug";
	private static final String LOGS = DEBUG + File.separator + "Logs";
	private static final String APP = LOGS + File.separator + "App";
	private static final String RECEIVERS = LOGS + File.separator + "Receivers";
	private static final String APP_LOGS = "app_log.txt";
	private static final String RECEIVER_LOGS = "receivers_log.txt";

	private static Cache mInstance = null;
	private ExternalStorageTool mExternalStorageTool;
	private static LogConfiguration mConfiguration;

	private Cache() {

		init();

	}

	public static void setConfiguration(LogConfiguration logConfiguration) {
		mConfiguration = logConfiguration;
	}

	/**
	 * Create directories
	 */
	private void init() {
		mExternalStorageTool = StorageTool.getExternalStorageTool();

		// create folders
		createFolder(ROOT);
		createFolder(DEBUG);
		createFolder(LOGS);
		createFolder(APP);
		createFolder(RECEIVERS);

		// create files
		createFile(APP, APP_LOGS);
		createFile(RECEIVERS, RECEIVER_LOGS);
	}

	private void createFile(String dir, String file) {
		if (!mExternalStorageTool.isFileExist(dir, file)) {
			mExternalStorageTool.createFile(dir, file, "");
		}
	}

	private void createFolder(String path) {
		if (!mExternalStorageTool.isDirectoryExists(path)) {
			mExternalStorageTool.createDirectory(path);
		}
	}

	private void appendAppLog(String string) {
		mExternalStorageTool.appendFile(APP, APP_LOGS, string.getBytes());
	}

	private void appendReceiverLog(String string) {
		mExternalStorageTool.appendFile(RECEIVERS, RECEIVER_LOGS, string.getBytes());
	}

	public static Cache getInstance() {
		if (mInstance == null) {
			mInstance = new Cache();
		}
		return mInstance;
	}

	/**
	 * Flush to cache
	 * 
	 * @param logs
	 */
	public void flush(List<LogEntry> logs) {

		LogEntryFormatter logEntryFormatter = mConfiguration.getLogEntryFormatter();

		for (LogEntry logEntry : logs) {

			switch (logEntry.type) {
			case Log.Types.RECEIVER:
				String formatted = logEntryFormatter.format(logEntry);
				appendReceiverLog(formatted);
				break;

			default:
				formatted = logEntryFormatter.format(logEntry);
				appendAppLog(formatted);
				break;
			}
		}
	}

	/**
	 * Clean cache
	 */
	public void clean() {

		mExternalStorageTool.deleteFile(APP, APP_LOGS);
		mExternalStorageTool.deleteFile(RECEIVERS, RECEIVER_LOGS);

		// create files
		createFile(APP, APP_LOGS);
		createFile(RECEIVERS, RECEIVER_LOGS);

	}

	// TODO - remove this method
	public File getAppLogFile() {
		String path = Environment.getExternalStorageDirectory().getAbsolutePath();
		return new File(path + File.separator + APP + File.separator + APP_LOGS);
	}

	// TODO - remove this method
	public File getReceiverFile() {
		String path = Environment.getExternalStorageDirectory().getAbsolutePath();
		return new File(path + File.separator + RECEIVERS + File.separator + RECEIVER_LOGS);
	}

}
