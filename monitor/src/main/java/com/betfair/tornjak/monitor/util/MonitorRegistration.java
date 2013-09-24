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

package com.betfair.tornjak.monitor.util;

import com.betfair.tornjak.monitor.Monitor;
import com.betfair.tornjak.monitor.MonitorRegistry;
import org.springframework.beans.factory.InitializingBean;

import java.util.List;

/**
 * Utility class that registers one or more monitors with a registry.
 */
public class MonitorRegistration implements InitializingBean {
    private List<Monitor> monitors;
    private MonitorRegistry registry;

    public void setMonitors(List<Monitor> monitors) {
        this.monitors = monitors;
    }

    public void setRegistry(MonitorRegistry registry) {
        this.registry = registry;
    }

    public void afterPropertiesSet() throws Exception {
        for (Monitor m : monitors) {
            registry.addMonitor(m);
        }
    }
}
