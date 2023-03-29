package com.pedantic.resources;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.time.Month;
//import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;

import com.pedantic.config.Secure;
import com.pedantic.entities.Employee;
import com.pedantic.service.PersistenceService;
import com.pedantic.service.QueryService;

@Path("employees")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EmployeeResource {
	
	@Inject
	Logger logger;
	
	@Context
	private UriInfo uriInfo;
	
	@Inject
	QueryService queryService;
	
	@Inject
	PersistenceService persistenceService;
	
	/**
	 * GET Request to retrieve all employees
	 * 
	 * @return List of Employees
	 */
	@GET
	@Path("employees")
//	@Produces("application/xml")
	@Secure
	public Response getEmployees(@Context HttpHeaders httpHeaders) {
		MediaType mediaType = httpHeaders.getAcceptableMediaTypes().get(0); // Get the media type of the highest priority
		
		return Response.ok(queryService.getEmployees()).status(Response.Status.FOUND).build();
	}
	
	/**
	 * GET Request to retrieve an employee by id
	 * 
	 * @param id
	 * @return Employee
	 */
	@GET
	@Path("employees/{id: \\d+}") // Restrict path input via regular expression
	public Response getEmployeeById(@PathParam("id") @DefaultValue("0") Long id, @Context Request request) {
		Employee employee = queryService.findEmployeeById(id);
		CacheControl cacheControl = new CacheControl(); // Add Cache
		cacheControl.setMaxAge(1000);
		
		EntityTag entityTag = new EntityTag(UUID.randomUUID().toString());
		
		ResponseBuilder responseBuilder = request.evaluatePreconditions(entityTag);
		
		if (responseBuilder != null) {
			responseBuilder.cacheControl(cacheControl);
			
			return responseBuilder.build();
		}
		responseBuilder = Response.ok(employee);
		responseBuilder.tag(entityTag);
		responseBuilder.cacheControl(cacheControl);
		
		return responseBuilder.build();
		
//		return Response.ok(queryService.findEmployeeById(id)).status(Response.Status.FOUND).build();
	}
	
	@GET
	@Path("id") // Path will be /id/id?={id}
	public Employee findEmployeeById(@QueryParam("id") @DefaultValue("0") Long id) {
		return queryService.findEmployeeById(id);
	}
	
	/**
	 * POST Request to create an employee
	 * 
	 * @param employee
	 */
	@POST
	@Path("employees")
//	@Consumes("application/xml")
	public Response createEmployee(@Valid Employee employee) { // Validate from Resource Layer instead from Persistence Layer
		persistenceService.saveEmployee(employee);
		
		URI uri = uriInfo.getAbsolutePathBuilder().path(employee.getId().toString()).build();
		return Response.created(uri).status(Response.Status.CREATED).build();
	}
	
	@POST
	@Path("upload") // employees/upload?id={id}
	@Consumes({MediaType.APPLICATION_OCTET_STREAM, "image/png", "image/jpeg", "image/jpg"})
	@Produces(MediaType.TEXT_PLAIN)
	public Response uploadPicture(File picture, @QueryParam("id") @NotNull Long id) {
		Employee employee = queryService.findEmployeeById(id);
		
		try (Reader reader = new FileReader(picture)) {
			employee.setPicture(Files.readAllBytes(Paths.get(picture.toURI())));
			persistenceService.saveEmployee(employee);
			
			int totalSize = 0;
			int count = 0;
			final char[] buffer = new char[256];
			while ((count = reader.read(buffer)) != -1) {
				totalSize += count;
			}
			
			return Response.ok(totalSize).build();
		} catch (IOException e) {
			e.printStackTrace();
			return Response.serverError().build();
		}
	}
	
	@GET
	@Path("download") // employees/download?id={id}
	@Produces({MediaType.APPLICATION_OCTET_STREAM, "image/png", "image/jpeg", "image/jpg"})
	public Response getEmployeePicture(@QueryParam("id") @NotNull Long id) throws IOException {
		NewCookie userId = new NewCookie("userId", id.toString());
		Employee employee = queryService.findEmployeeById(id);
		
		if (employee != null) {
			return Response.ok()
					.entity(Files.write(Paths.get("pic.png"), employee.getPicture()).toFile())
					.cookie(userId)
					.build();
		}
		
		return Response.noContent().build();
	}
	
	@DELETE
	@Path("{id: \\d+}") // api/v1/employees/{id} - DELETE
	public Response terminateEmployee(@PathParam("id") @NotNull Long id) {
		return Response.ok().build();
	}

}
