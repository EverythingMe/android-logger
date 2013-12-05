package com.evme.logger.cache;

import java.io.File;
import java.util.List;

import android.os.Environment;

import com.evme.logger.Log;
import com.evme.logger.Log.LogEntry;
import com.evme.logger.LogConfiguration;
import com.evme.logger.formatters.LogEntryFormatter;
import com.evme.logger.helpers.Constants;
import com.evme.logger.tools.storage.ExternalStorageTool;
import com.evme.logger.tools.storage.StorageTool;

public class Cache {

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
		createFolder(Constants.ROOT);
		createFolder(Constants.DEBUG);
		createFolder(Constants.LOGS);
		createFolder(Constants.APP);
		createFolder(Constants.RECEIVERS);

		// create files
		createFile(Constants.APP, Constants.APP_LOGS);
		createFile(Constants.RECEIVERS, Constants.RECEIVER_LOGS);
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
		// we check if file is missing, if so we first create it
		createFile(Constants.APP, Constants.APP_LOGS);
		mExternalStorageTool.appendFile(Constants.APP, Constants.APP_LOGS, string.getBytes());
	}

	private void appendReceiverLog(String string) {
		// we check if file is missing, if so we first create it
		createFile(Constants.RECEIVERS, Constants.RECEIVER_LOGS);
		mExternalStorageTool.appendFile(Constants.RECEIVERS, Constants.RECEIVER_LOGS, string.getBytes());
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

		mExternalStorageTool.deleteFile(Constants.APP, Constants.APP_LOGS);
		mExternalStorageTool.deleteFile(Constants.RECEIVERS, Constants.RECEIVER_LOGS);

		// create files
		createFile(Constants.APP, Constants.APP_LOGS);
		createFile(Constants.RECEIVERS, Constants.RECEIVER_LOGS);

	}

	// TODO - remove this method
	public File getAppLogFile() {
		String path = Environment.getExternalStorageDirectory().getAbsolutePath();
		return new File(path + File.separator + Constants.APP + File.separator + Constants.APP_LOGS);
	}

	// TODO - remove this method
	public File getReceiverFile() {
		String path = Environment.getExternalStorageDirectory().getAbsolutePath();
		return new File(path + File.separator + Constants.RECEIVERS + File.separator + Constants.RECEIVER_LOGS);
	}

}
