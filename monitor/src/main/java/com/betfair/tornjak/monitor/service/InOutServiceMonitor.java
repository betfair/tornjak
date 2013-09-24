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

import com.betfair.tornjak.monitor.OnDemandMonitor;
import com.betfair.tornjak.monitor.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

@ManagedResource
public class InOutServiceMonitor extends OnDemandMonitor {

    private static final Logger logger = LoggerFactory.getLogger(InOutServiceMonitor.class);
    private File cachedStatusFile;
    private volatile Status status;
    private String name = "InOutServiceMonitor";

    public InOutServiceMonitor(File cachedStatusFile) {
        this.cachedStatusFile = cachedStatusFile;
    }

    public InOutServiceMonitor(String name, File cachedStatusFile) {
        this.name = name;
        this.cachedStatusFile = cachedStatusFile;
    }

    // seperated out so can be overidden for testing..
    protected String readFirstLine(File f) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(cachedStatusFile));
        String line = br.readLine().trim();
        br.close();
        return line;
    }

    @Override
    protected Status checkStatus() throws Exception {
        if (status == null) {
            status = Status.OK;
            if (cachedStatusFile != null && cachedStatusFile.exists()) {
                try {
                    String line = readFirstLine(cachedStatusFile);
                    status = Status.valueOf(line);
                }
                catch (Exception e) {
                    // write out since it will only happen once, since now our status is OK..
                    logger.warn("Can't load cached status from disk, defaulting to OK", e);
                }
            }
        }
        return status;
    }

    @Override
    public String getName() {
        return name;
    }

    // seperated out so can be overidden for testing..
    protected void writeLine(String s, File f) throws FileNotFoundException {
        PrintWriter pw = new PrintWriter(f);
        pw.println(s);
        pw.close();
    }

    @ManagedAttribute
    public void setInService(boolean inService) throws IOException {
        try {
            Status newStatus = inService ? Status.OK : Status.FAIL;
            writeLine(newStatus.name(), cachedStatusFile);
            status = newStatus;
        }
        catch (IOException e) {
            logger.error("Can't write cached status to disk", e);
            throw e;
        }
    }

    @ManagedAttribute
    public boolean isInService() {
        return status == Status.OK;
    }
}
