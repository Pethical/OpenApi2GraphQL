/*
 * Copyright (c) 2019 i-Cell Mobilsoft Zrt. All rights reserved
 * Author: Péter Németh
 * This code is licensed under MIT license (see LICENSE.md for details)
 */
package hu.icell.graphql.converter.schema;

import graphql.GraphQL;
import hu.icell.graphql.converter.cache.SchemaCache;
import hu.icell.graphql.converter.schema.configuration.AbstractSchemaConverterConfiguration;

public abstract class AbstractGraphQLSchemaConverter<T extends AbstractSchemaConverterConfiguration>
        implements GraphQLSchemaConverter<T> {

    private SchemaCache schemaCache;
    private String graphQLSchema;

    public AbstractGraphQLSchemaConverter(SchemaCache schemaCache){
        this.schemaCache = schemaCache;
    }

    protected void setGraphQLSchemaText(T configuration, String schema) throws Exception {
        graphQLSchema = schema;
        if(schemaCache!=null)
            schemaCache.setItem(configuration.getUniqueId(), graphQLSchema);
    }

    protected String getGraphQLSchemaText(T configuration) throws Exception {
        if(schemaCache!=null) {
            graphQLSchema = schemaCache.getItem(configuration.getUniqueId());
        }
        return graphQLSchema;
    }

    protected SchemaCache getSchemaCache() {
        return schemaCache;
    }

    public abstract GraphQL ConvertToGraphQLSchema(T configuration) throws Exception;
}
