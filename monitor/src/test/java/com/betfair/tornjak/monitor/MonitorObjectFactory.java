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

import com.betfair.tornjak.monitor.active.Check;

public class MonitorObjectFactory {
    static int uniqueName;

    private static String uniqueName() {
        return "Monitor number " + (uniqueName++);
    }

    public static DefaultMonitor createBrokenMonitor(Check activePolicy) {
        DefaultMonitor monitor = createWarnMonitor(activePolicy);
        monitor.failure("to failure");
        org.junit.Assert.assertThat(monitor.getStatus(), org.hamcrest.core.Is.is(Status.FAIL)); // sanity check
        monitor.setWarningThreshold(5000000);
        return monitor;
    }

    public static DefaultMonitor createWarnMonitor(long warnWindow) {
        return createWarnMonitor(null, warnWindow);
    }

    public static DefaultMonitor createWarnMonitor(Check activePolicy) {
        return createWarnMonitor(activePolicy, 5000000);
    }

    public static DefaultMonitor createWarnMonitor(Check activePolicy, long warnWindow) {
        DefaultMonitor monitor = createOkMonitor(activePolicy);
        monitor.setWarningThreshold(warnWindow);
        monitor.failure("just to warn");
        org.junit.Assert.assertThat(monitor.getStatus(), org.hamcrest.core.Is.is(Status.WARN)); // sanity check
        return monitor;
    }

    public static DefaultMonitor createOkMonitor(Check activePolicy) {
        DefaultMonitor monitor = new DefaultMonitor(uniqueName());
        if (activePolicy != null) {
            monitor.setActiveMonitor(activePolicy);
        }
        monitor.success();
        org.junit.Assert.assertThat(monitor.getStatus(), org.hamcrest.core.Is.is(Status.OK)); // sanity check
        monitor.setWarningThreshold(5000000);
        return monitor;
    }

    public static DefaultMonitor createOkMonitor() {
        return createOkMonitor(null);
    }
    
    public static DefaultMonitor createWarnMonitor() {
        return createWarnMonitor(null);
    }

    public static DefaultMonitor createBrokenMonitor() {
        return createBrokenMonitor(null);
    }
}
