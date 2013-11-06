---
---

<table style='background-color: #FFFFCE;'>
         <tr>
           <td valign='top'><img src='attachments/warning.gif' width='16' height='16' align='absmiddle' alt='' border='0'></td>
           <td><p>This implementation is now deprecated in favour of [[simple|SimpleKPIMonitor 2.8] and [StatsE|StatsEMonitor 2.8]] kpi monitors</p></td>
          </tr>
</table>



## Features

* Supports logging of kpis on a periodic basis (configurable - defaults to 1 minute) to disk for later analysis
* Supports optional locking of all kpi recording whilst slicing through the measurements for logging
* Delineation between successful and unsuccessful calls is achieved by creating two KPI monitors, by adding an 'OK' or 'Failed' suffix to the given KPI name.
* Exposes latest KPIs over JMX as a JSON string
* Exposes 5 most recent KPIs over JMX as a JSON string

## Configuration

In order to configure KPI functionality, you need to do the following:

### 1. Specify pom.xml dependency in xyz-services project

In ```pom.xml```

```
<dependency>
            <groupId>com.betfair.tornjaj</groupId>
            <artifactId>kpi</artifactId>
            <version>3.0-SNAPSHOT</version>
        </dependency>
```

(Explanation: kpi-base has the main functionality, kpi-quartzpublisher rigs up the publisher quartz jobs. You would get kpi-base if you just imported kpi-quartzpublisher, but my preference is to explicitly import kpi-base because it's more explicit about where most of the magic is.)

### 2. Import kpi Spring config into the xyz-service project

In your app's ```xyz-service.xml``` Spring config, add the line:
```
<import resource="classpath:kpi-all.xml"/>
```
if you are proxying interfaces or
```
<import resource="classpath:kpi-all-proxy-target-class.xml" />
```
if you are proxying classes as per section 6.6 in [[Spring AOP|http://static.springframework.org/spring/docs/2.5.x/reference/aop.html]].

(The kpi projects have lots of Spring config files which wire up defaults. You could get fancy and selectively import these, and/or replace some of them, but you don't need to. Importing ```kpi-all.xml``` will import all configs.)

Note that this config does **not** set up logging output details. For our services, that's done as part of the web project - see next sections.

### 3. Set up kpi log files in xyz-web project

KPIs are logged to a logger named ```com.betfair.KPI```. You are free to configure your logging as you choose, but common practice is to store kpi logs in a ```/kpi``` subdirectory in your main logging directory.

If you're using [Cougar] or the service-commons [BST:Service Archetype], then logging is already configured for you.

#### 3.1 Setting up KPI logging using the [Log4j Extended] project.

You only need to do this if you're not using Cougar or a project based on the Service Architype, AND you're using the [Log4j Extended] project. You are also free to steal the log4j configuration from ```kpi-base/src/main/resources/kpi-publisher-log4j.xml```.

In web.xml, include kpi log4j config (assuming you're using our extended log4j configurator):

```
<context-param>
        <param-name>log4j.config.files</param-name>
        <param-value>
            classpath:xyz-service-log4j.xml,
            classpath:kpi-publisher-log4j.xml,                <-- this is the default kpi log4j config file
            classpath:x509access-publisher-log4j.xml
        </param-value>
    </context-param>
```

In xyz-service-logging.properties (usually in xyz-env project), specify the location of the log file:

```
kpi.log.file=${catalina.home}/logs/xyz-service-kpi.log
```

(and it goes without saying that you will have configured the extended log4 configurator to use the the xyz-service-logging.properties file when setting up log4j)

(Explanation: KPI by default relies on log4j to log to an output file. The kpi packages have a log4j configuration that takes care of the details, except for one thing: it needs to be told what the name of the logging file is. We use our extended log4j configuration to (a) use the log4j 'fragment' in the KPI packages, and (b) to inject the file name property.)

## Gotchas and notes

### 1. Locking

As of KPI 2.4, it's possible to disable the use of locks internally within the KPIMonitor when reading the set of values for a period. However, due to the way the spring configurations are setup, you would need to unpack the relevant spring config that you previously imported into your app's spring config and then change the import of _kpi-beans.xml_ to redefine the _kpiMonitor_ bean.

For example, if you previously had a spring config:

```
    <import resource="classpath:kpi-all.xml"/>
```

You would now need:

```
    <!-- previously loaded via kpi-beans.xml -->
    <bean id="kpiMonitor" class="com.betfair.monitoring.kpi.KPIMonitorImpl">
        <property name="useLocking" value="false"/>
    </bean>
    <import resource="classpath:kpi-annotations.xml"/>      <!-- annotation-based KPIs -->
    <import resource="classpath:kpi-jmx.xml"/>              <!-- publish most recent KPI stats via JMX -->
    <import resource="classpath:kpi-publisher-slf4j.xml"/>  <!-- log publisher -->
    <import resource="classpath:kpi-quartz-job.xml"/>       <!-- quartz job for publishing -->
    <import resource="classpath:kpi-quartz-trigger.xml"/>   <!-- default every-minute trigger -->
    <import resource="classpath:kpi-quartz-scheduler.xml"/> <!-- scheduler - not needed if quartz is used elsehwere -->
```

