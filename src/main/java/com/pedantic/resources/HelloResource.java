package com.pedantic.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

@Path("hello")
public class HelloResource {

	/**
	 * GET Request to say "Hello"
	 * 
	 * @param name
	 * @return Response
	 */
	@GET
	@Path("{name}")
	public Response sayHello(@PathParam("name") String name) {
		String greeting = "Hello " + name;
		return Response.ok(greeting).build();
	}
	
	@GET
	@Path("greet")
	public String greet() {
		return "Hello World!";
	}
}
