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

package com.betfair.tornjak.monitor.active;

import java.util.ArrayList;
import java.util.Collection;

/**
 * I allow multiple checks to be run in sequence. I don't implement the actual checks,
 * instead deferring to the checks passed into the constructor.
 *
 * @author vanbrakelb
 */
public class MultiCheck implements Check {

    private final Collection<Check> checks;

    /**
     * Create a new MultiCheck using the given checks. Run in the order provided. A defensive copy is made. Null
     * entries are not allowed.
     *
     * @param checks
     */
    public MultiCheck(Collection<Check> checks) {
        if (checks == null) {
            throw new IllegalArgumentException("Cannot provide a null list of checks");
        }
        //use a linked list for fast walking. We only iterate this list in one direction
        this.checks = new ArrayList<Check>();
        for (Check check : checks) {
            if (check == null) {
                throw new IllegalArgumentException("Cannot handle null check entries. Check " + this.checks.size() + " is null (zero based)");
            }
            this.checks.add(check);
        }
        //no point in having no checks to run
        if (this.checks.size() < 1) {
            throw new IllegalArgumentException("Need to contain at least one check. Currently have no non-null checks");
        }
    }

    @Override
    public void check() throws Exception {
        for (Check check : checks) {
            check.check();
        }
    }

    @Override
    public String getDescription() {
        StringBuilder sb = new StringBuilder("MultiCheck:");
        int count = 0;
        for (Check check : checks) {
            sb.append("check[");
            sb.append(count);
            sb.append("](");
            sb.append(check.getDescription());
            sb.append(");");
            count++;
        }
        return sb.toString();
    }
}
