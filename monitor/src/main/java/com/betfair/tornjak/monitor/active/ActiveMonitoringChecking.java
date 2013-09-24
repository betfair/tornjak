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

import com.betfair.tornjak.monitor.ActiveMethodMonitor;
import com.betfair.tornjak.monitor.Monitor;
import com.betfair.tornjak.monitor.MonitorRegistry;
import com.betfair.tornjak.monitor.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class ActiveMonitoringChecking {
    private static Logger LOG = LoggerFactory.getLogger(ActiveMonitoringChecking.class);
    
    private ExecutorService executorService = Executors.newFixedThreadPool(10, new NamedThreadFactory("MonitorChecker"));

    public void check(MonitorRegistry monitorRegistry) {
        Set<Monitor> set = monitorRegistry.getMonitorSet();

        Collection<Callable<Object>> checkMonitorTask = new ArrayList<Callable<Object>>();
        
        for (final Monitor monitor : set) {
            if (monitor instanceof ActiveMethodMonitor) {
                if (monitor.getStatus() == Status.FAIL) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(monitor.getName()+": In a FAIL state, scheduling active check.");
                    }
                    checkMonitorTask.add(new CheckMonitorTask((ActiveMethodMonitor) monitor));
                }
            }
        }

        try {
            executorService.invokeAll(checkMonitorTask);
        } catch (Exception e) {
            LOG.error("Error invoking checks",e);
        }
    }

    public void shutdown() {
        executorService.shutdownNow();
    }

    private void checkMonitor(ActiveMethodMonitor monitor) {
        try {
            Check agent = monitor.getActiveMonitor();
            if (agent == null) {
                throw new RuntimeException(monitor.getName() + ": Active Monitor cannot be null");
            }
            agent.check();
            monitor.success();
        } catch (Exception e) {
            monitor.failure(e);
        }
    }

    private class CheckMonitorTask implements Callable<Object> {
        private final ActiveMethodMonitor monitor;

        public CheckMonitorTask(ActiveMethodMonitor monitor) {
            this.monitor = monitor;
        }

        public Object call() throws Exception {
            checkMonitor(monitor);
            return null;
        }
    }
}
