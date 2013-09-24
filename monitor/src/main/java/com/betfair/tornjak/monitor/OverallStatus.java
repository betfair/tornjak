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


import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Bean to represent and expose the overall status of a set of monitors.
 * <p/>
 * A {@link #setMonitorRegistry(MonitorRegistry)} must be provided.
 * <p/>
 * Note that this bean can return {@link Status#WARN}.
 */
public class OverallStatus implements StatusAggregator, MonitorRegistryAware, OverallStatusMBean {

	 private Logger logger = LoggerFactory.getLogger(getClass());

	 private MonitorRegistry monitorRegistry = null;

	 private AtomicReference<AggregatedStatus> status = new AtomicReference<AggregatedStatus>();

	 public OverallStatus(MonitorRegistry monitorRegistry) {
		  this();
		  this.monitorRegistry = monitorRegistry;
	 }

	 public OverallStatus() {
		  status.set(new AggregatedStatus());
	 }

	 private StatusChangeEventSupport listenerHelper = new StatusChangeEventSupport();

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
	 public Status getStatus() {

		  Status old = status.get().getStatus();

		  AggregatedStatus aggregate = new AggregatedStatus();
		  if (monitorRegistry != null) {
				for (Monitor m : monitorRegistry.getMonitorSet()) {
					 aggregate.accountFor(m.getStatus(), m.getMaxImpactToOverallStatus());
				}
		  }
		  status.set(aggregate);
		  if (!aggregate.getStatus().equals(old)) {
				tellAllListeners(new StatusChangeEvent(this, old, aggregate.getStatus()));
		  }
		  Status result = status.get().getStatus();
		  if (logger.isDebugEnabled()) {
				logger.debug("Returning status: " + result);
		  }
		  return result;
	 }

	 @Override
	 public String getStatusAsString() {
		  return getStatus().name();
	 }

	 @Override
	 public int getOkCount() {
		  return status.get().getOkCount();
	 }

	 @Override
	 public int getWarnCount() {
		  return status.get().getWarnCount();
	 }

	 @Override
	 public int getFailCount() {
		  return status.get().getFailCount();
	 }

	 public void setMonitorRegistry(MonitorRegistry monitorRegistry) {
		  this.monitorRegistry = monitorRegistry;
	 }

}