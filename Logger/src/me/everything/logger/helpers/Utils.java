package me.everything.logger.helpers;

import java.util.Set;

import me.everything.logger.Log;
import me.everything.logger.Log.Level;
import me.everything.logger.Log.LogEntry;
import me.everything.logger.Log.Types;
import android.os.Bundle;

public class Utils {

	public static String getLogLevelName(int bits) {
		String type = "";
		if ((bits & Log.Level.VERBOSE) == Log.Level.VERBOSE) {
			type = "VERBOSE";
		} else if ((bits & Log.Level.DEBUG) == Log.Level.DEBUG) {
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

	public static String buildString(String pattern, Object[] parameters) {

		for (Object object : parameters) {
			pattern = pattern.replaceFirst("[{}]+", String.valueOf(object));
		}

		return pattern;
	}

	public static String toString(Bundle bundle) {

		StringBuilder stringBuilder = new StringBuilder();

		Set<String> keySet = bundle.keySet();
		int size = keySet.size();
		int i = 0;
		for (String key : keySet) {
			stringBuilder.append(key);
			stringBuilder.append(":");
			stringBuilder.append(bundle.get(key));
			if (i != size - 1) {
				stringBuilder.append(", ");
			}
			i++;
		}

		return stringBuilder.toString();
	}

	/**
	 * Print to logcat
	 * 
	 * @param logEntry
	 */
	public static void printToLogcat(LogEntry logEntry) {

		// set TAG and message
		String TAG = logEntry.tag;

		switch (logEntry.type) {
		case Types.APP:

			String message = "";
			if (logEntry.message != null) {
				message = logEntry.message;
			} else {
				message = buildString(logEntry.pattern, logEntry.parameters);
			}

			// print based on log level
			switch (logEntry.level) {
			case Level.VERBOSE:
				android.util.Log.v(TAG, message);
				break;
			case Level.DEBUG:
				android.util.Log.d(TAG, message);
				break;
			case Level.INFO:
				android.util.Log.i(TAG, message);
				break;
			case Level.WARNING:
				if (logEntry.exception == null) {
					android.util.Log.w(TAG, message);
				} else {
					android.util.Log.w(TAG, message, logEntry.exception);
				}
				break;
			case Level.ERROR:
				if (logEntry.exception == null) {
					android.util.Log.e(TAG, message);
				} else {
					android.util.Log.e(TAG, message, logEntry.exception);
				}
				break;
			default:
				break;
			}

			break;

		case Types.RECEIVER:
			android.util.Log.i(TAG, logEntry.name + ": " + Utils.toString(logEntry.bundle));
			break;

		default:
			break;
		}

	}
}
