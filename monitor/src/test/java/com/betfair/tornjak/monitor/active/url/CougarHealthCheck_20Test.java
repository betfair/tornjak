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

package com.betfair.tornjak.monitor.active.url;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static junit.framework.Assert.fail;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

public class CougarHealthCheck_20Test {
    
    private MockCougarService mockArcadeService;

    @Test
    public void serverOk() throws Exception {
        mockArcadeService.enableContext(false);
        UrlCheck activeMonitor = CougarHealthCheck_20.create(new FixedUrlProvider("http://localhost:" + mockArcadeService.port + "/cougarBaseline/v2.0/simple"));
        activeMonitor.check();
    }

    @Test
    public void serverOkMajorVersionOnly() throws Exception {
        mockArcadeService.enableContext(false);
        UrlCheck activeMonitor = CougarHealthCheck_20.create(new FixedUrlProvider("http://localhost:" + mockArcadeService.port + "/cougarBaseline/v2/simple"));
        activeMonitor.check();
    }

    @Test
    public void serverOkPrefixed() throws Exception {
        mockArcadeService.enableContext(true);
        UrlCheck activeMonitor = CougarHealthCheck_20.create(new FixedUrlProvider("http://localhost:" + mockArcadeService.port + "/www/cougarBaseline/v2.0/simple"), "/www");
        activeMonitor.check();
    }

    @Test
    public void serverFail() throws Exception {
        mockArcadeService.enableContext(false);
        mockArcadeService.webpingReturnsFail();
        UrlCheck activeMonitor = CougarHealthCheck_20.create(new FixedUrlProvider("http://localhost:" + mockArcadeService.port + "/cougarBaseline/v2.0/simple"));
        try {
            activeMonitor.check();
            fail("Expected error");
        } catch (Exception e) {
            assertThat(e.getMessage(), containsString("which NOT contains"));
        }
    }

    @Test
    public void serverFailPrefixed() throws Exception {
        mockArcadeService.enableContext(true);
        mockArcadeService.webpingReturnsFail();
        UrlCheck activeMonitor = CougarHealthCheck_20.create(new FixedUrlProvider("http://localhost:" + mockArcadeService.port + "/www/cougarBaseline/v2.0/simple"), "/www");
        try {
            activeMonitor.check();
            fail("Expected error");
        } catch (Exception e) {
            assertThat(e.getMessage(), containsString("which NOT contains"));
        }
    }

    @Test
    public void serverNotThere() throws Exception {
        mockArcadeService.enableContext(false);
        mockArcadeService.stop();
        UrlCheck activeMonitor = CougarHealthCheck_20.create(new FixedUrlProvider("http://localhost:" + mockArcadeService.port + "/cougarBaseline/v2.0/simple"));
        try {
            activeMonitor.check();
            fail("Expected error");
        } catch (Exception e) {
            assertThat(e.getMessage(), containsString("Connection refused"));
        }
    }

    @Test
    public void serverNotTherePrefixed() throws Exception {
        mockArcadeService.enableContext(true);
        mockArcadeService.stop();
        UrlCheck activeMonitor = CougarHealthCheck_20.create(new FixedUrlProvider("http://localhost:" + mockArcadeService.port + "/www/cougarBaseline/v2.0/simple"), "/www");
        try {
            activeMonitor.check();
            fail("Expected error");
        } catch (Exception e) {
            assertThat(e.getMessage(), containsString("Connection refused"));
        }
    }

    @Before
    public void setUp() throws Exception {
        mockArcadeService = new MockCougarService();
        mockArcadeService.start();
    }

    @After
    public void tearDown() {
        mockArcadeService.stop();
    }

    public static class MockCougarService {
        private int port = 0;
        private Server server;
        private String webpingResponse;
        private Context normalContext;
        private Context prefixedContext;


        MockCougarService() {
            webpingReturnsOk();
        }

        public void stop() {
            if (server != null) {
                try {
                    server.stop();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    server = null;
                }
            }
        }

        public void webpingReturnsFail() {
            webpingResponse = "FAIL";
        }

        public void webpingReturnsOk() {
            webpingResponse = "OK";
        }

        public void start() {
            if (server == null) {
                server = new Server(port);
                normalContext = new Context(server, "", Context.SESSIONS);
                prefixedContext = new Context(server, "/www", Context.SESSIONS);
                ServletHolder holder = new ServletHolder(new HttpServlet() {
                    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
                        resp.getOutputStream().print("<?xml version='1.0' encoding='UTF-8'?><IsHealthyResponse xmlns=\"http://www.betfair.com/servicetypes/v2/Health/\"><HealthSummaryResponse><healthy>"+webpingResponse+"</healthy></HealthSummaryResponse></IsHealthyResponse>\n");
                    }
                });
                normalContext.addServlet(holder, "/healthcheck/v2/summary");
                prefixedContext.addServlet(holder, "/healthcheck/v2/summary");
            }
            try {
                server.start();
                port = server.getConnectors()[0].getLocalPort();
                normalContext.stop();
                prefixedContext.stop();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public void enableContext(boolean prefixed) {
            try {
                if (prefixed) {
                    prefixedContext.start();
                }
                else {
                    normalContext.start();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

}

