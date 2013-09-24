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

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;


public class DefaultMonitorRegistryTest {
    @Test
    public void registryListeners() {
        DefaultMonitorRegistry registry = new DefaultMonitorRegistry();
        RegistryListener listener = mock(RegistryListener.class);
        registry.addRegistryListener(listener);

        DefaultMonitor monitor = new DefaultMonitor("Monitor 1");
        assertEquals(null, registry.getMonitor(monitor.getName()));

        registry.addMonitor(monitor);
        verify(listener).monitorAdded(argThat(containsMonitorAndRegistry(registry, monitor)));
        assertEquals(monitor, registry.getMonitor(monitor.getName()));

        registry.removeMonitor(monitor);
        verify(listener).monitorRemoved(argThat(containsMonitorAndRegistry(registry, monitor)));
        assertEquals(null, registry.getMonitor(monitor.getName()));

        OverallStatus overall = new OverallStatus();

        assertEquals(null, registry.getStatusAggregator());
        registry.setStatusAggregator(overall);
        verify(listener).statusAggregatorChanged(argThat(containsAggregatorAndRegistry(registry, null, overall)));
        assertEquals(overall, registry.getStatusAggregator());
    }

    private Matcher<MonitorChangeEvent> containsMonitorAndRegistry(final DefaultMonitorRegistry registry, final DefaultMonitor monitor) {
        return new BaseMatcher<MonitorChangeEvent>() {
            public boolean matches(Object o) {
                MonitorChangeEvent mce = (MonitorChangeEvent) o;
                if (mce.getMonitor() != monitor) {
                    return false;
                }
                if (mce.getRegistry() != registry) {
                    return false;
                }
                return true;
            }

            public void describeTo(Description description) {
                description.appendValue("MonitorChangeEvent[registry="+registry+", monitor="+monitor+"]");
            }
        };
    }

    private Matcher<StatusAggregatorChangeEvent> containsAggregatorAndRegistry(final DefaultMonitorRegistry registry, final StatusAggregator oldAggregator, final StatusAggregator newAggregator) {
        return new BaseMatcher<StatusAggregatorChangeEvent>() {
            public boolean matches(Object o) {
                StatusAggregatorChangeEvent sace = (StatusAggregatorChangeEvent) o;
                if (sace.getOldAggregator() != oldAggregator) {
                    return false;
                }
                if (sace.getNewAggregator() != newAggregator) {
                    return false;
                }
                if (sace.getRegistry() != registry) {
                    return false;
                }
                return true;
            }

            public void describeTo(Description description) {
                description.appendValue("StatusAggregatorChangeEvent[registry="+registry+", oldAggregator="+oldAggregator+", newAggregator="+newAggregator+"]");
            }
        };
    }

    @Test(expected = IllegalStateException.class)
    public void addMonitorTwice() {
        DefaultMonitorRegistry registry = new DefaultMonitorRegistry();
        DefaultMonitor monitor = new DefaultMonitor("Monitor 1");
        registry.addMonitor(monitor);
        registry.addMonitor(monitor);
    }

    @Test(expected = IllegalStateException.class)
    public void removeUnknownMonitor() {
        DefaultMonitorRegistry registry = new DefaultMonitorRegistry();
        DefaultMonitor monitor = new DefaultMonitor("Monitor 1");
        registry.removeMonitor(monitor);
    }

    @Test
    public void addNullAggregator() {
        DefaultMonitorRegistry registry = new DefaultMonitorRegistry();
        registry.setStatusAggregator(null);
    }
    
}
