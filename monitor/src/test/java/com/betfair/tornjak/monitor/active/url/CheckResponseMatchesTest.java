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

import org.junit.Test;

import java.util.regex.Pattern;

public class CheckResponseMatchesTest extends CheckResponseAbstractTest {

    @Test
    public void matternMatches() throws Exception {
        assertReportsOk("Server OK", "Server OK");
        assertReportsOk("Server OK", "Server OK.*");
        assertReportsOk("Server OK", "Server OK.*");
    }

    @Test
    public void patternOr() throws Exception {
        assertReportsOk("Server OK", "Server OK|Server WARN");
        assertReportsOk("Server WARN", "Server OK|Server WARN");
        assertReportsOk("Server OK :  Tapi-Sso OK; SSOLite OK;", "Server OK.*|Server WARN.*");
        assertReportsError("Server FAIL :  Tapi-Sso OK; SSOLite OK;", "Server OK.*|Server WARN.*");
    }

    @Test
    public void doesNotMatch() throws Exception {
        assertReportsError("Server OK :  Tapi-Sso OK; SSOLite OK;", "Server OK");
        assertReportsError("Server FAIL :  Tapi-Sso FAIL; SSOLite OK;", "Server OK.*");
        assertReportsError("Server WARN :  Tapi-Sso WARN; SSOLite OK;", "Server OK.*");
    }

    @Test
    public void negativeMatching() throws Exception {
        assertReportsOk("Server OK :  Tapi-Sso WARN; SSOLite OK;", "^((?!Server FAIL).)*$");
        assertReportsOk("Server OK :  Tapi-Sso WARN; SSOLite OK;", "(?!Server FAIL).*");
        assertReportsError("Server FAIL :  Tapi-Sso WARN; SSOLite OK;", "(?!Server FAIL).*");
        assertReportsError("Fail in the middle Server FAIL :  Tapi-Sso WARN; SSOLite OK;", "((?!Server FAIL).)*");
    }

    protected Checker createChecker(String pattern) {
        return new CheckResponseMatches(Pattern.compile(pattern));
    }
}
