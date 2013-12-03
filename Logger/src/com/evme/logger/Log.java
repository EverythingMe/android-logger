package com.evme.logger;

import java.util.Calendar;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Message;

import com.evme.logger.cache.Cache;
import com.evme.logger.entities.LogEntry;
import com.evme.logger.queues.LogQueueList;
import com.evme.logger.receivers.SystemReceiver;

public class Log implements Callback {

	private static final String LOG = "Logger";

	public static final int TRACE = 1;
	public static final int DEBUG = 2;
	public static final int INFO = 4;
	public static final int WARNING = 8;
	public static final int ERROR = 16;
	public static final int SYSTEM = 32;

	// instance and configuration
	private static Log mInstance = null;
	private static LogConfiguration mConfiguration;

	// thread and handler
	private static final int WHAT_FLUSH = 1001;
	private static final int WHAT_ADD_LOG = 1002;

	private final HandlerThread mHandlerThread;
	private final Handler mHandler;

	// queue
	private final LogQueueList mLogQueueList;
	private final int MAX_SIZE;

	private Log() {

		// create new thread
		mHandlerThread = new HandlerThread(LOG);
		mHandlerThread.setPriority(mConfiguration.getPriority());
		mHandlerThread.start();

		// send to thread to setup log folder and files
		mHandler = new Handler(mHandlerThread.getLooper(), this);

		// queue
		mLogQueueList = new LogQueueList();
		MAX_SIZE = mConfiguration.getQueueMaxSize();

	}

	private static Log getInstance() {
		if (mInstance == null) {
			mInstance = new Log();
		}
		return mInstance;
	}

	/**
	 * Set configuration for logger. <br>
	 * <br>
	 * <b>Important:</b> Configuration must be set before <code>Log()</code>
	 * constructor is called, otherwise logger want do its work.
	 * 
	 * @param configuration
	 */
	public static void setConfiguration(LogConfiguration configuration) {
		mConfiguration = configuration;
		Cache.setConfiguration(mConfiguration);
	}

	/**
	 * Start logging
	 */
	public static void start() {
		for (SystemReceiver systemReceiver : mConfiguration.getSystemReceivers()) {
			systemReceiver.register(mConfiguration.getContext());
		}
	}

	/**
	 * Stop logging
	 */
	public static void stop() {
		for (SystemReceiver systemReceiver : mConfiguration.getSystemReceivers()) {
			systemReceiver.unregister(mConfiguration.getContext());
		}
	}

	/**
	 * Will be used to trace the ‘running’ lines of code.This will be used in:
	 * <ul>
	 * <li>entering methods</li>
	 * <li>exiting methods</li>
	 * </ul>
	 * 
	 * @param object
	 *            The object that made the call for this method
	 * @param message
	 *            The message to log
	 */
	public static void t(Object object, String message) {
		getInstance().logImpl(TRACE, object, message);
	}

	/**
	 * Will be used to trace the ‘running’ lines of code.This will be used in:
	 * <ul>
	 * <li><b>entering</b> methods</li>
	 * <li><b>exiting</b> methods</li>
	 * </ul>
	 * 
	 * @param object
	 *            The object that made the call for this method
	 * @param pattern
	 *            The message to log with placeholder <b>{}</b>
	 * @param parameters
	 *            The parameters to be replaces with placeholder
	 */
	public static void t(Object object, String pattern, Object... parameters) {
		getInstance().logImpl(TRACE, object, pattern, parameters);
	}

	/**
	 * While implementing new feature or when we are not sure whereas to log
	 * this place or not. This will be used in:
	 * <ul>
	 * <li><b>new features</b> implementations and we want to put temporal
	 * logging</li>
	 * <li>we are <b>not sure</b> about the severity</li>
	 * </ul>
	 * 
	 * @param object
	 *            The object that made the call for this method
	 * @param message
	 *            The message to log
	 */
	public static void d(Object object, String message) {
		getInstance().logImpl(DEBUG, object, message);
	}

	/**
	 * While implementing new feature or when we are not sure whereas to log
	 * this place or not. This will be used in:
	 * <ul>
	 * <li><b>new features</b> implementations and we want to put temporal
	 * logging</li>
	 * <li>we are <b>not sure</b> about the severity</li>
	 * </ul>
	 * 
	 * @param object
	 *            The object that made the call for this method
	 * @param pattern
	 *            The message to log with placeholder <b>{}</b>
	 * @param parameters
	 *            The parameters to be replaces with placeholder
	 */
	public static void d(Object object, String pattern, Object... parameters) {
		getInstance().logImpl(DEBUG, object, pattern, parameters);
	}

