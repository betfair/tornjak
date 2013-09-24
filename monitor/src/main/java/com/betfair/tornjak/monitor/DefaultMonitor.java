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

import com.betfair.tornjak.monitor.active.Check;
import com.betfair.tornjak.monitor.aop.DefaultErrorCountingPolicy;
import com.betfair.tornjak.monitor.util.MonitorUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.BeanNameAware;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Core monitoring implementation - ideally there should be one of these for
 * each external component used within a particular application.
 * 
 * <p>This monitor is bean name aware so that we only have to give this
 * bean an id and no extra config is required. The bean name will be used
 * for the name of the monitor by default. If the monitor name is explicitly
 * set, then this will be used</p>
 */
public class DefaultMonitor implements DefaultMonitorMBean, ActiveMethodMonitor, BeanNameAware {

    private Logger logger = LoggerFactory.getLogger(getClass());

    protected final Object monitorLock = new Object();

    private String name;
    private long lastFailureTime = 0;
    private long lastSuccessTime = 0;
    private long failureCount = 0;
    private String lastException = "";
    private long warningThreshold = 30000;
    private Status maxImpactToOverallStatus = Status.FAIL;
    private Status status = Status.FAIL;
    private TimerTask warnExpiry;
    private Timer timer;

    private ErrorCountingPolicy errorCountingPolicy = new DefaultErrorCountingPolicy();
    private Check check;
    private boolean oneSuccessAtLeast = false;
    private final List<StatusChangeListener> listeners = new ArrayList<StatusChangeListener>();

    public DefaultMonitor() {
        timer = new Timer(true);
    }

    public DefaultMonitor(String aName) {
        setName(aName);
    }

    /**
     * We are bean name aware so we don't have to duplicate monitor name in spring config
     * if use the bean id/name as the monitor name
     */
    @Override
    public void setBeanName(String name) {
        //only set the monitor name if it hasn't been done already via the constructor. Else
        //we'd always get the spring bean name
        if (getName() == null) {
            setName(name);
        }
    }


    @Override
    public String getMonitoredUrl() {
        return MonitorUtils.getUrlFromMonitor(this);
    }


    @Override
    public Status getMaxImpactToOverallStatus() {
        return maxImpactToOverallStatus;
    }

    @Override
    public String getMaxImpactToOverallStatusAsString() {
        return maxImpactToOverallStatus.name();
    }

    public void setMaxImpactToOverallStatus(Status maxImpactToOverallStatus) {
        if (maxImpactToOverallStatus == null) {
            throw new IllegalArgumentException("MaxImpactToOverallStatus cannot be null");

        }
        this.maxImpactToOverallStatus = maxImpactToOverallStatus;
    }

