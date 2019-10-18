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
import org.openapitools.codegen.CliOption;
import org.openapitools.codegen.ClientOptInput;
import org.openapitools.codegen.CodegenConfig;
import org.openapitools.codegen.CodegenConfigLoader;
import org.openapitools.codegen.DefaultGenerator;

import static graphql.schema.idl.TypeRuntimeWiring.newTypeWiring;

import hu.icell.graphql.converter.schema.GraphQLSchemaConverter;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.OpenAPIV3Parser;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletContext;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

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
            }else if(openAPI.getPaths().get(key).getPost()!=null){
                String operationId = openAPI.getPaths().get(key).getPost().getOperationId();
                wiringBuilder.type(newTypeWiring("Mutation")
                        .dataFetcher(operationId, new OpenApiDataFetcher(key, "http://127.0.0.1:8080/poc")));
            }
        });

        ClientOptInput clientOptInput = new ClientOptInput();

        clientOptInput.openAPI( openAPI);
        CodegenConfig codegenConfig = CodegenConfigLoader.forName("graphql-schema");
        codegenConfig.additionalProperties().put("openAPI", openAPI);
        String outputFolder = getTmpFolder().getAbsolutePath() + File.separator;
        codegenConfig.setOutputDir(outputFolder);

        DefaultGenerator defaultGenerator = new DefaultGenerator();
        
        clientOptInput.config(codegenConfig);
        defaultGenerator.opts(clientOptInput);
        defaultGenerator.setGenerateMetadata(false);
        List<File> files = defaultGenerator.generate();
        StringBuilder schema = new StringBuilder();
        for (File file : files) {
            Scanner scanner = new Scanner(file);
            while(scanner.hasNextLine()) {
              schema.append(scanner.nextLine());
              schema.append("\n");
            }
        }
        /*
        InputStream inStream = context.getResourceAsStream("/WEB-INF/schema.gqs");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inStream, StandardCharsets.UTF_8));
        String inputLine;
        StringBuilder sdl = new StringBuilder();
        while ((inputLine = bufferedReader.readLine()) != null) {
            sdl.append(inputLine);
        }
        bufferedReader.close();
        TypeDefinitionRegistry typeRegistry = new SchemaParser().parse(sdl.toString());
        */
        String gqlSchema = schema.toString().replaceAll("\\(\\)","").replaceAll("query", "Query");
        File tempFile = File.createTempFile("gqlpoc","poc");
        BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
        writer.write(gqlSchema);
        writer.close();
        TypeDefinitionRegistry typeRegistry = new SchemaParser().parse(gqlSchema);
        RuntimeWiring runtimeWiring = wiringBuilder.build();
        SchemaGenerator schemaGenerator = new SchemaGenerator();
        GraphQLSchema graphQLSchema = schemaGenerator.makeExecutableSchema(typeRegistry, runtimeWiring);
        return GraphQL.newGraphQL(graphQLSchema).build();
    }

    private static File getTmpFolder() {
        try {
            File outputFolder = File.createTempFile("codegen-", "-tmp");
            outputFolder.delete();
            outputFolder.mkdir();
            outputFolder.deleteOnExit();
            return outputFolder;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Cannot access tmp folder");
        }
    }

}