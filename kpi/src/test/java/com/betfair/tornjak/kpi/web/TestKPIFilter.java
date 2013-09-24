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

package com.betfair.tornjak.kpi.web;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.junit.Test;

import com.betfair.tornjak.kpi.KPIMonitor;
import com.mockrunner.mock.web.MockFilterChain;
import com.mockrunner.mock.web.MockFilterConfig;
import com.mockrunner.mock.web.MockHttpServletRequest;
import com.mockrunner.mock.web.MockHttpServletResponse;
import org.mockito.ArgumentMatcher;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Test working of {@link com.betfair.tornjak.kpi.web.KPIFilter}
 */
public class TestKPIFilter {

    private static final String CTX = "/mycontext";
    
    private static final String NAME1 = "kpi1";
    private static final String NAME2 = "kpi2";

    private static final String URL1 = "/blah/blah/1";
    private static final String URL2 = "/blah/blah/2";



    @Test
    public void testMatch() throws ServletException, IOException {

        //-- prep
        final KPIMonitor monitor = mock(KPIMonitor.class);

        // config
        FilterConfig filterConfig = initFilterConfig();

        // request
        ServletRequest request = new MockHttpServletRequest() {{ 
            setRequestURI(CTX + URL1);
            setContextPath(CTX);
        }};

        // chain
        FilterChain chain = new MockFilterChain() {

            @Override
            public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
                try {
                    Thread.sleep(50);   // just long enough for timer to tick over
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            } 
        };


        //-- go        
        KPIFilter filter = new KPIFilter();
        filter.setMonitor(monitor);
        filter.init(filterConfig);
        filter.doFilter(request, new MockHttpServletResponse(), chain);

        verify(monitor, times(1)).addEvent(eq(NAME1),doubleThat(Matchers.greaterThanOrEqualTo(0.0)));
    }


    /**
     * Test that an unmapped URL results in no calls to kpiMonitor, but that filter chain is in fact called
     */
    @Test
    public void testNoMatch() throws IOException, ServletException {

        //-- mock objs
        
        // monitor
        final KPIMonitor monitor = mock(KPIMonitor.class);

        // request
        final ServletRequest request = new MockHttpServletRequest() {{ 
            setRequestURI(CTX + "a dummy url");
            setContextPath(CTX);
        }};
        
        // response
        final HttpServletResponse response = new MockHttpServletResponse();

        // chain - which should be called
        final FilterChain chain = mock(FilterChain.class);

        //-- go        
        KPIFilter filter = new KPIFilter();
        filter.setMonitor(monitor);
        try {
            filter.init(initFilterConfig());            
            filter.doFilter(request, response, chain);

        } catch (Exception e) {
            throw new RuntimeException("Exception from app:" + e.getMessage(), e);
        }

        verify(monitor, never()).addEvent(eq(NAME1),anyLong());
        verify(chain, times(1)).doFilter(request,response);
    }

    
    /**
     * Ensure that the client ignores init-param 'targetFilterLifeCycle'.
     */
    @Test
    public void testSpringParamIgnored() throws IOException, ServletException {
        
        final String springParam = "targetFilterLifecycle";
        
        KPIFilter filter = new KPIFilter();
        FilterConfig filterConfig = new MockFilterConfig() {{ 
            setInitParameter(springParam, "true");
        }};
        
        final HttpServletRequest req = new MockHttpServletRequest() {{ 
            setContextPath(CTX);
            setRequestURI(CTX + springParam);
        }};
        
        final HttpServletResponse response = new MockHttpServletResponse();
        
        final FilterChain chain = mock(FilterChain.class);
        
        try {
            filter.init(filterConfig);            
            filter.doFilter(req, response, chain);

        } catch (Exception e) {
            throw new RuntimeException("Exception from app:" + e.getMessage(), e);
        }

        verify(chain, times(1)).doFilter(req,response);
    }
    

    private FilterConfig initFilterConfig() {

        return new MockFilterConfig() {{
            setInitParameter(URL1, NAME1);
            setInitParameter(URL2, NAME2);
        }};
    }


    /**
     * Util to return matcher which expects a double greater than zero (ensure counter value being set)
     */
    private Matcher<Double> doubleGreaterThanZero() {
        return new ArgumentMatcher<Double>() {
            public boolean matches(Object o) {
                if (o==null) {
                    return false;
                }
                Double d = (Double) o;
                return d>0.0;
            }
        };
    }
}
