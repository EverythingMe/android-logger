package com.evme.logger.dispatchers;

import com.evme.logger.entities.Report;

public class ServerCrashDispatcher implements CrashDispatcher {

	@Override
	public void dispatch(Report report) {
		// TODO Send report to server
	}

}
