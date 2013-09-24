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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * Holder for a set of matchers for a given operation (READ). Contains
 * a set of allow and disallow matchers.
 * 
 * <p>
 * Rules on how to gain access to a given mbean attribute:
 * <ol>
 * <li>you need to firstly have it match an entry in the allow list</li>
 * <li>if there are no matches in the disallows, passes</li>
 * <li>if there are matches in the disallows, fails
 * </p>
 * <li>if no entries in the allow list (or its null), you don't get access</li>
 * </p>
 * <p>
 * Immutable, and thread safe.
 * </p>
 * 
 * @author vanbrakelb, kiersztynw
 * 
 */
public class AccessRules {

	/**
	 * If any allow matchers are given, then this allows access provided
	 * there are no matching disallow matchers
	 */
	private final List<MBeanAttributeMatcher> matchersAllowed = new ArrayList<MBeanAttributeMatcher>();

	/**
	 * If any disallow matchers are given, they override any allow matchers
	 */
	private final List<MBeanAttributeMatcher> matchersDisallowed = new ArrayList<MBeanAttributeMatcher>();
	
	public AccessRules addMatcherAllowed(MBeanAttributeMatcher allowed) {
		if (allowed != null) {
			matchersAllowed.add(allowed);
		}
		return this;
	}
	
	public AccessRules addMatcherDisallowed(MBeanAttributeMatcher disallowed) {
		if (disallowed != null) {
			matchersDisallowed.add(disallowed);
		}
		return this;
	}
	/**
	 * If access to the given attribute is granted
	 * 
	 * @param input
	 * @return true if access granted
	 */
	public boolean pass(String domainRexEx, String keyPropertyListRexEx, String attributeRegEx) {
		if (StringUtils.isBlank(domainRexEx) || 
				StringUtils.isBlank(keyPropertyListRexEx) || 
				StringUtils.isBlank(attributeRegEx)) {
			return false;
		}
		
		if (matchersAllowed.isEmpty()) {
			return false;
		}

		// if any disallowed matches, no way we can access this attribute
		for (MBeanAttributeMatcher disallow : matchersDisallowed) {
			if (disallow.match(domainRexEx, keyPropertyListRexEx, attributeRegEx)) {
				return false;
			}
		}
		// if if no diasllowed matches, still have to match an allow
		for (MBeanAttributeMatcher allow : matchersAllowed) {
			if (allow.match(domainRexEx, keyPropertyListRexEx, attributeRegEx)) {
				return true;
			}
		}
		return false;
	}
}