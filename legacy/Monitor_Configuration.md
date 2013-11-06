---
layout: default
---
### ErrorCountingPolicy

By default each monitor uses the [[DefaultErrorPolicy|http://kpidw.dev.ham.uk.betfair/project_docs/monitor/2.0/monitor/apidocs/com/sportex/betex/monitor/aop/DefaultErrorCountingPolicy.html] to know when an exception or return result shoud be counted as a failure. You can configure the monitor with your own [ErrorCountingPolicy|http://kpidw.dev.ham.uk.betfair/project_docs/monitor/2.0/monitor/apidocs/com/sportex/betex/monitor/ErrorCountingPolicy.html]]

### MaxImpactToOverallStatus

By default all monitors are critical for the application, which means that a failure in any of them will put the whole
application in a FAIL state, which means that the Netscalers will take it off the list of available servers.
If a monitor is not critical, and your application can still work without it, you can configure the MaxImpactToOverallStatus to WARN or OK.

Note that the decision for the MaxImpactToOverallStatus value should be considered from the Netscalers point of view.

### ActiveMonitor

Property to set the active monitor agent. See [Active Monitoring 2.8]


## WarnWindow

Sets the period of time that a solitary failure event is considered to put a ```Monitor``` into the WARN state.

## Monitoring Coherence caches

If you want to be able to monitor Coherence caches (for hit/miss information and etc.), your application should be started with the following system property:

```
    tangosol.coherence.management=all
```

For examples please look into Cougar's baseline-launch module and BootstrapTomcat's archetype (please not that default application based on BST archetype does not use Coherence and hence JMX components will not appear unless application utilises Coherence directly).
For more info please look into Coherence Documentation [http://wiki.tangosol.com/display/COH33UG/Managing<u>Coherence</u>using+JMX]
