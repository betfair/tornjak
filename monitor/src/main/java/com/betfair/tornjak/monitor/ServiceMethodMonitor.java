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

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.beans.factory.BeanNameAware;


/**
 * A convenience monitor to use on 'business' operation methods. These are methods which
 * third party client may call which we would like to be exposed in the 
 * diagnostics pages when they fail. However we do not want these failures
 * to cause the service to be marked as unavailable. But we do want fast
 * access to failing calls and the exception cause.
 * 
 * <p>This monitor has no active checks associated with it and only
 * contributes up to {@link Status.WARN} to the overall status. It starts
 * with status {@link Status.OK}</p>
 *  
 * <p>This monitor is bean name aware so that we only have to give this
 * bean an id and no extra config is required. The bean name will be used
 * for the name of the monitor by default. If the monitor name is explicitly
 * set, then this will be used</p>
 *  
 * @author vanbrakelb
 *
 */
public class ServiceMethodMonitor implements Monitor, DefaultMonitorMBean, PassiveMethodMonitor, BeanNameAware {
    //defer most operation to this monitor so we don't duplicate functionality
    //Protected so we can access the delegate for testing. Final so no issue.
    protected final DefaultMonitor delegate;
    
    public ServiceMethodMonitor(){
        delegate = new DefaultMonitor();
        delegate.setMaxImpactToOverallStatus(Status.WARN);
        delegate.success();
    }
    
    /**
     * Create this monityor with the given name. the bean name will be ignored
     * @param name
     */
    public ServiceMethodMonitor(String name) {
        delegate = new DefaultMonitor(name);
        delegate.setMaxImpactToOverallStatus(Status.WARN);
        delegate.success();
    }
    
    /**
     * We are bean name aware so we don't have to duplicate monitor name in spring config
     * if use the bean id/name as the monitor name
     */
    @Override
    public void setBeanName(String name) {
        //only set the monitor name if it hasn't been done already via the constructor. Else
        //we'd always get the spring bean name
        if( name != null && delegate.getName() == null){
            delegate.setName(name);
        }
    }
  
    /**
     * Set the name to be used for this monitor. Overrides the name set by the
     * bean name
     * @param name
     */
    public void setName(String name){
        delegate.setName(name);
    }


    @Override
    public String getMonitoredUrl() {
        return delegate.getMonitoredUrl();
    }


    @Override
    public Status getMaxImpactToOverallStatus() {
        return delegate.getMaxImpactToOverallStatus();
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public void addStatusChangeListener(StatusChangeListener listener) {
        delegate.addStatusChangeListener(listener);
    }

    @Override
    public Status getStatus() {
        return delegate.getStatus();
    }

    @Override
    public void removeStatusChangeListener(StatusChangeListener listener) {
        delegate.removeStatusChangeListener(listener);
    }


    @Override
    public ErrorCountingPolicy getErrorCountingPolicy() {
        return delegate.getErrorCountingPolicy();
    }

    @Override
    public long getFailureCount() {
        return delegate.getFailureCount();
    }

    @Override
    public String getLastException() {
        return delegate.getLastException();
    }

    @Override
    public long getLastFailureTime() {
        return delegate.getLastFailureTime();
    }

    @Override
    public long getLastSuccessTime() {
        return delegate.getLastSuccessTime();
    }

    @Override
    public String getMaxImpactToOverallStatusAsString() {
        return delegate.getMaxImpactToOverallStatusAsString();
    }

    @Override
    public String getStatusAsString() {
        return delegate.getStatusAsString();
    }

    @Override
    public long getWarningThreshold() {
        return delegate.getWarningThreshold();
    }

    public void setErrorCountingPolicy(ErrorCountingPolicy policy) {
        delegate.setErrorCountingPolicy(policy);
    }

    public void setWarningThreshold(long warningThreshold) {
        delegate.setWarningThreshold(warningThreshold);
    }

    @Override
    public void failure(Throwable cause) {
        delegate.failure(cause);
    }

    @Override
    public void failure(String cause) {
        delegate.failure(cause);
    }

    @Override
    public void success() {
        delegate.success();
    }
    
    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(17,11, this);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
