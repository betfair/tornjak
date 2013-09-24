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

/**
 * A policy that indicates that NPEs do not count for monitoring
 * 
 * @author sorokod
 *
 */
public class NPEDoesNotCountPolicy implements ErrorCountingPolicy{ 


    public boolean countsAsError(Throwable t) {
        return ! (t instanceof NullPointerException);
    }

    public boolean countsAsError(Object o) {
        return false;
    }
}
