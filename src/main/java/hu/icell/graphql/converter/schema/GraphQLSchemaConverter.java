/*
 * Copyright (c) 2019 i-Cell Mobilsoft Zrt. All rights reserved
 * Author: Péter Németh
 * This code is licensed under MIT license (see LICENSE.md for details)
 */
package hu.icell.graphql.converter.schema;

import graphql.GraphQL;
import hu.icell.graphql.converter.schema.configuration.AbstractSchemaConverterConfiguration;

public interface GraphQLSchemaConverter<T extends AbstractSchemaConverterConfiguration> {
    GraphQL ConvertToGraphQLSchema(T configuration) throws Exception;
}
