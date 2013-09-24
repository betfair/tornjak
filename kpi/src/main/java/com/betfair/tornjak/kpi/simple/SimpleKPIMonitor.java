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
import com.betfair.tornjak.kpi.KPIMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * KPIMonitor which keeps track of the number of calls, failed calls, and the last 20 call times.
 */
public class SimpleKPIMonitor implements KPIMonitor {

    private static Logger log = LoggerFactory.getLogger(SimpleKPIMonitor.class);

    private Map<String, SimpleKPI> kpiMap = new ConcurrentHashMap<String, SimpleKPI>();

    private long readTime = 0;

    private MBeanServer mbeanServer;

    private String jmxDomain = "KPI.Simple";

    @Override
    public void addEvent(String name) {
        addEvent(name, null);
    }

    @Override
    public void addEvent(String name, boolean succeeded) {
        addEvent(name, null, succeeded);
    }

    @Override
    public void addEvent(String name, double duration) {
        addEvent(name, null, duration);
    }

    @Override
    public void addEvent(String name, double duration, boolean succeeded) {
        addEvent(name, null, duration, succeeded);
    }

    @Override
    public void addEvent(String name, String operationName) {
        SimpleKPI kpi = getKPI(name, operationName);
        kpi.recordCall(0);
    }

    @Override
    public void addEvent(String name, String operationName, boolean succeeded) {
        SimpleKPI kpi = getKPI(name, operationName);
        if (succeeded) {
            kpi.recordCall(0);
        } else {
            kpi.recordFailure(0);
        }
    }

    @Override
    public void addEvent(String name, String operationName, double duration) {
        SimpleKPI kpi = getKPI(name, operationName);
        kpi.recordCall(duration);
    }

    @Override
    public void addEvent(String name, String operationName, double duration, boolean succeeded) {
        SimpleKPI kpi = getKPI(name, operationName);
        if (succeeded) {
            kpi.recordCall(duration);
        } else {
            kpi.recordFailure(duration);
        }
    }

    private SimpleKPI getKPI(String name, String operation) {
        String key = name+(operation!=null?"."+operation:"");
        SimpleKPI kpi = kpiMap.get(key);
        if (kpi == null) {
            kpi = new SimpleKPI(key);
            kpiMap.put(key, kpi);
            // register in jmx
            if (mbeanServer != null) {
                try {
                    mbeanServer.registerMBean(kpi, new ObjectName(jmxDomain + ":name=" + name+(operation!=null?",operation="+operation:"")));
                }
                catch (InstanceAlreadyExistsException e) {
                    // don't care, is likely to happen as this call isn't synchronized
                }
                catch (Exception e) {
                    log.warn("Error registering kpi in JMX", e);
                }
            }
        }
        return kpi;
    }

    @Override
    public long getLastReadTime() {
        return readTime;
    }

    @Override
    public List<KPI> readKPIs() {
        readTime = System.currentTimeMillis();
        return new ArrayList<KPI>(kpiMap.values());
    }

    @Override
    public void setReadInterval(long millis) {
        throw new UnsupportedOperationException();
    }

    public void setMbeanServer(MBeanServer mbeanServer) {
        this.mbeanServer = mbeanServer;
    }

    public void setJmxDomain(String jmxDomain) {
        this.jmxDomain = jmxDomain;
    }
}
