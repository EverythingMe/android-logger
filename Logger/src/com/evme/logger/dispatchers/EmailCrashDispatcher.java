package com.evme.logger.dispatchers;

import com.evme.logger.entities.Report;

public class EmailCrashDispatcher implements CrashDispatcher {

	@Override
	public void dispatch(Report report) {
		// TODO - send report to email/s
	}

}
