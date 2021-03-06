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

package com.betfair.monitoring.nagios;

import com.betfair.platform.shunit.ShUnitRunner;
import com.betfair.platform.shunit.ShUnitWrapper;
import org.junit.runner.RunWith;

import java.io.File;

@RunWith(ShUnitRunner.class)
public class TestCheckServiceDependencyNagios3 implements ShUnitWrapper {

    public File getScript() {
        return new File("src/test/shell/test_check_service_dependency_nagios3");
    }
}