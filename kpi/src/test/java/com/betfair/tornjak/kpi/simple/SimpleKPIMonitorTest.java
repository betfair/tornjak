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

package com.betfair.tornjak.kpi.simple;

import com.betfair.tornjak.kpi.KPI;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Date: 14/06/2013
 * Time: 12:10:49
 */
@RunWith(MockitoJUnitRunner.class)
public class SimpleKPIMonitorTest {

    @Mock
    private MBeanServer mbeanServer;

    private SimpleKPIMonitor monitor;

    @Before
    public void setUp() throws Exception {
        monitor = new SimpleKPIMonitor();

    }

    @Test
    public void registersWithJmx() throws NotCompliantMBeanException, InstanceAlreadyExistsException, MBeanRegistrationException, MalformedObjectNameException {
        monitor.setMbeanServer(mbeanServer);

        monitor.addEvent("event1");
        monitor.addEvent("event1");

        SimpleKPI kpi = (SimpleKPI) monitor.readKPIs().get(0);
        verify(mbeanServer, times(1)).registerMBean(eq(kpi), eq(new ObjectName("KPI.Simple:name=event1")));
    }

    @Test
    public void registersWithJmxAndCustomDomain() throws NotCompliantMBeanException, InstanceAlreadyExistsException, MBeanRegistrationException, MalformedObjectNameException {
        monitor.setMbeanServer(mbeanServer);
        monitor.setJmxDomain("KPI.Wibble");

        monitor.addEvent("event1");
        monitor.addEvent("event1");

        SimpleKPI kpi = (SimpleKPI) monitor.readKPIs().get(0);
        verify(mbeanServer, times(1)).registerMBean(eq(kpi), eq(new ObjectName("KPI.Wibble:name=event1")));
    }

    @Test
    public void shouldAddEvent() {
        monitor.addEvent("event1");
        monitor.addEvent("event1");

        SimpleKPI kpi = (SimpleKPI) monitor.readKPIs().get(0);

        assertEquals("event1", kpi.getName());
        assertEquals(2, kpi.getCalls());
        assertEquals(0, kpi.getFailures());
    }

    @Test
    public void shouldAddEventSuccess() {
        monitor.addEvent("event1", true);
        monitor.addEvent("event1", false);

        SimpleKPI kpi = (SimpleKPI) monitor.readKPIs().get(0);

        assertEquals("event1", kpi.getName());
        assertEquals(2, kpi.getCalls());
        assertEquals(1, kpi.getFailures());
    }

    @Test
    public void shouldAddEventDuration() {
        monitor.addEvent("event1", 12);
        monitor.addEvent("event1", 22);

        SimpleKPI kpi = (SimpleKPI) monitor.readKPIs().get(0);

        assertEquals("event1", kpi.getName());
        assertEquals(2, kpi.getCalls());
        assertEquals(0, kpi.getFailures());
        assertEquals(17D, kpi.getLatestTimePerCall(), 0.0001);

    }

    @Test
    public void shouldAddEventSuccessDuration() {
        monitor.addEvent("event1", 12, true);
        monitor.addEvent("event1", 22, false);

        SimpleKPI kpi = (SimpleKPI) monitor.readKPIs().get(0);

        assertEquals("event1", kpi.getName());
        assertEquals(2, kpi.getCalls());
        assertEquals(1, kpi.getFailures());
        assertEquals(17D, kpi.getLatestTimePerCall(), 0.0001);
    }


    @Test
    public void shouldAddEventsForDifferentKPIs() {
        monitor.addEvent("event1");
        monitor.addEvent("event2");

        Map<String, SimpleKPI> kpiMap = new HashMap<String, SimpleKPI>();
        for (KPI kpi : monitor.readKPIs()) {
            kpiMap.put(kpi.getName(), (SimpleKPI) kpi);
        }

        SimpleKPI kpi = kpiMap.remove("event1");
        assertEquals(1, kpi.getCalls());
        assertEquals(0, kpi.getFailures());

        kpi = kpiMap.remove("event2");
        assertEquals(1, kpi.getCalls());
        assertEquals(0, kpi.getFailures());

        assertTrue(kpiMap.isEmpty());
    }
}
