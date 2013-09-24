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

package com.betfair.tornjak.monitor.active;

import com.betfair.tornjak.monitor.DefaultMonitor;
import com.betfair.tornjak.monitor.DefaultMonitorRegistry;
import com.betfair.tornjak.monitor.MonitorObjectFactory;
import com.betfair.tornjak.monitor.Status;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import static org.junit.matchers.JUnitMatchers.containsString;

public class ActiveMonitoringCheckingTest {
    private DefaultMonitorRegistry monitorRegistry;
    private CheckSpy activePolicy;

    @Before
    public void setUp() {
        monitorRegistry = new DefaultMonitorRegistry();
        activePolicy = new CheckSpy();
    }

    @Test
    public void workingMonitorsNotCalled() throws Exception {
        createOkMonitor();

        checkMonitors();

        activePolicy.assertNotCalled();
    }

    @Test
    public void warnMonitorsNotCalled() throws Exception {
        createWarnMonitor();

        checkMonitors();

        activePolicy.assertNotCalled();
    }

    @Test
    public void brokenMonitorsAreChecked() throws Exception {
        createBrokenMonitor();

        checkMonitors();

        activePolicy.assertCalled(1);
    }

    @Test
    public void successFixesMonitor() throws InterruptedException {
        DefaultMonitor broken = createBrokenMonitor();
        broken.setWarningThreshold(100);

        checkMonitors();

        assertThat(broken.getStatus(), is(Status.WARN));
        assertThat(broken.getFailureCount(), is(0L));
        Thread.sleep(110);
        assertThat(broken.getStatus(), is(Status.OK));
    }

    @Test
    public void exceptionLeavesInBroken() {
        DefaultMonitor broken = createBrokenMonitor();

        long numberOfFailuresBeforeCheck = broken.getFailureCount();

        String exceptionMessage = "This exception should be in the monitor";
        activePolicy.exceptionToThrow = new Exception(exceptionMessage);

        checkMonitors();

        assertThat(broken.getStatus(), is(Status.FAIL));
        assertThat(broken.getFailureCount(), is(numberOfFailuresBeforeCheck + 1));
        assertThat(broken.getLastException(), containsString(exceptionMessage));
    }

    @Test
    public void checksAllTheBrokenMonitors() {
        createOkMonitor();
        createBrokenMonitor();
        createOkMonitor();
        createBrokenMonitor();
        createBrokenMonitor();

        checkMonitors();

        activePolicy.assertCalled(3);

    }

    @Test
    public void nullActiveMonitoringIsAnError() {
        activePolicy = null;
        DefaultMonitor broken = createBrokenMonitor();

        long numberOfFailuresBeforeCheck = broken.getFailureCount();

        checkMonitors();

        assertThat(broken.getStatus(), is(Status.FAIL));
        assertThat(broken.getFailureCount(), is(numberOfFailuresBeforeCheck + 1));
        assertThat(broken.getLastException(), containsString("Active Monitor cannot be null"));
    }

    private DefaultMonitor createBrokenMonitor() {
        DefaultMonitor brokenMonitor = MonitorObjectFactory.createBrokenMonitor(activePolicy);
        monitorRegistry.addMonitor(brokenMonitor);
        return brokenMonitor;
    }

    private void createOkMonitor() {
        monitorRegistry.addMonitor(MonitorObjectFactory.createOkMonitor(activePolicy));
    }

    private void createWarnMonitor() {
        monitorRegistry.addMonitor(MonitorObjectFactory.createWarnMonitor(activePolicy));
    }

    private void checkMonitors() {
        ActiveMonitoringChecking activeMonitoringChecking = new ActiveMonitoringChecking();
        activeMonitoringChecking.check(monitorRegistry);
    }

    private static class CheckSpy implements Check {
        private int isWorkingCalls;
        public Throwable exceptionToThrow;

        public void check() throws Exception {
            isWorkingCalls++;
            if (exceptionToThrow != null) {
                if (exceptionToThrow instanceof Error) {
                    throw (Error) exceptionToThrow;
                }
                if (exceptionToThrow instanceof Exception) {
                    throw (Exception) exceptionToThrow;
                }
            }
        }

        private void assertNotCalled() {
            assertCalled(0);
        }

        private void assertCalled(int expectedNumberOfTimes) {
            assertThat(isWorkingCalls, is(expectedNumberOfTimes));
        }

        @Override
        public String getDescription() {
            return "Some description";
        }
    }

}
