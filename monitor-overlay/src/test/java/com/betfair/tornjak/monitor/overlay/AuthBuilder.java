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

import com.betfair.tornjak.monitor.overlay.Auth.Validator;

/**
 * I create a test {@link Auth} object.
 * 
 * Related to https://jira.app.betfair/browse/MON-75
 * 
 * @author kiersztynw
 *
 */
public class AuthBuilder {
	
	private final Auth auth;
	private final TestValidator testValidator;
	private final RolePerms rolePerms;
	
	private AccessRules accessRules;
	
	public AuthBuilder() {
		rolePerms = new RolePerms();
		testValidator = new TestValidator();
		auth = new Auth(testValidator, rolePerms);
	}
	
	public AuthBuilder role(String role) {
		this.accessRules = new AccessRules();
		this.rolePerms.addAccessRules(role, accessRules);
		
		return this;
	}
	
	public AuthBuilder allow(String filter) {
		String[] splitFilter = filter.split(":");
		if (splitFilter == null || splitFilter.length != 3) {
			throw new IllegalArgumentException("Wrong filter expression");
		}
		
		String domainRegEx = splitFilter[0];
		String keyPropertyRegEx = splitFilter[1];
		String attributeRegEx = splitFilter[2];
		accessRules.addMatcherAllowed(new RegExpMatcher(domainRegEx, keyPropertyRegEx, attributeRegEx));
		
		return this;
	}
	
	public AuthBuilder deny(String filter) {
		String[] splitFilter = filter.split(":");
		if (splitFilter == null || splitFilter.length != 3) {
			throw new IllegalArgumentException("Wrong filter expression");
		}
		
		String domainRegEx = splitFilter[0];
		String keyPropertyRegEx = splitFilter[1];
		String attributeRegEx = splitFilter[2];
		accessRules.addMatcherDisallowed(new RegExpMatcher(domainRegEx, keyPropertyRegEx, attributeRegEx));
		
		return this;
	}
	
	public AuthBuilder authenticated(boolean value) {
		testValidator.authenticated = value;
		return this;
	}
	
	public AuthBuilder inRole(boolean value) {
		testValidator.userInRole = value;
		return this;
	}
	
	public Auth getAuth() {
		return auth;
	}
	
	public RolePerms getRolePerms() {
		return rolePerms;
	}
	
	private class TestValidator implements Validator {
		
		private boolean authenticated;
		private boolean userInRole;

		public TestValidator() {
			this.authenticated = true;
			this.userInRole = true;
		}

		@Override
		public boolean isAuthenticated() {
			return authenticated;
		}

		@Override
		public boolean isUserInRole(String role) {
			return userInRole;
		}
	}
}
