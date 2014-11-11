Sends exceptions to [http://tracemycode.net](tracemycode.net) from JBoss 7.

Disclaimer
=

This is just a simple demo to test posting exceptions to [http://tracemycode.net](tracemycode.net). **Use at your own risk!**

Installation in JBoss 7.1.1 Final
=

This involves the following steps:

1. Compile source code
1. Add new module to JBoss 7
1. Configure custom log handler in JBoss 7

Compile source code
==

`mvn clean install`

Add new module to JBoss 7
==

Create folders `<jboss-home>/modules/com/jorge/logging/tracemycode/main`.

Create text document `<jboss-home>/modules/com/jorge/logging/tracemycode/main/module.xml` with the following contents:

    <?xml version="1.0" encoding="UTF-8"?>
    <module xmlns="urn:jboss:module:1.1" name="com.jorge.logging.tracemycode">
      <resources>
        <resource-root path="tracemycode-0.0.1-SNAPSHOT.jar"/>
      </resources>
      <dependencies>
        <module name="org.codehaus.jackson.jackson-mapper-asl"/>
        <module name="org.apache.httpcomponents"/>
      </dependencies>
    </module>

Copy file `test-tracemycode-0.0.1-SNAPSHOT.jar` from `target` folder into `<jboss-home>/modules/com/jorge/logging/tracemycode/main/`.

Configure custom log handler in JBoss 7
==

Open `standalone.xml`.

Locate `<subsystem xmlns="urn:jboss:domain:logging:1.1">`.

Add, inside that `<subsystem>` xml tag:

    <custom-handler name="tracemycode" class="com.jorge.logging.tracemycode.TraceMyCodeLogHandler" module="com.jorge.logging.tracemycode">
    	<level name="ERROR"/>
    	<formatter>
    		<pattern-formatter pattern="%d{MMM dd HH:mm:ss} %-5p [%c] (%t) %s%n"/>
    	</formatter>
    	<properties>
    		<property name="clientId" value="ffffffff-ffff-ffff-ffff-ffffffffffff"/>
    		<property name="version" value="1.0"/>
    	</properties>
    </custom-handler>

Make sure to change the value for properties `clientId` and `version` accordingly:

- `clientId` - the identifier of the project from tracemycode

![](http://snag.gy/J0Wbw.jpg)

- `version` - the version of your application. it is debatable why this setting is here.

Add the custom handler to the `<root-logger>`.

    <root-logger>
    	<level name="INFO"/>
    	<handlers>
    		<handler name="CONSOLE"/>
    		<handler name="FILE"/>
    		<handler name="tracemycode"/>
    	</handlers>
    </root-logger>
