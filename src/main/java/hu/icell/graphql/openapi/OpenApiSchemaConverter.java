/*
 * Copyright (c) 2019 i-Cell Mobilsoft Zrt. All rights reserved
 * Author: Péter Németh
 * This code is licensed under MIT license (see LICENSE.md for details)
 */
package hu.icell.graphql.openapi;

import graphql.GraphQL;
import graphql.schema.*;
import graphql.schema.idl.*;
import graphql.schema.idl.RuntimeWiring.Builder;
import hu.icell.graphql.converter.cache.SchemaCache;
import hu.icell.graphql.converter.schema.AbstractGraphQLSchemaConverter;
import hu.icell.graphql.openapi.configuration.OpenApiEndPointConfiguration;
import hu.icell.security.Hasher;
import org.openapitools.codegen.ClientOptInput;
import org.openapitools.codegen.CodegenConfig;
import org.openapitools.codegen.CodegenConfigLoader;
import org.openapitools.codegen.DefaultGenerator;

import static graphql.schema.idl.TypeRuntimeWiring.newTypeWiring;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.OpenAPIV3Parser;

import java.io.*;
import java.util.List;
import java.util.Scanner;

/**
 * @author peter.nemeth
 */
public class OpenApiSchemaConverter extends AbstractGraphQLSchemaConverter<OpenApiEndPointConfiguration> {

    private final Hasher hasher;

    public OpenApiSchemaConverter(SchemaCache schemaCache, Hasher hasher) {
        super(schemaCache);
        this.hasher = hasher;
    }

    public GraphQL ConvertToGraphQLSchema(OpenApiEndPointConfiguration endPoint) throws IOException {
        String openAPISchema = null;
        if(hasher!=null && getSchemaCache()!=null) {
            String key = hasher.SHA256(endPoint.getOpenAPISchemaUrl());
            try {
                openAPISchema = getSchemaCache().getItem(key);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        OpenAPI openAPI;
        if(openAPISchema != null) {
            openAPI = new OpenAPIV3Parser().readContents(openAPISchema).getOpenAPI();
        }else {
            openAPI = new OpenAPIV3Parser().read(endPoint.getOpenAPISchemaUrl());
        }
        Builder wiringBuilder = RuntimeWiring.newRuntimeWiring();
        wireOpenAPIEndpoints(endPoint, openAPI, wiringBuilder);

        String gqlSchema = null;
        try {
            gqlSchema = getGraphQLSchemaText(endPoint);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(gqlSchema==null || gqlSchema.isEmpty()) {
            gqlSchema = GenerateSchema(endPoint, openAPI);
        }
        assert !gqlSchema.isEmpty() : "Invalid or empty schema";
        TypeDefinitionRegistry typeRegistry = new SchemaParser().parse(gqlSchema);
        RuntimeWiring runtimeWiring = wiringBuilder.build();
        SchemaGenerator schemaGenerator = new SchemaGenerator();
        GraphQLSchema graphQLSchema = schemaGenerator.makeExecutableSchema(typeRegistry, runtimeWiring);
        return GraphQL.newGraphQL(graphQLSchema).build();
    }

    private void wireOpenAPIEndpoints(OpenApiEndPointConfiguration endPoint, OpenAPI openAPI, Builder wiringBuilder) {
        openAPI.getPaths().keySet().forEach((key) -> {
            if (openAPI.getPaths().get(key).getGet() != null) {
                String operationId = openAPI.getPaths().get(key).getGet().getOperationId();
                wiringBuilder.type(newTypeWiring("Query")
                        .dataFetcher(operationId, new OpenApiDataFetcher(key, endPoint.getBaseUrl())));
            } else if(openAPI.getPaths().get(key).getPost()!=null){
                String operationId = openAPI.getPaths().get(key).getPost().getOperationId();
                wiringBuilder.type(newTypeWiring("Mutation")
                        .dataFetcher(operationId, new OpenApiDataFetcher(key, endPoint.getBaseUrl())));
            }
        });
    }

    private String GenerateSchema(OpenApiEndPointConfiguration endPoint, OpenAPI openAPI) throws FileNotFoundException {

        DefaultGenerator defaultGenerator = getDefaultGenerator(openAPI);
        String gqlSchema = parseGeneratedFiles(defaultGenerator);
        try {
            setGraphQLSchemaText(endPoint, gqlSchema);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return gqlSchema;
    }

    private String parseGeneratedFiles(DefaultGenerator defaultGenerator) throws FileNotFoundException {
        List<File> files = defaultGenerator.generate();
        StringBuilder schema = new StringBuilder();
        for (File file : files) {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                schema.append(scanner.nextLine());
                schema.append("\n");
            }
        }
        return schema.toString().replaceAll("\\(\\)", "").replaceAll("query", "Query");
    }

    private DefaultGenerator getDefaultGenerator(OpenAPI openAPI) {
        File tempFolder = getTmpFolder();
        assert tempFolder!=null;
        String outputFolder = tempFolder.getAbsolutePath() + File.separator;
        CodegenConfig codegenConfig = CodegenConfigLoader.forName("graphql-schema");
        codegenConfig.additionalProperties().put("openAPI", openAPI);
        codegenConfig.setOutputDir(outputFolder);

        ClientOptInput clientOptInput = new ClientOptInput();
        clientOptInput.openAPI(openAPI);
        clientOptInput.config(codegenConfig);

        DefaultGenerator defaultGenerator = new DefaultGenerator();
        defaultGenerator.opts(clientOptInput);
        defaultGenerator.setGenerateMetadata(false);
        return defaultGenerator;
    }

    private File getTmpFolder() {
        try {
            /*
              @Todo File => String
             */
            File outputFolder = File.createTempFile("codegen-", "-tmp");
            if(!outputFolder.delete()) return null;
            if(!outputFolder.mkdir()) return null;
            outputFolder.deleteOnExit();
            return outputFolder;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Cannot access tmp folder");
        }
    }

}