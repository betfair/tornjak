---
---
{toc}

### Check Service Dependency

Check service dependency has different output depending on the version of Nagios being used. Currently production uses version 2, whereas development uses version 3.

#### Nagios 2

The scripts support 2 output modes for Nagios 2: extended mode and normal mode. Extended mode allows the output additional information (current LUS URL and latest exception stack trace) via use of &lt;br> tags which are intepreted via the browser when viewing detailed check information. Extended mode is triggered by passing an optional "-e" parameter.

h5. testSimpleOk

{quote}
Mimic the monitoring interface indicating an OK status and a maximum possible status of FAIL
Ensure the script prints "Status: OK (Max Status: FAIL)"
Ensure the script exit code is 0 (OK to Nagios)
{quote}
Example output:
```
Status: OK (Max Status: FAIL)
```

h5. testSimpleOkExtended

{quote}
Use extended mode.
Mimic the monitoring interface indicating an OK status and a maximum possible status of FAIL
Ensure the script prints "Status: OK (Max Status: FAIL)"
Ensure the script exit code is 0 (OK to Nagios)
{quote}
Example output:
```
Status: OK (Max Status: FAIL)|Current Entry URL: http://some_url<br>Last Exception:
```

h5. testSimpleWarn

{quote}
Mimic the monitoring interface indicating an WARN status and a maximum possible status of FAIL
Ensure the script prints "Status: WARN (Max Status: FAIL)"
Ensure the script exit code is 1 (WARN to Nagios)
{quote}
Example output:
```
Status: WARN (Max Status: FAIL)
```

h5. testSimpleWarnExtended

{quote}
Use extended mode.
Mimic the monitoring interface indicating an WARN status and a maximum possible status of FAIL
Ensure the script prints "Status: WARN (Max Status: FAIL)"
Ensure the script exit code is 1 (WARN to Nagios)
{quote}
Example output:
```
Status: WARN (Max Status: FAIL)|Current Entry URL: http://some_url<br>Last Exception:
```

h5. testSimpleFail

{quote}
Mimic the monitoring interface indicating an FAIL status and a maximum possible status of FAIL
Ensure the script prints "Status: FAIL (Max Status: FAIL)"
Ensure the script exit code is 2 (FAIL to Nagios)
{quote}
Example output:
```
Status: FAIL (Max Status: FAIL)
```

h5. testSimpleFailExtended

{quote}
Use extended mode.
Mimic the monitoring interface indicating an FAIL status and a maximum possible status of FAIL
Ensure the script prints "Status: FAIL (Max Status: FAIL)"
Ensure the script exit code is 2 (FAIL to Nagios)
{quote}
Example output:
```
Status: FAIL (Max Status: FAIL)|Current Entry URL: http://some_url<br>Last Exception:
```

h5. testSimpleFailLimitedToWarn

{quote}
Mimic the monitoring interface indicating an FAIL status and a maximum possible status of WARN
Ensure the script prints "Status: FAIL (Max Status: WARN)"
Ensure the script exit code is 1 (WARN to Nagios)
{quote}
Example output:
```
Status: FAIL (Max Status: WARN)
```

h5. testSimpleFailLimitedToWarnExtended

{quote}
Use extended mode.
Mimic the monitoring interface indicating an FAIL status and a maximum possible status of WARN
Ensure the script prints "Status: FAIL (Max Status: WARN)"
Ensure the script exit code is 1 (WARN to Nagios)
{quote}
Example output:
```
Status: FAIL (Max Status: WARN)|Current Entry URL: http://some_url<br>Last Exception:
```

h5. testSimpleFailLimitedToOk

{quote}
Mimic the monitoring interface indicating an FAIL status and a maximum possible status of OK
Ensure the script prints "Status: FAIL (Max Status: OK)"
Ensure the script exit code is 0 (OK to Nagios)
{quote}
Example output:
```
Status: FAIL (Max Status: OK)
```

h5. testSimpleFailLimitedToOkExtended

{quote}
Use extended mode
Mimic the monitoring interface indicating an FAIL status and a maximum possible status of OK
Ensure the script prints "Status: FAIL (Max Status: OK)"
Ensure the script exit code is 0 (OK to Nagios)
{quote}
Example output:
```
Status: FAIL (Max Status: OK)|Current Entry URL: http://some_url<br>Last Exception:
```

h5. testOneLineExceptionInExtendedMode

