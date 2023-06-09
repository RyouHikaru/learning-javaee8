package com.pedantic.config;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class SecurityExceptionMapper implements ExceptionMapper<SecurityException> {

	@Override
	public Response toResponse(SecurityException exception) {
        return Response.status(Response.Status.UNAUTHORIZED).entity(exception.getMessage())
                .build();
	}

}
