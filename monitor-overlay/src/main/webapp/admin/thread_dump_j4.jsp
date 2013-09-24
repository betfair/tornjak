<%@ page contentType="text/html; charset=utf-8"
         import="java.lang.management.*,java.util.*"
         import="com.betfair.tornjak.monitor.overlay.AuthUtils"
		 import="com.betfair.tornjak.monitor.overlay.Auth" %>
<%--
  ~ Copyright 2013, The Sporting Exchange Limited
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~     http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  --%>

<%
	Auth auth = AuthUtils.checkAuthorised(request, response, getServletContext());
	if (auth == null) {
	    return;
	}
%>
<%!
    String toStringThreadInfo(ThreadMXBean mx, ThreadInfo info) {
        StringBuffer buf = new StringBuffer();
        buf
        .append("\tid:").append(info.getThreadId())
        .append(" name:'").append(info.getThreadName()).append("'")
        .append(" state:").append(info.getThreadState());
        if (info.getLockName()!=null) {
            buf.append(" monitor:'").append(info.getLockName()).append("'")
            .append(" ownerId:").append(info.getLockOwnerId())
            .append(" ownerName:'").append(info.getLockOwnerName()).append("'");          
        }
        
        buf.append("\n\tCPUTime(ns): ")
        .append(mx.getThreadCpuTime(info.getThreadId()))
        .append("\tUserTime(ns): ")
        .append(mx.getThreadUserTime(info.getThreadId()))
        .append(", inNative: ")
        .append(info.isInNative())
        .append(", suspended: ")
        .append(info.isSuspended())
        .append(", blockedCount: ")
        .append(info.getBlockedCount())
        .append(", blockedTime(ms)")
        .append(info.getBlockedTime())
        .append(", waitedCount: ")
        .append(info.getWaitedCount())
        .append(", waitedTime(ms): ")
        .append(info.getWaitedTime());
        StackTraceElement [] traces = info.getStackTrace();
        if (traces!=null) {
            buf.append("\n");
            for (int i=0;i<traces.length;i++) {
                buf.append("\t\t").append(traces[i]).append("\n");
            }
        }
        
        return buf.toString();
    }
%>
<html><body><h2>Thread control</h2>
<%
    
    
    
    
    ThreadMXBean mx = ManagementFactory.getThreadMXBean();

    
    
    //process commands
    String command = request.getParameter("command") ;
    if (command!=null) {
        if (command.equals("toggleCM")) {
            if (mx.isThreadContentionMonitoringEnabled()) {
            mx.setThreadContentionMonitoringEnabled(false);
            } else {
            mx.setThreadContentionMonitoringEnabled(true);
            }
        } else if (command.equals("resetPeak")) {
            mx.resetPeakThreadCount();
        }
    }


    out.println("Live thread count: " + mx.getThreadCount() + "(of which " + mx.getDaemonThreadCount() + " are daemons)<br>");
    out.println("Peak thread count: " + mx.getPeakThreadCount() + "(<a href='?command=resetPeak'>reset</a>)<br>");
    out.println("Total started thread count: " + mx.getTotalStartedThreadCount() + "<br>");
    out.println("Contention monitoring enabled: " + 
    mx.isThreadContentionMonitoringEnabled() + "(" + (mx.isThreadContentionMonitoringSupported()?
    ("<a href='?command=toggleCM'>toggle</a> - switch off then on again to reset thread timings")
    :"not supported") + ") <br>"    
    );

    out.println("\tThread CPU monitoring enabled: " + mx.isThreadCpuTimeEnabled() + "(" + (mx.isThreadCpuTimeSupported()?"":"not ") + "supported)" +  "<br>");
    
    
    out.println("<pre>");
    
    out.println("Report generated at " + new java.util.Date());
    
    long [] deadLockedIds = mx.findMonitorDeadlockedThreads();
    ThreadInfo [] infos;
    if (deadLockedIds!=null) {
        infos = mx.getThreadInfo(deadLockedIds, Integer.MAX_VALUE);
        out.println("\nDead locks:");
        for (int i=0;i<infos.length;i++) {
            out.println(toStringThreadInfo(mx, infos[i]));
        }
    }
    infos = mx.getThreadInfo(mx.getAllThreadIds(), Integer.MAX_VALUE);  
    
    Map blockers = new HashMap();
   
    
    StringBuffer buf = new StringBuffer();
    
    buf.append("\nAll threads:\n");
    
    for (int i=0;i<infos.length;i++) {
        buf.append(toStringThreadInfo(mx, infos[i])).append("\n");
        String lockOwner = infos[i].getLockOwnerName();
        if (lockOwner!=null) {
            List blockees = (List)blockers.get(lockOwner);
            if (blockees == null) {
                blockees = new ArrayList();
                blockers.put(lockOwner, blockees);
            }
            blockees.add(infos[i].getThreadName());
        }
        
    }
    
    if (blockers.size()>0) {
        out.println("\nBlockers:");
        Iterator i = blockers.keySet().iterator();
        while (i.hasNext()) {
            String blocker = (String)i.next();
            out.println("\t" + blocker + " is blocking:");
            Iterator j = ((ArrayList)blockers.get(blocker)).iterator();
            while (j.hasNext()) {
                out.println("\t\t" + j.next());
            }
        }
    }
    
    out.println(buf.toString());
    
    out.println("</pre>");
%>
</body></html>
    