{quote}
Use extended mode
Mimic the monitoring interface indicating an FAIL status and a maximum possible status of FAIL
Mimic the monitoring interface indicating a single line exception as the last exception
Ensure the script prints "Status: FAIL (Max Status: FAIL)"
Ensure the script exit code is 2 (Fail to Nagios)
Ensure the script prints the exception after an exception label
{quote}
Example output:
```
Status: FAIL (Max Status: FAIL)|Current Entry URL: http://some_url<br>Last Exception:<br>SomeException
```

h5. testTwoLineExceptionInExtendedMode

{quote}
Use extended mode
Mimic the monitoring interface indicating an FAIL status and a maximum possible status of FAIL
Mimic the monitoring interface indicating a 2 line exception as the last exception
Ensure the script prints "Status: FAIL (Max Status: FAIL)"
Ensure the script exit code is 2 (Fail to Nagios)
Ensure the script prints the exception after an exception label
{quote}
Example output:
```
Status: FAIL (Max Status: FAIL)|Current Entry URL: http://some_url<br>Last Exception:<br>SomeException1<br>SomeException2
```

h5. testThreeLineExceptionInExtendedMode

{quote}
Use extended mode
Mimic the monitoring interface indicating an FAIL status and a maximum possible status of FAIL
Mimic the monitoring interface indicating a 3 line exception as the last exception
Ensure the script prints "Status: FAIL (Max Status: FAIL)"
Ensure the script exit code is 2 (Fail to Nagios)
Ensure the script prints the exception after an exception label
{quote}
Example output:
```
Status: FAIL (Max Status: FAIL)|Current Entry URL: http://some_url<br>Last Exception:<br>SomeException1<br>  at SomeException2<br>  at SomeException3
```

h5. testLusUrlInExtendedMode

