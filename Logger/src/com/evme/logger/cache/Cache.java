package com.evme.logger.cache;

import java.io.File;
import java.util.List;

import com.evme.logger.Log;
import com.evme.logger.Log.LogEntry;
import com.evme.logger.LogConfiguration;
import com.evme.logger.formatters.LogEntryFormatter;
import com.evme.logger.helpers.Constants;
import com.evme.logger.tools.date.DateTool;
import com.sromku.simple.storage.Storage;
import com.sromku.simple.storage.helpers.SizeUnit;

/**
 * TODO - This is temporal solution for persisting and flushing logs into cache.
 * The potential design will have Strategy pattern when each strategy
 * implementation will handle the log in each phase - memory and disk with
 * restrictions of history and memory sizes.
 * 
 * @author sromku
 */
public class Cache {

	private static Cache mInstance = null;
	private static Storage mStorage;
	private static LogConfiguration mConfiguration;

	private Cache() {

		// init storage
		mStorage = mConfiguration.getStorage();

		// create root folder if such doesn't exist
		createFolder(Constants.DIR_ROOT);

		// create folder which all have all logger content
		createFolder(Constants.DIR_DEBUG);

		// create folder for logs
		createFolder(Constants.DIR_LOGS);

		// create folder for app logs
		createFolder(Constants.DIR_APP);

		// create folder for receivers logs
		createFolder(Constants.DIR_RECEIVERS);

	}

	/**
	 * Get logger configuration
	 * 
	 * @return
	 */
	public LogConfiguration getLogConfiguration() {
		return mConfiguration;
	}

	/**
	 * Set logger configuration. <br>
	 * <br>
	 * <b>Important:</b><br>
	 * You must set the configuration before creating first instance of
	 * <code>Cache.getInstance()</code>
	 * 
	 * @param logConfiguration
	 */
	public static void setConfiguration(LogConfiguration logConfiguration) {
		mConfiguration = logConfiguration;
	}

	/**
	 * Get instance of {@link Cache}. <br>
	 * <br>
	 * <b>Important:</b><br>
	 * You must call {@link Cache#setConfiguration(LogConfiguration)} before
	 * creating first instance of <code>Cache.getInstance()</code>
	 * 
	 * @return {@link Cache}
	 */
	public static Cache getInstance() {
		if (mInstance == null) {
			mInstance = new Cache();
		}
		return mInstance;
	}

	/**
	 * Flush to cache.
	 * 
	 * @param logs
	 */
	public void flush(List<LogEntry> logs) {

		LogEntryFormatter logEntryFormatter = mConfiguration.getLogEntryFormatter();

		// TODO - create batch of logs and append only one time
		for (LogEntry logEntry : logs) {
			String dirName = null;
			String fileName = null;
			switch (logEntry.type) {
			case Log.Types.RECEIVER:
				dirName = Constants.DIR_RECEIVERS;
				fileName = Constants.LOG_RECEIVER;
				break;
			case Log.Types.APP:
				dirName = Constants.DIR_APP;
				fileName = Constants.LOG_APP;
				break;
			}
			// append entry to file
			String formattedEntry = logEntryFormatter.format(logEntry);
			appendFile(dirName, fileName, formattedEntry);
		}
	}

	/**
	 * TODO - Clean cache that is more than passed history days
	 */
	public void clean(int days) {

		// mStorage.deleteFile(Constants.DIR_APP, Constants.LOG_APP);
		// mStorage.deleteFile(Constants.DIR_RECEIVERS, Constants.LOG_RECEIVER);

	}

	/**
	 * Append log to file.
	 * 
	 * @param log
	 *            The log to add
	 * @param logFileNamePattern
	 *            The file pattern name
	 */
	public void appendFile(String dirName, String fileNamePattern, String log) {

		String fileName = getLogFileNameToday(fileNamePattern);
		if (!mStorage.isFileExist(dirName, fileName)) {
			// we check if file is missing, if so we first create it
			createFile(dirName, fileName);
		}
		else {
			// we check for max size of the file. if it reached the max size,
			// then we rename the file
			File file = mStorage.getFile(dirName, fileName);
			if (mStorage.getSize(file, SizeUnit.MB) > mConfiguration.getFileMaxMbSize()) {
				String newName = getLogFileNameTimeToday(fileNamePattern);
				mStorage.rename(file, newName);
				
				// create the file of the day from 0
				createFile(dirName, fileName);
			}
		}
		// append
		mStorage.appendFile(dirName, fileName, log.getBytes());
	}

	/**
	 * Append batch of logs
	 * 
	 * @param dirName
	 * @param fileNamePattern
	 * @param logs
	 */
	public void appendFile(String dirName, String fileNamePattern, List<String> logs) {

		for (String log : logs) {
			appendFile(dirName, fileNamePattern, log);
		}
	}

	/**
	 * Create file if such doesn't exist
	 * 
	 * @param dir
	 * @param file
	 */
	public void createFile(String dir, String file) {

		String fileName = getLogFileNameToday(file);
		if (!mStorage.isFileExist(dir, fileName)) {
			mStorage.createFile(dir, fileName, "");
		}
	}

	/**
	 * Create folder if such doesn't exist
	 * 
	 * @param path
	 */
	public void createFolder(String path) {
		if (!mStorage.isDirectoryExists(path)) {
			mStorage.createDirectory(path);
		}
	}

	/**
	 * Get file content
	 * 
	 * @param dirName
	 * @param logFileNamePattern
	 * @return
	 */
	public String getFileContent(String dirName, String logFileNamePattern) {
		String fileName = getLogFileNameToday(logFileNamePattern);
		if (mStorage.isFileExist(dirName, fileName)) {
			return mStorage.readTextFile(dirName, fileName);
		}
		return "";
	}

	/**
	 * Get the file name of the log of today. For example: log_app_25_04.txt
	 * 
	 * @param logFileNamePattern
	 * @return
	 */
	private String getLogFileNameToday(String logFileNamePattern) {
		String dateStr = DateTool.getString(DateTool.getNowDate(), Constants.DATE_LOG_DAY_FORMAT);
		String fileName = String.format(logFileNamePattern, dateStr);
		return fileName;
	}
	
	/**
	 * Get the file name of the log of today. For example: log_app_25_04 (12:04).txt
	 * 
	 * @param logFileNamePattern
	 * @return
	 */
	private String getLogFileNameTimeToday(String logFileNamePattern) {
		String dateStr = DateTool.getString(DateTool.getNowDate(), Constants.DATE_LOG_DAY_TIME_FORMAT);
		String fileName = String.format(logFileNamePattern, dateStr);
		return fileName;
	}

	/**
	 * Get file.
	 * 
	 * @param reportFolderName
	 * @param logFileNamePattern
	 * @return
	 */
	public File getFile(String reportFolderName, String logFileNamePattern) {
		String fileName = getLogFileNameToday(logFileNamePattern);
		return mStorage.getFile(reportFolderName, fileName);
	}

}
