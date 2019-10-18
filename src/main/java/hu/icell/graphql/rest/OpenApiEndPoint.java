/*
 * Copyright (c) 2019 i-Cell Mobilsoft Zrt. All rights reserved
 * Author: Péter Németh
 * This code is licensed under MIT license (see LICENSE.md for details)
 */
package hu.icell.graphql.rest;

import hu.icell.mock.UserCatRepository;
import hu.icell.mock.entity.Cat;
import hu.icell.mock.repository.MockRepository;
import hu.icell.mock.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

/**
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
    @Operation(responses = {
            @ApiResponse(content = @Content(mediaType = "application/json", schema = @Schema(implementation = Cat.class)))
    })
    public Response GetCat(@PathParam("id") int id){
        return Response.ok(repository.findCatById(id)).build();
    }

    @GET    
    @Produces("application/json")
    @Path("/users")
    @Operation(responses = {
            @ApiResponse(content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = User.class))))
    })
    public Response GetUsers(){
        return Response.ok(repository.getUsers()).build();
    }
    @POST
    @Path("/user/{id}")
    @Produces("application/json")
    @Operation(responses = {
            @ApiResponse(content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class)))
    })
    public Response UpdateUser(@PathParam("id") int id, @RequestBody User user) {
        return Response.ok(repository.findCatById(id)).build();
    }

    @GET    
    @Produces("application/json")
    @Path("/cats/{isLive}")
    @Operation(responses = {
            @ApiResponse(content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = Cat.class))))
    })
    public Response GetCats(@PathParam("isLive") Boolean isLive){
        if(isLive == null){
            return Response.ok(repository.getCats()).build();
        }
        return Response.ok(repository.findCatByLive(isLive)).build();
    }

    @GET
    @Produces("application/json")
    @Path("/cats")
    @Operation(responses = {
            @ApiResponse(content = @Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = Cat.class))))
    })
    public Response GetAllCats() {
        return Response.ok(repository.getCats()).build();
    }

    @GET    
    @Produces("application/json")
    @Path("/userbyname/{name}")
    @Operation(responses = {
            @ApiResponse(content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class)))
    })
    public Response GetUserByName(@PathParam("name") String name){
        return Response.ok(repository.findUserByName(name)).build();
    }

    @GET    
    @Produces("application/json")
    @Path("/catbyname/{name}")
    @Operation(responses = {
            @ApiResponse(content = @Content(mediaType = "application/json", schema = @Schema(implementation = Cat.class)))
    })
    public Response GetCatByName(@PathParam("name") String name){
        return Response.ok(repository.findCatByName(name)).build();
    }
}
