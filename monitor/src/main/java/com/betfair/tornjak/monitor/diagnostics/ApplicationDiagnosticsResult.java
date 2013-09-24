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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ApplicationDiagnosticsResult {
    
    private Date date;
    private List<CheckResult> checks = new ArrayList<CheckResult>();
    private List<DependencyDiagnosticsResult> dependencyResults = new ArrayList<DependencyDiagnosticsResult>();

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<CheckResult> getChecks() {
        return checks;
    }

    public void setChecks(List<CheckResult> checks) {
        this.checks = checks;
    }

    public List<DependencyDiagnosticsResult> getDependencyResults() {
        return dependencyResults;
    }

    public void setDependencyResults(List<DependencyDiagnosticsResult> dependencyResults) {
        this.dependencyResults = dependencyResults;
    }
}
