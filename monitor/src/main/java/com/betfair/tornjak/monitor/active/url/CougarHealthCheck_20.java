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
 * Factory class that builds a {@link com.betfair.tornjak.monitor.active.url.UrlCheck} appropiate for services that are
 * implemented on Cougar and using HealthService 2.0.
 */
public class CougarHealthCheck_20 {

    public static UrlCheck create(UrlProvider provider) {
        return create(provider, null);
    }

    public static UrlCheck create(UrlProvider provider, String optionalPrefix) {
        UrlCheck urlActiveMonitor = new UrlCheck();

        Checker matches = new CheckResponseContains("<healthy>OK</healthy>");
        urlActiveMonitor.setChecker(matches);

        UrlProvider urlProvider;
        if (optionalPrefix == null) {
            urlProvider = new DeriveUrl("(https?://[a-zA-Z0-9\\.-]*(:[0-9]*)?)/.*", "$1/healthcheck/v2/summary?alt=xml", provider);
        }
        else {
            urlProvider = new DeriveUrl("(https?://[a-zA-Z0-9\\.-]*(:[0-9]*)?"+optionalPrefix+")/.*", "$1/healthcheck/v2/summary?alt=xml", provider);
        }
        urlActiveMonitor.setUrlProvider(urlProvider);
        return urlActiveMonitor;
    }

}
