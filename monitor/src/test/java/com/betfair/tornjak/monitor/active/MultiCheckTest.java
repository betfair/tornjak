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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;

import org.mockito.InOrder;

/**
 * 
 */
public class MultiCheckTest {

    @Test(expected = IllegalArgumentException.class)
    public void testRejectsNullConstructor() {
        new MultiCheck(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRejectsEmptyConstructor() {
        new MultiCheck(new ArrayList<Check>());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRejectsNullsConstructor() {
        new MultiCheck(Arrays.asList(new Check[] { null }));
    }
    
    /**
     * Ensure a single check works
     */
    @Test
    public void testSingleCheck() throws Exception {
        final Check c = mock(Check.class);

        MultiCheck multi = new MultiCheck(Arrays.asList(c));
        multi.check();

        verify(c, times(1)).check();
    }
    
    /**
     * Ensure each check is called in order
     */
    @Test
    public void testMultiCheck() throws Exception {
        final Check c1 = mock(Check.class);
        final Check c2 = mock(Check.class);

        MultiCheck multi = new MultiCheck(Arrays.asList(c1, c2));
        multi.check();

        InOrder order = inOrder(c1, c2);
        order.verify(c1).check();
        order.verify(c2).check();
    }
    
    /**
     * Ensure exceptions are not eaten up
     */
    @Test(expected = MultiCheckTestException.class)
    public void testMultiCheckThrowsException() throws Exception {
        
        final Check c1 = mock(Check.class);
        final Check c2 = mock(Check.class);

        doThrow(new MultiCheckTestException()).when(c1).check();

        MultiCheck multi = new MultiCheck(Arrays.asList(c1, c2));
        multi.check();
    }
  
    
    /**
     * Ensure defensive copy is made
     */
    @Test
    public void testDefensiveCopy() throws Exception {
        
        final Check c1 = mock(Check.class);
        final Check c2 = mock(Check.class);

        Collection<Check> checks =  new ArrayList<Check>();
        checks.add(c1);
        checks.add(c2);
        
        MultiCheck multi = new MultiCheck(checks);
        //defensive copy should ensure the next call doesn't affect the checks
        checks.clear();        
        multi.check();

        InOrder order = inOrder(c1, c2);
        order.verify(c1).check();
        order.verify(c2).check();
    }
    
    /**
     * Ensure useful descriptions are returned which includes each check
     */
    @Test()
    public void testMultiDescription() throws Exception {
        
        final Check c1 = mock(Check.class);
        final Check c2 = mock(Check.class);

        when(c1.getDescription()).thenReturn("myCheckOne");
        when(c2.getDescription()).thenReturn("myCheckTwo");

        MultiCheck multi = new MultiCheck(Arrays.asList(c1, c2 ));

        assertEquals("MultiCheck:check[0](myCheckOne);check[1](myCheckTwo);", multi.getDescription());
    }
    
    private static class MultiCheckTestException extends RuntimeException {
        private static final long serialVersionUID = 1L;        
    }
}
