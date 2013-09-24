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

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class StatusChangeEventSupport {

	 private Logger logger = LoggerFactory.getLogger(getClass());

	 private CopyOnWriteArrayList<StatusChangeListener> listeners = new CopyOnWriteArrayList<StatusChangeListener>();

	 private List<StatusChangeListener> getListeners() {
		  if (listeners == null) {
				listeners = new CopyOnWriteArrayList<StatusChangeListener>();
		  }
		  return listeners;
	 }

	 public void addStatusChangeListener(StatusChangeListener listener) {
		  if (!getListeners().contains(listener)) {
				getListeners().add(listener);
				if (logger.isDebugEnabled()) {
					 logger.debug("Added status change Listener " + listener);
				}
		  }
	 }

	 public void removeStatusChangeListener(StatusChangeListener listener) {
		  getListeners().remove(listener);
		  if (logger.isDebugEnabled()) {
				logger.debug("Removed status change Listener " + listener);
		  }
	 }

	 public void tellAllListeners(StatusChangeEvent event) {
		  if (listeners == null) {
				return;
		  }
		  for (StatusChangeListener listener : listeners) {
				listener.statusChanged(event);
		  }
	 }

}
