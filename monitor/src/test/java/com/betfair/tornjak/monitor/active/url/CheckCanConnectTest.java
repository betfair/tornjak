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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.CountDownLatch;

public class CheckCanConnectTest {
    private int port;
    private Server server = new Server();
    private int urlConnectionTimeout = 0;

    @Before
    public void setUp() throws Exception {
        startServer();
    }

    @After
    public void tearDown() throws Exception {
        server.stop();
    }
    
    @Test
    public void serviceNotAvailable() throws Exception {
        server.stop();

        assertFails();
    }

    @Test
    public void serverReturns404IsOk() throws Exception {
        assertCanConnect();
    }

    @Test
    public void serverReturns500IsOk() throws Exception {
        server.setHandler(new AbstractHandler() {
            public void handle(String target, HttpServletRequest request, HttpServletResponse response, int dispatch) throws IOException, ServletException {
                throw new RuntimeException("If the server is really really broken");
            }
        });
        assertCanConnect();
    }
    
    @Test
    public void serverReturnsFailIsOk() throws Exception {
        server.setHandler(new AbstractHandler() {
            public void handle(String target, HttpServletRequest request, HttpServletResponse response, int dispatch) throws IOException, ServletException {
                response.getOutputStream().write("Server FAIL".getBytes());
            }
        });
        assertCanConnect();
    }

    @Test
    public void readTimeoutIsOk() throws Exception {
        StuckedServlet stuckedServlet = null;
        try {
            stuckedServlet = new StuckedServlet();
            server.setHandler(stuckedServlet);

            urlConnectionTimeout = 200; 
            assertCanConnect();
        } finally {
            if (stuckedServlet != null) {
                stuckedServlet.finish();
            }
        }
    }

    private String serverUrl() {
        return "http://localhost:" + port + "/";
    }

    private void startServer() throws Exception {
        Connector connector=new SocketConnector();
        connector.setPort(0);
        server.setConnectors(new Connector[]{connector});
        server.start();
        port = connector.getLocalPort();
    }

    private void assertCanConnect() throws Exception {
        CheckCanConnect checkCanConnect = new CheckCanConnect();
        URLConnection urlConnection = new URL(serverUrl()).openConnection();
        urlConnection.setReadTimeout(urlConnectionTimeout);
        urlConnection.setConnectTimeout(200);
        checkCanConnect.check(urlConnection);
    }
    
    private void assertFails() throws Exception {
        try {
            assertCanConnect();
            fail("No exception throw");
        } catch (ConnectException e) {
            // expected
        } catch (SocketTimeoutException e) {
            // expected also. Probably something to do with the S.O. socket close time. See [Socket#getSoLinger()]
        }
    }

    private static class StuckedServlet extends AbstractHandler {
        private final CountDownLatch waiting;

        public StuckedServlet() {
            this.waiting = new CountDownLatch(1);
        }

        public void handle(String target, HttpServletRequest request, HttpServletResponse response, int dispatch) throws IOException, ServletException {
            try {
                waiting.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public void finish() {
            waiting.countDown();
        }
    }
}
