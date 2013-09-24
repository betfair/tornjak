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

package com.betfair.tornjak.monitor.active;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.betfair.tornjak.monitor.ActiveMethodMonitor;
import com.betfair.tornjak.monitor.DefaultMonitorRegistry;
import com.betfair.tornjak.monitor.MonitorRegistry;
import com.betfair.tornjak.monitor.Status;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for clean shutdown, see also FRA-1303.
 */
public class ActiveMonitorServiceShutdownTest {

    ActiveMonitorService service;
    List<String> initialThreadNames;

    private final Logger log = LoggerFactory.getLogger(getClass());

    private List<Thread> getCurrentThreads() {
        List<Thread> ret = new ArrayList<Thread>();
        Thread[] threads = new Thread[Thread.activeCount() + 1];
        int returnedThreads;
        while ((returnedThreads = Thread.currentThread().getThreadGroup().enumerate(threads)) >= threads.length) {
            threads = new Thread[returnedThreads + 1];
        }
        for (int i=0; i<returnedThreads; i++) {
            ret.add(threads[i]);
        }
        return ret;
    }

    private List<String> getCurrentThreadNames() {
        List<String> ret = new ArrayList<String>();
        List<Thread> threads = getCurrentThreads();
        for (Thread t : threads) {
            ret.add(t.getName());
        }
        return ret;
    }

    @Before
    public void before() throws InterruptedException {
        initialThreadNames = getCurrentThreadNames();
        service = new ActiveMonitorService();
        service.setCheckIntervalInMillis(500);

        MonitorRegistry r = new DefaultMonitorRegistry();
        for (int i=0; i<10; i++) {
            final int count = i;
            ActiveMethodMonitor m = mock(ActiveMethodMonitor.class);
            when(m.getActiveMonitor()).thenReturn(new Check() {
                @Override
                public void check() throws Exception {
                    log.info("Check Monitor-"+count);
                }
                @Override
                public String getDescription() {
                    return "Some description";
                }
            });
            when(m.getStatus()).thenAnswer(new Answer<Status>() {
                public Status answer(InvocationOnMock invocationOnMock) throws Throwable {
                    return getFailStatus(count);
                }
            });
            when(m.getName()).thenReturn("Monitor-"+count);
            r.addMonitor(m);
        }
        service.setMonitorRegistry(r);

        service.start();
        // wait to ensure at least one check will have occurred
        Thread.sleep(1000);
    }

    private Status getFailStatus(int n) {
        log.info("Returning fail status for Monitor-"+n);
        return Status.FAIL;
    }

    @Test
    public void noOutstandingThreadsAfterShutdown() throws InterruptedException {
        service.stop();
        // allows for checks to complete
        Thread.sleep(10);
        List<String> currentNames = getCurrentThreadNames();
        if (initialThreadNames.size() < currentNames.size()) {
            currentNames.removeAll(initialThreadNames);
            StringBuilder buffer = new StringBuilder();
            for (String s : currentNames) {
                buffer.append("\n").append(s);
            }
            fail("Created threads not destroyed, names are:"+buffer);
        }
    }
}
