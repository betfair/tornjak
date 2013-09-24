<%@ page import="com.betfair.tornjak.monitor.overlay.JMXUtils" %>
<%@ page import="com.betfair.tornjak.monitor.overlay.MBeanInstance" %>
<%@ page import="com.betfair.tornjak.monitor.overlay.DiagnosticUtils" %>
<%@ page import="com.betfair.tornjak.monitor.overlay.SubSystem" %>
<%@ page import="com.betfair.tornjak.monitor.overlay.Auth" %>
<%@ page import="com.betfair.tornjak.monitor.overlay.AuthUtils" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Collections" %>
<%@ page import="java.security.Principal" %>
<%@ page contentType="text/html; charset=utf-8"%>
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
<html>

<head>

<style type="text/css">

body{
	font-family: Trebuchet MS, Lucida Sans Unicode, Arial, sans-serif;
	margin:0;
    font-size: smaller;

}

table {
    border:solid #000000;
    border-width: 1px 1px 0px 0px;
    padding: 0px;
    border-spacing: 0;
}
th {
    border:solid #000000;
    border-width: 0px 0px 1px 1px;
    padding: 2px;
}
td {
    border:solid #000000;
    border-width: 0px 0px 1px 1px;
    padding: 2px;
}

.dhtmlgoodies_question{	/* Styling question */
	/* Start layout CSS */
	color:#000000;
	font-size:0.9em;
    text-decoration:underline;
	background-color:#ffffff;
	width:100%;

	/* End layout CSS */

	overflow:hidden;
	cursor:pointer;
}
.dhtmlgoodies_answer{	/* Parent box of slide down content */
	/* Start layout CSS */
	background-color:#d8d8d8;
	width:100%;

	/* End layout CSS */

	visibility:hidden;
	height:0;
	overflow:hidden;
	position:relative;

}
.dhtmlgoodies_answer_content{	/* Content that is slid down */
	padding:1px;
	font-size:0.9em;
	position:relative;
}

</style>
<script type="text/javascript">
<!--
/************************************************************************************************************
(C) www.dhtmlgoodies.com, November 2005

This is a script from www.dhtmlgoodies.com. You will find this and a lot of other scripts at our website.

Terms of use:
You are free to use this script as long as the copyright message is kept intact. However, you may not
redistribute, sell or repost it without our permission.

Thank you!

www.dhtmlgoodies.com
Alf Magne Kalleland

************************************************************************************************************/

var dhtmlgoodies_slideSpeed = 100;	// Higher value = faster
var dhtmlgoodies_timer = 5;	// Lower value = faster

var objectIdToSlideDown = false;
var dhtmlgoodies_activeId = false;
var dhtmlgoodies_slideInProgress = false;
function showHideContent(e,inputId)
{
	if(dhtmlgoodies_slideInProgress)return;
	dhtmlgoodies_slideInProgress = true;
	if(!inputId)inputId = this.id;
	inputId = inputId + '';
	var numericId = inputId.replace(/[^0-9]/g,'');
	var answerDiv = document.getElementById('dhtmlgoodies_a' + numericId);

	objectIdToSlideDown = false;

	if(!answerDiv.style.display || answerDiv.style.display=='none'){
		if(dhtmlgoodies_activeId &&  dhtmlgoodies_activeId!=numericId){
			objectIdToSlideDown = numericId;
			slideContent(dhtmlgoodies_activeId,(dhtmlgoodies_slideSpeed*-1));
		}else{

			answerDiv.style.display='block';
			answerDiv.style.visibility = 'visible';

			slideContent(numericId,dhtmlgoodies_slideSpeed);
		}
	}else{
		slideContent(numericId,(dhtmlgoodies_slideSpeed*-1));
		dhtmlgoodies_activeId = false;
	}
}

