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

package com.betfair.tornjak.monitor.web;

import com.betfair.tornjak.monitor.DefaultMonitorRegistry;
import com.betfair.tornjak.monitor.StatusAggregator;
import static com.betfair.tornjak.monitor.Status.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import javax.servlet.http.HttpServletResponse;

import java.io.PrintWriter;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PingControllerTest {

    private PingController controller;
    private StatusAggregator statusAggregator;

    @Before
    public void before() {
        controller = new PingController();
        DefaultMonitorRegistry registry = new DefaultMonitorRegistry();
        statusAggregator = mock(StatusAggregator.class);
        registry.setStatusAggregator(statusAggregator);
        controller.setMonitorRegistry(registry);
    }

    @Test
    public void returnsOkWhenOk() throws Exception {
        when(statusAggregator.getStatus()).thenReturn(OK);

        ArgumentCaptor<String> outputCaptor = ArgumentCaptor.forClass(String.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        PrintWriter writer = mock(PrintWriter.class);
        when(response.getWriter()).thenReturn(writer);
        // response.getWriter().print
        controller.handleRequestInternal(null, null).getView().render(null, null, response);

        verify(writer).print(outputCaptor.capture());

        assertTrue(outputCaptor.getValue()+"+should contain 'Server OK'", outputCaptor.getValue().contains("Server OK"));
    }

    @Test
    public void returnsOkWhenWarn() throws Exception {
        when(statusAggregator.getStatus()).thenReturn(WARN);

        ArgumentCaptor<String> outputCaptor = ArgumentCaptor.forClass(String.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        PrintWriter writer = mock(PrintWriter.class);
        when(response.getWriter()).thenReturn(writer);
        // response.getWriter().print
        controller.handleRequestInternal(null, null).getView().render(null, null, response);

        verify(writer).print(outputCaptor.capture());

        assertTrue(outputCaptor.getValue()+"+should contain 'Server OK'", outputCaptor.getValue().contains("Server OK"));
    }

    @Test
    public void returnsFailWhenFail() throws Exception {
        when(statusAggregator.getStatus()).thenReturn(FAIL);

        ArgumentCaptor<String> outputCaptor = ArgumentCaptor.forClass(String.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        PrintWriter writer = mock(PrintWriter.class);
        when(response.getWriter()).thenReturn(writer);
        // response.getWriter().print
        controller.handleRequestInternal(null, null).getView().render(null, null, response);

        verify(writer).print(outputCaptor.capture());

        assertTrue(outputCaptor.getValue()+"+should contain 'Server FAIL'", outputCaptor.getValue().contains("Server FAIL"));
    }
}
