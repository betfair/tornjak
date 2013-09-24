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

package com.betfair.tornjak.monitor.aop;

import com.betfair.tornjak.monitor.PassiveMethodMonitor;

import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.core.Ordered;

public final class FixedMonitorAOP implements Ordered {

    private int order = 15;
    private PassiveMethodMonitor monitor;

    public Object monitorMethod(final ProceedingJoinPoint pjp) throws Throwable {

        if (monitor == null) {
            throw new IllegalArgumentException("The monitor cannot be null");
        }

        return new MonitoredMethodCall().call(pjp, monitor);
    }

    // ============ Dependency injection =============

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public void setMonitor(PassiveMethodMonitor monitor) {
        this.monitor = monitor;
    }
}