package me.everything.logger.formatters;

import me.everything.logger.Log.LogEntry;

public interface LogEntryFormatter {

	String format(LogEntry logEntry);
	
	String getName();

}
