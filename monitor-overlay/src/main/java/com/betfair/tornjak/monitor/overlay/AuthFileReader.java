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

import java.io.IOException;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;


/**
 * Reads in an auth properties file to determine mbean access
 * 
 * <p>
 * Format of file is:
 * 
 * <pre>
 * jmx.roles=&lt;role1&gt;,&lt;role2&gt;,&lt;role3&gt;...
 * 
 * &lt;role&gt;.&lt;readAllow|readDeny&gt;.&lt;number&gt;.domain=&lt;domain-match-regexp, or null if match all&gt;
 * &lt;role&gt;.&lt;readAllow|readDeny&gt;.&lt;number&gt;.keyProperty=&lt;key-property-match-regexp, or null if match all&gt;
 * &lt;role&gt;.&lt;readAllow|readDeny&gt;.&lt;number&gt;.attr=&lt;attribute-match-regexp, required&gt;
 * </pre>
 * 
 * Example
 * 
 * <pre>
 *  jmx.roles=jmxSupport, jmxadmin, jmxUseless
 *  
 *  # jmxadmin can read any attribute on any bean apart from attributes that match pass.* and secret
 *  role.jmxadmin.readAllow.1.domain=.*
 *  role.jmxadmin.readAllow.1.keyProperty=.*
 *  role.jmxadmin.readAllow.1.attr=.*
 *  
 *  role.jmxadmin.readDeny.1.domain=.*
 *  role.jmxadmin.readDeny.1.keyProperty=.*
 *  role.jmxadmin.readDeny.1.attr=pass.*
 *  
 *  role.jmxadmin.readDeny.2.attr=secret
 * </pre>
 * 
 * Entres are case sensitive.
 * </p>
 * 
 * @author vanbrakelb, kiersztynw
 * 
 */
public class AuthFileReader {

	private static final int MAX_MATCHERS_PER_ROLE = 100;
	
	public static RolePerms load(Resource resource) throws IOException {
		Properties properties = PropertiesLoaderUtils.loadProperties(resource);
		
		
		// find all the roles we'll be using.
		String roles = properties.getProperty("jmx.roles");
		RolePerms rolePerms = new RolePerms();
		if (roles != null) {
			// now build up the list of perms
			for (String roleName : roles.split(",")) {
				roleName = StringUtils.trim(roleName);
				AccessRules accessRules = new AccessRules();
				// find all the entries for each role
				for (int i = 0; i < MAX_MATCHERS_PER_ROLE; i++) {
					String allowPrefix = String.format("role.%s.readAllow.%d.", roleName, i);
					String denyPrefix = String.format("role.%s.readDeny.%d.", roleName, i);
					
					accessRules.addMatcherAllowed(createMatcher(allowPrefix, properties));
					accessRules.addMatcherDisallowed(createMatcher(denyPrefix, properties));
				}
				rolePerms.addAccessRules(roleName, accessRules);
			}
		}
		return rolePerms;
	}

	private static MBeanAttributeMatcher createMatcher(String prefix, Properties p) {
		String domainRegEx = p.getProperty(prefix + "domain");
		String keyPropertyRegEx = p.getProperty(prefix + "keyProperty");
		String attrRegEx = p.getProperty(prefix + "attr");
		if (StringUtils.isNotBlank(attrRegEx)) {
			if (StringUtils.isBlank(domainRegEx)) {
				domainRegEx = ".*";
			}
			if (StringUtils.isBlank(keyPropertyRegEx)) {
				keyPropertyRegEx = ".*";
			}
			
			return new RegExpMatcher(domainRegEx, keyPropertyRegEx, attrRegEx);
		}
		
		return null;
	}
}