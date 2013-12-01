package com.evme.logger.entities;

import android.os.Bundle;

public class LogEntry {

	public long time;
	public int type;
	public String message;
	public String classname;
	public String thread;
	public String pattern;
	public String[] parameters;
	public Throwable exception;
	public Bundle bundle;
}