{quote}
Use extended mode
Mimic the monitoring interface indicating an OK status and a maximum possible status of FAIL
Mimic the monitoring interface indicating the url from the LUS is [http://some_url]
Ensure the script prints "Status: OK (Max Status: FAIL)"
Ensure the script prints "Current Entry URL: [http://some_url]"
Ensure the script exit code is 0 (OK to Nagios)
{quote}
Example output:
```
Status: OK (Max Status: FAIL)|Current Entry URL: http://some_url<br>Last Exception:
```

h5. testFirstWgetFailed

{quote}
Mimic the first wget command (to retrieve status info) failing with a non-zero exit code
Ensure the script prints "Status: UNKNOWN (Cannot connect to monitoring interface)"
Ensure the script exit code is 3 (Unknown to Nagios)
{quote}
Example output:
```
Status: UNKNOWN (Cannot connect to monitoring interface)
```

h5. testFirstWgetFailedExtended

{quote}
Use extended mode
Mimic the first wget command (to retrieve status info) failing with a non-zero exit code
Ensure the script prints "Status: UNKNOWN (Cannot connect to monitoring interface)"
Ensure the script exit code is 3 (Unknown to Nagios)
{quote}
Example output:
```
Status: UNKNOWN (Cannot connect to monitoring interface)
```

h5. testSecondWgetFailed

{quote}
Mimic the second wget command (to retrieve lus info) failing with a non-zero exit code
Ensure the script prints "Status: UNKNOWN (Cannot connect to monitoring interface)"
Ensure the script exit code is 3 (Unknown to Nagios)
{quote}
Example output:
```
Status: UNKNOWN (Cannot connect to monitoring interface)
```

h5. testSecondWgetFailedExtended

{quote}
Use extended mode
Mimic the second wget command (to retrieve lus info) failing with a non-zero exit code
Ensure the script prints "Status: UNKNOWN (Cannot connect to monitoring interface)"
Ensure the script exit code is 3 (Unknown to Nagios)
{quote}
Example output:
```
Status: UNKNOWN (Cannot connect to monitoring interface)
```

h5. testBlankOutput

{quote}
Mimic the monitoring interface returning no data
Ensure the script prints "Status: UNKNOWN (Invalid response from monitoring interface)"
Ensure the script exit code is 3 (Unknown to Nagios)
{quote}
Example output:
```
Status: UNKNOWN (Invalid response from monitoring interface)
```

h5. testBlankOutputExtended

{quote}
Use extended mode
Mimic the monitoring interface returning no data
Ensure the script prints "Status: UNKNOWN (Invalid response from monitoring interface)"
Ensure the script exit code is 3 (Unknown to Nagios)
{quote}
Example output:
```
Status: UNKNOWN (Invalid response from monitoring interface)
```

h5. testGarbledOutput

{quote}
Mimic the monitoring interface returning rubbish data
Ensure the script prints "Status: UNKNOWN (Invalid response from monitoring interface)"
Ensure the script exit code is 3 (Unknown to Nagios)
{quote}
Example output:
```
Status: UNKNOWN (Invalid response from monitoring interface)
```

h5. testGarbledOutputExtended

{quote}
Use extended mode
Mimic the monitoring interface returning rubbish data
Ensure the script prints "Status: UNKNOWN (Invalid response from monitoring interface)"
Ensure the script exit code is 3 (Unknown to Nagios)
{quote}
Example output:
```
Status: UNKNOWN (Invalid response from monitoring interface)
```

h5. testInvalidStatus

{quote}
Mimic the monitoring interface indicating an unrecognized status (FAILED)
Ensure the script prints "Status: FAILED (Invalid response from monitoring interface)"
Ensure the script exit code is 3 (Unknown to Nagios)
{quote}
Example output:
```
Status: FAILED (Invalid response from monitoring interface)
```

h5. testInvalidStatusExtended

{quote}
Use extended mode
Mimic the monitoring interface indicating an unrecognized status (FAILED)
Ensure the script prints "Status: FAILED (Invalid response from monitoring interface)"
Ensure the script exit code is 3 (Unknown to Nagios)
{quote}
Example output:
```
Status: FAILED (Invalid response from monitoring interface)
```

h5. testBothAuthenticationParametersSentThrough

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

h5. testBothAuthenticationParametersRequired

{quote}
In turn, invoke the script with only one of the authentication parameters
Ensure the script exit code is 3 (Unknown to Nagios)
{quote}
Example output:
```
Must specify either neither, or both, of -U and -P. Missing option: -P
Must specify either neither, or both, of -U and -P. Missing option: -U
```

h5. testHttpAuthenticationFailed

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

h5. testHelpPage

{quote}
Invoke the script with a \-? argument
Ensure that the script responds with usage info
Ensure that the script exit code is 3
Ensure that the script prints it's version
{quote}
Example output:
```
Check service dependency, version 1.2-SNAPSHOT
./check_service_dependency <options> (All options are mandatory)
  -h <host address>          ; e.g. localhost
  -p <http port>             ; e.g. 8080
  -b <batch query path>      ; e.g. casinofacade/admin/batchquery.jsp
  -m <monitoring_jmx_domain> ; e.g. Monitoring.Facade.Casino
  -l <lus_jmx_domain>        ; e.g. LUS.Facade.Casino
  -d <dependency_name>       ; e.g. points
  -n <nagios_version>        ; Nagios compatibility mode (Optional - defaults to 2)
  -U <username>              ; Authentication for jmx interface (Optional)
  -P <password>              ; Authentication for jmx interface (Optional)
  -e                         ; Extended mode (Optional - Not compatible with Nagios 3)
  -v                         ; Verbose mode (Optional)
  -?                         ; Display this help page
```

h5. testInvalidArgument

{quote}
Invoke the script with a \-z argument
Ensure that the script exit code is 3
{quote}
Example output:
```
./check_service_dependency: illegal option -- z
Check service dependency, version 1.0-SNAPSHOT
./check_service_dependency <options> (All options are mandatory)
  -h <host address>          ; e.g. localhost
  -p <http port>             ; e.g. 8080
  -b <batch query path>      ; e.g. casinofacade/admin/batchquery.jsp
  -m <monitoring_jmx_domain> ; e.g. Monitoring.Facade.Casino
  -l <lus_jmx_domain>        ; e.g. LUS.Facade.Casino
  -d <dependency_name>       ; e.g. points
  -n <nagios_version>        ; Nagios compatibility mode (Optional - defaults to 2)
  -U <username>              ; Authentication for jmx interface (Optional)
  -P <password>              ; Authentication for jmx interface (Optional)
  -e                         ; Extended mode (Optional - Not compatible with Nagios 3)
  -v                         ; Verbose mode
  -?                         ; Display this help page
```

h5. testMandatoryArguments

{quote}
Invoke the script numerous times, each time omitting one of the mandatory arguments
Ensure that the script exit code is 3 each time
{quote}
Example output:
```
Missing option: -h
Check service dependency, version 1.0-SNAPSHOT
./check_service_dependency <options> (All options are mandatory)
  -h <host address>          ; e.g. localhost
  -p <http port>             ; e.g. 8080
  -b <batch query path>      ; e.g. casinofacade/admin/batchquery.jsp
  -m <monitoring_jmx_domain> ; e.g. Monitoring.Facade.Casino
  -l <lus_jmx_domain>        ; e.g. LUS.Facade.Casino
  -d <dependency_name>       ; e.g. points
  -n <nagios_version>        ; Nagios compatibility mode (Optional - defaults to 2)
  -U <username>              ; Authentication for jmx interface (Optional)
  -P <password>              ; Authentication for jmx interface (Optional)
  -e                         ; Extended mode (Optional - Not compatible with Nagios 3)
  -v                         ; Verbose mode
  -?                         ; Display this help page
```

h5. testTestModeOutput

{quote}
Invoke the script with the undocumented test mode argument
Ensure that the script prints a warning that running in test mode
{quote}
Example output:
```
WARNING: Running in test mode
Missing option: -h
Check service dependency, version 1.0-SNAPSHOT
./check_service_dependency <options> (All options are mandatory)
  -h <host address>          ; e.g. localhost
  -p <http port>             ; e.g. 8080
  -b <batch query path>      ; e.g. casinofacade/admin/batchquery.jsp
  -m <monitoring_jmx_domain> ; e.g. Monitoring.Facade.Casino
  -l <lus_jmx_domain>        ; e.g. LUS.Facade.Casino
  -d <dependency_name>       ; e.g. points
  -n <nagios_version>        ; Nagios compatibility mode (Optional - defaults to 2)
  -U <username>              ; Authentication for jmx interface (Optional)
  -P <password>              ; Authentication for jmx interface (Optional)
  -e                         ; Extended mode (Optional - Not compatible with Nagios 3)
  -v                         ; Verbose mode
  -?                         ; Display this help page
```

h5. testOptionalArguments

{quote}
Invoke the script with the optional -e argument
Ensure that the script exit code is 0
{quote}
Example output:
```
Status: OK (Max Status: FAIL)|Current Entry URL: http://some_url<br>Last Exception:
```

#### Nagios 3

When running in Nagios 3 compatibility mode, extended mode is switched on by default. Any attempt to set extended mode will result in error if combined with Nagios 3 compatibility mode.

h5. testSimpleOk

{quote}
Mimic the monitoring interface indicating an OK status and a maximum possible status of FAIL
Ensure the script prints "Status: OK (Max Status: FAIL)"
Ensure the script exit code is 0 (OK to Nagios)
{quote}
Example output:
```
Status: OK (Max Status: FAIL)
Current Entry URL: http://some_url
Last Exception:
```

h5. testSimpleWarn

{quote}
Mimic the monitoring interface indicating an WARN status and a maximum possible status of FAIL
Ensure the script prints "Status: WARN (Max Status: FAIL)"
Ensure the script exit code is 1 (WARN to Nagios)
{quote}
Example output:
```
Status: WARN (Max Status: FAIL)
Current Entry URL: http://some_url
Last Exception:
```

h5. testSimpleFail

{quote}
Mimic the monitoring interface indicating an FAIL status and a maximum possible status of FAIL
Ensure the script prints "Status: FAIL (Max Status: FAIL)"
Ensure the script exit code is 2 (FAIL to Nagios)
{quote}
Example output:
```
Status: FAIL (Max Status: FAIL)
Current Entry URL: http://some_url
Last Exception:
```

h5. testSimpleFailLimitedToWarn

{quote}
Mimic the monitoring interface indicating an FAIL status and a maximum possible status of WARN
Ensure the script prints "Status: FAIL (Max Status: WARN)"
Ensure the script exit code is 1 (WARN to Nagios)
{quote}
Example output:
```
Status: FAIL (Max Status: WARN)
Current Entry URL: http://some_url
Last Exception:
```

h5. testSimpleFailLimitedToOk

{quote}
Mimic the monitoring interface indicating an FAIL status and a maximum possible status of OK
Ensure the script prints "Status: FAIL (Max Status: OK)"
Ensure the script exit code is 0 (OK to Nagios)
{quote}
Example output:
```
Status: FAIL (Max Status: OK)
Current Entry URL: http://some_url
Last Exception:
```

h5. testOneLineException

{quote}
Mimic the monitoring interface indicating an FAIL status and a maximum possible status of FAIL
Mimic the monitoring interface indicating a single line exception as the last exception
Ensure the script prints "Status: FAIL (Max Status: FAIL)"
Ensure the script exit code is 2 (Fail to Nagios)
Ensure the script prints the exception after an exception label
{quote}
Example output:
```
Status: FAIL (Max Status: FAIL)
Current Entry URL: http://some_url
Last Exception:
SomeException
```

h5. testTwoLineException

{quote}
Mimic the monitoring interface indicating an FAIL status and a maximum possible status of FAIL
Mimic the monitoring interface indicating a 2 line exception as the last exception
Ensure the script prints "Status: FAIL (Max Status: FAIL)"
Ensure the script exit code is 2 (Fail to Nagios)
Ensure the script prints the exception after an exception label
{quote}
Example output:
```
Status: FAIL (Max Status: FAIL)
Current Entry URL: http://some_url
Last Exception:
SomeException1
SomeException2
```

h5. testThreeLineException

{quote}
Mimic the monitoring interface indicating an FAIL status and a maximum possible status of FAIL
Mimic the monitoring interface indicating a 3 line exception as the last exception
Ensure the script prints "Status: FAIL (Max Status: FAIL)"
Ensure the script exit code is 2 (Fail to Nagios)
Ensure the script prints the exception after an exception label
{quote}
Example output:
```
Status: FAIL (Max Status: FAIL)
Current Entry URL: http://some_url
Last Exception:
SomeException1
  at SomeException2
  at SomeException3
```

h5. testLusUrl

{quote}
Mimic the monitoring interface indicating an OK status and a maximum possible status of FAIL
Mimic the monitoring interface indicating the url from the LUS is [http://some_url]
Ensure the script prints "Status: OK (Max Status: FAIL)"
Ensure the script prints "Current Entry URL: [http://some_url]"
Ensure the script exit code is 0 (OK to Nagios)
{quote}
Example output:
```
Status: OK (Max Status: FAIL)
Current Entry URL: http://some_url
Last Exception:
```

h5. testFirstWgetFailed

{quote}
Mimic the first wget command (to retrieve status info) failing with a non-zero exit code
Ensure the script prints "Status: UNKNOWN (Cannot connect to monitoring interface)"
Ensure the script exit code is 3 (Unknown to Nagios)
{quote}
Example output:
```
Status: UNKNOWN (Cannot connect to monitoring interface)
```

h5. testSecondWgetFailed

{quote}
Mimic the second wget command (to retrieve lus info) failing with a non-zero exit code
Ensure the script prints "Status: UNKNOWN (Cannot connect to monitoring interface)"
Ensure the script exit code is 3 (Unknown to Nagios)
{quote}
Example output:
```
Status: UNKNOWN (Cannot connect to monitoring interface)
```

h5. testBlankOutput

{quote}
Mimic the monitoring interface returning no data
Ensure the script prints "Status: UNKNOWN (Invalid response from monitoring interface)"
Ensure the script exit code is 3 (Unknown to Nagios)
{quote}
Example output:
```
Status: UNKNOWN (Invalid response from monitoring interface)
```

h5. testGarbledOutput

{quote}
Mimic the monitoring interface returning rubbish data
Ensure the script prints "Status: UNKNOWN (Invalid response from monitoring interface)"
Ensure the script exit code is 3 (Unknown to Nagios)
{quote}
Example output:
```
Status: UNKNOWN (Invalid response from monitoring interface)
```

h5. testInvalidStatus

{quote}
Mimic the monitoring interface indicating an unrecognized status (FAILED)
Ensure the script prints "Status: FAILED (Invalid response from monitoring interface)"
Ensure the script exit code is 3 (Unknown to Nagios)
{quote}
Example output:
```
Status: FAILED (Invalid response from monitoring interface)
```

h5. testBothAuthenticationParametersSentThrough

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

h5. testBothAuthenticationParametersRequired

{quote}
In turn, invoke the script with only one of the authentication parameters
Ensure the script exit code is 3 (Unknown to Nagios)
{quote}
Example output:
```
Must specify either neither, or both, of -U and -P. Missing option: -P
Must specify either neither, or both, of -U and -P. Missing option: -U
```

h5. testHttpAuthenticationFailed

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

h5. testHelpPage

{quote}
Invoke the script with a \-? argument
Ensure that the script responds with usage info
Ensure that the script exit code is 3
Ensure that the script prints it's version
{quote}
Example output:
```
Check service dependency, version 1.0-SNAPSHOT
./check_service_dependency <options> (All options are mandatory)
  -h <host address>          ; e.g. localhost
  -p <http port>             ; e.g. 8080
  -b <batch query path>      ; e.g. casinofacade/admin/batchquery.jsp
  -m <monitoring_jmx_domain> ; e.g. Monitoring.Facade.Casino
  -l <lus_jmx_domain>        ; e.g. LUS.Facade.Casino
  -d <dependency_name>       ; e.g. points
  -n <nagios_version>        ; Nagios compatibility mode (Optional - defaults to 2)
  -U <username>              ; Authentication for jmx interface (Optional)
  -P <password>              ; Authentication for jmx interface (Optional)
  -e                         ; Extended mode (Optional - Not compatible with Nagios 3)
  -v                         ; Verbose mode
  -?                         ; Display this help page
```

h5. testInvalidArgument

{quote}
Invoke the script with a \-z argument
Ensure that the script exit code is 3
{quote}
Example output:
```
./check_service_dependency: illegal option -- z
Check service dependency, version 1.0-SNAPSHOT
./check_service_dependency <options> (All options are mandatory)
  -h <host address>          ; e.g. localhost
  -p <http port>             ; e.g. 8080
  -b <batch query path>      ; e.g. casinofacade/admin/batchquery.jsp
  -m <monitoring_jmx_domain> ; e.g. Monitoring.Facade.Casino
  -l <lus_jmx_domain>        ; e.g. LUS.Facade.Casino
  -d <dependency_name>       ; e.g. points
  -n <nagios_version>        ; Nagios compatibility mode (Optional - defaults to 2)
  -U <username>              ; Authentication for jmx interface (Optional)
  -P <password>              ; Authentication for jmx interface (Optional)
  -e                         ; Extended mode (Optional - Not compatible with Nagios 3)
  -v                         ; Verbose mode
  -?                         ; Display this help page
```

h5. testMandatoryArguments

{quote}
Invoke the script numerous times, each time omitting one of the mandatory arguments
Ensure that the script exit code is 3 each time
{quote}
Example output:
```
Missing option: -h
Check service dependency, version 1.0-SNAPSHOT
./check_service_dependency <options> (All options are mandatory)
  -h <host address>          ; e.g. localhost
  -p <http port>             ; e.g. 8080
  -b <batch query path>      ; e.g. casinofacade/admin/batchquery.jsp
  -m <monitoring_jmx_domain> ; e.g. Monitoring.Facade.Casino
  -l <lus_jmx_domain>        ; e.g. LUS.Facade.Casino
  -d <dependency_name>       ; e.g. points
  -n <nagios_version>        ; Nagios compatibility mode (Optional - defaults to 2)
  -U <username>              ; Authentication for jmx interface (Optional)
  -P <password>              ; Authentication for jmx interface (Optional)
  -e                         ; Extended mode (Optional - Not compatible with Nagios 3)
  -v                         ; Verbose mode
  -?                         ; Display this help page
```

h5. testTestModeOutput

{quote}
Invoke the script with the undocumented test mode argument
Ensure that the script prints a warning that running in test mode
{quote}
Example output:
```
WARNING: Running in test mode
Missing option: -h
Check service dependency, version 1.0-SNAPSHOT
./check_service_dependency <options> (All options are mandatory)
  -h <host address>          ; e.g. localhost
  -p <http port>             ; e.g. 8080
  -b <batch query path>      ; e.g. casinofacade/admin/batchquery.jsp
  -m <monitoring_jmx_domain> ; e.g. Monitoring.Facade.Casino
  -l <lus_jmx_domain>        ; e.g. LUS.Facade.Casino
  -d <dependency_name>       ; e.g. points
  -n <nagios_version>        ; Nagios compatibility mode (Optional - defaults to 2)
  -U <username>              ; Authentication for jmx interface (Optional)
  -P <password>              ; Authentication for jmx interface (Optional)
  -e                         ; Extended mode (Optional - Not compatible with Nagios 3)
  -v                         ; Verbose mode
  -?                         ; Display this help page
```

h5. testOptionalArguments

{quote}
Invoke the script with the optional -n argument, passing in 3 to indicate Nagios 3.
Ensure that the script exit code is 0
{quote}
Example output:
```
Status: OK (Max Status: FAIL)|Current Entry URL: http://some_url<br>Last Exception:
```

{quote}
Invoke the script with the optional -n argument, passing in 4 to indicate Nagios 4.
Ensure that the script exit code is 3
Ensure that the script prints a warning message
{quote}
Example output:
```
Unsupported Nagios version: 4
```

{quote}
Invoke the script with the optional -n argument, passing in 3 to indicate Nagios 3, and also pass the optional -e argument.
Ensure that the script exit code is 3
Ensure that the script prints a warning message
{quote}
Example output:
```
Extended mode not supported for Nagios 3
```