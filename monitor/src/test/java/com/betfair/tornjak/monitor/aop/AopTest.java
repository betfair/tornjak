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

package com.betfair.tornjak.monitor.aop;

import static org.junit.Assert.*;

import com.betfair.tornjak.monitor.aop.notannotated.NotAnnotatedInteface;
import com.betfair.tornjak.monitor.DefaultMonitor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.beans.factory.annotation.Autowired;

import com.betfair.tornjak.monitor.DefaultMonitorRegistry;

import java.util.ArrayList;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/aop-test.xml" })
public class AopTest {

    @Autowired
    private AopBean aopBean;
    
    @Autowired
    private AopInterface interfaceBasedAop;
    
    @Autowired  
    private NotAnnotatedInteface notAnnotatedBean;

    @Autowired  
    private DefaultMonitorRegistry monitorRegistry;

    @Autowired
    private ArrayList<DefaultMonitor> monitors;

    @Before
    public void populateRegistry() {
        for (DefaultMonitor m : monitors) {
            monitorRegistry.addMonitor(m);
        }
    }

    @Test
    @DirtiesContext
    public void testSimple() throws Exception {

        try {
            aopBean.methodOne();
            fail("Exception should be thrown");
        } catch (RuntimeException e) {
            DefaultMonitor monitor = (DefaultMonitor) monitorRegistry.getMonitor("monitorA");
            assertEquals(1, monitor.getFailureCount());
            assertTrue(monitor.getLastException().contains("AopBean.methodOne"));
        }
    }

    @Test
    @DirtiesContext
    public void testWithErrorCountingPolicy() throws Exception {

        try {
            aopBean.methodTwo();
            fail("Exception should be thrown");
        } catch (NullPointerException e) {
            DefaultMonitor monitor = (DefaultMonitor) monitorRegistry.getMonitor("monitorB");
            assertEquals(0, monitor.getFailureCount());
        }

        try {
            aopBean.methodThree();
            fail("Exception should be thrown");
        } catch (RuntimeException e) {
            DefaultMonitor monitor = (DefaultMonitor) monitorRegistry.getMonitor("monitorB");
            assertEquals(1, monitor.getFailureCount());
        }
    }
    
    @Test
    @DirtiesContext
    public void testBadMonitorName() throws Exception {
        try {
            aopBean.methodFour();
            fail("Exception should be thrown");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().contains("flubber"));
        }
    }
    
    @Test
    @DirtiesContext
    public void testWithInterfaceErrorCountingPolicy() throws Exception {

        aopBean.methodFive();
        DefaultMonitor monitor = (DefaultMonitor) monitorRegistry.getMonitor("monitorC");
        assertEquals(1, monitor.getFailureCount());
        assertTrue(monitor.getLastException().contains("Hi"));
        
        try {
            aopBean.methodSix();
            fail("Exception should be thrown");
        } catch (RuntimeException e) {
            monitor = (DefaultMonitor) monitorRegistry.getMonitor("monitorC");
            assertEquals(2, monitor.getFailureCount());
        }
    }

    @Test
    @DirtiesContext
    public void testWhenImplementsInterface() throws Exception {

        try {
            interfaceBasedAop.methodInTheInterface();
            fail("Exception should be thrown");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("not implemented at all"));
        }
        DefaultMonitor monitor = (DefaultMonitor) monitorRegistry.getMonitor("monitorA");
        assertEquals(1, monitor.getFailureCount());
    }
    
    @Test
    @DirtiesContext
    public void testWhenImplementsInterfaceButDoesNotHaveAnnotations() throws Exception {

        try {
            notAnnotatedBean.methodInTheInterface();
            fail("Exception should be thrown");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("not implemented at all"));
        }
        DefaultMonitor monitor = (DefaultMonitor) monitorRegistry.getMonitor("monitorA");
        assertEquals(1, monitor.getFailureCount());
    }
}
