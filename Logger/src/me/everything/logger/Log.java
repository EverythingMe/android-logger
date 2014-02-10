package me.everything.logger;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import me.everything.logger.cache.Cache;
import me.everything.logger.dispatchers.ReportDispatcher;
import me.everything.logger.helpers.Constants;
import me.everything.logger.helpers.Utils;
import me.everything.logger.queues.LogQueueList;
import me.everything.logger.receivers.SystemReceiver;
import me.everything.logger.reports.Report;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.SparseArray;


public class Log implements Callback {

	private static final String LOG = "Logger";

	public static final class Level {

		public static final int VERBOSE = 1;
		public static final int DEBUG = 2;
		public static final int INFO = 4;
		public static final int WARNING = 8;
		public static final int ERROR = 16;

	}

	public static final class Types {
		public static final int APP = 1;
		public static final int RECEIVER = 2;
	}

	// instance and configuration
	private static Log mInstance = null;
	private static LogConfiguration mConfiguration;
	private static Context context;

	private static UncaughtExceptionHandler defaultUncaughtExceptionHandler;

	// thread and handler
	private static final int __FLUSH = 1001;
	private static final int __ADD_LOG = 1002;

	// looper thread
	private final HandlerThread mHandlerThread;
	private final Handler mHandler;

	// queue
	private final LogQueueList mLogQueueList;
	private final int MAX_SIZE;
	private AtomicInteger atomicInteger = new AtomicInteger(1);
	private SparseArray<PostTask> postTasks = new SparseArray<Log.PostTask>();

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

