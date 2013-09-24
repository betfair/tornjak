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

import java.util.*;

/**
 * The default implementation of the registry.
 */
public class DefaultMonitorRegistry implements MonitorRegistry {

    /**
     * Maps monitor names to monitors
     */
    private final Map<String, Monitor> monitorMap = new HashMap<String, Monitor>();

    private StatusAggregator statusAggregator;
    private final Object statusAggregatorLock = new Object();

    private final List<RegistryListener> listeners = new ArrayList<RegistryListener>();

    /**
     * @return the set of all the monitors
     */
    public Set<Monitor> getMonitorSet() {
        synchronized (monitorMap) {
            return new HashSet<Monitor>(monitorMap.values());
        }
    }

    /**
     * @return the named monitor or null if no such monitor is defined
     */
    public Monitor getMonitor(String monitorName) {
        synchronized (monitorMap) {
            return monitorMap.get(monitorName);
        }
    }

    @Override
    public String toString() {
        synchronized (monitorMap) {
            return new StringBuilder("DefaultMonitorRegistry[").append(monitorMap).append(']').toString();
        }
    }

    public void setStatusAggregator(StatusAggregator statusAggregator) {
    	
        StatusAggregatorChangeEvent evt;
        synchronized (statusAggregatorLock) {
            evt = new StatusAggregatorChangeEvent(this, this.statusAggregator, statusAggregator);
            this.statusAggregator = statusAggregator;
            if (statusAggregator != null) {
                if (statusAggregator instanceof MonitorRegistryAware) {
                    ((MonitorRegistryAware)statusAggregator).setMonitorRegistry(this);
                }
            }
        }
        
        ArrayList<RegistryListener> listenersCopy;
        synchronized (listeners) {
            listenersCopy = new ArrayList<RegistryListener>(this.listeners);
        }
                
	    for (RegistryListener listener : listenersCopy) {
	    	listener.statusAggregatorChanged(evt);
	    }        
    }

    public StatusAggregator getStatusAggregator() {
        synchronized (statusAggregatorLock) {
            return statusAggregator;
        }
    }

    public void addRegistryListener(RegistryListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    public void removeRegistryListener(RegistryListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    /**
     * Provided as a convenience method for injecting a List of Monitors
     * via Spring properties.
     */
    public void setMonitors(List<Monitor> monitors) {
        for (Monitor m : monitors) {
            addMonitor(m);
        }
    }

    public void addMonitor(Monitor monitor) {
        Monitor oldMonitorByName;
        synchronized (monitorMap) {
            oldMonitorByName = monitorMap.put(monitor.getName(), monitor);
        }
        if (oldMonitorByName != null) {
            throw new IllegalStateException("Monitors must have unique names. To change a name->Monitor association, first remove the Monitor from this MonitorRegistry");
        }
        MonitorChangeEvent evt = new MonitorChangeEvent(this, monitor);
        ArrayList<RegistryListener> listenersCopy;
        synchronized (listeners) {
            listenersCopy = new ArrayList<RegistryListener>(this.listeners);
        }
        for (RegistryListener listener : listenersCopy) {
            listener.monitorAdded(evt);
        }
    }

    public void removeMonitor(Monitor monitor) {
        Monitor oldMonitorByName;
        synchronized (monitorMap) {
            oldMonitorByName = monitorMap.remove(monitor.getName());
        }
        if (oldMonitorByName == null) {
            throw new IllegalStateException("You're trying to remove a Monitor from this MonitorRegistry which isn't registered");
        }
        MonitorChangeEvent evt = new MonitorChangeEvent(this, monitor);
        ArrayList<RegistryListener> listenersCopy;
        synchronized (listeners) {
            listenersCopy = new ArrayList<RegistryListener>(this.listeners);
        }
        for (RegistryListener listener : listenersCopy) {
            listener.monitorRemoved(evt);
        }
    }
}
