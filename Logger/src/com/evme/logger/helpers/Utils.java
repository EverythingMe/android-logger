package com.evme.logger.helpers;

import com.evme.logger.Log;

public class Utils {

	public static String getLogType(int bits) {
		String type = "";
		if ((bits & Log.TRACE) == Log.TRACE) {
			type = "TRACE";
		} else if ((bits & Log.DEBUG) == Log.TRACE) {
			type = "DEBUG";
		} else if ((bits & Log.INFO) == Log.INFO) {
			type = "INFO";
		} else if ((bits & Log.WARNING) == Log.WARNING) {
			type = "WARNING";
		} else if ((bits & Log.ERROR) == Log.ERROR) {
			type = "ERROR";
		} else if ((bits & Log.SYSTEM) == Log.SYSTEM) {
			type = "SYSTEM";
		}
		return type;
	}
}
