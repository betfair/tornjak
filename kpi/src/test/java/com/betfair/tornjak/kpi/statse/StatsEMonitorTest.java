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

package com.betfair.tornjak.kpi.statse;

import com.betfair.sre.statse.client.StatsEMsgBuilder;
import com.betfair.sre.statse.client.StatsESender;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * User: mcintyret2
 * Date: 16/08/2013
 */


public class StatsEMonitorTest {

    @Mock
    private StatsESender mockSender;
    @Mock
    private StatsEMsgBuilder mockBuilder;
    @InjectMocks
    private StatsEMonitor statsEMonitor;

    @Before
    public void before() {
        statsEMonitor = new StatsEMonitor();
        initMocks(this);

        when(mockSender.newMessageForMetric(anyString())).thenReturn(mockBuilder);

        when(mockBuilder.error(anyBoolean())).thenReturn(mockBuilder);
        when(mockBuilder.operation(anyString())).thenReturn(mockBuilder);
        when(mockBuilder.time(anyDouble())).thenReturn(mockBuilder);
    }

    @Test
    public void shouldAddEvent() {
        statsEMonitor.addEvent("my.service");

        verify(mockSender).newMessageForMetric("my.service");
        verify(mockBuilder).error(false);
        verify(mockBuilder).send();
        verifyNoMoreInteractions(mockBuilder);
    }

    @Test
    public void shouldAddEvent2() {
        statsEMonitor.addEvent("my.service", false);

        verify(mockSender).newMessageForMetric("my.service");
        verify(mockBuilder).error(true);
        verify(mockBuilder).send();
        verifyNoMoreInteractions(mockBuilder);
    }

    @Test
    public void shouldAddEvent3() {
        statsEMonitor.addEvent("my.service", 12.34D);

        verify(mockSender).newMessageForMetric("my.service");
        verify(mockBuilder).error(false);
        verify(mockBuilder).time(12.34D);
        verify(mockBuilder).send();
        verifyNoMoreInteractions(mockBuilder);
    }

    @Test
    public void shouldAddEvent4() {
        statsEMonitor.addEvent("my.service", 12.34D, false);

        verify(mockSender).newMessageForMetric("my.service");
        verify(mockBuilder).error(true);
        verify(mockBuilder).time(12.34D);
        verify(mockBuilder).send();
        verifyNoMoreInteractions(mockBuilder);
    }

    @Test
    public void shouldAddEvent5() {
        statsEMonitor.addEvent("my.service", "my.op");

        verify(mockSender).newMessageForMetric("my.service");
        verify(mockBuilder).error(false);
        verify(mockBuilder).operation("my.op");
        verify(mockBuilder).send();
        verifyNoMoreInteractions(mockBuilder);
    }

    @Test
    public void shouldAddEvent6() {
        statsEMonitor.addEvent("my.service", "my.op", false);

        verify(mockSender).newMessageForMetric("my.service");
        verify(mockBuilder).error(true);
        verify(mockBuilder).operation("my.op");
        verify(mockBuilder).send();
        verifyNoMoreInteractions(mockBuilder);
    }

    @Test
    public void shouldAddEvent7() {
        statsEMonitor.addEvent("my.service", "my.op", 12.34D);

        verify(mockSender).newMessageForMetric("my.service");
        verify(mockBuilder).error(false);
        verify(mockBuilder).time(12.34D);
        verify(mockBuilder).operation("my.op");
        verify(mockBuilder).send();
        verifyNoMoreInteractions(mockBuilder);
    }

    @Test
    public void shouldAddEvent8() {
        statsEMonitor.addEvent("my.service", "my.op", 12.34D, false);

        verify(mockSender).newMessageForMetric("my.service");
        verify(mockBuilder).error(true);
        verify(mockBuilder).time(12.34D);
        verify(mockBuilder).operation("my.op");
        verify(mockBuilder).send();
        verifyNoMoreInteractions(mockBuilder);
    }

}
