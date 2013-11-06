---
---

## Maven Import

The following dependency should be added to the web project:

```xml
<dependency>
    <groupId>com.betfair.monitoring</groupId>
    <artifactId>monitoring-overlay</artifactId>
    <version>2.2</version>
    <type>war</type>
    <scope>runtime</scope>
</dependency>
```

## URLs

With the dependency in place, the content will be available under:

* ```.../servicename/admin/index.html```
* ```.../servicename/admin/manifest.jsp```
* ```.../servicename/admin/batchquery.jsp```
* ```.../servicename/admin/humanquery.jsp```
* ```.../servicename/admin/diagnostics.jsp```
* ```.../servicename/admin/thread_dump_j4.jsp```

## Security

All jsp pages require an authenticated user with a defined role (see [[below|#Authorisation]]) to execute any request.
It is the including project's responsibility to add the following piece of configuration to their web.xml to hook into
their container's user management and to link to the authorisation config file:

```
    <context-param>
        <param-name>contextAuthConfigLocation</param-name>
        <param-value>classpath:com/betfair/monitoring/overlay/auth-default.properties</param-value>
    </context-param>
    <security-constraint>
        <web-resource-collection>
            <web-resource-name>all</web-resource-name>
            <url-pattern>/admin/*</url-pattern>
            <http-method>GET</http-method>
            <http-method>POST</http-method>
            <http-method>HEAD</http-method>
            <http-method>PUT</http-method>
            <http-method>TRACE</http-method>
            <http-method>CONNECT</http-method>
            <http-method>DELETE</http-method>
        </web-resource-collection>
        <auth-constraint>
            <role-name>jmxadmin</role-name>
        </auth-constraint>
    </security-constraint>
    <login-config>
        <auth-method>BASIC</auth-method>
    </login-config>
    <security-role>
        <role-name>jmxadmin</role-name>
    </security-role>
```

### Authorisation

In order to access any monitoring pages a configuration file need to be defined.  The location is specified by providing
context init parameter _contextAuthConfigLocation_ in _web.xml_.  Format of file is:

```
jmx.roles=(roleName).*
roleName.(readAllow|readDeny).number.domain=regex        # domain match regexp, or null if match all
roleName.(readAllow|readDeny).number.keyProperty=regex   # key-property match regexp, or null if match all
roleName.(readAllow|readDeny).number.attr=regex          # attribute match regex
```

example


        # the roles we are setting perms on. if no role defined, no access granted.
        jmx.roles=jmxSupport, jmxadmin, jmxUseless
        # jmxadmin can read any antribute on any bean apart from attributes that match pass.* and secret
        role.jmxadmin.readAllow.1.domain=.*
        role.jmxadmin.readAllow.1.keyProperty=.*
        role.jmxadmin.readAllow.1.attr=.*
        #
        role.jmxadmin.readDeny.1.domain=.*
        role.jmxadmin.readDeny.1.keyProperty=.*
        role.jmxadmin.readDeny.1.attr=pass.*
        #
        role.jmxadmin.readDeny.2.attr=secret
        # jmxSupport can read anything in MyDomain apart from password on MyBean
        role.jmxSupport.readAllow.1.domain=MyDomain
        role.jmxSupport.readAllow.1.attr=.*
        #
        role.jmxSupport.readDeny.1.domain=MyDomain
        role.jmxSupport.readDeny.1.attr=password
        # jmxUseless does not have access to anything

## Container configuration

Here is an example of how to configure Tomcat with users and passwords for the ```jmxadmin``` role.

Firstly, define a memory realm.  This loads the config file ```tomcat-users.xml``` into memory.

.../tomcat/conf/server.xml:

```
    ...
    <Engine name="Catalina" defaultHost="localhost">
        ...
        <Realm className="org.apache.catalina.realm.MemoryRealm" />
```

Secondly, define a user for the given role in .../tomcat/conf/tomcat-users.xml

```
<?xml version='1.0' encoding='utf-8'?>
<tomcat-users>
  <role rolename="jmxadmin"/>
  <user username="jmxadmin" password="password03" roles="jmxadmin"/>
</tomcat-users>
```
