---
layout: default
---

Welcome to Tornjak!
==================

Tornjak is a monitoring framework, originally developed by [Betfair](http://www.betfair.com) supporting derivation and exposure of both performance and health monitoring, typically for use within back-end services.

It is released under the [Apache Software Licence v2](http://www.apache.org/licenses).

Using Tornjak
------------

If you're new to Tornjak we suggest checking out the following 2 guides:

* [What is Tornjak?](tornjak-guide.html)
* [Tornjak in under 5 minutes](getting-started.html)

Alternatively, checkout our [documentation hub](documentation.html) for other guides and references.

Releases
--------

Tornjak has only recently been made an open source project after 5 years as a closed source project. Source code is available on GitHub, click [here](http://github.com/betfair/tornjak) or checkout the "Fork Me" banner.

Whilst we're cleaning up Tornjak (mostly in the documentation space), plus adding features we think are needed, Tornjak is available as a SNAPSHOT release (3.0-SNAPSHOT to be precise) in the Sonatype OSS Repository:

```
		<repository>
			<id>sonatype-nexus-snapshots</id>
			<name>Sonatype Nexus Snapshots</name>
			<url>https://oss.sonatype.org/content/repositories/snapshots</url>
			<releases>
				<enabled>false</enabled>
			</releases>
			<snapshots>
				<enabled>true</enabled>
			</snapshots>
		</repository>
```
