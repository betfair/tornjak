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

import com.betfair.tornjak.monitor.ErrorCountingPolicy;
import com.betfair.tornjak.monitor.PassiveMethodMonitor;
import org.slf4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.LoggerFactory;

class MonitoredMethodCall {

    private static final Logger LOG = LoggerFactory.getLogger(MonitoredMethodCall.class);
    
    public Object call(ProceedingJoinPoint pjp, PassiveMethodMonitor monitor) throws Throwable {
        ErrorCountingPolicy errorCountingPolicy = monitor.getErrorCountingPolicy();

        Throwable error = null;
        Object ret = null;
        try {
            ret = pjp.proceed();
            return ret;
        } catch (Throwable t) {
            error = t;
            throw t;
        } finally {
            if (error != null) {

                if (LOG.isDebugEnabled()) {
                    LOG.debug("Got a Throwable in monitor: " + monitor.getName(), error);
                }

                if (errorCountingPolicy.countsAsError(error)) {
                    monitor.failure(error);
                } else {
                    monitor.success();
                }
            } else {
                if (errorCountingPolicy.countsAsError(ret)) {
                    monitor.failure("Got return value which is considered an error: " + ret);
                } else {
                    monitor.success();
                }
            }
        }
    }
}
