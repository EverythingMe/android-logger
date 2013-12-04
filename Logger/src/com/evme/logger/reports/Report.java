package com.evme.logger.reports;

import java.io.File;
import java.util.List;

import com.evme.logger.dispatchers.ReportDispatcher;

/**
 * The final report to be sent on crash or by demand.
 * 
 * @author sromku
 */
public class Report {

	private boolean mIncludeDeviceInfo = false;
	private LogsFilter mLogsFilter = null;
	private boolean mDoMerge = false;

	private Report(Builder builder) {
		this.mIncludeDeviceInfo = builder.mIncludeDeviceInfo;
		this.mLogsFilter = builder.mLogsFilter;
		this.mDoMerge = builder.mDoMerge;
	}

	/**
	 * Performs the report generation and return the files to be sent. <br>
	 * <br>
	 * <b>Note:</b> This is a heavy task and it also access the file system. Pay
	 * attention to call this method on thread safe environment.
	 * 
	 * @return
	 */
	public List<File> create() {
		ReportGenerator reportGenerator = new ReportGenerator();
		return reportGenerator.generate(this);
	}

	public static class Builder {

		private boolean mIncludeDeviceInfo = false;
		private LogsFilter mLogsFilter = null;
		private boolean mDoMerge;

		/**
		 * Set <code>True</code> if you want to include in the final report
		 * information about your device. Otherwise, set <code>False</code>
		 * 
		 * @return {@link Builder}
		 */
		public Builder setIncludeDeviceInfo(boolean include) {
			mIncludeDeviceInfo = include;
			return this;
		}

		/**
		 * Set <code>True</code> if the final report should be merged into one
		 * file ordered by times.
		 * 
		 * @param doMerge
		 * @return {@link Builder}
		 */
		public Builder setMergeLogs(boolean doMerge) {
			mDoMerge = doMerge;
			return this;
		}

		/**
		 * Add to the report logs which meet the filtering settings.
		 * 
		 * @param logsFilter
		 *            The filter
		 * @return {@link Builder}
		 */
		public Builder setLogsFilter(LogsFilter logsFilter) {
			mLogsFilter = logsFilter;
			return this;
		}

		/**
		 * Build the report description. <br>
		 * <br>
		 * <b>Note:</b> By calling this method the report is only configured and
		 * not generated. The generation phase will me done once the
		 * {@link ReportDispatcher} will be executed.
		 * 
		 * @return {@link Report}
		 */
		public Report build() {
			return new Report(this);
		}
	}

	/**
	 * Indicates if report should include information about the current device.
	 * 
	 * @return <code>True</code> if device information should be included in the
	 *         report, otherwise returned <code>False</code>
	 */
	public boolean getIncludeDeviceInfo() {
		return mIncludeDeviceInfo;
	}

	/**
	 * Gets the filters to be taken in consideration when the final report will
	 * be built.
	 * 
	 * @return {@link LogsFilter}
	 */
	public LogsFilter getLogsFilter() {
		return mLogsFilter;
	}

	/**
	 * Indicates if merging of logs by time should be done
	 * 
	 * @return 
	 */
	public boolean getMergeLogs() {
		return mDoMerge;
	}

}
