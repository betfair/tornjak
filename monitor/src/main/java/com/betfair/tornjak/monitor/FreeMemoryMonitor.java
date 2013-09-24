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

import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

/**
 * Monitors memory utilization to see whether a minimum (configurable) percentage remains free.
 * Will affect overall status to the configured extent (FAIL by default) if free memory drops below this amount, OK otherwise.
 * If the default max impact to overall status is FAIL and free memory drops below the specified amount,
 * the application's health will go bad and the load balancer will take the node out of service.
 * If the node then recovers, it will automatically go to OK, and the load balancer will again start directing requests
 * to it.
 */
@ManagedResource(description="FreeMemoryMonitor")
public class FreeMemoryMonitor extends OnDemandMonitor implements FreeMemoryMonitorMBean {

    private final int requiredFreeMemoryPercentage;

    /**
     * Construct a free memory monitor with a required free memory percentage.
     */
    public FreeMemoryMonitor(int requiredFreeMemoryPercentage) {
        this.requiredFreeMemoryPercentage = requiredFreeMemoryPercentage;
    }

    @Override
    protected Status checkStatus() throws Exception {
        if (getFreeMemoryPercentage() < requiredFreeMemoryPercentage) {
            return Status.FAIL;
        }
        return Status.OK;
    }

    /**
     * Answers the amount of free memory in this runtime environment, as a percentage.
     */
    @Override
    @ManagedAttribute()
    public int getFreeMemoryPercentage() {
        Runtime runtime = Runtime.getRuntime();
        // 'totalMemory' is how much heap we have *currently*
        // - this may increase up to 'maxMemory' (and can decrease)
        long totalMemory = runtime.totalMemory();
        // 'freeMemory' is 'totalMemory' - allocated)
        long freeMemory = runtime.freeMemory();
        // Work out how much we have used
        long allocatedMemory = totalMemory - freeMemory;
        // 'maxMemory' is equivalent to the JVM option '-Xmx'
        long maxMemory = runtime.maxMemory();
        // Work out the percentage used
        return (int) (100 - (allocatedMemory * 100 / maxMemory));
    }

    @Override
    public String getName() {
        return "Free memory monitor (" + requiredFreeMemoryPercentage + "% free required, else " + getMaxImpactToOverallStatus() + ")";
    }
}