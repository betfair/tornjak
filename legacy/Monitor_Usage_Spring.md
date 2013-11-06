---
layout: default
---
# Monitor definition

The monitors are defined as Spring beans allowing Spring to be the monitor factory. The monitors are aggregated in
a MonitorRegistry which is a Spring bean as well. Here is an example Spring config snippet that does that:

```
<bean id="monitorDB" class="com.sportex.betex.monitor.DefaultMonitor">
    <property name="name" value="DB"/>
</bean>
<bean id="monitorSomethingWithErrorCountingPolicy" class="com.sportex.betex.monitor.DefaultMonitor">
    <property name="errorCountingPolicy" ref="someErrorPolicy"/>
    <property name="name" value="Something"/>
</bean>
<!-- The monitor registry -->
<bean id="monitorRegistry" class="com.sportex.betex.monitor.DefaultMonitorRegistry">
    <property name="statusAggregator" ref="overallStatus"/>
</bean>
<!-- Status aggregator - required for webping, nagios & lus publishing -->
<bean name="overallStatus" class="com.sportex.betex.monitor.OverallStatus">
    <property name="monitorRegistry" ref="monitorRegistry"/>
</bean>
<!-- Register the monitors with the registry -->
<bean class="com.sportex.betex.monitor.util.MonitorRegistration" lazy-init="false">
    <property name="registry" ref="monitorRegistry"/>
    <property name="monitors">
        <list>
            <ref bean="monitorDB"/>
            <ref bean="monitorSomethingWithErrorCountingPolicy"/>
        </list>
    </property>
</bean>
```

See [Monitor Configuration](Monitor_Configuration.html) for more details about the monitor configuration.

# Monitor usage

## With annotations

To have a method invocation be tracked by a monitor a developer needs to decorate the method with the @MonitorMethod
annotation, for example:

```
@MonitorMethod ( monitorName = "monitorDB" )
public void aDbCall() {
    ...
}
```
**Note** that the monitorName is the value of the "name" attribute and not the name of the spring bean.

The annotated methods are recognised by the Spring provided AOP machinery. For this to work we need a definition such as
the following:

```xml
<!-- <u></u>== Associate the monitor registrey with the AOP logic <u></u>== -->
<bean id="monitorAOP" class="com.sportex.betex.monitor.aop.MonitorAOP">
    <property name="monitorRegistry" ref="monitorRegistry"/>
</bean>
<!-- <u></u>== Define the pointcut on @MonitorMethod and associate with th AOP logic <u></u>== -->
<aop:config proxy-target-class="true">
    <aop:pointcut id="componentMonitor" expression="@annotation(com.sportex.betex.monitor.aop.MonitorMethod)"/>
    <aop:aspect id="monitoring" ref="monitorAOP">
        <aop:around pointcut-ref="componentMonitor" method="monitorMethod"/>
    </aop:aspect>
</aop:config>
```

## Without annotations

If it is difficult to annotate the classes to be monitored (generated code for example), or you want to use a different
pointcut expression, you can use the ```FixedMonitorAOP```. This class will always use the same monitor.

This is particularly useful to monitor CXF generated clients. Note that proxy-target-class must be false for this
approach to work with CXF.

Note that you will need one pointcut/FixedMonitorAOP/Aspect per monitor, as this approach doesn't have the flexibility
of the annotation parameters.

```xml
<bean id="fixedMonitorAOP" class="com.sportex.betex.monitor.aop.FixedMonitorAOP">
    <property name="monitor" ref="monitorDB"/>
</bean>
<!-- <u></u>== Define the pointcut on an interface (for example) and associate with th AOP logic <u></u>== -->
<aop:config proxy-target-class="false">
    <aop:pointcut id="componentMonitor" expression="@this(some.interface.that.you.want.to.monitor)"/>
    <aop:aspect id="monitoring" ref="fixedMonitorAOP">
        <aop:around pointcut-ref="componentMonitor" method="monitorMethod"/>
    </aop:aspect>
</aop:config>
```

## Programmatically

It is also possible to use the monitor beans directly. You just need to inject the monitor in the class that wants to
use it and then call the monitor.success() or monitor.failure() from the code.

**Note that in this case the ErrorCountingPolicy will not be used**. Of course you can use it programmatically if you want.

# Active Monitoring

The implementation of the active monitoring mode of the framework is **ActiveMonitoringService**. See also
[Active Monitoring](Active_Monitoring.html) for further detail

The appropiate initialization for the class is:

```xml
<bean class="com.sportex.betex.monitor.active.ActiveMonitorService" init-method="start" destroy-method="stop"
          lazy-init="false">
        <property name="monitorRegistry" ref="monitorRegistry"/>
</bean>
```

# Monitor querying

## Webping

