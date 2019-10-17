package hu.icell.graphql.rest;

import graphql.ExecutionResult;
import graphql.GraphQL;
import hu.icell.graphql.converter.schema.GraphQLSchemaConverter;
import org.slf4j.Logger;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("/o2g")
@io.swagger.v3.oas.annotations.Hidden
public class O2GEndpoint {

    @Inject
    private Logger logger;

    @Inject
    private GraphQLSchemaConverter schemaConverter;

    public O2GEndpoint(){

    }

    @POST
    @Produces("application/json")
    @io.swagger.v3.oas.annotations.Hidden
    public Response gql(String query) {
        if (query == null || query.isEmpty()) {
            return Response.status(400).build();
        }
        try {
            GraphQL graphQL = schemaConverter.ConvertToGraphQLSchema("http://127.0.0.1:8080/poc/openapi.json");
            ExecutionResult result = graphQL.execute(query);
            if (result.getErrors() != null && result.getErrors().size() > 0) {
                return Response.serverError().entity(result.getErrors()).build();
            }
            return Response.ok(result.getData()).build();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return Response.serverError().build();
        }
    }
}
