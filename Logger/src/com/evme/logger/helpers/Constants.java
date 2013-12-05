package com.evme.logger.helpers;

import java.io.File;

public class Constants {

	public static final String ROOT = "Evme";
	public static final String DEBUG = ROOT + File.separator + "Debug";
	public static final String LOGS = DEBUG + File.separator + "Logs";
	public static final String REPORTS = DEBUG + File.separator + "Reports";
	public static final String APP = LOGS + File.separator + "App";
	public static final String RECEIVERS = LOGS + File.separator + "Receivers";
	public static final String APP_LOGS = "app_log.txt";
	public static final String MERGED_LOGS = "merged.txt";
	public static final String RECEIVER_LOGS = "receivers_log.txt";

	public static final String DATE_FORMAT = "dd-MM-yyyy HH:mm:ss.SSS";
	public static final String DATE_FORMAT_REGEX = "[0-9-]+-[0-9 :.]+[0-9]";

	public static final String LOG_PREFIX = "[#]";
}
