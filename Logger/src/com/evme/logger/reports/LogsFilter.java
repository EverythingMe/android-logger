package com.evme.logger.reports;

import com.evme.logger.Log;

public class LogsFilter {

	private int mLogLevel;
	private int mLogTypes;
	private Long mFromTime = null;
	private boolean mLevelsExact = false;

	/**
	 * The level¤ of logs to be taken into account.<br>
	 * <ul>
	 * <li>
	 * {@link Log.Level#TRACE}</li>
	 * <li>
	 * {@link Log.Level#DEBUG}</li>
	 * <li>
	 * {@link Log.Level#INFO}</li>
	 * <li>
	 * {@link Log.Level#WARNING}</li>
	 * <li>
	 * {@link Log.Level#ERROR}</li>
	 * </ul>
	 * 
	 * @param level
	 */
	public void setLogLevel(int level) {
		mLogLevel = level;
	}

	/**
	 * Set the exact level of logs you want to filter.<br>
	 * <br>
	 * For example:
	 * 
	 * <pre>
	 * setLogLevelsExact(Level.TRACE | Level.DEBUG)
	 * </pre>
	 * 
	 * @param levels
	 */
	public void setLogLevelsExact(int levels) {
		mLogLevel = levels;
		mLevelsExact = true;
	}

	/**
	 * Set the time of logs to be filtered from
	 * 
	 * @param time
	 */
	public void setFromTime(long time) {
		mFromTime = time;
	}

	/**
	 * Set types of logs you want to filter.
	 * 
	 * @param types
	 */
	public void setLogTypes(int types) {
		mLogTypes = types;
	}

	/**
	 * Get the log type
	 * 
	 * @return
	 */
	public int getLogLevel() {
		return mLogLevel;
	}

	/**
	 * Get log types
	 * 
	 * @return
	 */
	public int getLogTypes() {
		return mLogTypes;
	}

	/**
	 * Is the logs filtering should take into account only exact levels of logs
	 * 
	 * @return
	 */
	public boolean isLogLevelsExact() {
		return mLevelsExact;
	}

	/**
	 * Get the time of logs to be filtered from
	 * 
	 * @return
	 */
	public Long getFromTime() {
		return mFromTime;
	}

}
