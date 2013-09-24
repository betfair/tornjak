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

package com.betfair.tornjak.kpi.repeater;

import com.betfair.tornjak.kpi.KPI;
import com.betfair.tornjak.kpi.KPIMonitor;

import java.util.List;

/**
 * Repeats KPI events to any registered monitors.
 */
public class KPIRepeater implements KPIMonitor {

    private List<KPIMonitor> monitors;

    @Override
    public void addEvent(String name) {
        for (final KPIMonitor monitor : monitors) {
            monitor.addEvent(name);
        }
    }

    @Override
    public void addEvent(String name, boolean succeeded) {
        for (final KPIMonitor monitor : monitors) {
            monitor.addEvent(name, succeeded);
        }
    }

    @Override
    public void addEvent(String name, double duration) {
        for (final KPIMonitor monitor : monitors) {
            monitor.addEvent(name, duration);
        }
    }

    @Override
    public void addEvent(String name, double duration, boolean succeeded) {
        for (final KPIMonitor monitor : monitors) {
            monitor.addEvent(name, duration, succeeded);
        }
    }

    @Override
    public void addEvent(String name, String operationName) {
        for (final KPIMonitor monitor : monitors) {
            monitor.addEvent(name, operationName);
        }
    }

    @Override
    public void addEvent(String name, String operationName, boolean succeeded) {
        for (final KPIMonitor monitor : monitors) {
            monitor.addEvent(name, operationName, succeeded);
        }
    }

    @Override
    public void addEvent(String name, String operationName, double duration) {
        for (final KPIMonitor monitor : monitors) {
            monitor.addEvent(name, operationName, duration);
        }
    }

    @Override
    public void addEvent(String name, String operationName, double duration, boolean succeeded) {
        for (final KPIMonitor monitor : monitors) {
            monitor.addEvent(name, operationName, duration, succeeded);
        }
    }

    @Override
    public long getLastReadTime() {
        throw new UnsupportedOperationException("getLastReadTime not supported by KPIRepeater. ");
    }

    @Override
    public List<KPI> readKPIs() {
        throw new UnsupportedOperationException("readKPIs not supported by KPIRepeater. ");
    }

    @Override
    public void setReadInterval(long millis) {
        throw new UnsupportedOperationException("setReadInterval not supported by KPIRepeater. ");
    }

    public void setMonitors(List<KPIMonitor> monitors) {
        this.monitors = monitors;
    }
}
