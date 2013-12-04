package com.evme.logger.reports;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.evme.logger.cache.Cache;

public class ReportGenerator {

	public List<File> generate(Report report) {

		List<File> results = new ArrayList<File>();

		/*
		 * TODO - currently takes all without filtering options
		 * create report folder with current time
		 */
		
		results.add(Cache.getInstance().getAppLogFile());
		results.add(Cache.getInstance().getReceiverFile());
		
		// copy the APP/LOG file and put into report folder
		// copy the RECEIVER/LOG file and put into report folder
		// create device info file and put into report folder
		
		// get all files from report new folder
		
		return results;

	}

}
