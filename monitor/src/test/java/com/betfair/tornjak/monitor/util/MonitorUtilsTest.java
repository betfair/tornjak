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

package com.betfair.tornjak.monitor.util;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.*;
import com.betfair.tornjak.monitor.ActiveMethodMonitor;
import com.betfair.tornjak.monitor.active.Check;
import com.betfair.tornjak.monitor.active.url.UrlCheck;
import com.betfair.tornjak.monitor.active.url.UrlProvider;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;



/**
 * @author Rolf Schuster
 */
public class MonitorUtilsTest {

    private static final String URL = "custom_url";


    @Mock
    private ActiveMethodMonitor monitor;
    @Mock
    private UrlCheck check;
    @Mock
    private UrlProvider urlProvider;


    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getUrlFromMonitor_OK() throws Exception {
        when(check.getUrlProvider()).thenReturn(urlProvider);
        when(monitor.getActiveMonitor()).thenReturn(check);
        when(urlProvider.get()).thenReturn(URL);

        String result = MonitorUtils.getUrlFromMonitor(monitor);
        assertEquals(URL, result);
    }

    @Test
    public void getUrlFromMonitor_wrong_class() throws Exception {
        Check check2 = Mockito.mock(Check.class);
        when(check.getUrlProvider()).thenReturn(urlProvider);
        when(monitor.getActiveMonitor()).thenReturn(check2);
        when(urlProvider.get()).thenReturn(URL);

        String result = MonitorUtils.getUrlFromMonitor(monitor);
        assertNull(result);
    }

    @Test
    public void getUrlFromMonitor_exception() throws Exception {
        String msg = "This is expected";
        when(check.getUrlProvider()).thenReturn(urlProvider);
        when(monitor.getActiveMonitor()).thenReturn(check);
        when(urlProvider.get()).thenThrow(new Exception(msg));

        String result = MonitorUtils.getUrlFromMonitor(monitor);
        assertTrue(result.contains(msg));
    }
}
