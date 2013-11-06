---
layout: default
---
{toc}

## Features

* Emits KPI events as [[StatsE|SRE:StatsE]] events. 

## Configuration

In order to configure KPI functionality, you need to do the following:

### 1. Specify pom.xml dependency in xyz-services project

In ```pom.xml```
```
        <dependency>
            <groupId>com.betfair.monitoring</groupId>
            <artifactId>kpi-base</artifactId>
            <version>2.8</version>
        </dependency>
        <dependency>
            <groupId>com.betfair.monitoring</groupId>
            <artifactId>kpi-base-spring</artifactId>
            <version>2.8</version>
        </dependency>
```


### 2. Import kpi Spring config into the xyz-service project

In your app's ```xyz-service.xml``` Spring config, add the line:
```
<import resource="classpath:kpi-statse.xml"/>
```

## Notes

* Emits to the default StatsE agent location of ```tcp://localhost:14444```

