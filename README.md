android-logger
==============

Description and features will be explained later.

## Features
In process of development.

## Setup Project

TBE (To be explained)

## Usage

### Start Logger
To start logging add next lines in your `Application` class in `onCreate()` method. In fact, you can start the logger in any other place in your app, just take into consideration, that the logger will log only after starting it. Do it only once.

``` java
public class YourApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		
		// set very basic configuration
		LogConfiguration logConfiguration = new LogConfiguration.Builder(this)
			.build();
			
		// start logger
		Log.setConfiguration(logConfiguration);
		Log.start()
			
		...
	}
}
```

### Log 

The idea is not to make any difference between `android.util.Log` usage and this logger usage. But it still have few very minor differences to make it even more simpler for coding:

**Log levels**:
* TRACE
* DEBUG
* INFO
* WARNING
* ERROR

**Example:**

``` java
// simple text
Log.e(this, "Your error text message");

// text with parameters
Log.e(this, "Your {} text {} number {}", param1, param2, param3);
	
// simple text with exception
Log.e(this, "Your error text message", throwable);
```


## Configuration

Logger has many options of configuration. In addition, you can add your own implementations and adjust the logger for your needs. Let's go over all configuration options and what will you get for each of them.

### 1. Format the output
Set the output format of the logs. If you need your own structure, because you want to analyze or do any other action on the final logs, you would probably prefer them in specific structure.

Implement `LogEntryFormatter` and set in `LogConfiguration`.<br>
Out of the box formatters:
* `SimpleLogEntryFormatter`
* `JsonLogEntryFormatter`

**Example:**
``` java
// create logger configuration
LogConfiguration logConfiguration = new LogConfiguration.Builder(this)
	.setLogEntryFormatter(new JsonLogEntryFormatter())
	...
	.build();
```

### 2. System events
To have a better picture of what's going on in your application when something goes wrong, you would be glad to have information about battery, screen, wifi, network and many other device changes. This logger gives this option and will listen to a system intents. 

Implement `SystemReceiver` and set in `LogConfiguration`.<br>
Out of the box receivers:
* `BatteryReceiver`
* `ScreenReceiver`
 
**Example:**

``` java
// create logger configuration
LogConfiguration logConfiguration = new LogConfiguration.Builder(this)
	.addSystemReceiver(new BatteryReceiver())
	.addSystemReceiver(new ScreenReceiver())
	...
	.build();
```

### 3. Reporting
Report and send your logs if crash happened or just by demand. This logger library allows you to implement the dispatcher that will take the `Report` and send/share it to your server or any other place.

Implement `ReportDispatcher`<br>
Out of the box dispatchers:
* `EmailReportDispatcher`
* `DriveReportDispatcher`
 
#### Crash report

Once the crash happened, you would like to report it. Prepare the `Report` you want to send and set it in `LogConfiguration`.
 
**Prepare `Report` example:**

``` java
// create filter for crash report
LogsFilter logsFilter = new LogsFilter();
logsFilter.setLogLevel(Level.TRACE);
logsFilter.setLogTypes(Types.APP | Types.RECEIVER);
logsFilter.setFromTime(Calendar.getInstance().getTimeInMillis() - 1000 * 60 * 60);

// create crash report definition
Report crashReport = new Report.Builder()
    .setIncludeDeviceInfo(true)
    .setMergeLogs(true)
    .setLogsFilter(logsFilter)
    .build();
```

**Set crash configuration:**

``` java
// create logger configuration
LogConfiguration logConfiguration = new LogConfiguration.Builder(this)
    .addCrashDispatcher(new EmailReportDispatcher("roman@everything.me"))
    .setCrashReport(crashReport)
    ...
    .build();
```

### 4. More

- Set **thread priority** - The thread priority of the logger
- Set cache **memory buffer** log size - The size of number of logs in-memory
- Set **cache target** type:
	- Memory only - logs will be persisted in-memory only
	- Internal - logs will be flushed into internal disk
	- External - logs will be flushed into external disk if such exists
- Set **history** max days - The max number of history days of logs on disk	

**Example:**

``` java
// create logger configuration
LogConfiguration logConfiguration = new LogConfiguration.Builder(this)
    .setLogPriority(Thread.MIN_PRIORITY)
    .setMemoryBufferSize(100)
    .setCacheTargetType(CacheTargetType.EXTERNAL)
    .setMaxHistoryDays(7)
    ...
    .build();
```

[![Bitdeli Badge](https://d2weczhvl823v0.cloudfront.net/EverythingMe/android-logger/trend.png)](https://bitdeli.com/free "Bitdeli Badge")