    public void setName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Monitor name can not be null");
        }
        this.name = name;
        timer = new Timer(name, true);
        logger = LoggerFactory.getLogger(getClass()+"."+name);
    }
    
    @Override
    public String getName() {
        return name;
    }

    @Override
    public long getWarningThreshold() {
        return warningThreshold;
    }

    public void setWarningThreshold(long warningThreshold) {
        this.warningThreshold = warningThreshold;
    }

    @Override
    public long getLastFailureTime() {
        synchronized (monitorLock) {
            return lastFailureTime;
        }
    }

    @Override
    public long getLastSuccessTime() {
        synchronized (monitorLock) {
            return lastSuccessTime;
        }
    }

    @Override
    public long getFailureCount() {
        synchronized (monitorLock) {
            return failureCount;
        }
    }

    @Override
    public String getLastException() {
        synchronized (monitorLock) {
            return lastException;
        }
    }

    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public String getStatusAsString() {
        return getStatus().name();
    }

    @Override
    public void success() {
        if (logger.isDebugEnabled()) {
            logger.debug(getName()+": Success event received");
        }
        StatusChangeEvent evt;
        synchronized (monitorLock) {
            lastSuccessTime = System.currentTimeMillis();
            failureCount = 0;
            oneSuccessAtLeast = true;
            // going to warn
            if (status == Status.FAIL) {
                scheduleWarnExpiry();
            }
            // update status
            evt = updateStatus();
        }
        if (evt != null) {
            fireStatusChangedEvent(evt);
        }
    }

    @Override
    public void failure(Throwable aCause) {

        if (aCause == null) {
            throw new IllegalArgumentException("Must supply a cause");
        }
        failure(getExceptionAsString(aCause));
    }

    @Override
    public void failure(String cause) {
        if (StringUtils.isEmpty(cause)) {
            throw new IllegalArgumentException("Must supply a cause");
        }
        if (logger.isDebugEnabled()) {
            logger.debug(getName()+": Failure event received");
        }

        StatusChangeEvent evt;
        synchronized (monitorLock) {
            ++failureCount;
            lastFailureTime = System.currentTimeMillis();
            lastException = cause;
            boolean startStateIsWarn = false;
            // going into warn
            if (status == Status.OK) {
                scheduleWarnExpiry();
            }
            // going into faiure
            else if (status == Status.WARN) {
                startStateIsWarn = true;
            }
            // update status
            evt = updateStatus();
            if (startStateIsWarn) {
                // staying in warn state, need to reset the expiry
                if (evt == null) {
                    scheduleWarnExpiry();
                }
                // otherwise we're moving to a fail state, so just cancel it
                else {
                    cancelExistingWarnExpiry();
                }
            }
        }

        if (evt != null) {
            fireStatusChangedEvent(evt);
        }
    }

    private void scheduleWarnExpiry() {
        cancelExistingWarnExpiry();
        if (logger.isDebugEnabled()) {
            logger.debug(getName()+": Scheduling warn expiry task to run in "+warningThreshold+"ms");
        }
        warnExpiry = new TimerTask() {
            @Override
            public void run() {
                StatusChangeEvent evt;
                synchronized (monitorLock) {
                    if (logger.isDebugEnabled()) {
                        logger.debug(getName()+": Executing warn expiry task");
                    }
                    evt = updateStatus();
                }
                if (evt != null) {
                    fireStatusChangedEvent(evt);
                }
            }
        };
        timer.schedule(warnExpiry, warningThreshold);
    }

    private void cancelExistingWarnExpiry() {
        if (warnExpiry != null) {
            if (logger.isDebugEnabled()) {
                logger.debug(getName()+": Cancelling existing warn expiry task");
            }
            warnExpiry.cancel();
            warnExpiry = null;
        }
    }

    private StatusChangeEvent updateStatus() {
        StatusChangeEvent ret = null;
        Status oldStatus = status;
        status = calcStatus();
        if (logger.isDebugEnabled()) {
            logger.debug(getName()+": Status is "+status);
        }
        if (status != oldStatus) {
            ret = new StatusChangeEvent(this, oldStatus, status);
        }
        return ret;
    }
    private Status calcStatus() {
        long absoluteTimeWarnWindow = System.currentTimeMillis() - warningThreshold;
        if (!oneSuccessAtLeast || getFailureCount() > 1) {
            return Status.FAIL;
        }
        return getLastFailureTime() > absoluteTimeWarnWindow ? Status.WARN : Status.OK;
    }

    private String getExceptionAsString(Throwable t) {
        return ExceptionUtils.getFullStackTrace(t);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Monitor) {
            return getName().equals(((Monitor)obj).getName());
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return getName().hashCode();
    }
    
    public ErrorCountingPolicy getErrorCountingPolicy() {
        return errorCountingPolicy;
    }

    public void setErrorCountingPolicy(ErrorCountingPolicy policy) {
        if (policy == null) {
            throw new IllegalArgumentException("ErrorCountingPolicy cannot be null");
        }
        this.errorCountingPolicy = policy;
    }
    
    @Override
    public Check getActiveMonitor() {
        return check;
    }

    public void setActiveMonitor(Check check) {
        this.check = check;
    }

    @Override
    public String toString() {
        return new StringBuilder("Monitor[").append( getName() ).append(']').toString();
    }

    @Override
    public void addStatusChangeListener(StatusChangeListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    @Override
    public void removeStatusChangeListener(StatusChangeListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    private void fireStatusChangedEvent(StatusChangeEvent evt) {
        if (logger.isDebugEnabled()) {
            logger.debug(getName()+": Status changed from "+evt.getOldStatus()+" to "+evt.getNewStatus());
        }
        // take a copy of the listener list
        ArrayList<StatusChangeListener> listenersCopy;
        synchronized (listeners) {
            listenersCopy = new ArrayList<StatusChangeListener>(this.listeners);
        }
        for (StatusChangeListener listener : listenersCopy) {
            listener.statusChanged(evt);
        }
    }
}
