package com.evme.logger.formaters;

import com.evme.logger.entities.LogEntry;

public interface LogEntryFormatter {

	String format(LogEntry logEntry);

}
