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

package com.betfair.tornjak.monitor.overlay;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestDiagnosticUtils {

    @Test
    public void timeSince() {
        Assert.assertEquals("Less than 1 second ago", DiagnosticUtils.timeSince(System.currentTimeMillis() - 500L));
        assertEquals("1 second ago", DiagnosticUtils.timeSince(System.currentTimeMillis()-1000L));
        assertEquals("2 seconds ago", DiagnosticUtils.timeSince(System.currentTimeMillis()-2000L));
        assertEquals("1 minute ago", DiagnosticUtils.timeSince(System.currentTimeMillis()-60000L));
        assertEquals("2 minutes ago", DiagnosticUtils.timeSince(System.currentTimeMillis()-120000L));
        assertEquals("1 minute, 1 second ago", DiagnosticUtils.timeSince(System.currentTimeMillis()-61000L));
        assertEquals("1 hour ago", DiagnosticUtils.timeSince(System.currentTimeMillis()-3600000L));
        assertEquals("2 hours ago", DiagnosticUtils.timeSince(System.currentTimeMillis()-7200000L));
        assertEquals("1 hour, 1 minute ago", DiagnosticUtils.timeSince(System.currentTimeMillis()-3660000L));
        assertEquals("1 hour, 1 minute, 1 second ago", DiagnosticUtils.timeSince(System.currentTimeMillis()-3661000L));
        assertEquals("1 day ago", DiagnosticUtils.timeSince(System.currentTimeMillis()-86400000L));
        assertEquals("1 day, 1 hour ago", DiagnosticUtils.timeSince(System.currentTimeMillis()-90000000L));
        assertEquals("1 day, 1 hour, 1 minute ago", DiagnosticUtils.timeSince(System.currentTimeMillis()-90060000L));
        assertEquals("1 day, 1 hour, 1 minute, 1 second ago", DiagnosticUtils.timeSince(System.currentTimeMillis()-90061000L));
        assertEquals("2 days ago", DiagnosticUtils.timeSince(System.currentTimeMillis()-172800000L));
    }

    @Test
    public void colourForStatus() {
        assertEquals("#00FF00", DiagnosticUtils.colourForStatus("OK").toUpperCase());
        assertEquals("#FF8040", DiagnosticUtils.colourForStatus("WARN").toUpperCase());
        assertEquals("#FF0000", DiagnosticUtils.colourForStatus("FAIL").toUpperCase());
        assertEquals("#888888", DiagnosticUtils.colourForStatus("Random").toUpperCase());
    }
}
