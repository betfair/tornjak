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

package com.betfair.tornjak.kpi.aop;

import com.betfair.tornjak.kpi.KPIMonitor;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class TestKPIMeasuringAspect {

    private static final String BEAN_ADVICE = "aspect";
    private static final String BEAN_TARGET = "target";

    /**
     * Not pretty as a static, but reloading context for each test is just slow and unnecessary in this case.
     */
    private static ApplicationContext context;

    /**
     * Default KPI name for testing
     */
    private static final String KPI_NAME = "kpiname";

    /**
     * Dummy test return value. Int to make sure AOP doesn't break primitives
     */
    private static final int RETVAL = 999;

    private static final String SPRING_CONFIG_FILE = "test-kpi-annotations.xml";

    /**
     * Dummy parameter to test methods to make sure that AOP doesn't lose parms
     */
    private static final String TEST_PARM = "test parameter";


    private KPIMeasuringAspect advice;
    private KPIMonitor monitor;


    TestKPIMeasuringAspect target;


    /**
     * Init spring context - do as static to avoid reloading overhead (quite slow)
     */
    @BeforeClass
    public static void beforeClass() {
        context = new ClassPathXmlApplicationContext(new String[] { SPRING_CONFIG_FILE, "kpi-beans.xml" });
    }


    @Before
    public void before() {

        monitor = mock(KPIMonitor.class);

        advice = (KPIMeasuringAspect) context.getBean(BEAN_ADVICE);
        advice.setMonitor(monitor);

        target = (TestKPIMeasuringAspect) context.getBean(BEAN_TARGET);
    }


    @KPITimedEvent(KPI_NAME)
    public int dummyErrorThrowingSleepingMethod1(String parm) {
        checkParm(parm);
        sleepBriefly();
        throw new SpecialTestException("any old exception will do");
    }


    @KPITimedEvent(value = KPI_NAME, catchFailures = true)
    public int dummyErrorThrowingSleepingMethod2(String parm) {
        checkParm(parm);
        sleepBriefly();
        throw new SpecialTestException("any old exception will do");
    }


    @KPIEvent(KPI_NAME)
    public int dummyErrorThrowingMethod3(String parm) {
        checkParm(parm);
        throw new SpecialTestException("any old exception will do");
    }


    @KPIEvent(value = KPI_NAME, catchFailures = true)
    public int dummyErrorThrowingMethod4(String parm) {
        checkParm(parm);
        throw new SpecialTestException("any old exception will do");
    }

    @KPITimedEvent
    public int dummySuccessfulMethod(String parm) {
        checkParm(parm);
        return RETVAL;
    }

    @KPIEvent
    public int dummySuccessfulMethod1(String parm) {
        checkParm(parm);
        return RETVAL;
    }


    @KPITimedEvent(KPI_NAME)
    public int dummySuccessfulSleepingMethod1(String parm) {
        checkParm(parm);
        sleepBriefly();
        return RETVAL;
    }


    @KPITimedEvent(value = KPI_NAME, catchFailures = true)
    public int dummySuccessfulSleepingMethod2(String parm) {
        checkParm(parm);
        sleepBriefly();
        return RETVAL;
    }


    @KPIEvent(KPI_NAME)
    public int dummySuccessfulMethod3(String parm) {
        checkParm(parm);
        return RETVAL;
    }


    @KPIEvent(value = KPI_NAME, catchFailures = true)
    public int dummySuccessfulMethod4(String parm) {
        checkParm(parm);
        return RETVAL;
    }


    @Test(expected = SpecialTestException.class)
    public void testEventCheckedFails() {

        target.dummyErrorThrowingMethod4(TEST_PARM);

        verify(monitor, times(1)).addEvent(eq(KPI_NAME), eq("dummyErrorThrowingMethod4"), eq(false));
    }


    @Test
    public void testEventCheckedSucceeds() {

        target.dummySuccessfulMethod4(TEST_PARM);

        verify(monitor, times(1)).addEvent(eq(KPI_NAME), eq("dummySuccessfulMethod4"), eq(true));
    }

    @Test
    public void testEventHasCorrectName() {
        target.dummySuccessfulMethod1(TEST_PARM);

        verify(monitor, times(1)).addEvent(eq(TestKPIMeasuringAspect.class.getSimpleName()), eq("dummySuccessfulMethod1"));
    }

    @Test
    public void testTimedEventHasCorrectName() {
        target.dummySuccessfulMethod(TEST_PARM);
        verify(monitor, times(1)).addEvent(eq(TestKPIMeasuringAspect.class.getSimpleName()), eq("dummySuccessfulMethod"), anyLong());
    }


    @Test(expected = SpecialTestException.class)
    public void testEventUncheckedFails() {
        target.dummyErrorThrowingMethod3(TEST_PARM);
        verify(monitor, times(1)).addEvent(eq(KPI_NAME), eq("dummyErrorThrowingMethod3"));
    }


    @Test
    public void testEventUncheckedSucceeds() {
        target.dummySuccessfulMethod3(TEST_PARM);
        verify(monitor, times(1)).addEvent(eq(KPI_NAME), eq("dummySuccessfulMethod3"));
    }


    /**
     * Timed event, with checking enabled, should be counted if method call fails
     */
    @Test(expected = SpecialTestException.class)
    public void testTimedEventCheckedFails() {
        target.dummyErrorThrowingSleepingMethod2(TEST_PARM);
        verify(monitor, times(1)).addEvent(eq(KPI_NAME), eq("dummyErrorThrowingSleepingMethod2"), doubleThat(Matchers.greaterThan(0.0)), eq(false));
    }


    /**
     * Timed event, with checking enabled
     */
    @Test
    public void testTimedEventCheckedSucceeds() {
        target.dummySuccessfulSleepingMethod2(TEST_PARM);
        verify(monitor, times(1)).addEvent(eq(KPI_NAME), eq("dummySuccessfulSleepingMethod2"), doubleThat(Matchers.greaterThan(0.0)), eq(true));
    }


    /**
     * Ensure that even if not checking failures, a thrown error on a monitored timed event gets counted.
     */
    @Test(expected = SpecialTestException.class)
    public void testTimedEventUncheckedFails() {
        target.dummyErrorThrowingSleepingMethod1(TEST_PARM);
        verify(monitor, times(1)).addEvent(eq(KPI_NAME), eq("dummyErrorThrowingSleepingMethod1"), doubleThat(Matchers.greaterThan(0.0)));
    }


    /**
     * Test annotated timed event (without failure checking) gets counted
     */
    @Test
    public void testTimedEventUncheckedSucceeds() {
        assertEquals(RETVAL, target.dummySuccessfulSleepingMethod1(TEST_PARM));
        verify(monitor, times(1)).addEvent(eq(KPI_NAME), eq("dummySuccessfulSleepingMethod1"), doubleThat(Matchers.greaterThan(0.0)));
    }

    /**
     * Check that given parameter matches expected (used by 'dummy' KPI-d methods)
     */
    private void checkParm(String parm) {
        assertEquals("parm not passed down", TEST_PARM, parm);
    }


    /**
     * Not nice lagging unit tests, but I want a non-zero duration for called methods, so we can ensure that we're
     * actually measuring time when methods get called.
     */
    private void sleepBriefly() {

        try {
            Thread.sleep(50);      // should beat timer granularity
        } catch (InterruptedException e) {
            // no reason we should be interrupted, propagate
            Thread.currentThread().interrupt();
        }
    }


    /**
     * Our own exception, to be clearly separate from any other problems
     */
    private static class SpecialTestException extends RuntimeException {
        public SpecialTestException(String msg) {
            super(msg);
        }

    }

}
