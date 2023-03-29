package com.pedantic.resources;

import javax.validation.constraints.NotNull;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.pedantic.config.MaxAge;

@Path("departments")
public class DepartmentResource {

	@GET
	@Produces({"application/json; qs=0.9", "application/xml; qs=0.7"})
	public Response getDepartments() {
		return Response.ok().status(Response.Status.OK).build();
	}
	
	@GET
	@Path("{id}") // api/v1/departments/{id}
	@Produces("application/json")
	@MaxAge(age = 200)
	public Response getDepartmentById(@PathParam("id") @NotNull Long id) {
		// Fetch from DB
		
		return Response.ok().status(Response.Status.OK).build();
	}
}
