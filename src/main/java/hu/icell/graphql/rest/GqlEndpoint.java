/*
 * Copyright (c) 2019 i-Cell Mobilsoft Zrt. All rights reserved
 * Author: Péter Németh
 * This code is licensed under MIT license (see LICENSE.md for details)
 */
package hu.icell.graphql.rest;

import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.GraphQLSchema;
import hu.icell.mock.CatService;
import hu.icell.mock.UserService;
import hu.icell.mock.repository.MockRepository;
import hu.icell.mock.UserCatRepository;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.Produces;

import io.leangen.graphql.GraphQLSchemaGenerator;
import javax.ws.rs.POST;

@Path("/gql")
@io.swagger.v3.oas.annotations.Hidden
public class GqlEndpoint {

    private final GraphQL graphQL;

    public GqlEndpoint() {
        UserCatRepository repository = new MockRepository();
        GraphQLSchema schema = new GraphQLSchemaGenerator()
                .withOperationsFromSingleton(new UserService(repository))
                .withOperationsFromSingleton(new CatService(repository))
                .generate();
        graphQL = new GraphQL.Builder(schema).build();
    }
    
    @POST
    @Produces("application/json")
    @io.swagger.v3.oas.annotations.Hidden
    public Response query(String query) {
        if (query == null || query.isEmpty()) {
            return Response.status(400).build();
        }
        ExecutionResult result = graphQL.execute(query);
        if (result.getErrors() != null && result.getErrors().size() > 0) {
            return Response.serverError().entity(result.getErrors()).build();
        }
        return Response.ok(result.getData()).build();
    }
}
