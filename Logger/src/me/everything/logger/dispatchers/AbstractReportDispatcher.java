package me.everything.logger.dispatchers;


public abstract class AbstractReportDispatcher implements ReportDispatcher {

	private OnReportDispatchListener mOnReportDispatchListener;

	@Override
	public void setOnReportDispatchListener(OnReportDispatchListener onReportDispatchListener) {
		mOnReportDispatchListener = onReportDispatchListener;
	}
	
	protected void startDispatching() {
		mOnReportDispatchListener.onStart();
	}
	
	protected void stopDispatching() {
		mOnReportDispatchListener.onFinish();
	}

}
