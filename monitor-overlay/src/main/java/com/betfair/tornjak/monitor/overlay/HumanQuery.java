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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanServer;
import javax.management.ObjectInstance;
import javax.management.ObjectName;

public class HumanQuery {
	
    private final Auth auth;

	public HumanQuery(Auth auth) {
		this.auth = auth;
	}
    
    public String query(String [] onames) {
        MBeanServer server = JMXUtils.getMBeanServer();
        StringBuilder buf = new StringBuilder();
        if (server!=null) {
            List<ObjectName> allObjectNames = new ArrayList<ObjectName>();
            for (int i=0;i<onames.length;i++) {
                try {
                    ObjectName on = new ObjectName(onames[i]);
                    if (on.isPattern()) {
                        Set<?> res = server.queryMBeans(on, null);
                        if (res!=null && res.size()>0) {
                            Iterator<?> j = res.iterator();
                            while (j.hasNext()) {
                                on = ((ObjectInstance)j.next()).getObjectName();
                                allObjectNames.add(on);
                            }
                        }
                    } else {
                        allObjectNames.add(on);
                    }
                } catch (Exception e) { }
            }
            Map<String, Set<ObjectName>> domainsToObjectNames = new HashMap<String, Set<ObjectName>>();
            List<String> allDomains = new ArrayList<String>();
            Iterator<?> it = allObjectNames.iterator();
            while (it.hasNext()) {
                ObjectName objectName = (ObjectName) it.next();
                String domain = objectName.getDomain();
                Set<ObjectName> objectsForDomain = domainsToObjectNames.get(domain);
                if (objectsForDomain == null) {
                    objectsForDomain = new HashSet<ObjectName>();
                    domainsToObjectNames.put(domain, objectsForDomain);
                    allDomains.add(domain);
                }
                objectsForDomain.add(objectName);
            }
            Collections.sort(allDomains);

            buf.append("<h2>Domains</h2>\n");
            
            it = allDomains.iterator();
            while (it.hasNext()) {
                String domain = (String) it.next();
                buf.append("<a href='#").append(domain).append("'>").append(domain).append("</a><br/>\n");
            }

            it = allDomains.iterator();
            while (it.hasNext()) {
                String domain = (String) it.next();
                buf.append("<a name='").append(domain).append("'/>\n");
                buf.append("<h2>").append(domain).append("</h2>\n");
                Set<ObjectName> objectNames = domainsToObjectNames.get(domain);
                Iterator<ObjectName> it2 = objectNames.iterator();
                while (it2.hasNext()) {
                    ObjectName objectName = it2.next();
                    buf.append("<table border='0'>\n<tr><td colspan='2'><b>").append(objectName).append("</b></td></tr>\n");
                    appendMBean(server, objectName, buf);
                    buf.append("</table><br/>\n");
                    buf.append("<a href='#page_top'>Top of page</a><br/><br/><br/>\n");
                }
                buf.append("<br/><br/><hr width='50%'/><br/><br/>\n");
            }
        }
        return buf.toString();
    }

    protected void appendMBean(MBeanServer server, ObjectName on, StringBuilder buf) {
        try {

            MBeanInfo info = server.getMBeanInfo(on);
            //buf.append("<tr><td colspan='2'>").append(on).append("</td></tr>\n");
            MBeanAttributeInfo[] attr = info.getAttributes();
            for (int i=0;i<attr.length;i++) {
                if (attr[i].isReadable()) {
                    String name = attr[i].getName();
                    Object value = server.getAttribute(on, name);

                    buf.append("<tr><td><i>").append(name).append("</i></td>");
                    buf.append("<td>").append(JMXUtils.getAttributeValueAsString(auth, on, name, value)).append("</td></tr>\n");
                }
            }
        } catch (Exception e) {
            buf.append("<tr><td colspan='2'><i>Exception: ").append(e.getMessage()).append("</i></td></tr>\n");
            return;
        }
    }
}
