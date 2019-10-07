/*
 * Copyright (c) 2019 i-Cell Mobilsoft Zrt. All rights reserved
 * Author: Péter Németh (Pethical)
 * This code is licensed under MIT license (see LICENSE.md for details)
 */
package hu.icell.graphql.openapi;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import hu.icell.graphql.converter.json.JsonToGraphQLConverter;

import javax.json.JsonStructure;
import java.io.IOException;

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
            url = url.replace("{" + name.toString() + "}", value.toString());
        });
        url = url.replaceAll("(\\/)?\\{[A-z,0-9]+}", "");
        JsonStructure jsonStructure = new OpenApiClient().sendGet(baseUrl+url);
        return new JsonToGraphQLConverter(dataFetchingEnvironment).ConvertToGraphQLResponse(jsonStructure);
    }
}
