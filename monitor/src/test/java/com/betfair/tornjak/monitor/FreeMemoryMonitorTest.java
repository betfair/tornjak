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

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Test the free memory monitor.  Previously this test was implemented in such a way that it actually manipulated
 * the free memory available, but this was found to cause issues on the build server with test stability, which are
 * hard to debug because they did not happen on developer boxes.  So we decided to convert to returning fixed values
 * from getFreeMemoryPercentage().
 */
public class FreeMemoryMonitorTest {

    // The min percentage of memory that should be free before the monitor starts to FAIL
	private static int MIN_FREE_MEMORY_PERCENT = 80;

    @Test
	public void testOk() {
		Monitor monitor = createMonitor(MIN_FREE_MEMORY_PERCENT + 1);
		Assert.assertEquals(Status.OK, monitor.getStatus());
	}

    @Test
    public void testFail() throws Exception {
        FreeMemoryMonitor monitor = createMonitor(MIN_FREE_MEMORY_PERCENT - 1);
        assertEquals(Status.FAIL, monitor.checkStatus());
	}

    @Test
	public void testDefaultMaxImpactToOverallStatus() {
		Monitor monitor = createMonitor(100);
		assertEquals(Status.FAIL, monitor.getMaxImpactToOverallStatus());
	}

	private FreeMemoryMonitor createMonitor(final int valueToReturn) {
		FreeMemoryMonitor monitor = new FreeMemoryMonitor(MIN_FREE_MEMORY_PERCENT) {
            public int getFreeMemoryPercentage() {
                return valueToReturn;
            }
        };
		return monitor;
	}
}
