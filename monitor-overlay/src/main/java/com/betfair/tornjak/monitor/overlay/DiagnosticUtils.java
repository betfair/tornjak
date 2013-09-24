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

package com.betfair.tornjak.monitor.overlay;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiagnosticUtils {

    private static final long DAY_LENGTH = 86400000L;
    private static final long HOUR_LENGTH = 3600000L;
    private static final long MINUTE_LENGTH = 60000L;
    private static final long SECOND_LENGTH  = 1000L;
    
    public static String colourForStatus(String status) {
        String color = "#888888";
        if ("WARN".equals(status)) {
            color = "#FF8040";
        }
        else if ("FAIL".equals(status)) {
            color = "#ff0000";
        }
        else if ("OK".equals(status)) {
            color = "#00ff00";
        }
        return color;
    }

    public static String timeSince(long lastTimeInMillis) {
        if (lastTimeInMillis == 0) {
            return "Never";
        }
        long timeDiff = System.currentTimeMillis() - lastTimeInMillis;
        String ret = "";
        String sep = "";
        if (timeDiff < SECOND_LENGTH) {
            ret = "Less than 1 second";
        }
        else {
            if (timeDiff >= DAY_LENGTH) {
                long days = timeDiff / DAY_LENGTH;
                ret += sep + days + " day";
                if (days > 1) {
                    ret += "s";
                }
                sep = ", ";
                timeDiff -= (days * DAY_LENGTH);
            }
            if (timeDiff >= HOUR_LENGTH) {
                long hours = timeDiff / HOUR_LENGTH;
                ret += sep + hours + " hour";
                if (hours > 1) {
                    ret += "s";
                }
                sep = ", ";
                timeDiff -= (hours * HOUR_LENGTH);
            }
            if (timeDiff >= MINUTE_LENGTH) {
                long mins = timeDiff / MINUTE_LENGTH;
                ret += sep + mins + " minute";
                if (mins > 1) {
                    ret += "s";
                }
                sep = ", ";
                timeDiff -= (mins * MINUTE_LENGTH);
            }
            if (timeDiff >= SECOND_LENGTH) {
                long secs = timeDiff / SECOND_LENGTH;
                ret += sep + secs + " second";
                if (secs > 1) {
                    ret += "s";
                }
                timeDiff -= (secs * SECOND_LENGTH);
            }
        }

        ret += " ago";
        return ret;
    }

    public static Map<String, SubSystem> getSubSystems() {
        final String monitoringJmxInterface = "com.sportex.betex.monitor.DefaultMonitorMBean";
        // get all the beans implementing these interfaces
        
        List<MBeanInstance> monitors = JMXUtils.getInstancesOf(monitoringJmxInterface);
        Map<String, SubSystem> subsystems = new HashMap<String, SubSystem>();
        for (MBeanInstance monitor : monitors) {
            // name = monitor.accountServiceClient
            String name = monitor.getObjectName().getKeyProperty("name");
            if (name.startsWith("monitor.")) {
                name = name.substring(8);
            }
            if (name.endsWith("ServiceClient")) {
                name = name.substring(0, name.length() - 6);
            }
            // name = accountService
            subsystems.put(name, new SubSystem(name, monitor));
        }
        return subsystems;
    }

    public static MBeanInstance getOverallStatusBean() {
        final String overallStatusInterface = "com.sportex.betex.monitor.OverallStatusMBean";
        // get all the beans implementing these interfaces
        List<MBeanInstance> monitors = JMXUtils.getInstancesOf(overallStatusInterface);
        if (monitors.size() > 0) {
            return monitors.get(0);
        }
        return null;
    }
}
