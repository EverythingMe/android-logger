package com.evme.logger.formatters;

import com.evme.logger.Log.LogEntry;

public interface LogEntryFormatter {

	String format(LogEntry logEntry);
	
	String getName();

}
