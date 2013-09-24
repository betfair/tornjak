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

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class DeriveUrlTest {

    @Test
    public void globalApi() throws Exception {
        String toMatch = "(.*)/SomeService";
        String replaceWith = "$1/Ping.jsp";
        String originalUrl = "http://localhost/someservice/v3/SomeService";
        String expectedUrl = "http://localhost/someservice/v3/Ping.jsp";

        assertDerivedUrl(toMatch, replaceWith, originalUrl, expectedUrl);
    }

    @Test
    public void soaServices() throws Exception {
        assertDerivedUrl(
                "(.*)/service/.*",
                "$1/webping",
                "https://localhost:8443/someservice/service/SomeService",
                "https://localhost:8443/someservice/webping");
    }

    @Test
    public void replacing2Things() throws Exception {
        assertDerivedUrl(
                "(.*)/service/(.*)",
                "$1/$2/webping",
                "https://localhost:8443/someservice/service/SomeService",
                "https://localhost:8443/someservice/SomeService/webping");
    }

    @Test(expected = IllegalArgumentException.class)
    public void errorIfUrlDoesNotMatch() throws Exception {
        DeriveUrl deriveUrl = new DeriveUrl(
                "(.*)/service/(.*)",
                "$1/$2/webping",
                new FixedUrlProvider("https://localhost:8443/someservice/This doesn't match/SomeService"));
        deriveUrl.get();
    }

    private void assertDerivedUrl(String toMatch, String replaceWith, String originalUrl, String expectedUrl) throws Exception {
        DeriveUrl deriveUrl = new DeriveUrl(toMatch, replaceWith, new FixedUrlProvider(originalUrl));

        String finalUrl = deriveUrl.get();

        assertThat(finalUrl, is(expectedUrl));
    }

}
