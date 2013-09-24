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

public class CheckResponseNotContainsTest extends CheckResponseAbstractTest {

    @Test
    public void ok() throws Exception {
        assertReportsOk("Server OK", "Server FAIL");
        assertReportsOk("Any other string", "Server FAIL");
    }
    
    @Test
    public void fail() throws Exception {
        assertReportsError("Server FAIL", "Server FAIL");
    }

    protected Checker createChecker(String pattern) {
        return new CheckResponseNotContains(pattern);
    }
}