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

import static com.betfair.tornjak.monitor.overlay.Auth.AuthStatus.AUTHORISED;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.security.Principal;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.web.context.WebApplicationContext;

import com.betfair.tornjak.monitor.overlay.Auth.Validator;

public class AuthUtilsTest {
	
	@Test(expected=IllegalArgumentException.class)
	public void testNullValidatorOnAuthObjectCreation() {
		new Auth(null, new RolePerms());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testNullRolePermsOnAuthObjectCreation() {
		new Auth(new Validator() {
			
			@Override
			public boolean isUserInRole(String role) {
				return false;
			}
			
			@Override
			public boolean isAuthenticated() {
				return false;
			}
		}, null);
	}

    @Test
    public void testSuccessfulAuthorisation() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        ServletContext context = mock(ServletContext.class);

        Principal p = mock(Principal.class);

        when(context.getAttribute("com.betfair.tornjak.monitor.overlay.RolePerms")).thenReturn(
        		new AuthBuilder().role("jmxadmin").allow(".*:.*:.*").getRolePerms());
        when(request.getUserPrincipal()).thenReturn(p);
        when(request.isUserInRole("jmxadmin")).thenReturn(true);
        
        Auth auth = AuthUtils.checkAuthorised(request, response, context);

        assertThat("User should be authorised", auth.check(), equalTo(AUTHORISED));
    }

    @Test
    public void testNotAuthenticated() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        ServletContext context = mock(ServletContext.class);

        when(context.getAttribute("com.betfair.tornjak.monitor.overlay.RolePerms")).thenReturn(
        		new AuthBuilder().role("jmxadmin").allow(".*:.*:.*").getRolePerms());
        when(request.getUserPrincipal()).thenReturn(null);

        Auth auth = AuthUtils.checkAuthorised(request, response, context);
        assertThat("User should not be authorised", auth, nullValue());
        
        verify(response, times(1)).sendError(HttpServletResponse.SC_UNAUTHORIZED);
        verifyNoMoreInteractions(response);
    }

    @Test
    public void testNotAuthorised() throws Exception {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        ServletContext context = mock(ServletContext.class);

        Principal p = mock(Principal.class);

        when(context.getAttribute("com.betfair.tornjak.monitor.overlay.RolePerms")).thenReturn(
        		new AuthBuilder().role("jmxadmin").allow(".*:.*:.*").getRolePerms());
        when(request.getUserPrincipal()).thenReturn(p);
        when(request.isUserInRole("jmxadmin")).thenReturn(false);

        Auth auth = AuthUtils.checkAuthorised(request, response, context);
        assertThat("User should not be authorised", auth, nullValue());

        verify(response, times(1)).sendError(HttpServletResponse.SC_FORBIDDEN);
        verifyNoMoreInteractions(response);
    }
    
    @Test
    public void testCreateRolePerms() throws Exception {
    	HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        ServletContext context = mock(ServletContext.class);
        ApplicationContext appContext = mock(ApplicationContext.class);

        Principal p = mock(Principal.class);

        when(context.getAttribute("com.betfair.tornjak.monitor.overlay.RolePerms")).thenReturn(null);
		when(context.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE)).thenReturn(appContext);
		when(context.getInitParameter("contextAuthConfigLocation")).thenReturn("somewhere");
		when(appContext.getResource("somewhere")).thenReturn(
				new DefaultResourceLoader().getResource("com/betfair/tornjak/monitor/overlay/auth.properties"));
		
        
        when(request.getUserPrincipal()).thenReturn(p);
        when(request.isUserInRole("jmxadmin")).thenReturn(true);
        
        Auth auth = AuthUtils.checkAuthorised(request, response, context);

        assertThat(auth, notNullValue());
        assertThat("User should be authorised", auth.check(), equalTo(AUTHORISED));
    }
}
