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
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.betfair.tornjak.kpi.KPIMonitor;

/**
 * KPI filter which provides KPI stats for called methods.
 * <p>
 * This version requires a monitor to be injected - if you don't want to use with Spring, then a subclass of this
 * filter could be used to (eg) retrieve monitor from the context.
 * <p>
 * Configuration:<ul>
 * <li>inject a KPIMonitor</li>
 * <li>provide filter init-params, where param-name is the URL (mapping) and the param-value is the actual KPI name.
 * </ul>
 * <p>
 * <em>NOTE:</em> Games version of this filter also implemented regex checking. This version does not, since it wasn't
 * used in Games, and might not be used here. A more advanced version of this filter could be built if regex version
 * was ever needed.
 */
public class KPIFilter implements Filter {

    /**
     * this init-param can be set to tell Spring to invoke the filter's init() method (we want this for config).
     * We don't want it in the URI list, though!
     */
    private static final String SPRING_LIFECYCLE_PARAM = "targetFilterLifecycle";
    
    private KPIMonitor kpiMonitor;
    private Map<String, String> uriMap = new HashMap<String, String>();

    
    public void destroy() {
        // do nothing
    }

    
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) 
    throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
                
        String uri = deriveContextLessURI(request);
        String value = uriMap.get(uri);
        if (value != null) {
            doTimedFilter(servletRequest, servletResponse, filterChain, value);
        }
        else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }


    @SuppressWarnings("unchecked")
    public void init(FilterConfig filterConfig) throws ServletException {

        Enumeration<String> e = filterConfig.getInitParameterNames();
        while (e.hasMoreElements()) {
            String uri = e.nextElement();
            if (!uri.equals(SPRING_LIFECYCLE_PARAM)) {
                String name = filterConfig.getInitParameter(uri);
                uriMap.put(uri, name);                
            }
        }
    }


    /**
     * DI
     */
    public void setMonitor(KPIMonitor monitor) {
        this.kpiMonitor = monitor;
    }
    
    
    /**
     * Derive URI string minus the context bit in front
     */
    private String deriveContextLessURI(HttpServletRequest req) {
        
        int contextLength = req.getContextPath().length();
        String uri = req.getRequestURI();
        return uri.substring(contextLength, uri.length());
    }

    
    private void doTimedFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain,
            String value) 
    throws IOException, ServletException {

        long startTime = System.nanoTime();
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        }
        finally {
            long totalTime = System.nanoTime() - startTime;
            kpiMonitor.addEvent(value, (long) (totalTime / 1000000.0)); // convert to millis
        }
    }
}
