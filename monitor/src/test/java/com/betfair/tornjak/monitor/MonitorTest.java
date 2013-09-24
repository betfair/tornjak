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

import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;

import static com.betfair.tornjak.monitor.MonitorObjectFactory.*;
import static com.betfair.tornjak.monitor.Status.*;
import static junit.framework.TestCase.fail;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;

/**
 * 
 * @author sorokod
 * 
 */
public class MonitorTest {

    private Logger log = LoggerFactory.getLogger(getClass());

    private DefaultMonitor monitorA;
    private DefaultMonitor monitorB;

    @Before
    public void setUp() {
        monitorA = new DefaultMonitor("monitorA");
        monitorB = new DefaultMonitor("monitorB");
    }

    @Test
    public void testBeanNameBeingSet() {
        DefaultMonitor monitor = new DefaultMonitor();

        try {
            monitor.setName(null);
            fail("IllegalArgumentException should have been thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("Error message is wrong", "Monitor name can not be null", e.getMessage());
        }

        // setBeanName should not override existing name
        monitor.setName("firstName");
        monitor.setBeanName("secondName");
        assertEquals("firstName", monitor.getName());

        // setName should override existing name
        monitor = new DefaultMonitor();
        monitor.setBeanName("firstName");
        monitor.setName("secondName");
        assertEquals("secondName", monitor.getName());
    }

    @Test
    public void testInitalStateIsFail() {

        assertEquals("monitorA", monitorA.getName());
        Assert.assertEquals(Status.FAIL, monitorA.getStatus());
        assertEquals(0, monitorA.getFailureCount());
    }

    @Test
    public void testAtLeastOneSuccessToBecomeOk() {

        monitorA.success();
        
        assertEquals("monitorA", monitorA.getName());
        assertEquals(Status.OK, monitorA.getStatus());
        assertEquals(0, monitorA.getFailureCount());
    }

    @Test
    public void testEquals() {
        assertEquals(new DefaultMonitor("monitorA"), monitorA);
        assertFalse(new DefaultMonitor("monitorA").equals(monitorB));
    }

    @Test
    public void testFail() throws InterruptedException {
        log.info("Start testFail");
        monitorB.success();

        monitorB.failure(new RuntimeException());

        assertEquals(1, monitorB.getFailureCount());
        assertEquals(Status.WARN, monitorB.getStatus());
        assertTrue(monitorB.getLastException().contains("MonitorTest.testFail"));
        log.info("monitorB now in WARN state");

        monitorB.failure(new RuntimeException());
        assertEquals(2, monitorB.getFailureCount());
        assertEquals(Status.FAIL, monitorB.getStatus());
        log.info("monitorB now in FAIL state");

        long sleepTime = 100;
        monitorB.setWarningThreshold(sleepTime);

        monitorB.success();
        assertEquals(0, monitorB.getFailureCount());
        assertEquals(Status.WARN, monitorB.getStatus());
        assertTrue(monitorB.getLastException().contains("MonitorTest.testFail"));
        log.info("monitorB now in WARN state");

        // wait long enough for it to move to OK
        Thread.sleep(sleepTime + 100);

        log.info("Checking that WARN has automatically moved to OK");
        assertEquals(Status.OK, monitorB.getStatus());
        monitorB.setWarningThreshold(30000);
    }

    @Test
    public void testWarnToOk() throws InterruptedException {
        log.info("Start testWarnToOk");
        monitorA.success();
        monitorA.setWarningThreshold(500);
        monitorA.failure(new RuntimeException());
        assertEquals("WARN", monitorA.getStatusAsString());
        assertEquals(Status.WARN, monitorA.getStatus());

        Thread.sleep(600);
        assertEquals("OK", monitorA.getStatusAsString());
        assertEquals(Status.OK, monitorA.getStatus());
    }

    @Test
    public void statusChangeFailToOK() {
        log.info("Start statusChangeFailToOK");

        StatusChangeListener listener = mock(StatusChangeListener.class);
        monitorA.addStatusChangeListener(listener);

        monitorA.success();
        verify(listener).statusChanged(argThat(statusChange(monitorA, FAIL, OK)));
    }

    @Test
    public void statusChangeOkToWarn() {
        log.info("Start statusChangeOkToWarn");
        StatusChangeListener listener = mock(StatusChangeListener.class);
        DefaultMonitor monitor = createOkMonitor();

        monitor.addStatusChangeListener(listener);

        monitor.failure("fail");
        verify(listener).statusChanged(argThat(statusChange(monitor, OK, WARN)));
    }

    @Test
    public void statusChangeWarnToFail() {
        log.info("Start statusChangeWarnToFail");
        StatusChangeListener listener = mock(StatusChangeListener.class);
        DefaultMonitor monitor = createWarnMonitor();

        monitor.addStatusChangeListener(listener);

        monitor.failure("fail");
        verify(listener).statusChanged(argThat(statusChange(monitor, WARN, FAIL)));
    }

    @Test
    public void statusChangeFailToWarn() {
        log.info("Start statusChangeFailToWarn");
        StatusChangeListener listener = mock(StatusChangeListener.class);
        DefaultMonitor monitor = createBrokenMonitor();

        monitor.addStatusChangeListener(listener);

        monitor.success();
        verify(listener).statusChanged(argThat(statusChange(monitor, FAIL, WARN)));
    }

    @Test
    public void statusChangeWarnToOk() throws InterruptedException {
        log.info("Start statusChangeWarnToFail");
        StatusChangeListener listener = mock(StatusChangeListener.class);
        DefaultMonitor monitor = createWarnMonitor(100);
        log.info("Monitor '"+monitor.getName()+"' now in WARN state");
        monitor.success();
        assertEquals(WARN, monitor.getStatus());
        assertEquals(0, monitorB.getFailureCount());
        log.info("Monitor '"+monitor.getName()+"' still in WARN state");

        monitor.addStatusChangeListener(listener);

        Thread.sleep(110);
        verify(listener).statusChanged(argThat(statusChange(monitor, WARN, OK)));

    }

    @Test
    public void testWarnTimer() throws InterruptedException {
        log.info("Start testWarnTimer");
        StatusChangeListener listener = mock(StatusChangeListener.class);
        StatusChangeListener listener2 = mock(StatusChangeListener.class);
        StatusChangeListener listener3 = mock(StatusChangeListener.class);
        DefaultMonitor monitor = createBrokenMonitor();
        monitor.setWarningThreshold(100);
        monitor.success();

        log.info("Monitor '"+monitor.getName()+"' now in WARN state");
        monitor.addStatusChangeListener(listener);

        Thread.sleep(50);
        log.info("Monitor '"+monitor.getName()+"' still in WARN state (mid-timer)");

        assertEquals(WARN, monitor.getStatus());
        assertEquals(0, monitor.getFailureCount());
        monitor.failure("fail");
        assertEquals(WARN, monitor.getStatus());
        assertEquals(1, monitor.getFailureCount());
        verify(listener, never()).statusChanged(any(StatusChangeEvent.class));

        monitor.removeStatusChangeListener(listener);
        monitor.addStatusChangeListener(listener2);

        log.info("Monitor '"+monitor.getName()+"' still in WARN state (start of new timer)");
        monitor.success();
        assertEquals(WARN, monitor.getStatus());
        assertEquals(0, monitor.getFailureCount());

        // check that warn has been reset by virtue of that earlier failure
        Thread.sleep(60);
        assertEquals(WARN, monitor.getStatus());
        assertEquals(0, monitor.getFailureCount());
        verify(listener2, never()).statusChanged(any(StatusChangeEvent.class));
        log.info("Monitor '"+monitor.getName()+"' still in WARN state (middle of new timer)");

        monitor.removeStatusChangeListener(listener2);
        monitor.addStatusChangeListener(listener3);

        Thread.sleep(50);
        assertEquals(OK, monitor.getStatus());
        assertEquals(0, monitor.getFailureCount());
        verify(listener3).statusChanged(argThat(statusChange(monitor, WARN, OK)));

    }

    private Matcher<StatusChangeEvent> statusChange(final StatusSource ss, final Status from, final Status to) {
        return new BaseMatcher<StatusChangeEvent>() {
            public boolean matches(Object o) {
                StatusChangeEvent sce = (StatusChangeEvent) o;
                if (sce.getSource() != ss) {
                    return false;
                }
                if (sce.getOldStatus() != from) {
                    return false;
                }
                if (sce.getNewStatus() != to) {
                    return false;
                }
                return true;
            }

            public void describeTo(Description description) {
                description.appendValue("StatusChangeEvent[source="+ss+", oldStatus="+from+", newStatus="+to+"]");
            }
        };
    }

}
