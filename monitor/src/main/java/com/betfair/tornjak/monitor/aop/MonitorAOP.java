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

import com.betfair.tornjak.monitor.Monitor;
import com.betfair.tornjak.monitor.MonitorRegistry;
import com.betfair.tornjak.monitor.PassiveMethodMonitor;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.Ordered;

import java.lang.reflect.Method;

/**
 * @author langfords
 */
public final class MonitorAOP implements Ordered {

    private MonitorRegistry monitorRegistry;

    private int order = 15;
    private MonitoredMethodCall monitoredMethodCall = new MonitoredMethodCall();

    public Object monitorMethod(final ProceedingJoinPoint pjp) throws Throwable {

        final MonitorMethod annotation = getAnnotation(pjp);

        Monitor monitor = monitorRegistry.getMonitor(annotation.monitorName());
        if (monitor == null) {
            throw new IllegalArgumentException("The monitor named " + annotation.monitorName() + " is not defined.");
        }
        if (!(monitor instanceof PassiveMethodMonitor)) {
            throw new IllegalArgumentException("The monitor named " + annotation.monitorName() + " is not a method monitor (passive or active).");
        }

        return monitoredMethodCall.call(pjp, (PassiveMethodMonitor) monitor);
    }

    private MonitorMethod getAnnotation(final ProceedingJoinPoint pjp) {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        MonitorMethod fromSignature = signature.getMethod().getAnnotation(MonitorMethod.class);
        if (fromSignature != null) {
            return fromSignature;
        }
        // right, couldn't find it on the method signature, but we might be looking at an implementation of an interface
        // so now look at the target object/class
        try {
            Method m = pjp.getTarget().getClass().getDeclaredMethod(signature.getName(), signature.getParameterTypes());
            return m.getAnnotation(MonitorMethod.class);
        } catch (NoSuchMethodException e) {
            // hmm, not sure we should ever see this
            throw new IllegalStateException("Couldn't find method that was called on the object it was called on");
        }
    }

    // ============ Dependency injection =============

    public void setMonitorRegistry(MonitorRegistry monitorRegistry) {
        this.monitorRegistry = monitorRegistry;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

}
