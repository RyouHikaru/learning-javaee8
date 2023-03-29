package com.pedantic.config;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.CacheControl;

public class DynamicFilter implements ContainerResponseFilter {
	
	int age;
	
	public DynamicFilter() {
		
	}
	
	public DynamicFilter(int age) {
		this.age = age;
	}

	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
			throws IOException {
		if (requestContext.getMethod().equalsIgnoreCase("GET")) {
			CacheControl cacheControl = new CacheControl();
			cacheControl.setMaxAge(age);
			responseContext.getHeaders().add("Cache-Control", cacheControl);
			responseContext.getHeaders().add("Dynamic-Filter", "My Dynamic Filter was invoked. It works!");
		}
	}

}
