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

package com.betfair.tornjak.kpi.statse;

import com.betfair.tornjak.kpi.KPI;
import com.betfair.tornjak.kpi.KPIMonitor;
import com.betfair.sre.statse.client.StatsESender;

import java.util.List;

/**
 * Date: 14/06/2013
 * Time: 14:17:24
 */
public class StatsEMonitor implements KPIMonitor {

    private StatsESender sender;

    @Override
    public void addEvent(String name) {
        addEvent(name, true);
    }

    @Override
    public void addEvent(String name, boolean succeeded) {
        sender.newMessageForMetric(name)
            .error(!succeeded)
            .send();
    }

    @Override
    public void addEvent(String name, double duration) {
        addEvent(name, duration, true);
    }

    @Override
    public void addEvent(String name, double duration, boolean succeeded) {
        sender.newMessageForMetric(name)
            .time(duration)
            .error(!succeeded)
            .send();
    }

    @Override
    public void addEvent(String name, String operationName) {
        addEvent(name, operationName, true);
    }

    @Override
    public void addEvent(String name, String operationName, boolean succeeded) {
        sender.newMessageForMetric(name)
            .operation(operationName)
            .error(!succeeded)
            .send();
    }

    @Override
    public void addEvent(String name, String operationName, double duration) {
        addEvent(name, operationName, duration, true);
    }

    @Override
    public void addEvent(String name, String operationName, double duration, boolean succeeded) {
        sender.newMessageForMetric(name)
            .operation(operationName)
            .time(duration)
            .error(!succeeded)
            .send();
    }

    @Override
    public long getLastReadTime() {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<KPI> readKPIs() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setReadInterval(long millis) {
        throw new UnsupportedOperationException();
    }

    public void setSender(StatsESender sender) {
        this.sender = sender;
    }

}
