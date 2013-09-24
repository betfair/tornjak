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

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextException;
import org.springframework.web.context.WebApplicationContext;

public class AuthUtils {
	
	private static final String CONTEXT_ATTR_NAME = "com.betfair.tornjak.monitor.overlay.RolePerms";
	private static final String CONTEXT_INIT_PARAM_NAME = "contextAuthConfigLocation";
	
	/**
	 * Returns null if user is not authenticated or authorised, otherwise returns Auth object.
	 * 
	 */
    public static Auth checkAuthorised(final HttpServletRequest request, HttpServletResponse response, 
    		ServletContext servletContext) throws IOException {
    	
    	RolePerms rolePerms = getOrCreateRolePerms(servletContext);
    	
    	Auth auth = new Auth(new Auth.Validator() {
			
			@Override
			public boolean isUserInRole(String role) {
				return request.isUserInRole(role);
			}
			
			@Override
			public boolean isAuthenticated() {
				return request.getUserPrincipal() != null;
			}
		}, rolePerms);
    	
    	switch (auth.check()) {
	    	case UNAUTHORISED:
	    		response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
	            return null;
	    	case FORBIDDEN:
	    		response.sendError(HttpServletResponse.SC_FORBIDDEN);
	            return null;
	    	default:
	    		return auth;
    	}
    }
    
    private static synchronized RolePerms getOrCreateRolePerms(ServletContext servletContext) throws IOException {
    	RolePerms rolePerms;
    	
   		rolePerms = (RolePerms) servletContext.getAttribute(CONTEXT_ATTR_NAME);
    	
    	if (rolePerms != null) {
    		return rolePerms;
    	}
    	
    	ApplicationContext context = (ApplicationContext) servletContext.getAttribute(
    			WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
    	if (context == null) {
    		throw new ApplicationContextException("Unable to find application context");
    	}
    	String authFileLocation = servletContext.getInitParameter(CONTEXT_INIT_PARAM_NAME);
    	if (StringUtils.isBlank(authFileLocation)) {
    		throw new ApplicationContextException(String.format("Parameter '%s' not defined, unable to load jmx auth file", 
    				CONTEXT_INIT_PARAM_NAME));
    	}
    	
    	rolePerms = AuthFileReader.load(context.getResource(authFileLocation));
		servletContext.setAttribute(CONTEXT_ATTR_NAME, rolePerms);
    	
    	return rolePerms;
    	
    }
}
