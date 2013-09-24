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

package com.betfair.tornjak.kpi.repeater.repeater;

import com.betfair.tornjak.kpi.KPIMonitor;
import com.betfair.tornjak.kpi.repeater.KPIRepeater;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Date: 12/06/2013
 * Time: 17:15:23
 */

public class KPIRepeaterTest {
    private final Random random = new Random();

    @Mock
    private KPIMonitor mockMonitor1;
    @Mock
    private KPIMonitor mockMonitor2;

    private KPIRepeater repeater;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        repeater = new KPIRepeater();
        List<KPIMonitor> monitors = new ArrayList<KPIMonitor>();
        monitors.add(mockMonitor1);
        monitors.add(mockMonitor2);
        repeater.setMonitors(monitors);

    }

    @Test
    public void shouldAddEventToAll() {
        repeater.addEvent("event1");

        verify(mockMonitor1).addEvent("event1");
        verify(mockMonitor2).addEvent("event1");
        verifyNoMoreInteractions(mockMonitor1, mockMonitor2);
    }

    @Test
    public void shouldAddEventToAllSuccess() {
        boolean success = random.nextBoolean();
        repeater.addEvent("event1", success);

        verify(mockMonitor1).addEvent("event1", success);
        verify(mockMonitor2).addEvent("event1", success);
        verifyNoMoreInteractions(mockMonitor1, mockMonitor2);
    }

    @Test
    public void shouldAddEventToAllDuration() {
        long duration = random.nextInt(1000);
        repeater.addEvent("event1", duration);

        verify(mockMonitor1).addEvent("event1", duration);
        verify(mockMonitor2).addEvent("event1", duration);
        verifyNoMoreInteractions(mockMonitor1, mockMonitor2);
    }

    @Test
    public void shouldAddEventToAllDurationAndSuccess() {
        long duration = random.nextInt(1000);
        boolean success = random.nextBoolean();
        repeater.addEvent("event1", duration, success);

        verify(mockMonitor1).addEvent("event1", duration, success);
        verify(mockMonitor2).addEvent("event1", duration, success);
        verifyNoMoreInteractions(mockMonitor1, mockMonitor2);
    }


}
