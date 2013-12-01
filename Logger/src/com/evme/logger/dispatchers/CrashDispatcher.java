package com.evme.logger.dispatchers;

import com.evme.logger.entities.Report;

/**
 * @author sromku
 */
public interface CrashDispatcher {

	void dispatch(Report report);
}
