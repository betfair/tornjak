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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Transforms a url (usually the service endpoint) to another url (usually the webping url).
 * <p/>
 * A couple of examples:
 * <pre>
 * String toMatch = "(.*)/SomeService";
 * String replaceWith = "$1/Ping.jsp";
 * String originalUrl = "http://hostname/someservice/v3/SomeService";
 * String expectedUrl = "http://hostname/someservice/v3/Ping.jsp";
 * </pre>
 * <pre>
 * String toMatch = "(.*)/service/(.*)";
 * String replaceWith = "$1/$2/webping";
 * String originalUrl = "https://localhost:8443/blah/service/SomeService";
 * String expectedUrl = "https://localhost:8443/blah/SomeService/webping";
 * </pre>
 */
public class DeriveUrl implements UrlProvider {
    private String replaceWith;
    private UrlProvider urlProvider;
    private Pattern toMatch;

    /**
     * For Spring
     */
    public DeriveUrl() {
    }

    public DeriveUrl(String toMatch, String replaceWith, UrlProvider urlProvider) {
        this.replaceWith = replaceWith;
        this.urlProvider = urlProvider;
        this.toMatch = Pattern.compile(toMatch);
    }

    private String deriveUrlFor(String url) {
        Matcher matcher = toMatch.matcher(url);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Service url [" + url + "] does not match [" + toMatch + "]");
        }
        return matcher.replaceAll(replaceWith);
    }

    public String get() throws Exception {
        return deriveUrlFor(urlProvider.get());
    }

    /**
     * What to match in the url
     */
    public void setToMatch(Pattern toMatch) {
        this.toMatch = toMatch;
    }

    /**
     * What to replace the original url with
     */
    public void setReplaceWith(String replaceWith) {
        this.replaceWith = replaceWith;
    }

    /**
     * Where to get the original url from  
     */
    public void setUrlProvider(UrlProvider urlProvider) {
        this.urlProvider = urlProvider;
    }

    @Override
    public String toString() {
        return "DeriveUrl[urlProvider="+urlProvider+"]";
    }
}
