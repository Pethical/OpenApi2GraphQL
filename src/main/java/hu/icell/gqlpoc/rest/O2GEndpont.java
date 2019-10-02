package hu.icell.gqlpoc.rest;

import graphql.ExecutionResult;
import hu.icell.gqlpoc.O2G;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("/o2g")
public class O2GEndpont {
    public O2GEndpont(){
        O2G.Init();
    }

    @POST
    @Produces("application/json")
    public Response user(String query) {
        if (query == null || query.isEmpty()) {
            return Response.status(400).build();
        }
        ExecutionResult result = O2G.doQuery(query);
        if (result.getErrors() != null && result.getErrors().size() > 0) {
            return Response.serverError().entity(result.getErrors()).build();
        }
        return Response.ok(result.getData()).build();
    }

}
