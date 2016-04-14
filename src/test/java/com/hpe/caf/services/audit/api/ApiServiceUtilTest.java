package com.hpe.caf.services.audit.api;

import com.hpe.caf.services.audit.api.exceptions.BadRequestException;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;

public class ApiServiceUtilTest {

    @Test
    public void testGetAppConfigPropertiesSuccess () throws Exception {

        //  Set-up test database connection properties.
        HashMap<String, String> newEnv  = new HashMap<>();
        newEnv.put("CAF_DATABASE_URL","testUrl");
        newEnv.put("CAF_DATABASE_SERVICE_ACCOUNT","testServiceUser");
        newEnv.put("CAF_DATABASE_SERVICE_ACCOUNT_PASSWORD","testPassword");
        newEnv.put("CAF_DATABASE_LOADER_ACCOUNT","testLoaderUser");
        newEnv.put("CAF_DATABASE_LOADER_ACCOUNT_PASSWORD","testPassword");
        newEnv.put("CAF_DATABASE_READER_ROLE","testReaderRole");
        TestUtil.setSystemEnvironmentFields(newEnv);

        //  Test successful call to class method.
        AppConfig configProps = ApiServiceUtil.getAppConfigProperties();
        Assert.assertEquals(configProps.getDatabaseURL(),"testUrl");
        Assert.assertEquals(configProps.getDatabaseServiceAccount(),"testServiceUser");
        Assert.assertEquals(configProps.getDatabaseServiceAccountPassword(),"testPassword");
        Assert.assertEquals(configProps.getDatabaseLoaderAccount(),"testLoaderUser");
        Assert.assertEquals(configProps.getDatabaseLoaderAccountPassword(),"testPassword");
        Assert.assertEquals(configProps.getDatabaseReaderRole(),"testReaderRole");
    }

    @Test(expected = BadRequestException.class)
    public void testGetAppConfigPropertiesFailure_MissingDatabaseUrl () throws Exception {

        //  Set-up test database connection properties.
        HashMap<String, String> newEnv  = new HashMap<>();
        TestUtil.setSystemEnvironmentFields(newEnv);

        //  Test expected failure call to class method because of missing database url.
        AppConfig configProps = ApiServiceUtil.getAppConfigProperties();
    }

    @Test(expected = BadRequestException.class)
    public void testGetAppConfigPropertiesFailure_MissingServiceUserCredentials () throws Exception {

        //  Set-up test database connection properties.
        HashMap<String, String> newEnv  = new HashMap<>();
        newEnv.put("CAF_DATABASE_URL","testUrl");
        TestUtil.setSystemEnvironmentFields(newEnv);

        //  Test expected failure call to class method because of missing service user credentials.
        AppConfig configProps = ApiServiceUtil.getAppConfigProperties();
    }

    @Test(expected = BadRequestException.class)
    public void testGetAppConfigPropertiesFailure_MissingLoaderUserCredentials () throws Exception {

        //  Set-up test database connection properties.
        HashMap<String, String> newEnv  = new HashMap<>();
        newEnv.put("CAF_DATABASE_URL","testUrl");
        newEnv.put("CAF_DATABASE_SERVICE_ACCOUNT","testServiceUser");
        newEnv.put("CAF_DATABASE_SERVICE_ACCOUNT_PASSWORD","testPassword");
        TestUtil.setSystemEnvironmentFields(newEnv);

        //  Test expected failure call to class method because of missing loader user credentials.
        AppConfig configProps = ApiServiceUtil.getAppConfigProperties();
    }

}
