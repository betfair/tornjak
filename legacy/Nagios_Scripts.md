---
---
## Overview

Traditional monitoring in Betfair takes the form of IS Linux writing Nagios configuration to read a status from a Webping interface. This basic monitoring tends to ignore the need to see more detailed statuses for dependencies and information about those dependencies that can be used in diagnostic scenarios.

The aim of development providing the scripts is that we can expose more detailed information on specific monitoring interfaces and translate that into a format recognized by monitoring tools.

The scripts are simple shell scripts, written for bash.

## Compatibility

Each version of the Nagios scripts rely on the same version of the [[Monitoring Overlay] & [Monitor] libraries, and a specific version of [LUS Wrapper|LUS:lus-wrapper]], to provide the interface on the application for the scripts to glean the application status from.

Version 2.1 of the Nagios scripts depend on version version 1.1.0 of [[LUS Wrapper|LUS:lus-wrapper]].

The scripts also require ```wget``` to be available in the path, they have been tested with ```GNU Wget 1.10.2``` although may work on other versions.

## Scripts

2 scripts are currently provided:
* check_service: Provides status information on the application itself
* check_service_dependency: Provides status information on dependencies of the application as well as location information (e.g. current url from the LUS)

The 2.1 release can be downloaded from our [[maven repository|http://eisenbau.dev.uk.betfair:8888/buildbeast/lib/com/betfair/monitoring/nagios-scripts/2.1/nagios-scripts-2.1.tar.gz]].

## Logic

The logic of the scripts is best summarized by a textual description of the unit tests run on them.
* [[Check Service|Nagios Scripts Check Service 2.8]]
* [[Check Service Dependency|Nagios Scripts Check Service Dependency 2.8]]

## Deployment Notes Template

A [[template release note|Nagios Scripts Template Deployment Note 2.8]] excerpt for notifying IS of the Nagios script settings for your application.