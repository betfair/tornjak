---
---
{toc}

## How do I use batchquery.jsp?

To query the properties of an object you need to know the [[Object Name|http://java.sun.com/j2se/1.5.0/docs/api/javax/management/ObjectName.html]].
Given the object name such as ```**Monitoring.Facade.Orbis:name=AccountService**``` you can use it like so: ```http://hostwithservice/servicename/admin/batchquery.jsp?on=Monitoring.Facade.Orbis:name=AccountService```

## What is that weird output produced by batchquery.jsp?

Right, for the previous example you will get something like:
```
Monitoring.Facade.Orbis:name=AccountService~Name~AccountService~LastFailureTime~0~LastSuccessTime~1221579907204~FailureCount~0~LastException~~StatusAsString~OK~ErrorCountingPolicy~com.betfair.games.orbisfacade.monitoring.AndErrorPolicy@4842c1b0
```
This is a '~' delimited property-value list. There is a tool which reads and dispalys this information in a user friendly way (it can do more that) called jmx-client.
It's source is in ```perforce/se/research/platform/utils/jmx-client``` and the people who seem to know something about are **Matthew Searle** and **Joe Stapleton**.

## As a developer, how do I make and test changes to the monitoring-overlay JSPs?

This small guide assumes Windows.  It'll be much the same on Linux except with different paths.

* Check out the 'Reference Information Service' head (p4://depot/se/development/HEAD/referenceinformation-service/)
* Change it's 'web' module's POM to depend upon the version of monitoring-overlay you are developing on (not sure whether this step is needed, but it was recommended to me by Simon)
* Do a 'mvn package' on the Reference Information Service
* Unpack the Reference Information Service deployment package (e.g. c:\perforce\se\development\HEAD\referenceinformation-service\dist\target\referenceinformation-deploy-1.1-SNAPSHOT-devel.zip) somewhere sensible (like C:\dev\referenceinformation-deploy-1.1-SNAPSHOT-devel)
* Do a 'mvn package' on monitoring-overlay
* Drop the monitoring-overlay WAR (c:\perforce\se\development\com\betfair\monitoring\trunk\monitoring-overlay\target\monitoring-overlay.war) into the Reference Information Service's Bootstrap Tomcat deployment directory (C:\dev\referenceinformation-deploy-1.1-SNAPSHOT-devel\base\tomcat\webapps)
* Change the Tomcat container configuration to define a security Realm and a user with the correct (jmxadmin) role (see [[Container Configuration|https://confluence.app.betfair/display/SCO/Monitoring<u>Overlay</u>Usage+2.8#MonitoringOverlayUsage2.2-Containerconfiguration]])
* Start the Reference Information Service's Bootstrapped Tomcat (open a command window in C:\dev\referenceinformation-deploy-1.1-SNAPSHOT-devel\bootstrap\bin and then execute 'startjava.bat local.env')
* Point browser at the required JSP, e.g. http://localhost:8080/monitoring-overlay/admin/humanquery.jsp
* When prompted for a username and password, enter 'jmxadmin' and 'password03'
* Now develop and test in-situ on the JSPs (which will be unpacked by Tomcat at C:\dev\referenceinformation-deploy-1.1-SNAPSHOT-devel\base\tomcat\webapps\monitoring-overlay\admin)
* When you're happy with your changes, copy them back to the monitoring-overlay project
* Stop Tomcat (just close it's DOS window)
* Delete the exploded WAR directory (C:\dev\referenceinformation-deploy-1.1-SNAPSHOT-devel\base\tomcat\webapps\monitoring-overlay) since re-deployment didn't seem to work correctly for me
* Again do a 'mvn package' on monitoring-overlay
* Again drop the monitoring-overlay WAR (c:\perforce\se\development\com\betfair\monitoring\trunk\monitoring-overlay\target\monitoring-overlay.war) into the Reference Information Service's Bootstrap Tomcat deployment directory (C:\dev\referenceinformation-deploy-1.1-SNAPSHOT-devel\base\tomcat\webapps)
* Start Reference Information Service's Bootstrapped Tomcat back up again
* Verify that your fix still works and if it does, check it in and update the JIRA item