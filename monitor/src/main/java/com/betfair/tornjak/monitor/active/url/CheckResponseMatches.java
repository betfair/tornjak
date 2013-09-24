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

import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.net.URLConnection;
import java.util.regex.Pattern;

/**
 * Check that the response from the url <b>matches</b> a regular expression pattern. I will throw an exception if it doesn't.
 * <p/>
 * Examples:
 * <ul>
 * <li>  "Server OK.*|Server WARN.*" will mean that the server is ok if it returns OK or WARN. 
 * <li>  "((?!Server FAIL).)*" will mean that the server is ok if it doesn't return FAIL.
 * </ul>
 *
 * Note that if the response is html you will probably want the {@link Pattern#DOTALL} modifier
 *
 * Note that this implementation will consume <b>the entire response</b>. If this is an issue due to reponse size for
 * example, a different implementation will be needed.
 */
public class CheckResponseMatches implements Checker {
    private Pattern pattern;


    public CheckResponseMatches(Pattern pattern) {
        this.pattern = pattern;
    }

    public void check(URLConnection urlConnection) throws Exception {
        InputStream inputStream = urlConnection.getInputStream();
        String response = IOUtils.toString(inputStream);
        if (!pattern.matcher(response).matches()) {
            throw new RuntimeException(String.format("Service reports [%s], doesn't match [%s]", response, pattern));
        }
    }

    @Override
    public String toString() {
        return "CheckResponseMatches(\""+pattern+"\")";
    }
    
}
