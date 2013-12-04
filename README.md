android-logger
==============
In process of development

## Configuration

	``` java
    // create filter for crash report
	LogsFilter logsFilter = new LogsFilter();
	logsFilter.setLogLevel(Level.TRACE);
	logsFilter.setLogTypes(Types.APP | Types.RECEIVER);
	logsFilter.setStartTime(SystemClock.currentThreadTimeMillis() - 1000 * 60 * 60);

	// create crash report definition
	Report crashReport = new Report.Builder()
			.setIncludeDeviceInfo(true)
			.setMergeLogs(false)
			.setLogsFilter(logsFilter)
			.build();
		
		// create logger configuration
	LogConfiguration logConfiguration = new LogConfiguration.Builder(this)
			.addSystemReceiver(new BatteryReceiver())
			.addSystemReceiver(new ScreenReceiver())
			.addCrashDispatcher(new EmailReportDispatcher("roman@everything.me"))
			.setCrashReport(crashReport)
			.setLogPriority(Thread.MIN_PRIORITY)
			.setLogQueueListMaxSize(100)
			.setLogEntryFormatter(new JsonLogEntryFormatter())
			.build();

		// set and start 
		Log.setConfiguration(logConfiguration);
		Log.start();
	```

[![Bitdeli Badge](https://d2weczhvl823v0.cloudfront.net/EverythingMe/android-logger/trend.png)](https://bitdeli.com/free "Bitdeli Badge")

