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

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static java.lang.Math.abs;

/**
 * Date: 13/06/2013
 * Time: 12:15:14
 */
public class SimpleKPI extends KPI implements SimpleKPIMBean {

    private static final int LATEST_CALL_COUNT = 20;
    private final AtomicLong calls = new AtomicLong();
    private final AtomicLong failures = new AtomicLong();

    private final double[] latestCallTimes = new double[LATEST_CALL_COUNT];
    private AtomicInteger index = new AtomicInteger();


    /**
     * @param name the name of the KPI
     */
    public SimpleKPI(String name) {
        super(name);
    }

    /**
     * Records a call timing
     *
     * @param timeTaken time taken by the call
     */
    public void recordCall(double timeTaken) {
        calls.incrementAndGet();
        latestCallTimes[abs(index.getAndIncrement() % LATEST_CALL_COUNT)] = timeTaken;
    }

    /**
     * Records a failed call timing
     *
     * @param timeTaken time taken by the call
     */
    public void recordFailure(double timeTaken) {
        recordCall(timeTaken);
        failures.incrementAndGet();
    }

    /**
     * Get the call count.
     *
     * @return the number of calls counted by this KPI. Includes failed calls.
     */
    @Override
    public long getCalls() {
        return calls.get();
    }

    /**
     * Get the count of failed calls.
     *
     * @return the number of failed calls counted by this KPI.
     */
    @Override
    public long getFailures() {
        return failures.get();
    }


    /**
     * Gets the average time taken for the last 20 calls.
     *
     * @return the average call time
     */
    @Override
    public double getLatestTimePerCall() {
        double total = 0;
        long count = 0;
        for (double item : latestCallTimes) {
            if (item != 0) {
                total += item;
                count++;
            }
        }
        return count == 0 ? 0 : total / count;
    }

}
