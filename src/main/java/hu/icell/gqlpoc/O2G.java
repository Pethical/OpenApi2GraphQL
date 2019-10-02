/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.icell.gqlpoc;

import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.schema.*;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.RuntimeWiring.Builder;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import static graphql.schema.idl.TypeRuntimeWiring.newTypeWiring;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.OpenAPIV3Parser;

import javax.json.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

/**
 * @author peter.nemeth
 */
public class O2G {

    private static final HashMap<String, String> pathMap = new HashMap<>();
    private static final String OpenApiJson = "http://127.0.0.1:8080/poc/openapi.json";
    private static final String sdl = "type Query{GetUsers: [User]\nGetCats: [Cat]\nGetCat(id: Int): Cat\nGetUser(id: Int): User}\ntype User{ name : String, id: Int, regDate: String, cats:[Cat] }\ntype Cat{name:String, id: Int, live:Boolean}";
    private static GraphQL graphQL = null;
    private static GraphQLSchema graphQLSchema;

    public static void Init() {
        OpenAPI openAPI = new OpenAPIV3Parser().read(OpenApiJson);
        Builder wiringBuilder = RuntimeWiring.newRuntimeWiring();
        openAPI.getPaths().keySet().forEach((key) -> {
            if (openAPI.getPaths().get(key).getGet() != null) {

                wiringBuilder.type(newTypeWiring("Query")
                        .dataFetcher(openAPI.getPaths().get(key).getGet().getOperationId(),
                                getFetcher(openAPI.getPaths().get(key).getGet().getOperationId())));
                System.out.println(openAPI.getPaths().get(key).getGet().getOperationId());
                pathMap.put(openAPI.getPaths().get(key).getGet().getOperationId(), key);
                if (openAPI.getPaths().get(key).getGet().getParameters() != null) {
                    for (int i = 0; i < openAPI.getPaths().get(key).getGet().getParameters().size(); i++) {
                        System.out.println("\t" +
                                openAPI.getPaths().get(key).getGet().getParameters().get(i).getName()
                        );
                    }
                }
            }
        });

        TypeDefinitionRegistry typeRegistry = new SchemaParser().parse(sdl);
        RuntimeWiring runtimeWiring = wiringBuilder.build();
        SchemaGenerator schemaGenerator = new SchemaGenerator();
        graphQLSchema = schemaGenerator.makeExecutableSchema(typeRegistry, runtimeWiring);
        graphQL = GraphQL.newGraphQL(graphQLSchema).build();
    }

    public static ExecutionResult doQuery(String query) {
        ExecutionResult result = graphQL.execute(query);
        java.util.LinkedHashMap hashmap = (java.util.LinkedHashMap) result.getData();
        System.out.println(hashmap);
        return result;
    }

    private static String sendGet(String url) throws Exception {

        URL obj = new URL("http://127.0.0.1:8080" + url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", "O2G");
        int responseCode = con.getResponseCode();
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream(), "UTF-8"));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }


    public static Object ConvertType(JsonObject jsonToken, GraphQLType type, String name) {
        if (type instanceof GraphQLList) {
            GraphQLType wrappedtype = ((GraphQLList) type).getWrappedType();
            JsonArray array = jsonToken.getJsonArray(name);
            List<Object> list = new ArrayList<>();
            for (int i = 0; i < array.size(); i++) {
                Map<String, Object> properties = new LinkedHashMap<>();
                for (String s : array.getJsonObject(i).keySet()) {
                    System.out.println("List item: " + s + " " + wrappedtype.getName());
                    properties.put(s, ConvertType(array.getJsonObject(i), graphQLSchema.getObjectType(wrappedtype.getName()).getFieldDefinition(s).getType(), s));
                }
                list.add(properties);
            }
            return list;
        }

        switch (type.getName()) {
            case "Int":
                return jsonToken.getInt(name);
            case "String":
                return jsonToken.getString(name);
            case "Float":
                return jsonToken.getJsonNumber(name).doubleValue();
            case "Boolean":
                return jsonToken.getBoolean(name);
            default:
                return jsonToken;
        }
    }

    public static Map<String, Object> MapJson(JsonObject jsonObject, DataFetchingEnvironment dfe) {
        Map<String, Object> customAttributes = new LinkedHashMap<>();
        System.out.println(dfe.getField().getName() + " => " + dfe.getFieldType().getName());
        GraphQLObjectType objectType = graphQLSchema.getObjectType(dfe.getFieldType().getName());
        for (String s : jsonObject.keySet()) {
            System.out.print(s + " => ");
            if (objectType.getFieldDefinition(s) != null && objectType.getFieldDefinition(s).getType() != null) {
                System.out.println(objectType.getFieldDefinition(s).getType().getName());
                GraphQLType type = objectType.getFieldDefinition(s).getType();
                if (type instanceof GraphQLList) {
                    System.out.println(dfe.getField().getName() + " WRAPPED => " + type.getName());
                }
                Object o = ConvertType(jsonObject, type, s);
                customAttributes.put(s, o);
            } else {
                System.out.println("NULL");
            }
        }
        return customAttributes;
    }

    public static Map<String, Object> MapList(JsonObject jsonObject, DataFetchingEnvironment dfe) {
        Map<String, Object> customAttributes = new LinkedHashMap<>();
        System.out.println(dfe.getField().getName() + " => " + dfe.getFieldType().getName());
        GraphQLType type = dfe.getFieldDefinition().getType();
        if (!(type instanceof GraphQLList)) return customAttributes;
        type = ((GraphQLList) type).getWrappedType();
        GraphQLObjectType objectType = graphQLSchema.getObjectType(type.getName());
        for (String s : jsonObject.keySet()) {
            if (objectType.getFieldDefinition(s) != null && objectType.getFieldDefinition(s).getType() != null) {
                if (type.getName() == null) return customAttributes;
                System.out.println(type.getName() + " = " + s);
                Object o = ConvertType(jsonObject, objectType.getFieldDefinition(s).getType(), s);
                customAttributes.put(s, o);
            } else {
                System.out.println("NULL");
            }
        }
        return customAttributes;
    }

    static String url;

    public static DataFetcher getFetcher(String path) {
        return new DataFetcher() {
            @Override
            public Object get(DataFetchingEnvironment dfe) throws Exception {
                url = pathMap.get(path);
                dfe.getArguments().forEach((name, value) -> {
                    url = url.replace("{" + name.toString() + "}", value.toString());
                });
                url = url.replaceAll("(\\/)?\\{[A-z,0-9]+}", "");
                System.out.println(url);
                String json = sendGet(url);
                JsonReader jsonReader = Json.createReader(new StringReader(json));
                JsonStructure structure = jsonReader.read();
                jsonReader.close();
                if (structure.getValueType() == JsonValue.ValueType.OBJECT) {
                    JsonObject object = (JsonObject) structure;
                    return MapJson(object, dfe);
                } else if (structure.getValueType() == JsonValue.ValueType.ARRAY) {
                    JsonArray array = (JsonArray) structure;
                    List<Map<String, Object>> map = new ArrayList<>();
                    for (int i = 0; i < array.size(); i++) {
                        map.add(MapList(array.getJsonObject(i), dfe));
                    }
                    return map;
                }
                return null;
            }
        };
    }
}
