package com.evme.logger.formatters;

import java.util.Date;

import com.evme.logger.Log;
import com.evme.logger.Log.LogEntry;
import com.evme.logger.Log.Types;
import com.evme.logger.helpers.Constants;
import com.evme.logger.helpers.Utils;
import com.evme.logger.tools.common.CommonTool;
import com.evme.logger.tools.date.DateTool;

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
			case Log.Level.TRACE:
			case Log.Level.INFO:
			case Log.Level.DEBUG:
			case Log.Level.WARNING:
			case Log.Level.ERROR:

				stringBuilder.append(Utils.getLogLevelName(logEntry.level));
				stringBuilder.append(", ");

				stringBuilder.append("class:");
				stringBuilder.append(logEntry.classname);
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
					stringBuilder.append(CommonTool.getStackTrace(logEntry.exception, "   "));
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
