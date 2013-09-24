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

import static junit.framework.Assert.fail;
import static org.hamcrest.Matchers.containsString;

import org.junit.After;
import static org.junit.Assert.assertThat;
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

public class ServiceCommonsWebPingTest {
    private MockArcadeService mockArcadeService;

    @Test
    public void serverOk() throws Exception {
        UrlCheck activeMonitor = ServiceCommonsWebPing.create(new FixedUrlProvider("http://localhost:" + mockArcadeService.port + "/someservice/service/SomeService"));
        activeMonitor.check();
    }
    
    @Test
    public void serverFail() throws Exception {
        mockArcadeService.webpingReturnsFail();
        UrlCheck activeMonitor = ServiceCommonsWebPing.create(new FixedUrlProvider("http://localhost:" + mockArcadeService.port + "/someservice/service/SomeService"));
        try {
            activeMonitor.check();
            fail("Expected error");
        } catch (Exception e) {
            assertThat(e.getMessage(), containsString("which NOT contains"));
        }
    }
    
    @Test
    public void serverNotThere() throws Exception {
        mockArcadeService.stop();
        UrlCheck activeMonitor = ServiceCommonsWebPing.create(new FixedUrlProvider("http://localhost:" + mockArcadeService.port + "/someservice/service/SomeService"));
        try {
            activeMonitor.check();
            fail("Expected error");
        } catch (Exception e) {
            assertThat(e.getMessage(), containsString("Connection refused"));
        }
    }

    @Before
    public void setUp() throws Exception {
        mockArcadeService = new MockArcadeService();
        mockArcadeService.start();
    }

    @After
    public void tearDown() {
        mockArcadeService.stop();
    }

    public static class MockArcadeService {
        private int port = 0;
        private Server server;
        private String webpingResponse;


        MockArcadeService() {
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
            webpingResponse = "Server FAIL";
        }

        public void webpingReturnsOk() {
            webpingResponse = "Server OK";
        }

        public void start() {
            if (server == null) {
                server = new Server(port);
                Context context = new Context(server, "/someservice", Context.SESSIONS);
                context.addServlet(new ServletHolder(new HttpServlet() {
                    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
                        resp.getOutputStream().print("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">\n" +
                                "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
                                "\t<head><title>Web Ping</title></head>\n" +
                                "\t<body>\n" +
                                "\t\t<pre>" + webpingResponse + "</pre>\n" +
                                "\t</body>\n" +
                                "</html>");
                    }
                }), "/webping");
            }
            try {
                server.start();
                port = server.getConnectors()[0].getLocalPort();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

    }

}

