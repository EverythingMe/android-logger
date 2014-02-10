package me.everything.logger.formatters;

import me.everything.logger.Log.LogEntry;
import me.everything.logger.helpers.ExceptionSerializer;

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

	@Override
	public String getName() {
		return "Json Formatter";
	}

}