	/**
	 * Important business process has finished. In ideal world, administrator or
	 * advanced user should be able to understand INFO messages and quickly find
	 * out what the application is doing.
	 * <ul>
	 * <li>
	 * <b>important information</b> about starting internal flow, about reached
	 * place, about conclusions</li>
	 * </ul>
	 * 
	 * @param object
	 *            The object that made the call for this method
	 * @param message
	 *            The message to log
	 */
	public static void i(Object object, String message) {
		getInstance().logImpl(INFO, object, message);
	}

	/**
	 * Important business process has finished. In ideal world, administrator or
	 * advanced user should be able to understand INFO messages and quickly find
	 * out what the application is doing.
	 * <ul>
	 * <li>
	 * <b>important information</b> about starting internal flow, about reached
	 * place, about conclusions</li>
	 * </ul>
	 * 
	 * @param object
	 *            The object that made the call for this method
	 * @param pattern
	 *            The message to log with placeholder <b>{}</b>
	 * @param parameters
	 *            The parameters to be replaces with placeholder
	 */
	public static void i(Object object, String pattern, Object... parameters) {
		getInstance().logImpl(INFO, object, pattern, parameters);
	}

	/**
	 * We reached some place where we should be, but we can overcome and
	 * continue with the flow.
	 * <ul>
	 * <li>silence the problem, <b>empty try-catch</b>. If you in the situation
	 * where the catch statement is empty, put here at least warning log</li>
	 * <li><b>workarounds</b> and <b>loop</b> implementations. For example, if
	 * we make make server call and didn’t get a response in X time, then we can
	 * call it two more times before giving up. This is the place to log warning
	 * message</li>
	 * <li><b>cache</b> problems. if we tried to get data from cache and didn’t
	 * succeed</li>
	 * </ul>
	 * 
	 * @param object
	 *            The object that made the call for this method
	 * @param message
	 *            The message to log
	 */
	public static void w(Object object, String message) {
		getInstance().logImpl(WARNING, object, message);
	}

	/**
	 * We reached some place where we should be, but we can overcome and
	 * continue with the flow.
	 * <ul>
	 * <li>silence the problem, <b>empty try-catch</b>. If you in the situation
	 * where the catch statement is empty, put here at least warning log</li>
	 * <li><b>workarounds</b> and <b>loop</b> implementations. For example, if
	 * we make make server call and didn’t get a response in X time, then we can
	 * call it two more times before giving up. This is the place to log warning
	 * message</li>
	 * <li><b>cache</b> problems. if we tried to get data from cache and didn’t
	 * succeed</li>
	 * </ul>
	 * 
	 * @param object
	 *            The object that made the call for this method
	 * @param pattern
	 *            The message to log with placeholder <b>{}</b>
	 * @param parameters
	 *            The parameters to be replaces with placeholder
	 */
	public static void w(Object object, String pattern, Object... parameters) {
		getInstance().logImpl(WARNING, object, pattern, parameters);
	}

	/**
	 * We reached some place where we should be, but we can overcome and
	 * continue with the flow.
	 * <ul>
	 * <li>silence the problem, <b>empty try-catch</b>. If you in the situation
	 * where the catch statement is empty, put here at least warning log</li>
	 * <li><b>workarounds</b> and <b>loop</b> implementations. For example, if
	 * we make make server call and didn’t get a response in X time, then we can
	 * call it two more times before giving up. This is the place to log warning
	 * message</li>
	 * <li><b>cache</b> problems. if we tried to get data from cache and didn’t
	 * succeed</li>
	 * </ul>
	 * 
	 * @param object
	 *            The object that made the call for this method
	 * @param message
	 *            The message to log
	 * @param throwable
	 *            The throwable to be logged with the message
	 */
	public static void w(Object object, String message, Throwable throwable) {
		getInstance().logImpl(WARNING, object, message, throwable);
	}

	/**
	 * Fatal errors which crash the application or make the flow really
	 * difficult to recover.
	 * <ul>
	 * <li><b>exception</b> is thrown</li>
	 * <li><b>server result</b> is really bad like: 500 or Error result state</li>
	 * <li><b>parsing</b> problems</li>
	 * </ul>
	 * 
	 * @param object
	 *            The object that made the call for this method
	 * @param message
	 *            The message to log
	 */
	public static void e(Object object, String message) {
		getInstance().logImpl(ERROR, object, message);
	}

