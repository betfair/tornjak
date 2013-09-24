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

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;


/**
 * I test {@link RolePerms}
 * 
 * @author kiersztynw
 *
 */
public class RolePermsTest {

	@Test
	public void testCanReadAttribute() {
		AccessRules accessRules = new AccessRules();
		accessRules.addMatcherAllowed(new RegExpMatcher(".*", ".*", ".*"));
		accessRules.addMatcherDisallowed(new RegExpMatcher(".*", ".*foo", ".*pass.*"));
		
		RolePerms rolePerms = new RolePerms();
		rolePerms.addAccessRules("jmxadmin", accessRules);
		
		assertThat(rolePerms.canReadAttribute("jmxadmin", "domain", "name=foo", "value"), is(true));
		assertThat(rolePerms.canReadAttribute("jmxadmin", "domain", "name=foo", "password"), is(false));
		assertThat(rolePerms.canReadAttribute("someOtherRole", "domain", "name=foo", "value"), is(false));
	}
	
	@Test
	public void testCanReadBasedOnDefaults() {
		RolePerms rolePerms = new RolePerms();
		assertThat(rolePerms.canReadAttribute("jmxadmin", "domain", "name=foo", "value"), is(false));
	}
}
