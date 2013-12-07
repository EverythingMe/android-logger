package com.evme.logger.sample;

import java.util.Calendar;

import android.app.Application;

import com.evme.logger.Log;
import com.evme.logger.Log.Level;
import com.evme.logger.Log.Types;
import com.evme.logger.LogConfiguration;
import com.evme.logger.LogConfiguration.CacheTargetType;
import com.evme.logger.dispatchers.EmailReportDispatcher;
import com.evme.logger.formatters.SimpleLogEntryFormatter;
import com.evme.logger.receivers.BatteryReceiver;
import com.evme.logger.receivers.ScreenReceiver;
import com.evme.logger.reports.LogsFilter;
import com.evme.logger.reports.Report;

public class LoggerApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		// create filter for crash report
		LogsFilter logsFilter = new LogsFilter();
		logsFilter.setLogLevel(Level.TRACE);
		logsFilter.setLogTypes(Types.APP | Types.RECEIVER);
		logsFilter.setFromTime(Calendar.getInstance().getTimeInMillis() - 1000*60*60);

		// create crash report definition
		Report crashReport = new Report.Builder()
			.setIncludeDeviceInfo(true)
			.setMergeLogs(true)
			.setLogsFilter(logsFilter)
			.build();
		
		// create logger configuration
		LogConfiguration logConfiguration = new LogConfiguration.Builder(this)
			.addSystemReceiver(new BatteryReceiver())
			.addSystemReceiver(new ScreenReceiver())
			.addCrashDispatcher(new EmailReportDispatcher("roman@everything.me"))
			.setCrashReport(crashReport)
			.setLogPriority(Thread.MIN_PRIORITY)
			.setMemoryBufferSize(5)
			.setLogEntryFormatter(new SimpleLogEntryFormatter())
			.setCacheTargetType(CacheTargetType.EXTERNAL)
			.setMaxHistoryDays(7)
			.build();

		// set and start 
		Log.setConfiguration(logConfiguration);
		Log.start(this);

	}

}
