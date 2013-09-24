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
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.QueryExp;

/**
 * JMX helper functions.
 * 
 * @author ???, kiersztynw
 *
 */
public class JMXUtils {
	
	private static final String HIDDEN_VALUE = "*****";

	public static MBeanServer getMBeanServer() {
		try {
			return (MBeanServer) MBeanServerFactory.findMBeanServer(null).get(0);
		} catch (Exception e) {
			return null;
		}
	}
	
	public static MBeanInstance getMbean(String oname) {
		try {
			ObjectName on = new ObjectName(oname);
			return new MBeanInstance(oname, on, getMBeanServer().getMBeanInfo(on));
		} catch (Exception e) {
			return null;
		}
	}

	@SuppressWarnings("serial")
	public static List<MBeanInstance> getInstancesOf(final String mbeanInterface) {
		try {
			Set<ObjectInstance> beans = getMBeanServer().queryMBeans(null,
					new QueryExp() {

						MBeanServer s;

						@Override
						public boolean apply(ObjectName name) {
							try {
								return s.isInstanceOf(name, mbeanInterface);
							} catch (InstanceNotFoundException e) {
								throw new IllegalStateException("This should never occur", e);
							}
						}

						public void setMBeanServer(MBeanServer s) {
							this.s = s;
						}
					});
			List<MBeanInstance> ret = new ArrayList<MBeanInstance>();
			for (ObjectInstance oi : beans) {
				ret.add(new MBeanInstance(
						oi.getObjectName().getCanonicalName(), oi.getObjectName(), 
						getMBeanServer().getMBeanInfo(oi.getObjectName())));
			}
			return ret;
		} catch (Exception e) {
			// equivalent of finding nothing
			return new ArrayList<MBeanInstance>();
		}
	}
	
	public static Object getAttributeValue(MBeanInstance bean, String attr) {
		try {
            for (MBeanAttributeInfo attrInfo : bean.getBean().getAttributes()) {
                if (attrInfo.getName().equals(attr)) {
                	ObjectName objectName = bean.getObjectName();
                	return JMXUtils.getMBeanServer().getAttribute(objectName, attr);
                }
            }
        } catch (Exception e) {
            // so what?
        }
        return null;
	}
	
	public static String getAttributeValueAsString(Auth auth, ObjectName objectName, String attr, Object object) {
		if (!auth.canReadAttribute(objectName, attr)) {
			return HIDDEN_VALUE;
		}
		
		if (object == null) {
			return "null";
		}
		if (object.getClass().isArray()) {
			StringBuilder tmp = new StringBuilder(Arrays.deepToString(new Object[] { object }));
			tmp.deleteCharAt(tmp.length() - 1);
			tmp.deleteCharAt(0);
			return tmp.toString();
		} else {
			return object.toString();
		}
	}
}
