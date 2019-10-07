/*
 * Copyright (c) 2019 i-Cell Mobilsoft Zrt. All rights reserved
 * Author: Péter Németh (Pethical)
 * This code is licensed under MIT license (see LICENSE.md for details)
 */
package hu.icell.graphql.openapi;

import graphql.GraphQL;
import graphql.schema.*;
import graphql.schema.idl.*;
import graphql.schema.idl.RuntimeWiring.Builder;

import static graphql.schema.idl.TypeRuntimeWiring.newTypeWiring;

import hu.icell.graphql.converter.schema.GraphQLSchemaConverter;
import hu.icell.graphql.openapi.OpenApiDataFetcher;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.parser.OpenAPIV3Parser;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletContext;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author peter.nemeth
 */
@Singleton
public class OpenApiSchemaConverter implements GraphQLSchemaConverter {

    @Inject
    private ServletContext context;

    public GraphQL ConvertToGraphQLSchema(String OpenApiJsonUrl) throws IOException {
        OpenAPI openAPI = new OpenAPIV3Parser().read(OpenApiJsonUrl);
        Builder wiringBuilder = RuntimeWiring.newRuntimeWiring();

        openAPI.getPaths().keySet().forEach((key) -> {
            if (openAPI.getPaths().get(key).getGet() != null) {
                String operationId = openAPI.getPaths().get(key).getGet().getOperationId();
                wiringBuilder.type(newTypeWiring("Query")
                        .dataFetcher(operationId, new OpenApiDataFetcher(key, "http://127.0.0.1:8080/poc")));
            }
        });

        InputStream inStream = context.getResourceAsStream("/WEB-INF/schema.gqs");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inStream, StandardCharsets.UTF_8));
        String inputLine;
        StringBuilder sdl = new StringBuilder();
        while ((inputLine = bufferedReader.readLine()) != null) {
            sdl.append(inputLine);
        }
        bufferedReader.close();
        TypeDefinitionRegistry typeRegistry = new SchemaParser().parse(sdl.toString());
        RuntimeWiring runtimeWiring = wiringBuilder.build();
        SchemaGenerator schemaGenerator = new SchemaGenerator();
        GraphQLSchema graphQLSchema = schemaGenerator.makeExecutableSchema(typeRegistry, runtimeWiring);
        return GraphQL.newGraphQL(graphQLSchema).build();
    }
}