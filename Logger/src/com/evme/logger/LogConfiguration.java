package com.evme.logger;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Build;

import com.evme.logger.dispatchers.ReportDispatcher;
import com.evme.logger.formatters.LogEntryFormatter;
import com.evme.logger.formatters.SimpleLogEntryFormatter;
import com.evme.logger.queues.LogQueueList;
import com.evme.logger.receivers.SystemReceiver;
import com.evme.logger.reports.Report;
import com.evme.logger.reports.Report.ReportType;
import com.sromku.simple.storage.SimpleStorage;
import com.sromku.simple.storage.Storage;

/**
 * Configuration of the logger
 * 
 * @author sromku
 */
public class LogConfiguration {

	public static final int DEFAULT_MEMORY_BUFFER_SIZE = 20;
	public static final int DEFAULT_HISTORY_DAYS = 3;
	public static final double DEFAULT_MAX_FILE_SIZE_MB = 2d;
	public static final double DEFAULT_LIMIT_FILES_SIZE_MB = 10d;
	
	private final List<ReportDispatcher> mCrashDispatchers;
	private final LogEntryFormatter mLogEntryFormatter;
	private final List<SystemReceiver> mReceivers;
	private int mQueueMaxSize;
	private int mThreadPriority;
	private Report mCrashReport;
	private boolean mIsInMemoryOnly;
	private Storage mStorage;
	private final Context mContext;
	private int mHistoryDays;
	private double mFileMaxMbSize;
	private double mFilesDayMbSizeLimit;

	private LogConfiguration(Builder builder) {
		this.mReceivers = builder.mReceivers;
		this.mCrashDispatchers = builder.mCrashDispatchers;
		this.mContext = builder.mContext;
		this.mLogEntryFormatter = builder.mLogEntryFormatter;
		this.mQueueMaxSize = builder.mMemoryBufferSize;
		this.mThreadPriority = builder.mThreadPriority;
		this.mCrashReport = builder.mCrashReport;
		this.mHistoryDays = builder.mHistoryDays;
		this.mFileMaxMbSize = builder.mFileMaxMbSize;
		this.mFilesDayMbSizeLimit = builder.mFilesDayMbSizeLimit;

		switch (builder.mCacheTargetType) {
		case MEMORY:
			mIsInMemoryOnly = true;
			break;
		case INTERNAL:
			mIsInMemoryOnly = false;
			mStorage = SimpleStorage.getInternalStorage(mContext);
			break;
		case EXTERNAL:
			mIsInMemoryOnly = false;
			if (SimpleStorage.isExternalStorageWritable()) {
				mStorage = SimpleStorage.getExternalStorage();
			}
			else {
				mStorage = SimpleStorage.getInternalStorage(mContext);
			}
			break;
		default:
			break;
		}
	}

	public enum CacheTargetType {
		MEMORY,
		INTERNAL,
		EXTERNAL
	}

	/**
	 * Get application context
	 * 
	 * @return {@link Context}
	 */
	public Context getContext() {
		return mContext;
	}

	/**
	 * Get list of system events
	 * 
	 * @return List of system events
	 */
	public List<SystemReceiver> getSystemReceivers() {
		return mReceivers;
	}

	/**
	 * Get the output formatter of the log
	 * 
	 * @return {@link LogEntryFormatter}
	 */
	public LogEntryFormatter getLogEntryFormatter() {
		return mLogEntryFormatter;
	}

	/**
	 * Get thread priority of the logger
	 * 
	 * @return The thread priority of logger
	 */
	public int getPriority() {
		return mThreadPriority;
	}

	/**
	 * Get max queue size. <br>
	 * In fact, this is a buffer of logs to be saved in the memory before being
	 * flushed to disk.
	 * 
	 * @return
	 */
	public int getQueueMaxSize() {
		return mQueueMaxSize;
	}

	/**
	 * Get crash dispatchers
	 * 
	 * @return The list of crash dispatchers
	 */
	public List<ReportDispatcher> getCrashDispatchers() {
		return mCrashDispatchers;
	}

	/**
	 * Get crash report definition
	 * 
	 * @return {@link Report}
	 */
	public Report getCrashReport() {
		return mCrashReport;
	}

	/**
	 * Return <code>True</code> if the logs will be saved only to memory without
	 * being flushed to disk.
	 * 
	 * @return <code>True</code> if the logs will be saved only.
	 */
	public boolean isInMemoryOnly() {
		return mIsInMemoryOnly;
	}

	/**
	 * Get the place where the logs will be saved.
	 * 
	 * @return {@link IMemoryStorageTool}
	 */
	public Storage getStorage() {
		return mStorage;
	}

	/**
	 * Get the number of days the logs will be saved on disk.
	 * 
	 * @return The number of days the logs will be saved on disk
	 */
	public int getMaxHistoryDays() {
		return mHistoryDays;
	}

	/**
	 * Get the max size of the file that will hold the log data.
	 * 
	 * @return Max file size in MB
	 */
	public double getFileMaxMbSize() {
		return mFileMaxMbSize;
	}

	/**
	 * Get the maximum size in MB of all files that are created for the same
	 * day. It is important set the values such that you will know exactly how
	 * much memory you can use.
	 * 
	 * @return Limit of the MB to use in the same day
	 */
	public double getFilesDayMbSizeLimit() {
		return mFilesDayMbSizeLimit;
	}

