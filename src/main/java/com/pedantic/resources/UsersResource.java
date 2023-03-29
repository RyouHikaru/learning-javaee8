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
import com.pedantic.service.PersistenceService;
import com.pedantic.service.SecurityUtil;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.security.Key;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import static javax.ws.rs.core.HttpHeaders.AUTHORIZATION;
import static javax.ws.rs.core.Response.Status.UNAUTHORIZED;

@Path("users")
@Consumes("application/json")
@Produces("application/json")
@RequestScoped
public class UsersResource {

    @Inject
    ApplicationUser applicationState;
    
    @Inject
    private SecurityUtil securityUtil;
    
    @Inject
    JaxRsClient jaxRsClient;
    
    @Inject
    PersistenceService persistenceService;

    @Context
    private UriInfo uriInfo;

    @Inject
    private Logger logger;

    @POST
    public Response createUser(@Valid ApplicationUser user) {
//    	System.out.println("User Resource createUser");
//    	System.out.println(user.toString());
        persistenceService.saveUser(user);
//
//        jaxRsClient.checkBreachesRx(user.getEmail());
//
        return Response.created(uriInfo.getAbsolutePathBuilder().path(user.getId().toString()).build())
                .status(Response.Status.OK).build();
        
//        return null;
    }

    @POST //api/v1/users/form
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path("form")
    public Response createNewUser(@FormParam("email") String email, @FormParam("password") String password, @HeaderParam("Referer") String referer) {

        return null;
    }

    @POST
    @Path("login")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response login(@FormParam("email") @NotEmpty(message = "Email must be set") String email,
    		@NotEmpty(message = "Password must be set") @FormParam("password") String password) {


            if (!securityUtil.authenticateUser(email, password)) {
                throw new SecurityException("Email or password incorrect");
            }
            
            applicationState.setEmail(email);
            String token = getToken(email);

            return Response.ok().header(AUTHORIZATION, "Bearer " + token).build();
        }


    @POST
    @Path("map")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response createNewUser(MultivaluedMap<String, String> formMap, @Context HttpHeaders httpHeaders) {

//        httpHeaders.getHeaderString("Referer");

        httpHeaders.getRequestHeader("Referer").get(0);

        for (String h : httpHeaders.getRequestHeaders().keySet()) {
            System.out.println("Header key set " + h);
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


    //    GET /api/v1/employees/employees HTTP/1.1
//    Host www.ourdomain.com
//    User-Agent: Java/1.8.0_151
//    Content-Type: text/plain;charset=utf-8
//    Accept: application/json;q=.7, application/xml
    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getUserById(@PathParam("id") Long id) {

        return Response.ok().status(Response.Status.OK).build();
    }

    private String getToken(String email) {
        Key key = securityUtil.generateKey(email);

        String token = Jwts.builder().setSubject(email).setIssuer(uriInfo.getAbsolutePath().toString())
                .setIssuedAt(new Date()).setExpiration(securityUtil.toDate(LocalDateTime.now().plusMinutes(15)))
                .signWith(SignatureAlgorithm.HS512, key).setAudience(uriInfo.getBaseUri().toString())
                .compact();

        logger.log(Level.INFO, "Generated token is {0}", token);

        return token;
    }

}
