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

import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;


/**
 * Uses regular expressions to match an mbean name and attribute.
 * 
 * @author vanbrakelb, kiersztynw
 * 
 */
public class RegExpMatcher implements MBeanAttributeMatcher {
	
	private final Pattern domainPattern;
	private final Pattern keyPropertyListPattern;
	private final Pattern attributePattern;

	public RegExpMatcher(String domainRexEx, String keyPropertyListRexEx, String attributeRegEx) {
		domainPattern = createPattern(domainRexEx);
		keyPropertyListPattern = createPattern(keyPropertyListRexEx);
		attributePattern = createPattern(attributeRegEx);
	}
	
	private Pattern createPattern(String regEx) {
		if (StringUtils.isNotBlank(regEx)) {
			return Pattern.compile(regEx, Pattern.CASE_INSENSITIVE);
		}
		
		return null;
	}

	@Override
	public boolean match(String domain, String keyProperty, String attribute) {
		return match(domainPattern, domain) && match(keyPropertyListPattern, keyProperty) && match(attributePattern, attribute);
	}
	
	private boolean match(Pattern pattern, String str) {
		if (pattern == null) {
			return true;
		}
		return pattern.matcher(str).matches();
	}
}