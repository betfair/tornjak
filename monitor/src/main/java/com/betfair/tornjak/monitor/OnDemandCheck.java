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

import com.betfair.tornjak.monitor.active.Check;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * On demand monitor which delegates down to a Check from the active monitoring component. Can only return status
 * of OK or FAIL, since Check's do not support WARN.
 * @see com.betfair.tornjak.monitor.active.Check
 */
public class OnDemandCheck extends OnDemandMonitor {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private Check check;
    private String name;

    public OnDemandCheck(Check check) {
        this(check, null);
    }

    public OnDemandCheck(Check check, String name) {
        if (check == null) {
            throw new IllegalArgumentException("check cannot be null");
        }
        this.check = check;
        this.name = name;
    }

    @Override
    protected Status checkStatus() {
        try {
            check.check();
            return Status.OK;
        } catch (Exception e) {
            logger.warn("Error performing check", e);
        }
        return Status.FAIL;
    }

    @Override
    public String getName() {
        if (name != null) {
            return name;
        }
        return check.getDescription();
    }
}