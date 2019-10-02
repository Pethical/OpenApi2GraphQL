/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.icell.gqlpoc.rest;

import hu.icell.gqlpoc.UserCatRepository;
import hu.icell.gqlpoc.entity.MockRepository;
import hu.icell.gqlpoc.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 *
 * @author peter.nemeth
 */
@Path("/api")
public class OpenApiEndPoint {
    
    private final UserCatRepository repository = new MockRepository();
    
    @GET
    @Path("/user/{id}")
    @Produces("application/json")
    @Operation(responses = {
            @ApiResponse(content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class)))
    })
    public Response GetUser(@PathParam("id") int id){
        return Response.ok(repository.findUserById(id)).build();
    }

    @GET    
    @Path("/cat/{id}")
    @Produces("application/json")
    public Response GetCat(@PathParam("id") int id){
        return Response.ok(repository.findCatById(id)).build();
    }

    @GET    
    @Produces("application/json")
    @Path("/users")
    public Response GetUsers(){
        return Response.ok(repository.getUsers()).build();
    }
    
    @GET    
    @Produces("application/json")
    @Path("/cats/{isLive}")
    public Response GetCats(@PathParam("isLive") Boolean isLive){
        if(isLive == null){
            return Response.ok(repository.getCats()).build();
        }
        return Response.ok(repository.findCatByLive(isLive)).build();
    }

    @GET
    @Produces("application/json")
    @Path("/cats")
    public Response GetAllCats() {
        return Response.ok(repository.getCats()).build();
    }

    @GET    
    @Produces("application/json")
    @Path("/userbyname/{name}")
    public Response GetUserByName(@PathParam("name") String name){
        return Response.ok(repository.findUserByName(name)).build();
    }

    @GET    
    @Produces("application/json")
    @Path("/catbyname/{name}")
    public Response GetCatByName(@PathParam("name") String name){
        return Response.ok(repository.findCatByName(name)).build();
    }
}
