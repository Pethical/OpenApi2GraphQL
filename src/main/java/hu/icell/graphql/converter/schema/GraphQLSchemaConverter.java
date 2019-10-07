/*
 * Copyright (c) 2019 i-Cell Mobilsoft Zrt. All rights reserved
 * Author: Péter Németh (Pethical)
 * This code is licensed under MIT license (see LICENSE.md for details)
 */
package hu.icell.graphql.converter.schema;

import graphql.GraphQL;

public interface GraphQLSchemaConverter {
    GraphQL ConvertToGraphQLSchema(String sourceUrl) throws Exception;
}
