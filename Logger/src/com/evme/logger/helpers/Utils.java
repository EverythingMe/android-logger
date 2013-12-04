package com.evme.logger.helpers;

import com.evme.logger.Log;

public class Utils {

	public static String getLogLevelName(int bits) {
		String type = "";
		if ((bits & Log.Level.TRACE) == Log.Level.TRACE) {
			type = "TRACE";
		} else if ((bits & Log.Level.DEBUG) == Log.Level.TRACE) {
			type = "DEBUG";
		} else if ((bits & Log.Level.INFO) == Log.Level.INFO) {
			type = "INFO";
		} else if ((bits & Log.Level.WARNING) == Log.Level.WARNING) {
			type = "WARNING";
		} else if ((bits & Log.Level.ERROR) == Log.Level.ERROR) {
			type = "ERROR";
		}
		return type;
	}

	public static Object getLogTypeName(int bits) {
		String type = "";
		if ((bits & Log.Types.APP) == Log.Types.APP) {
			type = "APP";
		} else if ((bits & Log.Types.RECEIVER) == Log.Types.RECEIVER) {
			type = "RECEIVER";
		}
		return type;
	}
}
