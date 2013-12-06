package com.evme.logger.helpers;

import java.io.File;

public class Constants {

	// Directories
	public static final String DIR_ROOT = "Evme";
	public static final String DIR_DEBUG = DIR_ROOT + File.separator + "Debug";
	public static final String DIR_LOGS = DIR_DEBUG + File.separator + "Logs";
	public static final String DIR_REPORTS = DIR_DEBUG + File.separator + "Reports";
	public static final String DIR_APP = DIR_LOGS + File.separator + "App";
	public static final String DIR_RECEIVERS = DIR_LOGS + File.separator + "Receivers";

	// Logs
	public static final String LOG_APP = "log_app_%s.txt";
	public static final String LOG_RECEIVER = "log_receivers_%s.txt";

	// Report
	public static final String REPORT_MERGE = "log_merge.txt";

	// Dates & Regex
	public static final String LOG_PREFIX = "[#]";
	public static final String LOG_PREFIX_REGEX = "(\\[#\\])";
	public static final String DATE_LOG_DAY_FORMAT = "MM_dd";
	public static final String DATE_FORMAT = "dd-MM-yyyy HH:mm:ss.SSS";
	public static final String DATE_FORMAT_REGEX = "[0-9-]+-[0-9 :.]+[0-9]";

	// control
	public static final boolean PRINT_TO_LOG_CAT = true;

}
