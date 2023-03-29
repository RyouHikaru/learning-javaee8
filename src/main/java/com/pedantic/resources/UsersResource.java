package com.pedantic.resources;

import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import com.pedantic.entities.ApplicationUser;

@Path("users")
//@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UsersResource {

	@POST
	@Path("form")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED) // Used for simple form like Login
	public Response createNewUser(
			@FormParam("email") String email,
			@FormParam("password") String password,
			@HeaderParam("Referer") String referer) {
		
		return null;
	}

	@POST
	@Path("map")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response createNewUser(MultivaluedMap<String, String> formMap, @Context HttpHeaders httpHeaders) { // Usually used when form fields are more than 2
		httpHeaders.getHeaderString("Referer");
		httpHeaders.getRequestHeader("Referer").get(0);
		
		for (String h : httpHeaders.getRequestHeaders().keySet()) {
			System.out.println("Header key set: " + h);
		}
		
		String email = formMap.getFirst("email");
		String password = formMap.getFirst("password");
		
		return null;
	}
	
	@POST
	@Path("bean")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response createNewUser(@BeanParam ApplicationUser applicationUser, @CookieParam("user") String user) {
		return null;
	}
	
	// Accept: application/json;q=0.7, application/xml
	@GET
	@Path("{id}")
	@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public Response getUserById(@PathParam("id") Long id) {
		
		return null;
	}
}
