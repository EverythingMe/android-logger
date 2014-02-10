package me.everything.logger.dispatchers;

import me.everything.logger.reports.Report;

/**
 * @author sromku
 */
public interface ReportDispatcher {

	void dispatch(Report report);
}