		// register all receivers
		for (SystemReceiver systemReceiver : mConfiguration.getSystemReceivers()) {
			systemReceiver.register(mConfiguration.getContext());
		}
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
		context = mConfiguration.getContext();
		Cache.setConfiguration(mConfiguration);
	}

	/**
	 * Start logger with default configuration
	 * 
	 * @param context
	 */
	public static void start(Context context) {

		if (mConfiguration == null) {
			mConfiguration = new LogConfiguration.Builder(context).build();
		}

		// set uncaught exception handling and unregister all receivers
		setUncaughtException();
	}

	public static void stop() {

		// unregister from system events
		if (mConfiguration != null) {
			List<SystemReceiver> systemReceivers = mConfiguration.getSystemReceivers();
			for (SystemReceiver systemReceiver : systemReceivers) {
				systemReceiver.unregister(context);
			}
		}

		// flush what is remained
		flushMemory(new PostTask() {

			@Override
			public void run() {

				if (!Looper.myLooper().equals(Looper.getMainLooper())) {

					// set the instance to null
					mInstance = null;

					// stop the logger looper thread
					Looper.myLooper().quit();
				}
			}
		});

	}
	
	public static void send() {
		// flush all logs from memory into disk
		flushMemory(new PostTask() {
			@Override
			public void run() {
				// get on demand report
				Report onDemandReport = mConfiguration.getOnDemandReport();
				// deliver to crash dispatchers
				List<ReportDispatcher> dispatchers = mConfiguration.getOnDemandDispatchers();
				for (ReportDispatcher reportDispatcher : dispatchers) {
					reportDispatcher.dispatch(onDemandReport);
				}
			}
		});
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
	public static void v(String tag, String message) {
		getInstance().logImpl(Level.VERBOSE, tag, message);
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
	public static void v(String tag, String pattern, Object... parameters) {
		getInstance().logImpl(Level.VERBOSE, tag, pattern, parameters);
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
	public static void d(String tag, String message) {
		getInstance().logImpl(Level.DEBUG, tag, message);
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
	public static void d(String tag, String pattern, Object... parameters) {
		getInstance().logImpl(Level.DEBUG, tag, pattern, parameters);
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
	public static void i(String tag, String message) {
		getInstance().logImpl(Level.INFO, tag, message);
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
	public static void i(String tag, String pattern, Object... parameters) {
		getInstance().logImpl(Level.INFO, tag, pattern, parameters);
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
	public static void w(String tag, String message) {
		getInstance().logImpl(Level.WARNING, tag, message);
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
	public static void w(String tag, String pattern, Object... parameters) {
		getInstance().logImpl(Level.WARNING, tag, pattern, parameters);
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
	public static void w(String tag, String message, Throwable throwable) {
		getInstance().logImpl(Level.WARNING, tag, message, throwable);
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
	public static void e(String tag, String message) {
		getInstance().logImpl(Level.ERROR, tag, message);
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
	public static void e(String tag, String pattern, Object... parameters) {
		getInstance().logImpl(Level.ERROR, tag, pattern, parameters);
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
	public static void e(String tag, String message, Throwable throwable) {
		getInstance().logImpl(Level.ERROR, tag, message, throwable);
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
		logEntry.type = Types.RECEIVER;
		logEntry.bundle = bundle;
		logEntry.name = name;

		getInstance().logImpl(logEntry);
	}

	/**
	 * Flush all logs from memory to disk after all logs are written
	 */
	public static void flushMemory(PostTask postTask) {
		getInstance().flushMemoryImpl(postTask);
	}

	/**
	 * Set handling uncaught exception
	 */
	private static void setUncaughtException() {
	
		defaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
	
		// crash handler
		UncaughtExceptionHandler handler = new Thread.UncaughtExceptionHandler() {
	
			@Override
			public void uncaughtException(final Thread t, final Throwable e) {
	
				// we check if the crash was because of the logger
				if (t.getName().equals(LOG)) {
					// currently do nothing, the logger crashed for some reason
					android.util.Log.e(LOG, "Logger thread crashed", e);
					stop();
				} else {
	
					// log crash exception
					Log.e(Log.class.getName(), "Crash", e);
	
					// flush all logs from memory into disk
					flushMemory(new PostTask() {
	
						@Override
						public void run() {
	
							// get crash report
							Report crashReport = mConfiguration.getCrashReport();
							crashReport.setCrashThrowable(e);
	
							// deliver to crash dispatchers
							List<ReportDispatcher> dispatchers = mConfiguration.getCrashDispatchers();
							for (ReportDispatcher reportDispatcher : dispatchers) {
								reportDispatcher.dispatch(crashReport);
							}
						}
					});
	
					// continue and show to the user the crash
					defaultUncaughtExceptionHandler.uncaughtException(t, e);
				}
			}
	
		};
	
		// set our crash handler
		Thread.setDefaultUncaughtExceptionHandler(handler);
	}

	private void flushMemoryImpl(PostTask postTask) {
		int postTaskKey = atomicInteger.getAndIncrement();
		postTasks.append(postTaskKey, postTask);
		Message message = Message.obtain(mHandler, __FLUSH, postTaskKey, 0);
		message.sendToTarget();
	}

	private void logImpl(int level, String tag, String message) {

		LogEntry logEntry = new LogEntry();
		logEntry.tag = tag;
		logEntry.thread = Thread.currentThread().getName();
		logEntry.level = level;
		logEntry.type = Types.APP;
		logEntry.message = message;

		logImpl(logEntry);
	}

	private void logImpl(int level, String tag, String message, Throwable throwable) {

		LogEntry logEntry = new LogEntry();
		logEntry.tag = tag;
		logEntry.thread = Thread.currentThread().getName();
		logEntry.level = level;
		logEntry.type = Types.APP;
		logEntry.message = message;
		logEntry.exception = throwable;

		logImpl(logEntry);
	}

	private void logImpl(int level, String tag, String pattern, Object... parameters) {

		LogEntry logEntry = new LogEntry();
		logEntry.tag = tag;
		logEntry.thread = Thread.currentThread().getName();
		logEntry.level = level;
		logEntry.type = Types.APP;
		logEntry.pattern = pattern;
		logEntry.parameters = parameters;

		logImpl(logEntry);
	}

	private void logImpl(LogEntry logEntry) {

		if (Constants.PRINT_TO_LOG_CAT) {
			Utils.printToLogcat(logEntry);
		}

		// set time
		logEntry.time = Calendar.getInstance().getTimeInMillis();

		// post to new thread
		Message message = Message.obtain(mHandler, __ADD_LOG, logEntry);
		message.sendToTarget();

	}

	@Override
	public boolean handleMessage(Message msg) {

		int what = msg.what;
		switch (what) {
		case __FLUSH:

			/*
			 * save all logs in batch on disk
			 */
			Cache.getInstance().flush(mLogQueueList);
			// we can clear the collection without being worried
			mLogQueueList.clear();

			// check for post task
			int taskKey = msg.arg1;
			PostTask postTask = postTasks.get(taskKey);
			if (postTask != null) {
				postTask.run();
				postTasks.remove(taskKey);
			}

			break;

		case __ADD_LOG:

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
				Message message = Message.obtain(mHandler, __FLUSH, -1, 0);
				mHandler.sendMessageAtFrontOfQueue(message);
			}

			break;

		default:
			break;
		}

		return false;
	}

	public static class LogEntry {

		public long time;
		public int level;
		public int type = -1;
		public String message;
		public String tag;
		public String thread;
		public String pattern;
		public Object[] parameters;
		public Throwable exception;
		public String name;
		public Bundle bundle;
	}

	/**
	 * This task will be executed on Log looper thread
	 * 
	 * @author sromku
	 * 
	 */
	public interface PostTask extends Runnable {

	}
}
