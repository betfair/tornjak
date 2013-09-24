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

package com.betfair.tornjak.kpi;

/**
 * A timer which simplifies timing events.
 * <p/>
 * Usage:<ul>
 * <li>start() starts timer, stop() stops timer. Can start() and stop() repeatedly - duration will increment
 * accordingly.</li>
 * <li>done() is like a stop(). It stops if necessary, and then resets, so can be re-used if necessary.</li>
 * <li>Timer IS NOT THREADSAFE. Use only within a single thread.</li>
 * <li>timing is done with nano precision, but return values are converted to millis.
 * </ul>
 * <p/>
 * State manament: throws exceptions if not used correctly. Rather fail fast to identify usage errors than add spurious
 * statistics to monitors.
 */
public class KPITimer {

    private boolean active = false;
    private long duration = 0;
    private long startTime = 0;


    /**
     * Flag timer as done - stops timer if necessary, adds event to given monitor, and resets the counter.
     */
    public double done() {

        long nanos = flagDone();
        return toMillis(nanos);
    }


    /**
     * Start timer. Will throw exception if timer has already been started.
     */
    public void start() {

        if (!active) {
            startTime = System.nanoTime();
            active = true;
        } else {
            throw new IllegalStateException("Timer started but already active.");
        }
    }


    /**
     * Stop timer. Will throw exception if timer has not been started.
     *
     * @return current duration in milliseconds
     */
    public double stop() {

        doStop();
        return toMillis(duration);
    }


    /**
     * Do an actual stop. This version doesn't do the (relatively expensive) milli conversion
     */
    private void doStop() {

        if (active) {
            duration += (System.nanoTime() - startTime);
            active = false;

        } else {
            throw new IllegalStateException("Timer stopped but wasn't active.");
        }
    }


    /**
     * Flag timer as 'done' - stop if necessary, get duration, reset values. RETURNS NANOS!
     */
    private long flagDone() {

        if (active) {
            doStop();

        } else {
            if (startTime == 0) {
                throw new IllegalStateException("Timer flagged as done but was never started.");
            }
        }

        // reset everything
        startTime = 0;
        long doneDuration = duration;
        duration = 0;
        return doneDuration;
    }


    /**
     * Util method converting nano time to milliseconds
     */
    private double toMillis(long nanos) {
        return (nanos / 1000000.0);
    }

}
