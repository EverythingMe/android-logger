package me.everything.logger.dispatchers;

import me.everything.logger.reports.Report;

/**
 * @author sromku
 */
public interface ReportDispatcher {

	void dispatch(Report report);
	
	void setOnReportDispatchListener(OnReportDispatchListener onReportDispatchListener);
	
	public interface OnReportDispatchListener {
		void onStart();
		void onFinish();
	}
}
