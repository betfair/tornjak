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

package com.betfair.tornjak.kpi.aop;

import com.betfair.tornjak.kpi.statse.StatsEMonitor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import static org.junit.Assert.assertNotNull;

/**
 * Date: 26/06/2013
 * Time: 11:50:13
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:kpi-statse.xml")
public class TestStatsEKPIContext {

    @Resource
    private ApplicationContext context;

    @Test
    public void shouldLoadContext() {
        final StatsEMonitor monitor = (StatsEMonitor) context.getBean("kpiMonitor");

        assertNotNull(monitor);
    }


}
