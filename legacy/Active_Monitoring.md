---
---
{toc}

Each monitor must be configured with an **ActiveMonitor**. When the monitor is in a FAIL state, the framework will use
the configured active monitor to check if the monitored dependency is working or not. Note that the active monitor will
not be used if the monitor is in OK or WARN state and that the active monitor check will be performed by a background
thread which means that the performance of the webping will not be affected by the active monitor check, and hammering
the webping will not affect the active monitoring check.

The Active Monitor is implemented by the **ActiveMonitorService** see the javadoc for more details.

Implementations provided

### Database

The Database Monitor tries to execute a given sql statement against the database. See the implementation **Database** and [Active DB Monitoring](Active_DB_Monitoring.html)

### Url

The **UrlActiveMonitor** checks the availability and response from a URL.

This class can be configured with:

#### What to check

This monitor can check:

* The connection to the URL. See **CheckCanConnect**
* If the response contains a given string. See **CheckResponseContains**
* If the response not contains a given string. See **CheckResponseNotContains**
* If the response matches a pattern. See **CheckResponseMatches**

#### Which URL

The URL to be checked can be specified with:
* A fixed url (**FixedUrlProvider.html**)
* A url derived from another url (**DeriveUrl.html**)

#### Spring Configuration

To ease the spring configuration a little bit, there are two editors provided, a **CheckerEditor** and a **UrlProviderEditor**

Those will allow to configure the urlProvide and checker of the UrlMonitor like:

```xml
<bean id="aMonitor" class="com.sportex.betex.monitor.DefaultMonitor">
        <property name="activeMonitor">
            <bean class="com.sportex.betex.monitor.active.url.UrlActiveMonitor">
                <property name="urlProvider" value="http://otherhost/webping"/>
                <property name="checker" value="connect"/>
            </bean>
        </property>
    </bean>
```
See the javadocs for details.

#### Starting active monitoring

See [Spring Configuration](Monitor_Usage_Spring.html)

### MultiCheck

This active monitor allows you to aggregate a number of checks and exposes them as a single check without requiring a
custom checker to be coded. This can be useful if a given passive monitor makes calls to a number of external services
and each service needs to be checked before the failing monitor is reset.

Example:

```xml
      <!-- I aggregate a  bunch of monitors defined further down -->
      <bean id="activeCheckAll" class="com.sportex.betex.monitor.active.MultiCheck">
		<constructor-arg>
			<list>
				<ref bean="activeCheckDB_A" />
				<ref bean="activeCheckDB_B" />
				<ref bean="activeCheckDB_C" />
			</list>
		</constructor-arg>
     </bean>
    <!-- I am the active monitor for the configured datasource -->
    <bean id="activeCheckDB_A" class="com.sportex.betex.monitor.active.db.Database">
        <constructor-arg ref="aDataSource" />
    </bean>
    <bean id="activeCheckDB_B" class="com.sportex.betex.monitor.active.db.Database">
        <constructor-arg ref="bDataSource" />
    </bean>    
    <bean id="activeCheckDB_C" class="com.sportex.betex.monitor.active.db.Database">
        <constructor-arg ref="cDataSource" />
    </bean>  
```

Each of the monitors will be checked in the order listed in the constructor. Currently each check is performed serially
one after the other, but this is subject to change as we may want to parallelise these checks down the track. Currently
the first failing monitor will prevent the following active checks being performed (until the next call of the MultiCheck)
