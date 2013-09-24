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

import com.betfair.tornjak.monitor.MonitorRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of the active monitoring
 * <p/>
 * This class runs through the list of monitors every half a second, executing the
 * active monitors that are in a {@link com.betfair.tornjak.monitor.Status#FAIL} state.
 * <p/>
 * It needs a {@link com.betfair.tornjak.monitor.MonitorRegistry} passed by the constructor or the setter.
 * <p/>
 * The active monitoring will not start until the {@link #start()} method is called.
 * This method is <b>not thread-safe</b> and it should not be called more than <b>once</b>.
 * <p/>
 * The service should be stopped with the {@link #stop()} method.
 * <p/>
 * <b>Implemenation notes:</b>
 * <li/> Monitors are checked concurrently
 * <li/> Monitors are checked every ( 0.5 seconds + slowest active monitor time )
 * <li/> Implementation may change
 */
public class ActiveMonitorService {

    Logger logger = LoggerFactory.getLogger(ActiveMonitorService.class);

    private MonitorRegistry monitorRegistry;
    private ActiveMonitoringChecking activeMonitoringChecking;
    private ScheduledExecutorService executorService;
    private int checkIntervalInMillis = 500;

    /**
     * Spring constructor
     */
    public ActiveMonitorService() {
        activeMonitoringChecking = new ActiveMonitoringChecking();
        executorService = Executors.newScheduledThreadPool(1, new NamedThreadFactory("ActiveMonitoringThread"));
    }

    /**
     * Proper constructor
     */
    public ActiveMonitorService(MonitorRegistry monitorRegistry) {
        this();
        this.monitorRegistry = monitorRegistry;
    }

    /**
     * Should be called just once.
     */
    public void start() {
        if (monitorRegistry == null) {
            throw new IllegalStateException("Monitor registry cannot be null, please check that you have called setMonitorRegistry");
        }
        logger.info("Starting active monitor thread");
        executorService.scheduleWithFixedDelay(new Runnable() {
            public void run() {
                activeMonitoringChecking.check(monitorRegistry);
            }
        }, 0, checkIntervalInMillis, TimeUnit.MILLISECONDS);
    }

    public void stop() {
        executorService.shutdownNow();
        activeMonitoringChecking.shutdown();
    }

    public void setCheckIntervalInMillis(int checkIntervalInMillis) {
        this.checkIntervalInMillis = checkIntervalInMillis;
    }

    public void setMonitorRegistry(MonitorRegistry monitorRegistry) {
        this.monitorRegistry = monitorRegistry;
    }

}