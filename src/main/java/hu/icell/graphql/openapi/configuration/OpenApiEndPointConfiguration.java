/*
 * Copyright (c) 2019 i-Cell Mobilsoft Zrt. All rights reserved
 * Author: Péter Németh
 * This code is licensed under MIT license (see LICENSE.md for details)
 */
package hu.icell.graphql.openapi.configuration;

import hu.icell.graphql.converter.schema.configuration.AbstractSchemaConverterConfiguration;
import hu.icell.security.Hasher;

public class OpenApiEndPointConfiguration extends AbstractSchemaConverterConfiguration {

    private String baseUrl;
    private String openAPISchemaUrl;
    private Hasher hasher;

    public OpenApiEndPointConfiguration(String baseUrl, String openAPISchemaUrl, Hasher hasher)
    {
        this.baseUrl = baseUrl;
        this.openAPISchemaUrl = openAPISchemaUrl;
        this.hasher = hasher;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getOpenAPISchemaUrl() {
        return openAPISchemaUrl;
    }

    public void setOpenAPISchemaUrl(String openAPISchemaUrl) {
        this.openAPISchemaUrl = openAPISchemaUrl;
    }

    @Override
    public String getUniqueId() {
        return hasher.SHA256(baseUrl);
    }
}