The webping page will be used by Netscaler and other services to check the health status of the application and make a
decision about using the application or not.

The contract is very simple: An application should return "```Server OK```" or "```Server FAIL```". The framework provides
a Spring MVC implementation in **PingController**.

You can create a PingController with:

```xml
<bean name="/webping" class="com.sportex.betex.monitor.web.PingController">
    <property name="monitorRegistry" ref="monitorRegistry" />
</bean>
```

You also need to add the mapping in the web.xml like:

```xml
<servlet>
        <servlet-name>webping</servlet-name>
        <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
        <init-param>
            <param-name>contextConfigLocation</param-name>
            <param-value>/WEB-INF/config/webping-servlet.xml</param-value>
        </init-param>
        <load-on-startup>1</load-on-startup>
</servlet>
   ...
<servlet-mapping>
        <servlet-name>webping</servlet-name>
        <url-pattern>/webping</url-pattern>
</servlet-mapping>
```

## JMX

This is the main interface to provide information for the monitoring tools.

### Monitors

To expose all the monitors through JMX we recommend using the spring tag from the jmx-util project of the service commons
stack. See for more details.

```xml
<jmx:jmxExport jmxDomain="YourApplicationJmxDomain" jmxInterface="com.sportex.betex.monitor.DefaultMonitorMBean"/>
```

### OverallStatus

If you want to expose the overall status of the application through JMX you can publish your registry's **StatusAggregator**
in your spring context in the same manner. For example, for the provided ```OverallStatus``` aggregator:

```xml
<jmx:jmxExport jmxDomain="YourApplicationJmxDomain" jmxInterface="com.sportex.betex.monitor.OverallStatusMBean"/>
```

Note that a ```StatusAggregator``` can return a **WARN** status depending on the MaxImpactToOverallStatus of the monitors.

# Putting it all together

Assuming you've used the AOP approach covered above, here's a complete Spring context xml file:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:aop="http://www.springframework.org/schema/aop"
       xmlns:jmx="http://www.betfair.com/schema/service-commons/jmx"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
                           http://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop.xsd">
    <bean name="monitorRegistry" class="com.sportex.betex.monitor.DefaultMonitorRegistry">
        <property name="statusAggregator" ref="overallStatus"/>
    </bean>
    <bean name="overallStatus" class="com.sportex.betex.monitor.OverallStatus">
        <property name="monitorRegistry" ref="monitorRegistry"/>
    </bean>
    <bean name="dbMonitor" class="com.sportex.betex.monitor.DefaultMonitor">
        <property name="name" value="DB"/>
        <property name="errorCountingPolicy">
            <bean class="com.sportex.betex.monitor.aop.DefaultErrorCountingPolicy"/>
        </property>
        <property name="activeMonitor">
            <bean class="com.sportex.betex.monitor.active.db.Database">
                <constructor-arg ref="datasource"/>
            </bean>
        </property>
    </bean>
    <bean class="com.sportex.betex.monitor.util.MonitorRegistration">
        <property name="registry" ref="monitorRegistry"/>
        <property name="monitors">
            <list>
                <ref bean="dbMonitor"/>
            </list>
        </property>
    </bean>
    <bean id="monitorAOP" class="com.sportex.betex.monitor.aop.MonitorAOP">
        <property name="monitorRegistry" ref="monitorRegistry" />
    </bean>
    <aop:config proxy-target-class="false">
        <aop:pointcut id="annotatedMonitor" expression="@annotation(com.sportex.betex.monitor.aop.MonitorMethod)"/>
        <aop:aspect id="monitoring" ref="monitorAOP">
            <aop:around pointcut-ref="annotatedMonitor" method="monitorMethod"/>
        </aop:aspect>
    </aop:config>
    <jmx:jmxExport jmxDomain="YourApplicationJmxDomain" jmxInterface="com.sportex.betex.monitor.DefaultMonitorMBean"/>
    <jmx:jmxExport jmxDomain="YourApplicationJmxDomain" jmxInterface="com.sportex.betex.monitor.OverallStatusMBean"/>
    <bean name="/webping" class="com.sportex.betex.monitor.web.PingController">
        <property name="monitorRegistry" ref="monitorRegistry" />
    </bean>
</beans>
```

```xml
...
<servlet>
        <servlet-name>webping</servlet-name>
        <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
        <init-param>
            <param-name>contextConfigLocation</param-name>
            <param-value>/WEB-INF/config/monitor.xml</param-value>
        </init-param>
        <load-on-startup>1</load-on-startup>
</servlet>
   ...
<servlet-mapping>
        <servlet-name>webping</servlet-name>
        <url-pattern>/webping</url-pattern>
</servlet-mapping>
...
```

```java
...
@MonitorMethod ( monitorName = "DB" )
public void aDbCall() {
    ...
}
```

