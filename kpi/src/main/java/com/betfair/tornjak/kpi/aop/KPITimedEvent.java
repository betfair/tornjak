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

package com.betfair.tornjak.kpi.aop;

import com.betfair.tornjak.kpi.KPIMonitor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation which will, for methods of Spring-loaded beans, cause the method call's duration to be measured, in
 * nanoseconds, and added to the KPI monitor.
 * <p>
 * The <code>value</code> attribute takes the name of the KPI being measured (and is called 'value') to enable using
 * the annotation in shorthand <code>@KPITimedEvent("foo")</code>.
 * <p>
 * If <code>catchFailures</code> is true (default is false), then the KPI monitor will be called in a way that
 * distinguishes between successful and failed (ie. exception thrown) invocations. How these are reported is up to the
 * specific implementation of {@link KPIMonitor}.
 * <p>
 *
 * <h2>Nanos versus millis</h2>
 * <p>
 * Googling for <code>System.nanoTime()</code> versus <code>System.currentTimeMillis()</code> will return a number
 * of discussions about the benefits and drawbacks of respective methods. Briefly:
 * <ul>
 * <li><code>nanoTime</code> returns values relative to some arbitrary point in time, and these values are only
 *      meaningful for comparisons. Good granularity, but slower than <code>currentTimeMillis</code>.</li>
 * <li><code>currentTimeMillis</code> is a faster call, but has much worse granularity (especially on Windows), and
 *      can also be slightly inaccurate (due to clock drift and arbitrary changes to the system time)</code>
 * </ul>
 * <p>
 * We have decided to work with nanos, since this provides us with reliably good granularity across all platforms.
 * We have used nanos for KPI measuring in various projects with no noticeably adverse effects. If any systems ever
 * find nanos to be too expensive, alternative millisecond-based annotations could be created.
 *
 * The exported view of any kpi is always in millis, regardless of how they are captured.
 *
 * @see KPIEvent
 * @see KPIMonitor
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface KPITimedEvent {

    /**
     * Name of KPI event being measured
     */
    String value() default "";

    /**
     * Value for operation discrimination, used for the operation tag in TSDB.
     */
    String operation() default "";

    /**
     * If true, caught exceptions will be treated as a separate KPI
     */
    boolean catchFailures() default false;
}
