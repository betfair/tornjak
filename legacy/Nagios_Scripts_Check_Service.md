---
---
{toc}

### Check Service

#### testSimpleOk

{quote}
Mimic the monitoring interface indicating an OK status
Ensure the script prints "Status: OK"
Ensure the script exit code is 0 (OK to Nagios)
{quote}
Example output:
```
Status: OK
```

#### testSimpleWarn

{quote}
Mimic the monitoring interface indicating an WARN status
Ensure the script prints "Status: WARN"
Ensure the script exit code is 1 (Warn to Nagios)
{quote}
Example output:
```
Status: WARN
```

#### testSimpleFail

{quote}
Mimic the monitoring interface indicating an FAIL status
Ensure the script prints "Status: FAIL"
Ensure the script exit code is 2 (Fail to Nagios)
{quote}
Example output:
```
Status: FAIL
```

#### testWgetFailed

{quote}
Mimic the wget command failing with a non-zero exit code
Ensure the script prints "Status: FAIL"
Ensure the script exit code is 2 (Fail to Nagios)
{quote}
Example output:
```
Status: FAIL
```

#### testBlankOutput

{quote}
Mimic the monitoring interface returning no data
Ensure the script prints "Status: FAIL"
Ensure the script exit code is 2 (Fail to Nagios)
{quote}
Example output:
```
Status: FAIL
```

#### testGarbledOutput

{quote}
Mimic the monitoring interface returning rubbish data
Ensure the script prints "Status: FAIL"
Ensure the script exit code is 2 (Fail to Nagios)
{quote}
Example output:
```
Status: FAIL
```

#### testInvalidStatus

{quote}
Mimic the monitoring interface indicating an unrecognized status (FAILED)
Ensure the script prints "Status: FAILED"
Ensure the script exit code is 3 (Unknown to Nagios)
{quote}
Example output:
```
Status: FAILED
```

#### testBothAuthenticationParametersSentThrough

{quote}
Mimic the monitoring interface indicating an recognized status (OK)
Invoke the script with both username and password parameters
Ensure the wget command is issued with the username and password parameters
Ensure the script prints "Status: OK"
Ensure the script exit code is 0 (OK to Nagios)
{quote}
Example output:
```
Status: OK
```

#### testBothAuthenticationParametersRequired

{quote}
In turn, invoke the script with only one of the authentication parameters
Ensure the script exit code is 3 (Unknown to Nagios)
{quote}
Example output:
```
Must specify either neither, or both, of -U and -P. Missing option: -P
Must specify either neither, or both, of -U and -P. Missing option: -U
```

#### testHttpAuthenticationFailed

{quote}
Mimic the monitoring interface request failing due to authentication failing
Invoke the script with both username and password parameters
Ensure the wget command is issued with the username and password parameters
Ensure the script prints "Status: UNKNOWN (Monitoring interface authentication failed)"
Ensure the script exit code is 3 (Unknown to Nagios)
{quote}
Example output:
```
Status: UNKNOWN (Monitoring interface authentication failed)
```

#### testHelpPage

{quote}
Invoke the script with a \-? argument
Ensure that the script responds with usage info
Ensure that the script exit code is 3 (Unknown to Nagios)
Ensure that the script prints it's version
{quote}
Example output:
```
Check service, version 1.0-SNAPSHOT
./check_service <options> (All options are mandatory)
  -h <host address>          ; e.g. localhost
  -p <http port>             ; e.g. 8080
  -b <batch query path>      ; e.g. casinofacade/admin/batchquery.jsp
  -m <monitoring_jmx_domain> ; e.g. Monitoring.Facade.Casino
  -n <status_name>           ; e.g. overallStatus
  -U <username>              ; Authentication for jmx interface (Optional)
  -P <password>              ; Authentication for jmx interface (Optional)
  -v                         ; Verbose mode
  -?                         ; Display this help page
```

#### testInvalidArgument

{quote}
Invoke the script with a \-z argument
Ensure that the script exit code is 3 (Unknown to Nagios)
{quote}
Example output:
```
./check_service: illegal option -- z
Check service, version 1.0-SNAPSHOT
./check_service <options> (All options are mandatory)
  -h <host address>          ; e.g. localhost
  -p <http port>             ; e.g. 8080
  -b <batch query path>      ; e.g. casinofacade/admin/batchquery.jsp
  -m <monitoring_jmx_domain> ; e.g. Monitoring.Facade.Casino
  -n <status_name>           ; e.g. overallStatus
  -U <username>              ; Authentication for jmx interface (Optional)
  -P <password>              ; Authentication for jmx interface (Optional)
  -v                         ; Verbose mode
  -?                         ; Display this help page
```

#### testMandatoryArguments

{quote}
Invoke the script numerous times, each time omitting one of the mandatory arguments
Ensure that the script exit code is 3 each time (Unknown to Nagios)
{quote}
Example output:
```
Missing option: -p
Check service, version 1.0-SNAPSHOT
./check_service <options> (All options are mandatory)
  -h <host address>          ; e.g. localhost
  -p <http port>             ; e.g. 8080
  -b <batch query path>      ; e.g. casinofacade/admin/batchquery.jsp
  -m <monitoring_jmx_domain> ; e.g. Monitoring.Facade.Casino
  -n <status_name>           ; e.g. overallStatus
  -U <username>              ; Authentication for jmx interface (Optional)
  -P <password>              ; Authentication for jmx interface (Optional)
  -v                         ; Verbose mode
  -?                         ; Display this help page
```

#### testTestModeOutput

{quote}
Invoke the script with the undocumented test mode argument
Ensure that the script prints a warning that running in test mode
{quote}
Example output:
```
WARNING: Running in test mode
Missing option: -h
Check service, version 1.0-SNAPSHOT
./check_service <options> (All options are mandatory)
  -h <host address>          ; e.g. localhost
  -p <http port>             ; e.g. 8080
  -b <batch query path>      ; e.g. casinofacade/admin/batchquery.jsp
  -m <monitoring_jmx_domain> ; e.g. Monitoring.Facade.Casino
  -n <status_name>           ; e.g. overallStatus
  -U <username>              ; Authentication for jmx interface (Optional)
  -P <password>              ; Authentication for jmx interface (Optional)
  -v                         ; Verbose mode
  -?                         ; Display this help page
```

