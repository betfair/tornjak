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

package com.betfair.tornjak.monitor.service;

import com.betfair.tornjak.monitor.Status;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 */
public class InOutServiceMonitorTest {

    @Test
    public void defaultsToOkWhenNoCacheFile() {
        InOutServiceMonitor monitor = new InOutServiceMonitor(null);
        assertEquals(Status.OK, monitor.getStatus());
    }

    @Test
    public void defaultsToOkWhenNonExistentCacheFile() {
        File f = mock(File.class);
        when(f.exists()).thenReturn(false);
        InOutServiceMonitor monitor = new InOutServiceMonitor(f);
        assertEquals(Status.OK, monitor.getStatus());
    }

    @Test
    public void defaultsToOkWhenCantReadCacheFile() {
        File f = mock(File.class);
        when(f.exists()).thenReturn(true);
        InOutServiceMonitor monitor = new InOutServiceMonitor(f) {
            @Override
            protected String readFirstLine(File f) throws IOException {
                throw new IOException("Fred");
            }
        };
        assertEquals(Status.OK, monitor.getStatus());
    }

    @Test
    public void readCacheFile() {
        File f = mock(File.class);
        when(f.exists()).thenReturn(true);
        InOutServiceMonitor monitor = new InOutServiceMonitor(f) {
            @Override
            protected String readFirstLine(File f) throws IOException {
                return "OK";
            }
        };
        assertEquals(Status.OK, monitor.getStatus());

        monitor = new InOutServiceMonitor(f) {
            @Override
            protected String readFirstLine(File f) throws IOException {
                return "FAIL";
            }
        };
        assertEquals(Status.FAIL, monitor.getStatus());
    }

    @Test
    public void readCacheFileGarbled() {
        File f = mock(File.class);
        when(f.exists()).thenReturn(true);
        InOutServiceMonitor monitor = new InOutServiceMonitor(f) {
            @Override
            protected String readFirstLine(File f) throws IOException {
                return "Wibble";
            }
        };
        assertEquals(Status.OK, monitor.getStatus());

    }

    @Test
    public void writeCacheFile() throws IOException {
        File f = mock(File.class);
        InOutServiceMonitor monitor = new InOutServiceMonitor(f) {
            @Override
            protected void writeLine(String s, File f) throws FileNotFoundException {
                // works fine
            }
        };
        monitor.setInService(true);
    }

    @Test(expected = FileNotFoundException.class)
    public void cantWriteCacheFileIOException() throws IOException {
        File f = mock(File.class);
        InOutServiceMonitor monitor = new InOutServiceMonitor(f) {
            @Override
            protected void writeLine(String s, File f) throws FileNotFoundException {
                throw new FileNotFoundException("Wibble");
            }
        };
        monitor.setInService(true);
    }

    @Test(expected = FileNotFoundException.class)
    public void cantWriteCacheFileSetToNull() throws IOException {
        InOutServiceMonitor monitor = new InOutServiceMonitor(null) {
            @Override
            protected void writeLine(String s, File f) throws FileNotFoundException {
                throw new FileNotFoundException("Wibble");
            }
        };
        monitor.setInService(true);
    }
}
