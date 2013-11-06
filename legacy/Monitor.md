---
layout: default
---
# Overview

The monitor library provides an in-application framework for deriving application status. It does this by representing sub-components and dependencies and providing a mechanism to update the status of those via events. It provides a mechanism for aggregating these individual statuses to provide a headline status for an application.

The library provides beans for exposing this status as a webping, and also for driving LUS entry lodgement.

# Usage


## Spring

Spring integration requires some basic setup, plus [[instrumentation|Monitor Usage Spring 2.8]] of your application code.

## Non-Spring

If you're not using Spring, then you will interact with the monitor framework [[programatically|Monitor Usage Non Spring 2.8]].

# Related Libraries

Aside from the core monitoring framework, there are a number of related libraries that can be used with the framework:
* [Monitoring Overlay] \- provides jmx exposure
* [Nagios Scripts] \- for adapting between the overlay and Nagios

# Internals


## Monitor

The heart of the framework we have the _Monitor_ interface, with current implementations:
* _DefaultMonitor_ \- default implementation of the ActiveMethodMonitor interface with an associated active check. DefaultMonitor starts in a FAIL status.
* _ServiceMethodMonitor_ \- A convenience monitor to use on 'business' operation methods. These are methods which third party client may call which we would like to be exposed in the  diagnostics pages when they fail. However we do not want these failures to cause the service to be marked as unavailable. But we do want fast access to failing calls and the exception cause. Being a passive-only monitor implementation, this type of monitor has no active checks with it and only contributes up to WARN to the overall status. It also starts in an OK status.
* _FreeSpaceMonitor_ \- Given a Directory name and number of bytes, confirm that the given number of bytes is available for use on in the directory.
* _OnDemandCheck_ \- On demand monitor which delegates down to a _Check_ from the [[active monitoring|Active Monitoring 2.8]] component.
Additionally there is an abstract implementation to support ease of writing new concrete implementations:
* _OnDemandMonitor_ \- An implementation of Monitor which calculates Status on demand.
* _InOutServiceMonitor_ \- Monitor that allows JMX control of whether to put an application in or out of service. Check out [[Service Control|Monitor Service Control 2.8]] for more info.
* _FreeMemoryMonitor_  \- Given a required percentage of free memory,  check that that much memory is available.

Instances of MethodMonitor provide the ability to record successful and failed method invocations, optionally with an associated stack trace.

Each Monitor can be in one of three states:
* OK: No errors.
* WARN: One error in the last 30 seconds (this is configurable).
* FAIL: 2 or more errors

## Monitor Registry & Status Aggregator

The status a group of monitors can be aggregated to derive overall status, for such purposes as webping pages. Groups of Monitors are represented by a ```MonitorRegistry```. Each ```MonitorRegistry``` may have a ```StatusAggregator``` for aggregating statuses.

## Active / Passive

The framework implements an active-passive monitoring model. See [[Active-Passive Monitoring (Monitor 1.3)|Active-Passive Monitoring (Monitor 1.3)]] for more details:
* Passive Mode will be used when the monitor is in OK/WARN state.
* Active Mode will be used when the monitor is in FAIL state. Note that, as the monitors initial state is FAIL, the first thing the monitoring framework will do is to check each monitor. See [Active Monitoring 2.8] for more details.

## Events

The framework is event-driven, meaning you can drive additional functionality off changes in monitoring state. The following events currently exist
* ```Monitor```:
**** Change of status
** ```StatusAggregator```\**\* Change of status
* ```MonitorRegistry```:
**** Addition of a ```Monitor```
**** Removal of a ```Monitor```
**** Change of ```StatusAggregator```