function slideContent(inputId,direction)
{

	var obj =document.getElementById('dhtmlgoodies_a' + inputId);
	var contentObj = document.getElementById('dhtmlgoodies_ac' + inputId);
	height = obj.clientHeight;
	if(height==0)height = obj.offsetHeight;
	height = height + direction;
	rerunFunction = true;
	if(height>contentObj.offsetHeight){
		height = contentObj.offsetHeight;
		rerunFunction = false;
	}
	if(height<=1){
		height = 1;
		rerunFunction = false;
	}

	obj.style.height = height + 'px';
	var topPos = height - contentObj.offsetHeight;
	if(topPos>0)topPos=0;
	contentObj.style.top = topPos + 'px';
	if(rerunFunction){
		setTimeout('slideContent(' + inputId + ',' + direction + ')',dhtmlgoodies_timer);
	}else{
		if(height<=1){
			obj.style.display='none';
			if(objectIdToSlideDown && objectIdToSlideDown!=inputId){
				document.getElementById('dhtmlgoodies_a' + objectIdToSlideDown).style.display='block';
				document.getElementById('dhtmlgoodies_a' + objectIdToSlideDown).style.visibility='visible';
				slideContent(objectIdToSlideDown,dhtmlgoodies_slideSpeed);
			}else{
				dhtmlgoodies_slideInProgress = false;
			}
		}else{
			dhtmlgoodies_activeId = inputId;
			dhtmlgoodies_slideInProgress = false;
		}
	}
}



function initShowHideDivs()
{
	var divs = document.getElementsByTagName('DIV');
	var divCounter = 1;
	for(var no=0;no<divs.length;no++){
		if(divs[no].className=='dhtmlgoodies_question'){
			divs[no].onclick = showHideContent;
			divs[no].id = 'dhtmlgoodies_q'+divCounter;
			var answer = divs[no].nextSibling;
			while(answer && answer.tagName!='DIV'){
				answer = answer.nextSibling;
			}
			answer.id = 'dhtmlgoodies_a'+divCounter;
			contentDiv = answer.getElementsByTagName('DIV')[0];
			contentDiv.style.top = 0 - contentDiv.offsetHeight + 'px';
			contentDiv.className='dhtmlgoodies_answer_content';
			contentDiv.id = 'dhtmlgoodies_ac' + divCounter;
			answer.style.display='none';
			answer.style.height='1px';
			divCounter++;
		}
	}
}
window.onload = initShowHideDivs;
// -->
</script>


</head>

<body style="font-size: smaller;">

  <h2>Overall status</h2>
<%
    MBeanInstance overall = DiagnosticUtils.getOverallStatusBean();
    String overallStatus = overall != null ? (String) JMXUtils.getAttributeValue(overall, "StatusAsString") : "UNKNOWN";
%>
  <table height="30" bgcolor="<%=DiagnosticUtils.colourForStatus(overallStatus)%>">
    <tr>
      <td><%=overallStatus%></td>
    </tr>
  </table>

  <h2>Dependencies</h2>
  <table width="100%">
    <tr>
      <th>Name</th>
      <th>Status</th>
      <th>Last Success</th>
      <th>Failure Count</th>
      <th>Last Failure</th>
      <!-- last exception -->
      <th>Current URL</th>
    </tr>
<%
    Map<String, SubSystem> subsystems = DiagnosticUtils.getSubSystems();
    List<String> dependencies = new ArrayList<String>(subsystems.keySet());
    Collections.sort(dependencies);

    for (String dependency : dependencies) {
        MBeanInstance monitorBean = subsystems.get(dependency).getMonitorInfo();
        String depStatus = (String) JMXUtils.getAttributeValue(monitorBean, "StatusAsString");
        String monitoredUrl = (String) JMXUtils.getAttributeValue(monitorBean, "MonitoredUrl");
%>
    <tr>
      <td><%=JMXUtils.getAttributeValue(monitorBean, "Name")%></td>
      <td bgcolor="<%=DiagnosticUtils.colourForStatus(depStatus)%>"><%=depStatus%></td>
      <td><%=DiagnosticUtils.timeSince((Long) JMXUtils.getAttributeValue(monitorBean, "LastSuccessTime"))%></td>
      <td><%=JMXUtils.getAttributeValue(monitorBean, "FailureCount")%></td>
      <td><%=DiagnosticUtils.timeSince((Long) JMXUtils.getAttributeValue(monitorBean, "LastFailureTime"))%></td>
<%
        if (monitoredUrl != null) {
%>
        <td><%=monitoredUrl%></td>
<%
        } else {
%>
      <td bgcolor="#aaaaaa">N/A</td>
<%
        }
%>
    </tr>
    <tr>
      <td colspan="6">
<div class="dhtmlgoodies_question">Show latest exception</div>
<div class="dhtmlgoodies_answer">
	<div>
<pre>
<%=JMXUtils.getAttributeValue(monitorBean, "LastException")%>
</pre>
	</div>
</div>
      </td>
    </tr>
<%
    }

%>
  </table>
</body>
</html>