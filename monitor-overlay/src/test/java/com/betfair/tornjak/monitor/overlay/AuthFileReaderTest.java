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
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

/**
 * I test {@link AuthFileReader}
 * 
 * @author kiersztynw
 *
 */
public class AuthFileReaderTest {
	
	private final ResourceLoader resourceLoader = new DefaultResourceLoader();
	
	@Test
	public void testForInvalidFile() throws Exception {
		Resource resource = resourceLoader.getResource("/com/betfair/tornjak/monitor/overlay/auth-invalid.properties");
		RolePerms rolePerms = AuthFileReader.load(resource);
		
		assertThat(rolePerms.canReadAttribute("jmxadmin", "MyDomain", "name=foo", "someAttribute"), is(false));
		assertThat(rolePerms.canReadAttribute("arole", "MyDomain", "name=foo", "someAttribute"), is(true));
	}

	@Test
	public void testCreate() throws Exception {
		
		Resource resource = resourceLoader.getResource("/com/betfair/tornjak/monitor/overlay/auth.properties");
		
		RolePerms rolePerms = AuthFileReader.load(resource);
	
		{
			assertThat(rolePerms.canReadAttribute("jmxadmin", "MyDomain", "name=foo", "someAttribute"), is(true));
			assertThat(rolePerms.canReadAttribute("jmxadmin", "MyDomain", "name=foo", "value"), is(true));
			assertThat(rolePerms.canReadAttribute("jmxadmin", "MyDomain", "name=foo", "password"), is(false));
			assertThat(rolePerms.canReadAttribute("jmxadmin", "MyDomain", "name=foo", "secret"), is(false));
		}
		
		{
			assertThat(rolePerms.canReadAttribute("jmxSupport", "MyDomain", "name=foo", "value"), is(true));
			assertThat(rolePerms.canReadAttribute("jmxSupport", "MyDomain", "name=foo", "password"), is(false));
		}
		
		{
			assertThat(rolePerms.canReadAttribute("jmxUseless", "MyDomain", "name=foo", "value"), is(false));
		}
	}
	
	@Test
	public void testReadDisallowByDefault() throws Exception {
		Resource resource = resourceLoader.getResource("/com/betfair/tornjak/monitor/overlay/empty-auth.properties");
		
		RolePerms rolePerms = AuthFileReader.load(resource);
	
		assertThat(rolePerms.getRoles().size(), equalTo(0));
	}

}
