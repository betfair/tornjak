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

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.core.Ordered;


import com.betfair.tornjak.kpi.KPIMonitor;
import com.betfair.tornjak.kpi.KPITimer;

/**
 * AOP advice which is responsible for measuring and counting methods by means of annotations.
 */
@Aspect
public class KPIMeasuringAspect implements Ordered {

    private KPIMonitor kpiMonitor;
    private int order = 10;


    @AfterThrowing("@annotation(kpiEvent)")
    public void countAdvice(final JoinPoint jp, KPIEvent kpiEvent) {
        addEvent(jp, kpiEvent.value(), kpiEvent.operation(), false, kpiEvent.catchFailures());
    }

    private void addEvent(JoinPoint jp, String eventValue, String eventOperation, boolean succeeded, boolean catchFailures) {
        final String name = eventValue.isEmpty() ? jp.getTarget().getClass().getSimpleName() : eventValue;
        final String operation = eventOperation.isEmpty() ? jp.getSignature().getName() : eventOperation;

        if (catchFailures) {
            kpiMonitor.addEvent(name, operation, succeeded);
        } else {
            kpiMonitor.addEvent(name, operation);
        }
    }


    @AfterReturning("@annotation(kpiEvent)")
    public void doEventAdvice(final JoinPoint jp, KPIEvent kpiEvent) {
        addEvent(jp, kpiEvent.value(), kpiEvent.operation(), true, kpiEvent.catchFailures());
    }


    @Around("@annotation(kpiTimedEvent)")
    public Object measureDuration(final ProceedingJoinPoint pjp, KPITimedEvent kpiTimedEvent) throws Throwable {

        KPITimer timer = new KPITimer();
        boolean succeeded = true;
        try {
            timer.start();
            return pjp.proceed();

        } catch(Throwable t) {
            succeeded = false;
            throw t;

        } finally {
            // this looks like a reasonable and clean place to stop the clock, even if we've wasted a few nanos before
            //  we actually get here
            double duration = timer.stop();
            final String eventValue = kpiTimedEvent.value();
            final String name = eventValue.isEmpty() ? pjp.getTarget().getClass().getSimpleName() : eventValue;
            final String operation = kpiTimedEvent.operation().isEmpty() ? pjp.getSignature().getName() : kpiTimedEvent.operation();

            if (kpiTimedEvent.catchFailures()) {
                kpiMonitor.addEvent(name, operation, duration, succeeded);
            } else {
                kpiMonitor.addEvent(name, operation, duration);
            }
        }
    }


    /**
     * DI
     */
    @Required
    public void setMonitor(KPIMonitor kpiMonitor) {
        this.kpiMonitor = kpiMonitor;
    }

    public int getOrder() {
    	return this.order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

}
