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

import com.betfair.tornjak.monitor.aop.DefaultErrorCountingPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Configurable error policy for SOAP faults that follow the CXF/BetfairIDL convention.
 * <p/>
 * This error policy uses reflection to access the error code. The faults are expected to have a signature like:
 * <p/>
 * String fault.getFaultInfo().getErrorCode
 */
public class SoapFaultErrorPolicy implements ErrorCountingPolicy {
    Logger LOG = LoggerFactory.getLogger(SoapFaultErrorPolicy.class);

    private Method faultInfoMethod;
    private Method errorCodeMethod;
    private HashSet<String> nonCritialCodes;
    private Class faultClass;
    private ErrorCountingPolicy defaultPolicy = new DefaultErrorCountingPolicy();

    /**
     * see {@link #SoapFaultErrorPolicy(Class, java.util.Set)}
     */
    public SoapFaultErrorPolicy(Class faultClass, String... nonCriticalErrorCodes) throws NoSuchMethodException {
        this(faultClass, new HashSet<String>(Arrays.asList(nonCriticalErrorCodes)));
    }

    /**
     * @param nonCriticalErrorCodes List of error codes that will <b>not</b> count as errors
     */
    public SoapFaultErrorPolicy(Class faultClass, Set<String> nonCriticalErrorCodes) throws NoSuchMethodException {
        this.faultClass = faultClass;
        faultInfoMethod = faultClass.getMethod("getFaultInfo");
        errorCodeMethod = faultInfoMethod.getReturnType().getMethod("getErrorCode");
        nonCritialCodes = new HashSet<String>(nonCriticalErrorCodes);
    }

    public boolean countsAsError(Throwable t) {
        try {
            if (faultClass.isInstance(t)) {
                Object faultInfo = faultInfoMethod.invoke(t);
                Object errorCode = errorCodeMethod.invoke(faultInfo);
                return !nonCritialCodes.contains(errorCode);
            }
        } catch (Exception e) {
            LOG.error("Error", e);
        }
        return defaultPolicy.countsAsError(t);
    }

    public boolean countsAsError(Object o) {
        return defaultPolicy.countsAsError(o);
    }

}
