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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * Handles what operations/data a given role is allowed to perform.
 * 
 * <p>
 * Immutable, and thread safe.
 * </p>
 * 
 * @author vanbrakelb, kiersztynw
 * 
 */
public class RolePerms {
	
	private final Map<String, AccessRules> accessRulesMap = new HashMap<String, AccessRules>();

	public void addAccessRules(String role, AccessRules accessRules) {
		accessRulesMap.put(role, accessRules);
	}

	/**
	 * Can this role read the given attribute
	 * 
	 * @param input
	 * @return
	 */
	public boolean canReadAttribute(String role, String domain, String keyPropertyList, String attribute) {
		if (!accessRulesMap.containsKey(role)) {
			return false;
		}
		return accessRulesMap.get(role).pass(domain, keyPropertyList, attribute);
	}
	
	public Set<String> getRoles() {
		return new HashSet<String>(accessRulesMap.keySet());
	}
}