package com.evme.logger.dispatchers;

import com.evme.logger.reports.Report;

/**
 * @author sromku
 */
public interface ReportDispatcher {

	void dispatch(Report report);
}
