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

/**
 * Check that the response from the url contains the configured string. This class uses {@link String#contains(CharSequence)}
 * Note that this implementation will consume <b>the entire response</b>. If this is an issue due to reponse size for
 * example, a different implementation will be needed.
 *
 *
 * <p/>
 * Example:
 * <ul>
 * <li> "Server OK" will mean that the server is ok if it returns "Server OK" or "Server OKI". 
 * </ul>
 */
public class CheckResponseContains implements Checker {
    private String expectedString;

    public CheckResponseContains(String expectedString) {
        this.expectedString = expectedString;
    }

    public void check(URLConnection urlConnection) throws Exception {
        InputStream inputStream = urlConnection.getInputStream();
        String response = IOUtils.toString(inputStream);
        if (!response.contains(expectedString)) {
            throw new RuntimeException(String.format("Service reports [%s], which NOT contains [%s]", response, expectedString));
        }
    }

    @Override
    public String toString() {
        return "CheckResponseContains(\""+expectedString+"\")";
    }

}