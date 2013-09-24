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

import java.io.IOException;
import java.io.FileOutputStream;
import java.net.URLConnection;
import java.net.URL;

public abstract class CheckResponseAbstractTest {
    private String fileName = getClass().getResource(".").getFile() + "deleteme";

    protected void assertReportsOk(String serverResponse, String pattern) throws Exception {
        write(serverResponse);
        Checker checker = createChecker(pattern);
        checker.check(file());
    }

    protected abstract Checker createChecker(String pattern);

    protected void assertReportsError(String serverResponse, String pattern) throws Exception {
        try {
            assertReportsOk(serverResponse, pattern);
            junit.framework.Assert.fail(String.format("Expected error for response [%s], pattern [%s]", serverResponse, pattern));
        } catch (Exception e) {
            //ok
        }
    }

    private void write(String s) throws IOException {
        IOUtils.write(s, new FileOutputStream(fileName));
    }

    private URLConnection file() throws IOException {
        return new URL("file://localhost/" + fileName).openConnection();
    }
}
