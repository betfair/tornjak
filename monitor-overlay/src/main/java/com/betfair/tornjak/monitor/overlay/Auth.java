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

import javax.management.ObjectName;


/**
 * I am required to get independence from javax.servlet.http and static calls. 
 * 
 * @author kiersztynw
 *
 */
public class Auth {
	
	public enum AuthStatus {
		UNAUTHORISED, FORBIDDEN, AUTHORISED
	}

	private final Validator validator;
	private final RolePerms rolePerms;

	public Auth(Validator validator, RolePerms rolePerms) {
		if (validator == null || rolePerms == null) {
			throw new IllegalArgumentException("Both paramaters can not be null");
		}
		this.validator = validator;
		this.rolePerms = rolePerms;
	}
	
	public AuthStatus check() {
		if (!validator.isAuthenticated()) {
			return AuthStatus.UNAUTHORISED;
		} else {
			for(String role : rolePerms.getRoles()) {
				if (validator.isUserInRole(role)) {
					return AuthStatus.AUTHORISED;
				}
			}
		}
		
		return AuthStatus.FORBIDDEN;
	}
	
	public boolean canReadAttribute(ObjectName objectName, String attribute) {
		String domain = objectName.getDomain();
		String keyProperty = objectName.getKeyPropertyListString();
		
		if (validator.isAuthenticated()) {
			for(String role : rolePerms.getRoles()) {
				if (validator.isUserInRole(role)) {
					return rolePerms.canReadAttribute(role, domain, keyProperty, attribute);
				}
			}
		}
		
		return false;
	}
	
	public interface Validator {
		
		boolean isAuthenticated();
		
		boolean isUserInRole(String role);
	}
}
