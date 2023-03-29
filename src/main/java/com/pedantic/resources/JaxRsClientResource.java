package com.pedantic.resources;

import javax.inject.Inject;
import javax.validation.constraints.NotEmpty;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;

@Path("client")
@Produces(MediaType.APPLICATION_JSON)
public class JaxRsClientResource {

    @Inject
    JaxRsClient jaxRsClient;


    @Path("breach/{email}")
    @GET
    public Response checkBreaches(@PathParam("email") @NotEmpty String email) {
        int breaches = jaxRsClient.checkBreaches(email);
        return Response.ok(breaches + " breaches found for email " + email).build();
    }
}