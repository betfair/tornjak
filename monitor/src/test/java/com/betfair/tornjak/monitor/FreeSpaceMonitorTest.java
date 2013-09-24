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

import java.io.File;

import static junit.framework.Assert.assertTrue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.hamcrest.core.Is;
import org.junit.Test;

public class FreeSpaceMonitorTest {

	public static long LOTS = 900;
	public static long LITTLE = 100;
	public static long NONE = 0;


	private FreeSpaceMonitor fsm = null;

	private class FakeDirectory extends File {
		private static final long serialVersionUID = 1L;
		private boolean denyPermission = false;
		private long usableSpace = LITTLE;

		public FakeDirectory setDenyPermission(boolean denyPermission) {
			this.denyPermission = denyPermission;
			return this;
		}

		public FakeDirectory setUsableSpace(long usableSpace) {
			this.usableSpace = usableSpace;
			return this;
		}

		public FakeDirectory(String pathname) {
			super(pathname);
		}

		@Override
		public long getUsableSpace() {
			if (denyPermission) {
				throw new SecurityException("Security violation.");
			} else {
				return usableSpace;
			}
		}
	}

	@Test
	public void testGetName() throws Exception {
		fsm = new FreeSpaceMonitor(new FakeDirectory(""), LITTLE);
		assertThat(fsm.getName(), is("Free disk space monitor"));
	}

	@Test
	public void testGetStatusOK() {
		fsm = new FreeSpaceMonitor(new FakeDirectory(""), LITTLE);
		assertThat(fsm.getStatus(), Is.is(Status.OK));
	}

	@Test
	public void testGetStatusFAIL() {
		fsm = new FreeSpaceMonitor(new FakeDirectory(""), LOTS);
		assertThat(fsm.getStatus(), is(Status.FAIL));
	}

	@Test
	public void testGetStatusWARN() {
		fsm = new FreeSpaceMonitor(new FakeDirectory("").setDenyPermission(true), LITTLE);
		assertThat(fsm.getStatus(), is(Status.WARN));
	}

	//test as listener...
	@Test
	public void testListener() {

		DummyStatusChangeListener listener = new DummyStatusChangeListener();

		FakeDirectory f = (new FakeDirectory(""));
		fsm = new FreeSpaceMonitor(f, LITTLE);
		fsm.addStatusChangeListener(listener);


		listener.expectStatusChangeTo(Status.OK);
		fsm.getStatus();
		assertTrue("Listener was called on first getStatus", listener.wasCalled());

		f.setUsableSpace(NONE);
		listener.expectStatusChangeTo(Status.FAIL);
		fsm.getStatus();
		assertTrue("Failure causes the listener to be called",listener.wasCalled());

		fsm.getStatus();
		listener.setCalled(false);
		assertTrue("Status moves silently from FAIL to FAIL",!listener.wasCalled());

		f.setUsableSpace(LOTS);
		listener.expectStatusChangeTo(Status.OK);
		fsm.getStatus();
		assertTrue("Status moves back from FAIL to OK",listener.wasCalled());


	}


}
