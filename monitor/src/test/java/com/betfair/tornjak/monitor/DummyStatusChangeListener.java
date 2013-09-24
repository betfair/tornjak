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

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * User: scarrottr
 */
public class DummyStatusChangeListener implements StatusChangeListener {

	 private StatusChangeEvent expectedEvent = new StatusChangeEvent(null, Status.FAIL,Status.FAIL);

	 public void setCalled(boolean called) {
		  this.called = called;
	 }

	 private boolean called=false;

	 public boolean wasCalled() {
		  return called;
	 }

	 public void expectStatusChangeTo(Status s){
		   expectedEvent=new StatusChangeEvent(null,expectedEvent.getNewStatus(),s);
		  called=false;
	 }

	 @Override
	 public void statusChanged(StatusChangeEvent event) {
		  assertThat(event.getOldStatus(), is(expectedEvent.getOldStatus()));
		  assertThat(event.getNewStatus(), is(expectedEvent.getNewStatus()));
		  called=true;
	 }
}
