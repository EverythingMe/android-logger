package com.evme.logger.formatters;

import com.evme.logger.entities.LogEntry;

public interface LogEntryFormatter {

	String format(LogEntry logEntry);

}
