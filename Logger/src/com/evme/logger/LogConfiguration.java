package com.evme.logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Handler;

import com.evme.logger.dispatchers.ReportDispatcher;
import com.evme.logger.formatters.LogEntryFormatter;
import com.evme.logger.formatters.SimpleLogEntryFormatter;
import com.evme.logger.queues.LogQueueList;
import com.evme.logger.receivers.SystemReceiver;
import com.evme.logger.reports.Report;

public class LogConfiguration {

	private final Context context;
	private final List<SystemReceiver> receivers;
	private final List<ReportDispatcher> crashDispatchers;
	private final LogEntryFormatter logEntryFormatter;
	private int queueMaxSize;
	private int threadPriority;
	private Report crashReport;

	private LogConfiguration(Builder builder) {
		this.receivers = builder.receivers;
		this.crashDispatchers = builder.crashDispatchers;
		this.context = builder.context;
		this.logEntryFormatter = builder.logEntryFormatter;
		this.queueMaxSize = builder.queueMaxSize;
		this.threadPriority = builder.threadPriority;
		this.crashReport = builder.crashReport;
	}

	public Context getContext() {
		return context;
	}

	public List<SystemReceiver> getSystemReceivers() {
		return receivers;
	}

	public LogEntryFormatter getLogEntryFormatter() {
		return logEntryFormatter;
	}

	public int getPriority() {
		return threadPriority;
	}

	public int getQueueMaxSize() {
		return queueMaxSize;
	}

	public List<ReportDispatcher> getCrashDispatchers() {
		return crashDispatchers;
	}

	public Report getCrashReport() {
		return crashReport;
	}

	public static class Builder {

		private Context context;
		private List<SystemReceiver> receivers = new ArrayList<SystemReceiver>();
		private List<ReportDispatcher> crashDispatchers = new ArrayList<ReportDispatcher>();
		private List<Handler> handlers = new ArrayList<Handler>();
		private LogEntryFormatter logEntryFormatter = new SimpleLogEntryFormatter();
		private Integer queueMaxSize = 5;
		private int threadPriority = Thread.MIN_PRIORITY;
		private Report crashReport;

		public Builder(Context context) {
			this.context = context;
		}

		/**
		 * Add system receiver that will listen and add logs of the environment
		 * 
		 * @param systemLogger
		 * @return
		 */
		public Builder addSystemReceiver(SystemReceiver systemLogger) {
			receivers.add(systemLogger);
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
			crashDispatchers.add(crashDispatcher);
			return this;
		}

		/**
		 * Add handlers if you want to catch logs
		 * 
		 * @param handler
		 * @return
		 */
		public Builder addCallbackHandler(Handler handler) {
			handlers.add(handler);
			return this;
		}

		/**
		 * Create and define the report to be on crash event
		 * 
		 * @param report
		 * @return
		 */
		public Builder setCrashReport(Report report) {
			this.crashReport = report;
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
			this.logEntryFormatter = entryFormatter;
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
		 * Set the max size of the logs queue
		 * 
		 * @param size
		 * @return
		 */
		public Builder setLogQueueListMaxSize(Integer size) {
			this.queueMaxSize = size;
			return this;
		}

		/**
		 * Set log priority thread
		 * 
		 * @param priority
		 * @return
		 */
		public Builder setLogPriority(int priority) {
			threadPriority = priority;
			return this;
		}

		/**
		 * Instead or in addition to printing com.something.ClassName, this
		 * mapping will print the value you actually want. This can help in
		 * additional filtering and analyzing options
		 * 
		 * @param map
		 * @return
		 */
		public Builder setLogClassOutputMapping(Map<String, String> map) {
			throw new RuntimeException("unsupported");
		}

		public LogConfiguration build() {
			return new LogConfiguration(this);
		}
	}

}
