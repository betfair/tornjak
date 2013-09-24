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

import com.betfair.tornjak.kpi.KPIMonitor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation which will, for methods of Spring-loaded beans, increment a 'hit counter' for each call to the annotated
 * method.
 * <p>
 * The <code>value</code> attribute takes the name of the KPI being measured (and is called 'value') to enable using
 * the annotation in shorthand <code>@KPIEvent("foo")</code>.
 * <p>
 * If <code>catchFailures</code> is true (default is false), then the KPI monitor will be called in a way that
 * distinguishes between successful and failed (ie. exception thrown) invocations. How these are reported is up to the
 * specific implementation of {@link KPIMonitor}.
 * <p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface KPIEvent {

    /**
     * Name of KPI event being counted
     */
    String value() default "";

    /**
     * Value for operation discrimination, used for the operation tag in TSDB.
     */
    String operation() default "";

    /**
     * If true, caught exceptions will be treated as a separate KPI
     */
    boolean catchFailures() default false;
}
