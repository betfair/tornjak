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

import com.betfair.tornjak.monitor.active.Check;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Checks a dependency given its URL.
 * <p/>
 * This class has 3 extension points:
 * <ul>
 * <li>{@link #setUrlProvider(UrlProvider)}: class that decides what Url to check.
 * <li>{@link #setConnectionProvider(ConnectionProvider)}: class that knows how to create the connections.
 * <li>{@link #setChecker(Checker)}: class that knows what to check.
 * </ul>
 */
public class UrlCheck implements Check {
    private UrlProvider urlProvider;
    private Checker checker;
    private ConnectionProvider connectionProvider = new CreateUrlConnection();
    private int connectionTimeoutInMillis = 10 * 1000;
    private int readTimeoutInMillis = 10 * 1000;


    public void check() throws Exception {

        String urlToCheck = urlProvider.get();

        try {
            URLConnection urlConnection = connectionProvider.createConnection(urlToCheck);
            setTimeouts(urlConnection);
            checker.check(urlConnection);
        } catch (Exception e) {
            throw new Exception("Error checking URL [" + urlToCheck + "]: [" + e.getMessage() + "]", e);
        }
    }

    public String getDescription() {
        return "Checking url from "+urlProvider+" with checker "+checker;
    }

    public UrlProvider getUrlProvider() {
        return urlProvider;
    }

    public void setUrlProvider(UrlProvider urlProvider) {
        this.urlProvider = urlProvider;
    }

    public void setChecker(Checker checker) {
        this.checker = checker;
    }

    public void setConnectionProvider(ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    public void setConnectionTimeoutInMillis(int connectionTimeoutInMillis) {
        this.connectionTimeoutInMillis = connectionTimeoutInMillis;
    }

    public void setReadTimeoutInMillis(int readTimeoutInMillis) {
        this.readTimeoutInMillis = readTimeoutInMillis;
    }

    private void setTimeouts(URLConnection urlConnection) {
        urlConnection.setConnectTimeout(connectionTimeoutInMillis);
        urlConnection.setReadTimeout(readTimeoutInMillis);
    }

    private static class CreateUrlConnection implements ConnectionProvider {

        public URLConnection createConnection(String url) throws IOException {
            return new URL(url).openConnection();
        }
    }
}