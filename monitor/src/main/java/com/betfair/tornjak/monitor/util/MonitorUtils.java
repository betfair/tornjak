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

import com.betfair.tornjak.monitor.ActiveMethodMonitor;
import com.betfair.tornjak.monitor.active.Check;
import com.betfair.tornjak.monitor.active.url.UrlCheck;


/**
 *
 * Utility class for monitors.
 *
 * @author Rolf Schuster
 */
public final class MonitorUtils {

    /**
     * Retrieves the monitored URL from the specified Monitor, if it is based on
     * a UrlCheck, or null otherwise.
     *
     * This method was created so it would be easier for other monitors which extend DefaultMonitorMBean to
     * retrieve the monitored URL.
     *
     * @param monitor the Monitor
     * @return the monitored URL for this service if UrlCheck is used, or null if not
     */
    public static String getUrlFromMonitor(ActiveMethodMonitor monitor) {
        Check check = monitor.getActiveMonitor();
        if (check instanceof UrlCheck) {
            try {
                return ((UrlCheck)check).getUrlProvider().get();
            } catch (Exception e) {
                return e.getMessage();
            }
        }

        return null;
    }

    private MonitorUtils() {
    }
}
