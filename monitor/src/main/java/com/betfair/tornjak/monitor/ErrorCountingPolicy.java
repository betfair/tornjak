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

/**
 * An interface that allows to indicate that certain exception incurred during method invocation and return value
 * returned by a method should or shouln't be considered as errors,
 * 
 * @author sorokod
 * 
 */
public interface ErrorCountingPolicy {

    /**
     * @param t
     * @return true if and only if, the throwable t should count as an error
     */
    public boolean countsAsError(Throwable t);

    /**
     * @param o
     * @return true if and only if, the object o should count as an error
     */
    public boolean countsAsError(Object o);
}
