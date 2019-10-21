/*
 * Copyright (c) 2019 i-Cell Mobilsoft Zrt. All rights reserved
 * Author: Péter Németh
 * This code is licensed under MIT license (see LICENSE.md for details)
 */
package hu.icell.graphql.openapi;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import hu.icell.graphql.converter.json.JsonToGraphQLConverter;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import javax.json.Json;
import javax.json.JsonReader;
import javax.json.JsonStructure;
import java.io.IOException;
import java.io.StringReader;
import java.util.Objects;

public class OpenApiDataFetcher implements DataFetcher<Object> {

    private final String urlTemplate;
    private String url;
    private final String baseUrl;

    OpenApiDataFetcher(String urlTemplate, String baseUrl) {
        this.urlTemplate = urlTemplate;
        this.baseUrl = baseUrl;
    }

    @Override
    public Object get(DataFetchingEnvironment dataFetchingEnvironment) throws IOException {
        url = urlTemplate;
        dataFetchingEnvironment.getArguments().forEach((name, value) -> {
            String paramname = name;
            if(paramname.endsWith("_")) {
                paramname = name.substring(0, name.length()-1).toLowerCase();
            }
            url = url.replace("{" + paramname + "}", value.toString());
        });
        url = url.replaceAll("(\\/)?\\{[A-z,0-9]+}", "");
        return getOpenApiResponse(dataFetchingEnvironment, url);
    }

    private Object getOpenApiResponse(DataFetchingEnvironment dataFetchingEnvironment, String url) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(baseUrl+url).build();
        Response response = client.newCall(request).execute();
        if(response.isSuccessful() && response.body() != null) {
            JsonReader jsonReader = Json.createReader(new StringReader(Objects.requireNonNull(response.body()).string()));
            JsonStructure jsonStructure = jsonReader.read();
            jsonReader.close();
            return new JsonToGraphQLConverter(dataFetchingEnvironment).ConvertToGraphQLResponse(jsonStructure);
        }
        return null;
    }
}
