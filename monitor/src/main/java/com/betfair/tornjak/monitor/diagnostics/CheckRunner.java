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

package com.betfair.tornjak.monitor.diagnostics;

import com.betfair.tornjak.monitor.active.Check;

import java.util.ArrayList;
import java.util.List;

/**
 * Runs a set of checks, and collates the result of each
 */
public class CheckRunner implements DiagnosticsRunner {

    private List<Check> checks;

    public CheckRunner(List<Check> checks) {
        this.checks = checks;
    }

    public List<CheckResult> run() {
        List<CheckResult> ret = new ArrayList<CheckResult>();
        for (Check c : checks) {
            CheckResult cr = new CheckResult();
            cr.setDescription(c.getDescription());
            double start = System.nanoTime();
            try {
                c.check();
                cr.setSuccessful(true);
            } catch (Exception e) {
                cr.setSuccessful(false);
                cr.setMessage(e.getMessage());
            }
            double end = System.nanoTime();
            cr.setTime((end-start)/1000000);
            ret.add(cr);
        }
        return ret;
    }
}
