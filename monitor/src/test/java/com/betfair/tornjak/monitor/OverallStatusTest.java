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

import static com.betfair.tornjak.monitor.MonitorObjectFactory.*;
import static junit.framework.Assert.assertTrue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

public class OverallStatusTest {
	private OverallStatus overallStatus;
	private DefaultMonitorRegistry monitorRegistry;

	@Before
	public void setUp() throws Exception {
		overallStatus = new OverallStatus();
		monitorRegistry = new DefaultMonitorRegistry();
		monitorRegistry.setStatusAggregator(overallStatus);
	}
	
	@Test
	public void testOkMonitor() throws Exception {
		monitorRegistry.addMonitor(createOkMonitor());

		assertThat(overallStatus.getStatusAsString(), is("OK"));
	}

	@Test
	public void testOkMonitorMovesToWarnToFail() throws Exception {
		DefaultMonitor mon = createOkMonitor();
		mon.setWarningThreshold(1000);
		monitorRegistry.addMonitor(mon);

		assertThat(overallStatus.getStatusAsString(), is("OK"));

		mon.failure("Some cause");

		assertThat(overallStatus.getStatusAsString(), is("WARN"));

		mon.failure("Some cause");

		assertThat(overallStatus.getStatusAsString(), is("FAIL"));
	}

	@Test
	public void testRemovedMonitorHasNoEffect() throws Exception {
		DefaultMonitor mon = createOkMonitor();

		DefaultMonitor mon2 = createOkMonitor();

		mon.setWarningThreshold(1000);
		mon2.setWarningThreshold(1000);

		monitorRegistry.addMonitor(mon);
		monitorRegistry.addMonitor(mon2);


		assertThat(overallStatus.getStatusAsString(), is("OK"));

		mon.failure("Some cause");
		mon2.failure("Some cause");

		assertThat(overallStatus.getStatusAsString(), is("WARN"));

		mon.failure("Some cause");

		assertThat(overallStatus.getStatusAsString(), is("FAIL"));

		monitorRegistry.removeMonitor(mon);

		assertThat(overallStatus.getStatusAsString(), is("WARN"));

		Thread.sleep(2000);//needs to be larger than the warning threshold.

		assertThat(overallStatus.getStatusAsString(), is("OK"));

		mon.failure("Some cause");

		assertThat(overallStatus.getStatusAsString(), is("OK"));
	}

	@Test
	public void testWarnMonitor() throws Exception {
		DefaultMonitor warnMonitor = createWarnMonitor();
		monitorRegistry.addMonitor(warnMonitor);
		assertThat(overallStatus.getStatusAsString(), is("WARN"));
	}

	@Test
	public void testWarnMonitorMovesToFail() throws Exception {
		DefaultMonitor warnMonitor = createWarnMonitor();
		warnMonitor.setWarningThreshold(1000);
		monitorRegistry.addMonitor(warnMonitor);

		assertThat(overallStatus.getStatusAsString(), is("WARN"));

		warnMonitor.failure("Some cause");

		assertThat(overallStatus.getStatusAsString(), is("FAIL"));
	}

	@Test
	public void testFailMonitor() throws Exception {
		monitorRegistry.addMonitor(createBrokenMonitor());

		assertThat(overallStatus.getStatusAsString(), is("FAIL"));
	}

	@Test
	public void testFailMonitorMovesToWarnToOk() throws Exception {
		DefaultMonitor mon = createBrokenMonitor();
		mon.setWarningThreshold(1000);
		monitorRegistry.addMonitor(mon);

		assertThat(overallStatus.getStatusAsString(), is("FAIL"));

		mon.success();

		assertThat(overallStatus.getStatusAsString(), is("WARN"));

		Thread.sleep(2000);

		assertThat(overallStatus.getStatusAsString(), is("OK"));
	}

	@Test
	public void testMaxImpact() throws Exception {
		DefaultMonitor brokenMonitor = createBrokenMonitor();
		brokenMonitor.setMaxImpactToOverallStatus(Status.WARN);
		monitorRegistry.addMonitor(brokenMonitor);

		assertThat(overallStatus.getStatusAsString(), is("WARN"));
	}

	//test as listener...
	@Test
	public void testListener() {
		DummyStatusChangeListener listener = new DummyStatusChangeListener();
		overallStatus.addStatusChangeListener(listener);

		monitorRegistry.addMonitor(createOkMonitor());
		monitorRegistry.addMonitor(createOkMonitor());
		monitorRegistry.addMonitor(createOkMonitor());

		listener.expectStatusChangeTo(Status.OK);
		overallStatus.getStatus();
		assertTrue("Listener should be called on first getStatus", listener.wasCalled());

		monitorRegistry.addMonitor(createWarnMonitor());
		monitorRegistry.addMonitor(createWarnMonitor());

		listener.expectStatusChangeTo(Status.WARN);
		overallStatus.getStatus();
		assertTrue("Overall status is now WARN, and listener should be called", listener.wasCalled());

		monitorRegistry.addMonitor(createBrokenMonitor());

		listener.expectStatusChangeTo(Status.FAIL);
		overallStatus.getStatus();
		assertTrue("Overall status is now FAIL, and listener should be called",listener.wasCalled());

		listener.setCalled(false);
		overallStatus.getStatus();
		assertTrue("Overall status is still FAIL, but listener should NOT be called", !listener.wasCalled());

		assertThat(overallStatus.getOkCount(), is(3));
		assertThat(overallStatus.getWarnCount(), is(2));
		assertThat(overallStatus.getFailCount(), is(1));
	}
}
