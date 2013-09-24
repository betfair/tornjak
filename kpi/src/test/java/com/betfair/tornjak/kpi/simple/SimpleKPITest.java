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

package com.betfair.tornjak.kpi.simple;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
/**
 * Date: 14/06/2013
 * Time: 10:50:14
 */
public class SimpleKPITest {

    private SimpleKPI kpi;

    @Before
    public void setUp() throws Exception {
        kpi = new SimpleKPI("kpi.name");
    }

    @Test
    public void shouldAddCall() {
        kpi.recordCall(10);

        assertEquals(1, kpi.getCalls());
        assertEquals(0, kpi.getFailures());
        assertEquals(10D, kpi.getLatestTimePerCall(), 0.00001);
    }

    @Test
    public void shouldAddFailure() {
        kpi.recordFailure(10);

        assertEquals(1, kpi.getCalls());
        assertEquals(1, kpi.getFailures());
        assertEquals(10D, kpi.getLatestTimePerCall(), 0.00001);
    }

    @Test
    public void shouldAddMoreThan20() {
        for (int i =0; i < 50; i++) {
            kpi.recordCall(i);
        }

        assertEquals(50, kpi.getCalls());
        assertEquals(0, kpi.getFailures());
        assertEquals(39.5D, kpi.getLatestTimePerCall(), 0.00001);
    }



}
