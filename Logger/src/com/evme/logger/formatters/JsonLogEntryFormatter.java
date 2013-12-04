package com.evme.logger.formatters;

import com.evme.logger.Log.LogEntry;
import com.evme.logger.helpers.ExceptionSerializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonLogEntryFormatter implements LogEntryFormatter {

	private Gson gson;

	public JsonLogEntryFormatter() {

		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Throwable.class, new ExceptionSerializer());
		gson = gsonBuilder.create();

	}

	@Override
	public String format(LogEntry logEntry) {

		String json = gson.toJson(logEntry);
		return json;
		
	}

}
