/*
 * Copyright 2013, The Sporting Exchange Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.betfair.tornjak.monitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An implementation of Monitor which calculates Status on demand.
 * <p/>
 * StatusChangeListeners can be registered and will be automatically notified of any changes.
 * <p/>
 * Subclasses only need implement checkStatus to return Status.OK or Status.FAIL
 * See FreeSpaceMonitor for an example implementation.
 *
 * @author scarrottr
 */
public abstract class OnDemandMonitor implements Monitor {

	private Logger logger = LoggerFactory.getLogger(getClass());
	private Status oldStatus = Status.FAIL;
	private StatusChangeEventSupport listenerHelper = new StatusChangeEventSupport();
	private Status maxImpact = Status.FAIL;

	protected abstract Status checkStatus() throws Exception;

	@Override
	public abstract String getName();

	@Override
	public void addStatusChangeListener(StatusChangeListener listener) {
		listenerHelper.addStatusChangeListener(listener);
	}

	@Override
	public void removeStatusChangeListener(StatusChangeListener listener) {
		listenerHelper.removeStatusChangeListener(listener);
	}

	private void tellAllListeners(StatusChangeEvent event) {
		listenerHelper.tellAllListeners(event);
	}

	@Override
	public Status getMaxImpactToOverallStatus() {
		return maxImpact;
	}

	public void setMaxImpactToOverallStatus(Status maxImpact) {
		this.maxImpact = maxImpact;
	}

	@Override
	public Status getStatus() {
		Status copyOfStatus = oldStatus;
		Status newStatus = Status.FAIL;
		try {
			newStatus = checkStatus();
		} catch (Exception ex) {
			newStatus = log(ex);
		}
		if (!newStatus.equals(copyOfStatus)) {
			tellAllListeners(new StatusChangeEvent(this, copyOfStatus, newStatus));
			oldStatus = newStatus;
		}
		return newStatus;
	}

	protected Status log(Exception e) {
		if (logger.isWarnEnabled()) {
			logger.warn(e.getMessage());
		}
		return Status.WARN;
	}

}
