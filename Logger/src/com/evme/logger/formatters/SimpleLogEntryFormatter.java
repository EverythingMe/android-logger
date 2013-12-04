package com.evme.logger.formatters;

import com.evme.logger.Log;
import com.evme.logger.Log.LogEntry;
import com.evme.logger.helpers.Utils;

public class SimpleLogEntryFormatter implements LogEntryFormatter {

	@Override
	public String format(LogEntry logEntry) {

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(logEntry.time);
		stringBuilder.append(", ");

		stringBuilder.append(Utils.getLogTypeName(logEntry.type));
		stringBuilder.append(", ");

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
			stringBuilder.append(logEntry.message);

			if (logEntry.exception != null) {
				stringBuilder.append(", ");
				stringBuilder.append(",exception:");
				stringBuilder.append(logEntry.exception);
			}

			break;

		default:
			break;
		}

		return stringBuilder.toString();
	}

}
