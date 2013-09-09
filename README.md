tonjak
======

Welcome to Tonjak, supporting derivation and exposure of both performance and health monitoring, typically for use within back-end services.

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
* KPI
  * Annotation-driven collection of key performance indicators on public methods
  * API to enable custom integration of KPIs
  * Distinction between successful and unsuccessful transactions.
  * Exposure of statistics via JMX for ease of integration with tools such as OpenTSDB.
  * Integration with StatsE for streaming to Heka
* Web app overlay
  * Provides web interface for viewing detailed health status
  * Provides batch interface for querying JMX beans over HTTP (used by Nagios plugin)

Licensing
---------

The framework is covered by "The BSD 3-Clause License":

```
Copyright (c) 2013, The Sporting Exchange Limited
All rights reserved.
 
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 
    Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
    Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
    Neither the name of The Sporting Exchange Limited, Betfair Limited nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.
 
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
```
