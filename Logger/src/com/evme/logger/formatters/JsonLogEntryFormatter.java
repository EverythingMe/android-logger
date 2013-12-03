package com.evme.logger.formatters;

import com.evme.logger.entities.LogEntry;
import com.google.gson.Gson;


public class JsonLogEntryFormatter implements LogEntryFormatter {

	@Override
	public String format(LogEntry logEntry) {
		Gson gson = new Gson();
		return gson.toJson(logEntry);
	}

}
