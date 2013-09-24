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

public class AopBean {
    
    
    
    @MonitorMethod( monitorName = "monitorA" )
    public void methodOne() {
        throw new RuntimeException();
    }

    @MonitorMethod ( monitorName = "monitorB")
    public void methodTwo() {
        throw new NullPointerException();
    }

    @MonitorMethod ( monitorName = "monitorB" )
    public void methodThree() {
        throw new RuntimeException();
    }
 
    @MonitorMethod ( monitorName = "flubber" )
    public void methodFour() {
        throw new RuntimeException();
    }

    @MonitorMethod ( monitorName = "monitorC" )
    public String methodFive() {
        return "Hi";
    }

    @MonitorMethod ( monitorName = "monitorC" )
    public String methodSix() {
        throw new RuntimeException();
    }

}
