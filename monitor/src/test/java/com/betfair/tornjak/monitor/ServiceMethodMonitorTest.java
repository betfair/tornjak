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

import static junit.framework.Assert.assertEquals;

import junit.framework.Assert;
import org.junit.Test;

public class ServiceMethodMonitorTest {

    @Test
    public void testOnlyContriutesToWarn() {
        ServiceMethodMonitor mon = new ServiceMethodMonitor();
        Assert.assertEquals("incorrect contrib to over status", Status.WARN, mon.getMaxImpactToOverallStatus());
    }

    /**
     * Ensure setting the spring bean name does not override a custom monitor
     * name
     */
    @Test
    public void testMonitorNameNotOverriddenBySpringBeanName() {
        ServiceMethodMonitor mon = new ServiceMethodMonitor("mymonitor");
        mon.setBeanName("beanname");
        assertEquals("monitor name should not be overridden", "mymonitor", mon.getName());
    }

    /**
     * Ensure setting the spring bean name sets the monitor name
     */
    @Test
    public void testMonitorNameCanSetSpringName() {
        ServiceMethodMonitor mon = new ServiceMethodMonitor();
        mon.setBeanName("beanname");
        assertEquals("bean name should be set", "beanname", mon.getName());
    }

    /**
     * Since we don't have an active check, always ensure we start with status
     * OK. We rely on traffic
     */
    @Test
    public void testStartsWithStatusOK() {
        ServiceMethodMonitor mon = new ServiceMethodMonitor();
        assertEquals("incorrect start status", Status.OK, mon.getStatus());
    }
}
