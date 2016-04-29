# Code Generation Plugin

In order to use CAF Auditing in an application, the auditing events that the application uses must be specified along with the parameters that are associated with each of the events in an [Audit Event Definition File](https://github.hpe.com/caf/caf-audit-schema/blob/develop/README.md). After you have created the definition file you can use it to generate a client-side auditing library in order to raise the application defined audit events. 

The `caf-audit-maven-plugin` is a code generation plugin which uses the definition file to auto-generate Java auditing code that will make up the client-side auditing library. The generated class which is named `AuditLog`, uses the [Auditing Library](https://github.hpe.com/caf/caf-audit) to send audit event messages to the Apache Kafka messaging service. 

## Usage

The client-side auditing library will need to include the code generation plugin in the `<plugins>` section of the application’s POM file:
	
	<build>
	    <plugins>
	        <plugin>
	            <groupId>com.hpe.caf</groupId>
	            <artifactId>caf-audit-maven-plugin</artifactId>
	            <version>1.0</version>
	            <executions>
	                <execution>
	                    <id>generate-code</id>
	                    <phase>generate-sources</phase>
	                    <goals>
	                        <goal>xmltojava</goal>
	                    </goals>
	                </execution>
	            </executions>
	            <configuration>
	                <auditXMLConfig>src/main/xml/sampleapp-auditevents.xml</auditXMLConfig>
	                <packageName>${project.groupId}.auditing</packageName>
	            </configuration>
	        </plugin>
	    </plugins>
	</build>

The `xmltojava` goal of the plugin is used to generate the Java auditing code that will make up the library. The `auditXMLConfig` setting can be used to define the path to the Audit Event Definition file, and the `packageName` setting can be used to set the package in which the auditing code should be generated.

In this example the Audit Event Definition file is in the `src/main/xml/` folder, though of course it could be read from any folder. The name of the package to use is being built up by appending `.auditing` to the project's group identifier (i.e. `com.hpe.sampleapp` in this example).

## Auto-Generated Methods

Using the sample audit events XML specified in the [Audit Event Definition File](https://github.hpe.com/caf/caf-audit-schema/blob/develop/README.md), the code generation plugin will auto-generate the following methods as part of the `AuditLog` class:

	/**
	     * Audit the viewDocument event
	     * @param channel Identifies the channel to be used for message queuing 
	     * @param userId Identifies the user who triggered the event 
	     * @param docId Document identifier 
	     */
	    public static void auditViewDocument
	    (
	        final AuditChannel channel,
	        final String userId,
	        final long docId
	    )
	        throws Exception
	    {
	        final AuditEventBuilder auditEventBuilder = channel.createEventBuilder();
	        auditEventBuilder.setApplication(APPLICATION_IDENTIFIER);
	        auditEventBuilder.setUser(userId);
	        auditEventBuilder.setEventType("documentEvents", "viewDocument");
	        auditEventBuilder.addEventParameter("docId", null, docId);
	
	        auditEventBuilder.send();
	    }
	                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                
	    /**
	     * Audit the policyApplied event
	     * @param channel Identifies the channel to be used for message queuing 
	     * @param userId Identifies the user who triggered the event 
	     * @param policyId Policy identifier 
	     * @param policyName Policy Name 
	     * @param policyDef Policy definition 
	     */
	    public static void auditPolicyApplied
	    (
	        final AuditChannel channel,
	        final String userId,
	        final long policyId,
	        final String policyName,
	        final String policyDef
	    )
	        throws Exception
	    {
	        final AuditEventBuilder auditEventBuilder = channel.createEventBuilder();
	        auditEventBuilder.setApplication(APPLICATION_IDENTIFIER);
	        auditEventBuilder.setUser(userId);
	        auditEventBuilder.setEventType("policyEvents", "policyApplied");
	        auditEventBuilder.addEventParameter("policyId", null, policyId);
	        auditEventBuilder.addEventParameter("policyName", null, policyName);
	        auditEventBuilder.addEventParameter("policyDef", "policyDefinition", policyDef);
	
	        auditEventBuilder.send();
	    }

Calls to methods `auditViewDocument` and `auditPolicyApplied` would then be made to send document and policy event messages to Kafka.
