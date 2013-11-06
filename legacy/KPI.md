---
layout: default
---
{toc}

## Overview

The KPI framework is a library and set of utility packages which enable measurement of KPI (Key Performance Indicator)
statistics within an application.

In principle, **KPI Monitors** are notified of specific events. The monitors aggregate this information, waiting for
separate KPI Publishers to periodically retrieve the aggregated statistics, and publish them to external sources (currently,
on-disk log files or JMX).

The majority of the application should typically only care about the KPIMonitor interface. The publishing is 'housekeeping',
usually taken care of in the background by scheduled jobs.

At present, the KPI framework supports two monitors. The first, and simplest, is the KPIEvent. An event maintains a
simple hit count. A KPITimedEvent is more powerful. It takes timing information (length of time a method took to execute,
for example), and maintains aggregate information such as min, max, average, standard deviation etc.

The design is based on the original implementation in the Games (Casino/Gamex) codebase.

## Using KPIs

KPIs can be logged in one of two ways:
# call event methods of a KPIMonitor instance directly
# use annotations

### 1. Using KPIMonitor

The KPIMonitor interface provides standard ```addEvent()``` methods.
* each method requires a KPI name, which uniquely identifies the 'KPI' being measured.
* each method can optionally provide an operation name (defaults to the method name). This will form the basis of the
operation tag when used with StatsE and TSDB.
* a duration will be stored in a 'timed' KPI monitor. If no duration is specified, a simple 'hit count' monitor is used.
* the ```succeeded``` parameter is a convenient way to keep track of both successes and failures against a given KPI
(for cases where you might be interested in keeping errors separate from successful invocations of a method, say).

### 2. Annotations

Methods can be annotated using Spring-based AOP. Examples:

        // maintain a hit count
        @KPIEvent(value="my.kpi.name")
        public void foo() {
            ...
        }
        // maintain a hit count, treating thrown exceptions from the method as failures
        @KPIEvent(value="my.kpi.name2", catchFailures = true)
        public void bar() {
            ...
        }
        // measure durations, keeping track of failures
        @KPITimedEvent(value="my.kpi.name3", catchFailures=true)
        public void baz() {
            ...
        }
        // measure durations, keeping track of failures, specify operation name
        @KPITimedEvent(value="my.kpi", operation="name4", catchFailures=true)
        public void fred() {
            ...
        }

## Types of KPI

At present there are 5 implementations of the KPIMonitor interface (2 of which are deprecated), each pertaining to a
particular use case:

* [SimpleKPIMonitor](SimpleKPIMonitor.html) - Exports via JMX call counts, failure counts and the mean call duration for
the last 20 calls.
* [StatsEMonitor](StatsEMonitor.html) - Emits KPI events as **StatsE** events to the local agent.
* [KPIRepeater](KPIRepeater.html) - Allows sending KPI events to multiple monitors.

We recommend running a KPIRepeater into SimpleKPIMonitor and StatsEMonitor.

## Gotchas and notes

### Time units

The unit for duration is not specified or enforced, but using milliseconds is recommended - nanoseconds are far more
likely to result in overflow in some of the implementations (primary culprit being a sum of squares stored internally
to facilitate calculation of stddev)

Note though, that the KPI timer (and the annotations) measure time in nanos, and convert to millis before storing the
event.

## Web integration

### 1. Specify pom.xml dependency for KPIFilter in xyz-web project

If you don't want to use the KPIFilter, which is a servlet filter which can take KPI measurements of incoming web
requests, then you don't need this, or the next section.

Add dependency to ```pom.xml```

```
<dependency>
    <groupId>com.betfair.monitoring</groupId>
    <artifactId>kpi-web</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

(Explanation: kpi-web is just the filter, has servlet dependencies etc, so is specified as a dependency in the web project only)

### 2. Configure KPIFilter in web.xml in xyz-web project

(If you didn't do 1, you don't need this)

Specify filter in web.xml (heed the comment)

```
<!-- top of the chain to ensure we measure the whole lot -->
    <filter>
        <filter-name>kpiFilter</filter-name>
        <filter-class>org.springframework.web.filter.DelegatingFilterProxy</filter-class>
        <init-param>
            <param-name>/service/Xyz</param-name>
            <param-value>Incoming.Xyz</param-value>
        </init-param>
        <init-param>
            <!-- tell Spring to call filter's init() -->
            <param-name>targetFilterLifecycle</param-name>
            <param-value>true</param-value>
        </init-param>
    </filter>
    <filter-mapping>
        <filter-name>kpiFilter</filter-name>
        <url-pattern>/*</url-pattern>
    </filter-mapping>
```

(Explanation: KPIFilter is at the top of the filter list, to make sure we're measuring everything. By default we catch
all incoming requests, but if you felt strongly that that was wasteful, you could use a more restrictive URL pattern.)
