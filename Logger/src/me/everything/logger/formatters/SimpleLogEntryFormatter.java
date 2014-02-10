package me.everything.logger.formatters;

import java.util.Date;

import me.everything.logger.Log;
import me.everything.logger.Log.LogEntry;
import me.everything.logger.Log.Types;
import me.everything.logger.helpers.Constants;
import me.everything.logger.helpers.Utils;
import me.everything.logger.tools.common.CommonTool;
import me.everything.logger.tools.date.DateTool;


public class SimpleLogEntryFormatter implements LogEntryFormatter {

	@Override
	public String format(LogEntry logEntry) {

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(Constants.LOG_PREFIX);
		
		String niceDate = DateTool.getString(new Date(logEntry.time), Constants.DATE_FORMAT);
		stringBuilder.append(" ");
		stringBuilder.append(niceDate);
		stringBuilder.append(": ");

		stringBuilder.append(Utils.getLogTypeName(logEntry.type));
		stringBuilder.append(",");

		switch (logEntry.type) {
		case Types.APP:
			// The log is related to app flow

			switch (logEntry.level) {
			case Log.Level.VERBOSE:
			case Log.Level.INFO:
			case Log.Level.DEBUG:
			case Log.Level.WARNING:
			case Log.Level.ERROR:

				stringBuilder.append(Utils.getLogLevelName(logEntry.level));
				stringBuilder.append(", ");

				stringBuilder.append("tag:");
				stringBuilder.append(logEntry.tag);
				stringBuilder.append(", ");

				stringBuilder.append("thread:");
				stringBuilder.append(logEntry.thread);
				stringBuilder.append(", ");

				stringBuilder.append("message:");
				if (logEntry.message == null) {
					stringBuilder.append(Utils.buildString(logEntry.pattern, logEntry.parameters));
				} else {
					stringBuilder.append(logEntry.message);
				}

				if (logEntry.exception != null) {
					stringBuilder.append(", ");
					stringBuilder.append("exception:");
					stringBuilder.append("\n");
					stringBuilder.append(CommonTool.getStackTrace(logEntry.exception));
				}

				break;

			default:
				break;
			}

			break;
		case Types.RECEIVER:

			String strBundle = Utils.toString(logEntry.bundle);
			stringBuilder.append(" ");
			stringBuilder.append("name:"); 
			stringBuilder.append(logEntry.name);
			stringBuilder.append(", ");
			stringBuilder.append(strBundle);

			break;
		default:
			break;
		}

		return stringBuilder.toString();
	}

	@Override
	public String getName() {
		return "Simple Formatter";
	}

}
