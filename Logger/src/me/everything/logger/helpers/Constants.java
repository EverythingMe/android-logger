package me.everything.logger.helpers;

import java.io.File;

public class Constants {

	// Directories
	public static String DIR_DEBUG(String root) {
		return root + File.separator + "Debug";
	}
	
	public static String DIR_LOGS(String root) {
		return DIR_DEBUG(root) + File.separator + "Logs";
	}
	
	public static String DIR_REPORTS(String root) {
		return DIR_DEBUG(root) + File.separator + "Reports";
	}
	
	public static String DIR_APP(String root) {
		return DIR_LOGS(root) + File.separator + "App";
	}
	
	public static String DIR_RECEIVERS(String root) {
		return DIR_LOGS(root) + File.separator + "Receivers";
	}

	// Files
	public static final String LOG_APP = "log_app_%s.txt";
	public static final String LOG_RECEIVER = "log_receivers_%s.txt";
	public static final String REPORT_MERGE = "log_merge.txt";
	public static final String DEVICE_INFO = "device_info.txt";
	public static final String CRASH = "crash.txt";

	// Dates & Regex
	public static final String LOG_PREFIX = "[#]";
	public static final String LOG_PREFIX_REGEX = "(\\[#\\])";
	public static final String DATE_LOG_DAY_FORMAT = "MM_dd";
	public static final String DATE_LOG_DAY_TIME_FORMAT = "MM_dd (HH:mm)";
	public static final String DATE_FORMAT = "dd-MM-yyyy HH:mm:ss.SSS";
	public static final String DATE_FORMAT_REGEX = "[0-9-]+-[0-9 :.]+[0-9]";
	public static final String EXCEPTION_STRING_SPLITTER = "\\sat\\s";

	// control
	public static final boolean PRINT_TO_LOG_CAT = true;
	
	// shared preferences

}
