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

import static junit.framework.Assert.assertEquals;

import javax.management.ObjectName;

import junit.framework.Assert;
import org.junit.After;
import org.junit.Test;

public class JMXUtilsTest {
	
	private final AuthBuilder authBuilder = new AuthBuilder().role("jmxadmin").allow(".*:.*:.*");
	
	@After
	public void tearDown() {
		authBuilder.authenticated(true).inRole(true);
	}

    @Test
    public void testGetAttributeAsString() throws Exception {
    	Auth auth = authBuilder.getAuth();
    	ObjectName on = new ObjectName("MyDomain:name=myBean");
    	String attr = "someAttr";

        Assert.assertEquals("[1, 2, 3, 4, 5]", JMXUtils.getAttributeValueAsString(auth, on, attr, new int[]{1, 2, 3, 4, 5}));
        assertEquals("[1, 2, 3]", JMXUtils.getAttributeValueAsString(auth, on, attr, new Integer[] {1, 2, 3}));
        assertEquals("[test]", JMXUtils.getAttributeValueAsString(auth, on, attr, new String[] {"test"}));
        assertEquals("[false, true, false]", JMXUtils.getAttributeValueAsString(auth, on, attr, new boolean[]{false, true, false}));
        assertEquals("[]", JMXUtils.getAttributeValueAsString(auth, on, attr, new Object[]{}));

        assertEquals("null", JMXUtils.getAttributeValueAsString(auth, on, attr, null));
        assertEquals("[null]", JMXUtils.getAttributeValueAsString(auth, on, attr, new Object[] {null}));

        // test self-reference
        Object[] array = new Object[2];
        array[0] = array;
        array[1] = "test";
        assertEquals("[[...], test]", JMXUtils.getAttributeValueAsString(auth, on, attr, array));

        // test cyclic-reference
        Object[] array1 = new Object[2];
        Object[] array2 = new Object[2];
        array1[0] = array2;
        array1[1] = "test1";
        array2[0] = array1;
        array2[1] = "test2";
        assertEquals("[[[...], test2], test1]", JMXUtils.getAttributeValueAsString(auth, on, attr, array1));
    }

    @Test
    public void testGetAttributeAsStringNoAuthorised() throws Exception {
    	Auth auth = authBuilder.inRole(false).getAuth();
    	
    	ObjectName on = new ObjectName("MyDomain:name=myBean");
    	String attr = "someAttr";

        assertEquals("*****", JMXUtils.getAttributeValueAsString(auth, on, attr, new int[] {1, 2, 3, 4, 5}));
    }
    
    @Test
    public void testGetAttributeAsStringNoAuthenticated() throws Exception {
    	Auth auth = authBuilder.authenticated(false).getAuth();
    	
    	ObjectName on = new ObjectName("MyDomain:name=myBean");
    	String attr = "someAttr";

        assertEquals("*****", JMXUtils.getAttributeValueAsString(auth, on, attr, new int[] {1, 2, 3, 4, 5}));
    }
}
