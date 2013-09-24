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

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.betfair.tornjak.monitor.Status;
import static com.betfair.tornjak.monitor.Status.*;
import com.betfair.tornjak.monitor.StatusAggregator;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.AbstractController;

import com.betfair.tornjak.monitor.MonitorRegistry;

public class PingController extends AbstractController {
    
    static String webping = "<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">\n" +
    "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
    "\t<head><title>Web Ping</title></head>\n" +
    "\t<body>\n" +
    "\t\t<pre>Server ${status}</pre>\n" +
    "\t</body>\n" +
    "</html>";

    
    private MonitorRegistry monitorRegistry;
    
    public void setMonitorRegistry(MonitorRegistry monitorRegistry) {
        this.monitorRegistry = monitorRegistry;        
    }

    public ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        
        return new ModelAndView(new View() {
            public String getContentType() {
                return "text/html";
            }

            public void render(Map model, HttpServletRequest request,
                               HttpServletResponse response) throws Exception {
                StatusAggregator sa = monitorRegistry.getStatusAggregator();
                Status status = (sa == null) ? FAIL : sa.getStatus();
                // webping only supports OK or FAIL response
                if (status == WARN) {
                    status = OK;
                }
                response.getWriter().print(webping.replace("${status}", status.name()));
            }
        }, "status", "");
    }

}