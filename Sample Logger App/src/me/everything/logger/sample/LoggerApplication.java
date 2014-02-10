package me.everything.logger.sample;

import java.util.Calendar;

import me.everything.logger.Log;
import me.everything.logger.Log.Level;
import me.everything.logger.Log.Types;
import me.everything.logger.LogConfiguration;
import me.everything.logger.LogConfiguration.CacheTargetType;
import me.everything.logger.dispatchers.EmailReportDispatcher;
import me.everything.logger.formatters.SimpleLogEntryFormatter;
import me.everything.logger.receivers.BatteryReceiver;
import me.everything.logger.receivers.ScreenReceiver;
import me.everything.logger.reports.LogsFilter;
import me.everything.logger.reports.Report;
import android.app.Application;

public class LoggerApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		// create filter for crash report
		LogsFilter logsFilter = new LogsFilter();
		logsFilter.setLogLevel(Level.VERBOSE);
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
			.addOnDemandDispatcher(new EmailReportDispatcher("roman@everything.me"))
			.setLoggerRootDirectory("SAMPLE APP")
			.setCrashReport(crashReport)
			.setLogPriority(Thread.MIN_PRIORITY)
			.setMemoryBufferSize(5)
			.setLogEntryFormatter(new SimpleLogEntryFormatter())
			.setCacheTargetType(CacheTargetType.EXTERNAL)
			.setMaxHistoryDays(7)
			.setFileMaxMbSize(1)
			.setFilesDayMbSizeLimit(10)
			.build();

		// set and start 
		Log.setConfiguration(logConfiguration);  
		Log.start(this);

	}

}
