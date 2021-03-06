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
package com.hpe.caf.services.audit.api.generated;

import com.hpe.caf.services.audit.api.TenantAddPost;
import com.hpe.caf.services.audit.api.TenantUpdatePartitionsPost;
import com.hpe.caf.services.audit.api.exceptions.BadRequestException;
import com.hpe.caf.services.audit.api.exceptions.NotFoundException;
import com.hpe.caf.services.audit.api.generated.model.NewTenant;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2016-02-09T14:45:42.907Z")
public class TenantsApiServiceImpl extends TenantsApiService {

    @Override
    public Response tenantsPost(NewTenant newTenant, SecurityContext securityContext) throws Exception {

        try {
            TenantAddPost.addTenant(newTenant);
            return Response.status(Response.Status.OK).entity(new ApiResponseMessage("Success")).build();
        } catch(BadRequestException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(new ApiResponseMessage(e.getMessage())).build();
        } catch(Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new ApiResponseMessage(e.getMessage())).build();
        }
    }

    @Override
    public Response tenantsUpdatePartitionCountPost(String tenantId, String applicationId, SecurityContext securityContext) throws Exception{

        try {
            int partitionsAdded = TenantUpdatePartitionsPost.checkAndUpdatePartitions(tenantId, applicationId);
            return Response.ok().entity(new ApiPartitionResponseMessage(partitionsAdded, "Success")).build();
        } catch(NotFoundException e){
            return Response.status(Response.Status.NOT_FOUND).entity(new ApiPartitionResponseMessage(e.getMessage())).build();
        } catch(BadRequestException e){
            return Response.status(Response.Status.BAD_REQUEST).entity(new ApiPartitionResponseMessage(e.getMessage())).build();
        } catch (Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new ApiPartitionResponseMessage(e.getMessage())).build();
        }
    }
}
