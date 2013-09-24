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

import java.util.List;



/**
 * Main interface for the KPI monitor.
 * <p>
 * This interface, along with associated annotations (if used) are all that client code needs to use from the KPI
 * package.
 *
 * <h2>Usage for clients (ie. the rest of the system)</h2>
 * <p>
 * <code>event()</code> methods record the occurence of an event. Each event has a key (representing the type of event).
 * Different versions of this method allow the client to specify relevant pieces of information:
 * <ul>
 * <li>events which need to be timed would call an event method which specifies the time in nanoseconds.</li>
 * <li>if a client wishes to differentiate between successes and failures, call a method which specifies
 * 		succeeded/failed</li>
 * </ul>
 * <p>
 * The KPI methods simply take <code>long</code>s for durations, and could be used to store either nanosecond or
 * millisecond durations. Some of the related annotation-based wrappers use nanoseconds.
 */
public interface KPIMonitor {

	/**
	 * Add a KPI for an event with given name.
     *
     * @param name metric name
	 */
    public void addEvent(String name);

	/**
	 * Add a KPI for an event with the given name, and whether the event succeeded or not.
     *
     * @param name metric name
     * @param succeeded true if the call succeeded
	 */
    public void addEvent(String name, boolean succeeded);

	/**
	 * Add a KPIs with an event with given name, and duration.
     *
     * @param name metric name
     * @param duration duration of the call
	 */
	public void addEvent(String name, double duration);

	/**
	 * Add a KPI for an event with given name, and duration whether the event succeeded or not.
     *
     * @param name metric name
     * @param duration duration of the call
     * @param succeeded true if the call succeeded
	 */
	public void addEvent(String name, double duration, boolean succeeded);

    /**
     * Add a KPI for an event with given name.
     * @param name metric name
     * @param operationName operation discriminator (forms the operation tag in TSDB when emitted via StatsE)
     */
    public void addEvent(String name, String operationName);

    /**
     * Add a KPI for an event with the given name, and whether the event succeeded or not.
     * @param name metric name
     * @param operationName operation discriminator (forms the operation tag in TSDB when emitted via StatsE)
     * @param succeeded true if the call succeeded
     */
    public void addEvent(String name, String operationName, boolean succeeded);

    /**
     * Add a KPIs with an event with given name, and duration.
     *
     * @param name metric name
     * @param operationName operation discriminator (forms the operation tag in TSDB when emitted via StatsE)
     * @param duration duration of the call
     */
    public void addEvent(String name, String operationName, double duration);

    /**
     * Add a KPI for an event with given name, and duration whether the event succeeded or not.
     *
     * @param name metric name
     * @param operationName operation discriminator (forms the operation tag in TSDB when emitted via StatsE)
     * @param duration duration of the call
     * @param succeeded true if the call succeeded
     */
    public void addEvent(String name, String operationName, double duration, boolean succeeded);

    /**
     * Return the last time the KPIs were read
     * <p>
     * (This is for use by publishe, and client code normally need not call this method.)
     */
    public long getLastReadTime();

    /**
     * Retrieve a list of KPIs going back for the given duration.
     * <p>
     * (This is for use by publisher, and client code normally need not call this method.)
     */
    public List<KPI> readKPIs();

    /**
     * Specify the read interval for the monitor. Can be dynamically adjusted.
     */
    public void setReadInterval(long millis);
}
