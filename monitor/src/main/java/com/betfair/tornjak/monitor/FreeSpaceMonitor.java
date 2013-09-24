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

package com.betfair.tornjak.monitor;

import java.io.File;

/**
 * Given a Directory name and number of bytes, confirm that the given
 * number of bytes is available for use on in the directory.
 * <p/>
 * FAIL if the number of bytes is not available
 * WARN if a security exception prevents it form being checked.
 * <p/>
 * NOTE that the default behaviour of directory.getUsableSpace() is to return 0L
 * in the event that the number of available bytes cannot be established for any other reason.
 * Also, "This method makes no guarantee that write operations to this file system will succeed. "
 */
public class FreeSpaceMonitor extends OnDemandMonitor {

    private final File directory;
    private final long minBytesRequired;

    public FreeSpaceMonitor(String dirName, long minBytesRequired) {
        this(new File(dirName), minBytesRequired);
    }

    public FreeSpaceMonitor(File dir, long minBytesRequired) {
        this.directory = dir;
        this.minBytesRequired = minBytesRequired;
    }

    @Override
    public String getName() {
        return "Free disk space monitor";
    }

    @Override
    protected Status checkStatus() throws Exception {
        Status s = Status.FAIL;
        if (directory.getUsableSpace() >= minBytesRequired) {
            s = Status.OK;
        }
        return s;
    }
}