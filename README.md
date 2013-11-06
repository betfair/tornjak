tornjak
======

Welcome to Tornjak, supporting derivation and exposure of both performance and health monitoring, typically for use within back-end services.

Features
--------

* Monitoring
	* Aggregated health derived from one or more component monitors.
	* Ability to restrict the impact on overall health contributed by individual monitors.
	* Standard monitors for common sub-components:
		* External services
		* Free memory
		* Free disk
	* Ability to control overall health via JMX (for upgrade situations).
	* Passive and active monitoring styles supported.
	* Listener hooks to enable triggering other logic from changes in health state.
	* Nagios plugin to expose overall and component health details.
* Key performance indicators
	* Annotation-driven collection of key performance indicators on public methods
	* API to enable custom integration of KPIs
	* Distinction between successful and unsuccessful transactions.
	* Exposure of statistics via JMX for ease of integration with tools such as OpenTSDB.
	* Integration with StatsE for streaming to Heka
* Web app overlay
	* Provides web interface for viewing detailed health status
	* Provides batch interface for querying JMX beans over HTTP (used by Nagios plugin)
* Nagios plugin
	* Plugin capabilities for reading overall health
	* Can also expose sub-component health status

Project Info
------------

[Public site here](http://betfair.github.io/tornjak)
	
[![Build Status](https://travis-ci.org/betfair/tornjak.png?branch=master)](https://travis-ci.org/betfair/tornjak)

Licensing
---------

Tornjak is covered by "[The Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.html)":

    Copyright 2013, The Sporting Exchange Limited
    
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
        http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