	/**
	 * Fatal errors which crash the application or make the flow really
	 * difficult to recover.
	 * <ul>
	 * <li><b>exception</b> is thrown</li>
	 * <li><b>server result</b> is really bad like: 500 or Error result state</li>
	 * <li><b>parsing</b> problems</li>
	 * </ul>
	 * 
	 * @param object
	 *            The object that made the call for this method
	 * @param pattern
	 *            The message to log with placeholder <b>{}</b>
	 * @param parameters
	 *            The parameters to be replaces with placeholder
	 */
	public static void e(Object object, String pattern, Object... parameters) {
		getInstance().logImpl(ERROR, object, pattern, parameters);
	}

	/**
	 * Fatal errors which crash the application or make the flow really
	 * difficult to recover.
	 * <ul>
	 * <li><b>exception</b> is thrown</li>
	 * <li><b>server result</b> is really bad like: 500 or Error result state</li>
	 * <li><b>parsing</b> problems</li>
	 * </ul>
	 * 
	 * @param object
	 *            The object that made the call for this method
	 * @param message
	 *            The message to log
	 * @param throwable
	 *            The throwable to be logged with the message
	 */
	public static void e(Object object, String message, Throwable throwable) {
		getInstance().logImpl(ERROR, object, message, throwable);
	}

	/**
	 * Logs static system info
	 * 
	 * @param context
	 */
	public static void system(Context context) {
		// Bundle bundle = SystemInfo.getInstance(context).getBundle();
		// system("static", bundle);
	}

	/**
	 * Logs any system received information
	 * 
	 * @param name
	 *            The name of the system data. For example: battery, screen and
	 *            others.
	 * @param bundle
	 *            The parameters and the actual data which come from the system
	 */
	public static void system(String name, Bundle bundle) {
		LogEntry logEntry = new LogEntry();
		logEntry.type = SYSTEM;
		logEntry.bundle = bundle;

		getInstance().logImpl(logEntry);
	}

	public static void flushMemory() {
		getInstance().flushImpl();
	}
	
	private void flushImpl() {
		/*
		 * save all logs in batch on disk
		 */
		Cache.getInstance().flush(mLogQueueList);
		mLogQueueList.clear();
	}

	private void logImpl(int type, Object object, String message) {

		LogEntry logEntry = new LogEntry();
		logEntry.classname = object.getClass().getName();
		logEntry.thread = Thread.currentThread().getName();
		logEntry.type = type;
		logEntry.message = message;

		logImpl(logEntry);
	}

	private void logImpl(int type, Object object, String message, Throwable throwable) {

		LogEntry logEntry = new LogEntry();
		logEntry.classname = object.getClass().getName();
		logEntry.thread = Thread.currentThread().getName();
		logEntry.type = type;
		logEntry.message = message;
		logEntry.exception = throwable;

		logImpl(logEntry);
	}

	private void logImpl(int type, Object object, String pattern, Object... parameters) {

		LogEntry logEntry = new LogEntry();
		logEntry.classname = object.getClass().getName();
		logEntry.thread = Thread.currentThread().getName();
		logEntry.type = type;
		logEntry.pattern = pattern;
		logEntry.parameters = parameters;

		logImpl(logEntry);
	}

	private void logImpl(LogEntry logEntry) {

		// set time
		logEntry.time = Calendar.getInstance().getTimeInMillis();

		// post to new thread
		Message message = Message.obtain(mHandler, WHAT_ADD_LOG, logEntry);
		message.sendToTarget();

	}

	@Override
	public boolean handleMessage(Message msg) {

		int what = msg.what;
		switch (what) {
		case WHAT_FLUSH:

			/*
			 * save all logs in batch on disk
			 */
			flushMemory();
			break;

		case WHAT_ADD_LOG:

			/*
			 * get the log entry and add to queue
			 */
			LogEntry logEntry = (LogEntry) msg.obj;
			mLogQueueList.add(logEntry);

			/*
			 * if the size of the queue is in max size, then ask to dump the
			 * queue on disk
			 */
			if (mLogQueueList.size() == MAX_SIZE) {
				Message message = Message.obtain(mHandler, WHAT_FLUSH);
				mHandler.sendMessageAtFrontOfQueue(message);
			}

			break;

		default:
			break;
		}

		return false;
	}
}
