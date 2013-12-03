package com.evme.logger.formatters;

import com.evme.logger.Log;
import com.evme.logger.entities.LogEntry;
import com.evme.logger.helpers.Utils;


public class SimpleLogEntryFormatter implements LogEntryFormatter {

	@Override
	public String format(LogEntry logEntry) {

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(logEntry.time);
		stringBuilder.append(", ");

		stringBuilder.append(Utils.getLogType(logEntry.type));
		stringBuilder.append(", ");

		switch (logEntry.type) {
		case Log.TRACE:
		case Log.INFO:
		case Log.DEBUG:
		case Log.WARNING:
		case Log.ERROR:

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
			
		case Log.SYSTEM:

			break;

		default:
			break;
		}

		return stringBuilder.toString();
	}

}
