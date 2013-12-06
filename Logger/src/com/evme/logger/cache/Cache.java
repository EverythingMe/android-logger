package com.evme.logger.cache;

import java.io.File;
import java.util.List;

import com.evme.logger.Log;
import com.evme.logger.Log.LogEntry;
import com.evme.logger.LogConfiguration;
import com.evme.logger.formatters.LogEntryFormatter;
import com.evme.logger.helpers.Constants;
import com.evme.logger.tools.date.DateTool;
import com.evme.logger.tools.storage.IMemoryStorageTool;

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
	private static IMemoryStorageTool mStorage;
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

			switch (logEntry.type) {

			case Log.Types.RECEIVER:
				String formattedReceiverLog = logEntryFormatter.format(logEntry);
				appendFile(Constants.DIR_RECEIVERS, Constants.LOG_RECEIVER, formattedReceiverLog);
				break;

			case Log.Types.APP:
				String formattedAppLog = logEntryFormatter.format(logEntry);
				appendFile(Constants.DIR_APP, Constants.LOG_APP, formattedAppLog);
				break;
			}
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
	 * Append to log to file.
	 * 
	 * @param log
	 *            The log to add
	 * @param logFileNamePattern
	 *            The file pattern name
	 */
	public void appendFile(String dirName, String fileNamePattern, String log) {

		// we check if file is missing, if so we first create it
		String fileName = getLogFileNameToday(fileNamePattern);
		createFile(dirName, fileName);

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
		if (!mStorage.isFileExist(dir, file)) {
			mStorage.createFile(dir, file, "");
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
		return mStorage.readTextFile(dirName, fileName);
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
