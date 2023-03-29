package com.pedantic.config;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.ext.Provider;

// Invoked before a Request
@Provider
@PreMatching
public class PreMatchingServerRequestFilter implements ContainerRequestFilter {

	@Inject
	Logger logger;
	
	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		logger.log(Level.INFO, "Original http method " + requestContext.getMethod());
		
		String httpMethod = requestContext.getHeaderString("X-Http-Method-Override");
		
		if (httpMethod != null && !httpMethod.isEmpty()) {
			logger.log(Level.INFO, "Http method " + httpMethod);
			requestContext.setMethod(httpMethod);
			logger.log(Level.INFO, "Modified Http method " + requestContext.getMethod());
		}
	}

}
