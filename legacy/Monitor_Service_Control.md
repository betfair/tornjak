---
layout: default
---
## Overview

Given that the monitoring framework supports the exposure of application health to infrastructure such as load balancers, it's a short hop to considering the potential of the framework for human control of whether a service is in status or not.

## InOutServiceMonitor

This monitor provides the ability to manually move a service between OK and FAIL in order to control whether it's in service or not. Configuration is simple in spring:

```
<bean name="inOutServiceMonitor" class="com.sportex.betex.monitor.service.InOutServiceMonitor">
  <constructor-arg index="0" value="MyMonitorName"/>
  <constructor-arg index="1" value="/etc/bf-blah/service-monitor.status"/>
</bean>
```

The functionality of the monitor is simple:
* Default status is OK
* Status can be changed via the boolean JMX property ```inService```.
* When the status is changed via the JMX property, then it's written to the file specified in the constructor so it's persisted between restarts.
* If the file is not found, the status is the default (OK).

## Supporting AvB testing

Once you've got to grips with in/out of service control, then it's reasonably easy to build up more complex arrangements, such as enabling AvB testing:

{yuml:type=class|dir=td}
[PingController 1]->[OverallStatus 1]
[PingController 1]-[note: Exposed on /webpingA via your spring config]
[PingController 2]->[OverallStatus 2]
[PingController 2]-[note: Exposed on /webpingB via your spring config]
[OverallStatus 1]->[MonitorRegistry 1]
[OverallStatus 2]->[MonitorRegistry 2]
[MonitorRegistry 1]->[InOutServiceMonitor 1]
[MonitorRegistry 2]->[InOutServiceMonitor 2]
[MonitorRegistry 1]->[SharedMonitor 1]
[MonitorRegistry 1]->[SharedMonitor 2]
[MonitorRegistry 1]->[SharedMonitor 3]
[MonitorRegistry 2]->[SharedMonitor 1]
[MonitorRegistry 2]->[SharedMonitor 2]
[MonitorRegistry 2]->[SharedMonitor 3]
{yuml}


Or even AvB testing with a legacy webping for other traffic:

{yuml:type=class}
[PingController 1]->[OverallStatus 1]
[PingController 1]-[note: Exposed on /webpingA via your spring config]
[PingController 2]->[OverallStatus 2]
[PingController 2]-[note: Exposed on /webpingB via your spring config]
[PingController 3]->[OverallStatus 3]
[PingController 3]-[note: Exposed on /webping via your spring config]
[OverallStatus 1]->[MonitorRegistry 1]
[OverallStatus 2]->[MonitorRegistry 2]
[OverallStatus 3]->[MonitorRegistry 3]
[MonitorRegistry 1]->[InOutServiceMonitor 1]
[MonitorRegistry 2]->[InOutServiceMonitor 2]
[MonitorRegistry 1]->[SharedMonitor 1]
[MonitorRegistry 1]->[SharedMonitor 2]
[MonitorRegistry 1]->[SharedMonitor 3]
[MonitorRegistry 2]->[SharedMonitor 1]
[MonitorRegistry 2]->[SharedMonitor 2]
[MonitorRegistry 2]->[SharedMonitor 3]
[MonitorRegistry 3]->[SharedMonitor 1]
[MonitorRegistry 3]->[SharedMonitor 2]
[MonitorRegistry 3]->[SharedMonitor 3]
{yuml}

