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

package com.betfair.tornjak.monitor.active.url;

/**
 * Factory class that builds a {@link UrlCheck} appropiate for services that uses the service commons stack.
 * The service commons stack convention is:
 * <p/>
 * <li>Services are published in the endpoint like https://host:port/context/service/ServiceName
 * <li>Webping are published in https://host:port/context/webping
 * <li>Ok response is "Server OK"
 * <p/>
 * Note that you will probably need to set
 * the {@link UrlCheck#setConnectionProvider(ConnectionProvider)} to a
 * SSL aware one. See the client-factory package for a CXF specific one.
 * <p/>
 * Note that this active monitor should <b>not</b> be use with services that have just do
 * <a href="http://confluence.app.betfair/display/SCO/Issues+surrounding+passive+web+ping">passive monitoring</a>
 * (Those using < monitor-1.3)
 */
public class ServiceCommonsWebPing {

    public static UrlCheck create(UrlProvider provider) {
        UrlCheck urlActiveMonitor = new UrlCheck();

        Checker matches = new CheckResponseContains("Server OK");
        urlActiveMonitor.setChecker(matches);

        UrlProvider urlProvider = new DeriveUrl("(https?://.*)/service/.*", "$1/webping", provider);
        urlActiveMonitor.setUrlProvider(urlProvider);
        return urlActiveMonitor;
    }

}
