<%@ page contentType="text/html; charset=utf-8"
         import="com.betfair.tornjak.monitor.overlay.HumanQuery"
		 import="com.betfair.tornjak.monitor.overlay.AuthUtils"
		 import="com.betfair.tornjak.monitor.overlay.Auth"
%>
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
    
    String [] onames = request.getParameterValues("on");
    if (onames == null || (onames.length == 1 && onames[0].trim().equals(""))) {
        onames = new String[0];
    }
    String label = "Object names:";
%>
<html>
<body>
<a name="page_top"/>
<form method="GET">
  <table border="0">
<%
    for (String s : onames) {
        if (s != "") {
        	// XSS defense
        	// See http://www.owasp.org/index.php/XSS_(Cross_Site_Scripting)_Prevention_Cheat_Sheet#RULE_.231_-_HTML_Escape_Before_Inserting_Untrusted_Data_into_HTML_Element_Content
        	s = s.replace('&', '?')
	    		.replace('<', '?')
	    		.replace('>', '?')
	    		.replace('"', '?')
	    		.replace('\'', '?')
	    		.replace('/', '?');
%>
    <tr>
      <td><%=label%></td>
      <td><input name="on" value="<%=s%>"/></td>
<%
            label = "";
        }
    }
%>
    <tr>
      <td><%=label%></td>
      <td><input name="on"/></td>
    </tr>
    <tr><td colspan='2'><input type="submit"/></td></tr>
  </table>
</form>
<hr>
<%
    if ((onames.length == 1 && onames[0] == null) || (onames.length==0)) {
        onames = new String[]{"*:*"};
    }
    if (onames!=null) {
        out.println(new HumanQuery(auth).query(onames));
    }

%>
</body>
</html>