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

import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.StringContains.containsString;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Simple test for https://jira.app.betfair/browse/MON-75
 * 
 * @author kiersztynw
 *
 */
public class SensitiveDataFilterTest {
	
	private static final String ON = "MyDomain:key1=value1,key2=value2,key3=foo";
	private static final String SECRET = "myPassword";
	private static final String NOT_SECRET = "myValue";
	
	private final MBeanServer mbeanServer;
	private final AuthBuilder authBuilder;
	
	public SensitiveDataFilterTest() throws Exception {
		mbeanServer = ManagementFactory.getPlatformMBeanServer();
		authBuilder = new AuthBuilder().role("jmxadmin").allow(".*:.*:.*").deny("MyDomain:.*foo:secretValue");
	}
	
	@Before
	public void setUp() throws Exception {
		Foo foo = new Foo();
		foo.setValue(NOT_SECRET);
		foo.setSecretValue(SECRET);
		
		mbeanServer.registerMBean(foo, new ObjectName(ON));
	}
	
	@After
	public void tearDown() throws Exception {
		authBuilder.authenticated(true).inRole(true);
		mbeanServer.unregisterMBean(new ObjectName(ON));
		
	}
	
	@Test
	public void testSensitiveDataNotPresentInHumanQuery() throws Exception {
		{
			String query = new HumanQuery(authBuilder.getAuth()).query(new String[] { ON } );
			assertThat(query, not(containsString(SECRET)));
			assertThat(query, containsString(NOT_SECRET));
		}
		{
			String query = new HumanQuery(authBuilder.getAuth()).query(new String[] { ON } );
			assertThat(query, not(containsString(SECRET)));
			assertThat(query, containsString(NOT_SECRET));
		}
	}
	
	@Test
	public void testSensitiveDataNotPresentInBatchQuery() throws Exception {
		String query = new BatchQuery(authBuilder.getAuth()).query(new String[] { ON }, null );
		assertThat(query, not(containsString(SECRET)));
	}
	
	@Test
	public void testSensitiveDataNotAvailableForNotAuthenticated() throws Exception {
		{
			String query = new BatchQuery(authBuilder.authenticated(false).getAuth()).query(new String[] { ON }, null );
			assertThat(query, not(containsString(SECRET)));
			assertThat(query, not(containsString(NOT_SECRET)));
		}
		{
			String query = new HumanQuery(authBuilder.authenticated(false).getAuth()).query(new String[] { ON } );
			assertThat(query, not(containsString(SECRET)));
			assertThat(query, not(containsString(NOT_SECRET)));
		}
	}
	
	@Test
	public void testSensitiveDataNotAvailableForNotAuthorised() throws Exception {
		{
			String query = new BatchQuery(authBuilder.inRole(false).getAuth()).query(new String[] { ON }, null );
			assertThat(query, not(containsString(SECRET)));
			assertThat(query, not(containsString(NOT_SECRET)));
		}
		{
			String query = new HumanQuery(authBuilder.authenticated(false).getAuth()).query(new String[] { ON } );
			assertThat(query, not(containsString(SECRET)));
			assertThat(query, not(containsString(NOT_SECRET)));
		}
	}
}
