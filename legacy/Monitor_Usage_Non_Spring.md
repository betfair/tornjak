---
---
# Monitor definition

Without spring, you will need to construct your instances of ```Monitor``` yourself at application initialisation, and then register them with an instance of ```MonitorRegistry```.

Once registered, you can use the ```getMonitor(String)``` method on monitor registry to obtain monitors when using in your code.

You will also need to manually register your monitors in JMX.

See [Monitor Configuration 2.8] for more details about the monitor configuration.

# Monitor usage

The programmer has to make an explicit call to obtain a monitor and make the relevant invocation e.g:
```
registry.getInstance("DB").failure(someException);
```

# Active Monitoring

See the javadoc for [[ActiveMonitoringService|http://kpidw.dev.ham.uk.betfair/project_docs/monitor/2.1.0/monitor/apidocs/com/sportex/betex/monitor/active/ActiveMonitorService.html]]

# Monitor querying

The web ping capability is provided by the [[StatusStringifier|http://kpidw.dev.ham.uk.betfair/project_docs/monitor/2.1.0/monitor/apidocs/com/sportex/betex/monitor/StatusStringifier.html]] class which can be used in any servlet which is monitor aware (e.g. if it has a ```MonitorRegistry```).