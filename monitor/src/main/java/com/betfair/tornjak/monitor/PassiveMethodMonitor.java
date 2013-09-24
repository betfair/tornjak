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
 * An interface for monitors which can be used to monitor method
 * calls. An external agent will catch errors on monitored method
 * invocations and call the appropriate methods on this.
 * 
 * <p>Envisioned that the external agent is usually applied via the
 * use of AOP</p>
 * 
 * @author langfords
 * @author vanbrakelb
 */
public interface PassiveMethodMonitor extends Monitor {

    /**
     * Indicates a method invocation has been successful. The 
     * status of this monitor may be changed as a result of this
     */
    void success();

    /**
     * Indicates the method invocation threw an error. The status 
     * of this monitor may change as a result of this
     * 
     * @param cause
     */
    void failure(Throwable cause);

    /**
     * indicates a failure of a method invocation. Custom
     * error msg is supplied. The status 
     * of this monitor may change as a result of this
     * 
     * @param cause
     */
    void failure(String cause);

    /**
     * Return the error counting policy used to determine if a captured
     * error should be considered a failure. If it is, then the 
     * {@link #failure(Throwable)} method is invoked with the captured 
     * error
     * 
     * @return
     */
    ErrorCountingPolicy getErrorCountingPolicy();
}