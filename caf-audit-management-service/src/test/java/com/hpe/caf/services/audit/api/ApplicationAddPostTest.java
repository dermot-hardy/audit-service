/*
 * Copyright 2015-2017 Hewlett Packard Enterprise Development LP.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hpe.caf.services.audit.api;

import com.hpe.caf.auditing.schema.AuditEvent;
import com.hpe.caf.auditing.schema.AuditEventParam;
import com.hpe.caf.auditing.schema.AuditEventParamType;
import com.hpe.caf.auditing.schema.AuditedApplication;

import com.hpe.caf.services.audit.api.exceptions.BadRequestException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.Mockito;

import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.xml.bind.JAXBException;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import java.nio.charset.StandardCharsets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ApplicationAddPost.class,JAXBUnmarshal.class})
public class ApplicationAddPostTest {

    private static String AUDIT_EVENTS_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<AuditedApplication\n" +
            "\t\txmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "\t\txmlns=\"http://www.hpe.com/CAF/Auditing/Schema/AuditedApplication.xsd\"\n" +
            "\t\txsi:schemaLocation=\"http://www.hpe.com/CAF/Auditing/Schema/AuditedApplication.xsd\"\n" +
            ">\n" +
            "\t<ApplicationId>TestApplication</ApplicationId>\n" +
            "\t<AuditEvents>\n" +
            "\t\t<AuditEvent>\n" +
            "\t\t\t<TypeId>TestEvent1</TypeId>\n" +
            "\t\t\t<CategoryId>TestCategory1</CategoryId>\n" +
            "\t\t\t<Params>\n" +
            "\t\t\t\t<Param>\n" +
            "\t\t\t\t\t<Name>StringType</Name>\n" +
            "\t\t\t\t\t<Type>string</Type>\n" +
            "\t\t\t\t\t<ColumnName>StringType</ColumnName>\n" +
            "\t\t\t\t\t<Description>Description for StringType</Description>\n" +
            "\t\t\t\t</Param>\n" +
            "\t\t\t</Params>\n" +
            "\t\t</AuditEvent>\n" +
            "\t</AuditEvents>\n" +
            "</AuditedApplication>\n" +
            "\n";

    private HashMap<String, String> newEnv;

    @Before
    public void setup() throws Exception {
        newEnv  = new HashMap<>();
        newEnv.put("CAF_DATABASE_URL","testUrl");
        newEnv.put("CAF_DATABASE_SERVICE_ACCOUNT","testServiceUser");
        newEnv.put("CAF_DATABASE_SERVICE_ACCOUNT_PASSWORD","testPassword");
        newEnv.put("CAF_DATABASE_LOADER_ACCOUNT","testLoaderUser");
        newEnv.put("CAF_DATABASE_LOADER_ACCOUNT_PASSWORD","testPassword");
        newEnv.put("CAF_DATABASE_READER_ROLE","testReaderRole");
    }

    @Test
    public void testAddPost_Success_RegisterApplication () throws Exception {

        //  Mock JAXB xml binding code.
        AuditedApplication auditedApp = TestUtil.getAuditedApplication("TestApplication");
        PowerMockito.mockStatic(JAXBUnmarshal.class);
        PowerMockito.when(JAXBUnmarshal.bindAuditEventsXml(Mockito.any()))
                .thenReturn(auditedApp);

        //  Mock DatabaseHelper calls.
        DatabaseHelper mockDatabaseHelper = Mockito.mock(DatabaseHelper.class);
        Mockito.doNothing().when(mockDatabaseHelper).createAuditManagementSchema(Mockito.anyString());
        Mockito.when(mockDatabaseHelper.getEventsXMLForApp(Mockito.anyString())).thenReturn("");
        Mockito.doNothing().when(mockDatabaseHelper).insertApplicationEventsRow(Mockito.anyString(),Mockito.anyString());
        PowerMockito.whenNew(DatabaseHelper.class).withArguments(Mockito.any()).thenReturn(mockDatabaseHelper);

        //  Set-up test database connection properties.
        TestUtil.setSystemEnvironmentFields(newEnv);

        //  Test successful run of applications endpoint.
        InputStream auditConfigXMLStream = new ByteArrayInputStream(AUDIT_EVENTS_XML.getBytes(StandardCharsets.UTF_8));
        ApplicationAddPost.addApplication(auditConfigXMLStream);

        Mockito.verify(mockDatabaseHelper, Mockito.times(1)).createAuditManagementSchema(Mockito.anyString());
        Mockito.verify(mockDatabaseHelper, Mockito.times(1)).getEventsXMLForApp(Mockito.anyString());
        Mockito.verify(mockDatabaseHelper, Mockito.times(1)).insertApplicationEventsRow(Mockito.anyString(),Mockito.anyString());

    }

    @Test
    public void testAddPost_Success_ModifyApplication () throws Exception {

        //  Mock JAXB xml binding code.
        AuditedApplication auditedApp = TestUtil.getAuditedApplication("TestApplication");
        PowerMockito.mockStatic(JAXBUnmarshal.class);
        PowerMockito.when(JAXBUnmarshal.bindAuditEventsXml(Mockito.any()))
                .thenReturn(auditedApp);

        //  Mock DatabaseHelper calls.
        DatabaseHelper mockDatabaseHelper = Mockito.mock(DatabaseHelper.class);
        Mockito.doNothing().when(mockDatabaseHelper).createAuditManagementSchema(Mockito.anyString());
        Mockito.when(mockDatabaseHelper.getEventsXMLForApp(Mockito.anyString())).thenReturn("xml");
        Mockito.doNothing().when(mockDatabaseHelper).updateApplicationEventsRow(Mockito.anyString(),Mockito.anyString());
        List<String> tenants = new ArrayList<>();
        tenants.add("tenant1");
        tenants.add("tenant2");
        Mockito.when(mockDatabaseHelper.getTenantsForApp(Mockito.anyString())).thenReturn(tenants);
        Mockito.when(mockDatabaseHelper.doesColumnExist(Mockito.anyString(),Mockito.anyString(),Mockito.anyString())).thenReturn(false);
        Mockito.when(mockDatabaseHelper.getEventsXMLForApp(Mockito.anyString())).thenReturn("exists");
        Mockito.doNothing().when(mockDatabaseHelper).addColumn(Mockito.anyString());
        PowerMockito.whenNew(DatabaseHelper.class).withArguments(Mockito.any()).thenReturn(mockDatabaseHelper);

        //  Set-up test database connection properties.
        TestUtil.setSystemEnvironmentFields(newEnv);

        //  Test successful run of applications endpoint.
        InputStream auditConfigXMLStream = new ByteArrayInputStream(AUDIT_EVENTS_XML.getBytes(StandardCharsets.UTF_8));
        ApplicationAddPost.addApplication(auditConfigXMLStream);
        auditConfigXMLStream.close();

        Mockito.verify(mockDatabaseHelper, Mockito.times(1)).createAuditManagementSchema(Mockito.anyString());
        Mockito.verify(mockDatabaseHelper, Mockito.times(1)).getEventsXMLForApp(Mockito.anyString());
        Mockito.verify(mockDatabaseHelper, Mockito.times(1)).updateApplicationEventsRow(Mockito.anyString(),Mockito.anyString());
        Mockito.verify(mockDatabaseHelper, Mockito.times(1)).getTenantsForApp(Mockito.anyString());
        Mockito.verify(mockDatabaseHelper, Mockito.times(2)).doesColumnExist(Mockito.anyString(),Mockito.anyString(),Mockito.anyString());
        Mockito.verify(mockDatabaseHelper, Mockito.times(2)).addColumn(Mockito.anyString());

    }

    @Test(expected = BadRequestException.class)
    public void testAddPost_Failure_InvalidAuditEventsFile () throws Exception {

        //  Test expected failure run due to invalid XML.
        String INVALID_AUDIT_EVENTS_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<AuditedApplication\n" +
                "\t\txmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "\t\txmlns=\"http://www.hpe.com/CAF/Auditing/Schema/AuditedApplication.xsd\"\n" +
                "\t\txsi:schemaLocation=\"http://www.hpe.com/CAF/Auditing/Schema/AuditedApplication.xsd\"\n" +
                ">\n" +
                "\t<InvalidApplicationId>TestApplication</InvalidApplicationId>\n" +
                "\t<AuditEvents>\n" +
                "\t\t<AuditEvent>\n" +
                "\t\t\t<TypeId>TestEvent1</TypeId>\n" +
                "\t\t\t<CategoryId>TestCategory1</CategoryId>\n" +
                "\t\t\t<Params>\n" +
                "\t\t\t\t<Param>\n" +
                "\t\t\t\t\t<Name>StringType</Name>\n" +
                "\t\t\t\t\t<Type>string</Type>\n" +
                "\t\t\t\t\t<ColumnName>StringType</ColumnName>\n" +
                "\t\t\t\t\t<Description>Description for StringType</Description>\n" +
                "\t\t\t\t</Param>\n" +
                "\t\t\t</Params>\n" +
                "\t\t</AuditEvent>\n" +
                "\t</AuditEvents>\n" +
                "</AuditedApplication>\n" +
                "\n";

        //  Set-up test database connection properties.
        TestUtil.setSystemEnvironmentFields(newEnv);

        InputStream auditConfigXMLStream = new ByteArrayInputStream(INVALID_AUDIT_EVENTS_XML.getBytes(StandardCharsets.UTF_8));
        ApplicationAddPost.addApplication(auditConfigXMLStream);
        auditConfigXMLStream.close();

    }

    @Test(expected = Exception.class)
    public void testAddPost_Failure_FailedToBindXML () throws Exception {

        //  Mock JAXB xml binding code.
        PowerMockito.mockStatic(JAXBUnmarshal.class);
        PowerMockito.when(JAXBUnmarshal.bindAuditEventsXml(Mockito.any()))
                .thenThrow(new JAXBException("Test"));

        //  Set-up test database connection properties.
        TestUtil.setSystemEnvironmentFields(newEnv);

        //  Test expected failure run due to invalid XML.
        InputStream auditConfigXMLStream = new ByteArrayInputStream(AUDIT_EVENTS_XML.getBytes(StandardCharsets.UTF_8));
        ApplicationAddPost.addApplication(auditConfigXMLStream);
        auditConfigXMLStream.close();

    }

    @Test(expected = BadRequestException.class)
    public void testAddPost_Failure_ApplicationIdNotFound () throws Exception {

        //  Mock JAXB xml binding code.
        AuditedApplication auditedApp = TestUtil.getAuditedApplication("");
        PowerMockito.mockStatic(JAXBUnmarshal.class);
        PowerMockito.when(JAXBUnmarshal.bindAuditEventsXml(Mockito.any()))
                .thenReturn(auditedApp);

        //  Set-up test database connection properties.
        TestUtil.setSystemEnvironmentFields(newEnv);

        //  Test expected failure run due to invalid XML.
        InputStream auditConfigXMLStream = new ByteArrayInputStream(AUDIT_EVENTS_XML.getBytes(StandardCharsets.UTF_8));
        ApplicationAddPost.addApplication(auditConfigXMLStream);
        auditConfigXMLStream.close();

    }

    @Test
    public void testAddPost_Success_WebServiceDisabled () throws Exception {

        //  Mock DatabaseHelper calls.
        DatabaseHelper mockDatabaseHelper = Mockito.mock(DatabaseHelper.class);
        Mockito.doNothing().when(mockDatabaseHelper).createAuditManagementSchema(Mockito.anyString());
        Mockito.when(mockDatabaseHelper.getEventsXMLForApp(Mockito.anyString())).thenReturn("");
        Mockito.doNothing().when(mockDatabaseHelper).insertApplicationEventsRow(Mockito.anyString(),Mockito.anyString());
        PowerMockito.whenNew(DatabaseHelper.class).withArguments(Mockito.any()).thenReturn(mockDatabaseHelper);

        //  Set-up test database connection properties.
        newEnv.put("CAF_AUDIT_MANAGEMENT_DISABLE","true");
        TestUtil.setSystemEnvironmentFields(newEnv);

        //  Test successful run of applications endpoint.
        InputStream auditConfigXMLStream = new ByteArrayInputStream(AUDIT_EVENTS_XML.getBytes(StandardCharsets.UTF_8));
        ApplicationAddPost.addApplication(auditConfigXMLStream);

        Mockito.verify(mockDatabaseHelper, Mockito.times(0)).createAuditManagementSchema(Mockito.anyString());
        Mockito.verify(mockDatabaseHelper, Mockito.times(0)).getEventsXMLForApp(Mockito.anyString());
        Mockito.verify(mockDatabaseHelper, Mockito.times(0)).insertApplicationEventsRow(Mockito.anyString(),Mockito.anyString());
    }
}