	public static class Builder {

		private List<ReportDispatcher> mCrashDispatchers = new ArrayList<ReportDispatcher>();
		private List<SystemReceiver> mReceivers = new ArrayList<SystemReceiver>();
		private LogEntryFormatter mLogEntryFormatter = new SimpleLogEntryFormatter();
		private int mThreadPriority = Thread.MIN_PRIORITY;
		private CacheTargetType mCacheTargetType = CacheTargetType.EXTERNAL;
		private Integer mMemoryBufferSize = DEFAULT_MEMORY_BUFFER_SIZE;
		private Report mCrashReport;
		private Context mContext;
		private int mHistoryDays = DEFAULT_HISTORY_DAYS;
		private double mFileMaxMbSize = DEFAULT_MAX_FILE_SIZE_MB;
		private double mFilesDayMbSizeLimit = DEFAULT_LIMIT_FILES_SIZE_MB;

		public Builder(Context context) {
			this.mContext = context;
		}

		/**
		 * Add system receiver that will listen and add logs of the environment
		 * 
		 * @param systemLogger
		 * @return
		 */
		public Builder addSystemReceiver(SystemReceiver systemLogger) {
			mReceivers.add(systemLogger);
			return this;
		}

		/**
		 * Add dispatchers that will deliver the crash reports automatically,
		 * once crash occurred.
		 * 
		 * @param crashDispatcher
		 * @return
		 */
		public Builder addCrashDispatcher(ReportDispatcher crashDispatcher) {
			mCrashDispatchers.add(crashDispatcher);
			return this;
		}

		/**
		 * Create and define the report to be on crash event
		 * 
		 * @param report
		 * @return
		 */
		public Builder setCrashReport(Report report) {
			this.mCrashReport = report;
			this.mCrashReport.setReportType(ReportType.CRASH);
			return this;
		}

		/**
		 * Set logger root path
		 * 
		 * @param path
		 * @return
		 */
		public Builder setLogRootPath(String path) {
			throw new RuntimeException("unsupported");
		}

		/**
		 * Set the log main directory name
		 * 
		 * @param directoryName
		 * @return
		 */
		public Builder setLogRootDirectory(String directoryName) {
			throw new RuntimeException("unsupported");
		}

		/**
		 * Set in which format you want to see the logs
		 * 
		 * @param entryFormatter
		 * @return
		 */
		public Builder setLogEntryFormatter(LogEntryFormatter entryFormatter) {
			this.mLogEntryFormatter = entryFormatter;
			return this;
		}

		/**
		 * Set the implementation of the queue of logs
		 * 
		 * @param logQueueList
		 * @return
		 */
		public Builder setLogQueueList(LogQueueList logQueueList) {
			throw new RuntimeException("unsupported");
		}

		/**
		 * Set the max size of the logs to be saved in memory
		 * 
		 * @param size
		 * @return
		 */
		public Builder setMemoryBufferSize(Integer size) {
			this.mMemoryBufferSize = size;
			return this;
		}

		/**
		 * Set log priority thread
		 * 
		 * @param priority
		 * @return
		 */
		public Builder setLogPriority(int priority) {
			mThreadPriority = priority;
			return this;
		}

		/**
		 * Set where do you prefer to save logs. <br>
		 * <br>
		 * <b>Note:</b>
		 * <ul>
		 * <li>Setting {@link CacheTargetType#MEMORY} means that the logs will
		 * saved in a memory buffer only. <br>
		 * <b>Important:</b> In this case set the buffer to be big enough for
		 * being able to investigate problems on crash reports.
		 * <li>Setting {@link CacheTargetType#INTERNAL} means that the logs will
		 * be flushed to internal memory only.</li>
		 * <li>Setting {@link CacheTargetType#EXTERNAL} means that the logger
		 * will check if external memory exists, if so then the logs will be
		 * flushed to external memory. If no external memory exists on the
		 * device, then logs will be flushed to internal memory.</li>
		 * 
		 * @param cacheTargetType
		 *            {@link CacheTargetType}
		 * @return {@link Build}
		 */
		public Builder setCacheTargetType(CacheTargetType cacheTargetType) {
			mCacheTargetType = cacheTargetType;
			return this;
		}

		/**
		 * Return maximum number of days that the logs will be saved on disk. <br>
		 * The old logs will be deleted.
		 * 
		 * @param days
		 * @return {@link Build}
		 */
		public Builder setMaxHistoryDays(int days) {
			mHistoryDays = days;
			return this;
		}
		
		/**
		 * Set the max size of the file that will hold the log data.
		 * 
		 * @return Max file size in MB
		 */
		public void setFileMaxMbSize(double fileMaxMbSize) {
			mFileMaxMbSize = fileMaxMbSize;
		}

		/**
		 * Set the maximum size in MB of all files that are created for the same
		 * day. It is important set the values such that you will know exactly how
		 * much memory you can use.
		 * 
		 * @return Limit of the MB to use in the same day
		 */
		public void setFilesDayMbSizeLimit(double filesDayMbSizeLimit) {
			mFilesDayMbSizeLimit = filesDayMbSizeLimit;
		}

		public LogConfiguration build() {
			return new LogConfiguration(this);
		}
	}

}
