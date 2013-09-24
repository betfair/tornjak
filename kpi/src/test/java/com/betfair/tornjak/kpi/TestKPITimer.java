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

package com.betfair.tornjak.kpi;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

public class TestKPITimer {

    /**
     * Ensure that if we flag as 'done' without starting, we throw an exception
     */
    @Test
    public void testDoneButNotStarted() {
        
        KPITimer timer = new KPITimer();
        
        // done
        try {
            timer.done();
            fail("Should have thrown an exception");
        } catch (IllegalStateException e) {
            assertThat(e.getMessage(), equalTo("Timer flagged as done but was never started."));
        }
    }
    
    
    /**
     * Ensure that we can start and stop, and the counter continues
     */
    @Test
    public void testMultipleStartStops() {
        
        KPITimer timer = new KPITimer();
        
        timer.start();
        doSleep(100);
        double duration1 = timer.stop();
        assertTrue("Duration should be > 0", duration1 > 0);
        
        timer.start();
        doSleep(50);        // a little shorter than first - ensure we're not just comparing 100 and 99, say
        double duration2 = timer.stop();
        assertTrue("Duration2 should be > 0", duration2 > duration1);
    }


    /**
     * Test that a simple start/done sequence works correctly
     */
    @Test
    public void testSimpleDone() {
                
        KPITimer timer = new KPITimer();
        
        timer.start();
        doSleep(100);       // enough for timer to go
        // not calling stop
        double doneDuration = timer.done();
        assertGreaterThanZero(doneDuration);
        
        // want to check that we've reset. Do this by re-starting and stopping and ensuring that our duration
        //  is less than it was before
        timer.start();
        doSleep(10);        // much less than above
        double duration2 = timer.stop();
        assertTrue("Should have reset", doneDuration > duration2);
    }
    
    
    /**
     * Test start/done with an explicit stop
     */
    @Test
    public void testSimpleStopThenDone() {

        KPITimer timer = new KPITimer();
        
        timer.start();
        doSleep(50);       // enough for timer to go
        assertTrue("Duration should be > 0", timer.stop() > 0);
        assertGreaterThanZero(timer.done());
    }
    
    
    /**
     * Test that if we stop without explicitly starting, we get an exception
     */
    @Test
    public void testStopButNotStarted() {
        
        KPITimer timer = new KPITimer();
        
        // stop
        try {
            timer.stop();
            fail("Should have thrown an exception");
        } catch (IllegalStateException e) {
            assertThat(e.getMessage(), equalTo("Timer stopped but wasn't active."));
        }
    }
    

    private void assertGreaterThanZero(double l) {
        assertTrue("Should be greater than zero", l > 0);
    }
    
    
    /**
     * Util to hide interrruption gunk
     */
    private void doSleep(long duration) {
        
        try {
            Thread.sleep(duration);            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
