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

import java.lang.management.ManagementFactory;
import java.util.Iterator;
import java.util.Set;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanServer;
import javax.management.ObjectInstance;
import javax.management.ObjectName;

public class BatchQuery {
	
    private final Auth auth;

	public BatchQuery(Auth auth) {
		this.auth = auth;
	}
    
    public String query(String [] onames, String op) {
        MBeanServer server;
        MBeanServer defaultServer = JMXUtils.getMBeanServer();
        MBeanServer platformServer = ManagementFactory.getPlatformMBeanServer();
        StringBuilder buf = new StringBuilder();
        String sep = "";
        for (int i=0;i<onames.length;i++) {
            try {
                if (onames[i].startsWith("java.lang:")) {
                    server = platformServer;
                }
                else {
                    server = defaultServer;
                }
                if (server != null) {
                    ObjectName on = new ObjectName(onames[i]);
                    if (on.isPattern()) {
                        Set<ObjectInstance> res = server.queryMBeans(on, null);
                        if (res!=null && res.size()>0) {
                            Iterator<ObjectInstance> j = res.iterator();
                            while (j.hasNext()) {
                                on = (j.next()).getObjectName();
                                buf.append(sep);

                                if (op!=null) {
                                    invokeMBean(server, on, op);
                                }

                                appendMBean(server, on, buf);
                                sep = "|";
                            }
                        }
                    } else {
                        buf.append(sep);

                        if (op!=null) {
                            invokeMBean(server, on, op);
                        }

                        appendMBean(server, on, buf);
                        sep = "|";
                    }
                }
            } catch (Exception e) { }
        }
        return buf.toString();
    }
    
    private void invokeMBean(MBeanServer server, ObjectName on, String op) {
		try {
            // disabled for now as a security hole, proper implementation to be covered by MON-102
			//server.invoke(on, op, new Object[] {}, new String[] {});
		} catch (Exception e) {
		}
	}

    protected void appendMBean(MBeanServer server, ObjectName on, StringBuilder buf) {
        try {

            MBeanInfo info = server.getMBeanInfo(on);
            buf.append(on.toString());
            MBeanAttributeInfo [] attr = info.getAttributes();
            for (int i=0;i<attr.length;i++) {
                if (attr[i].isReadable()) {
                    String name = attr[i].getName();
                    Object value = server.getAttribute(on, name);

                    buf.append("~");
                    buf.append(name);
                    buf.append("~");
                    buf.append(JMXUtils.getAttributeValueAsString(auth, on, name, value));
                }
            }
        } catch (Exception e) {
            // no extra output supported on this interface, just ignore and carry on
        }
    }
}