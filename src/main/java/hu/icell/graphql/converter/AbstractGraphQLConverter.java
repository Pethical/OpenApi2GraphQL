/*
 * Copyright (c) 2019 i-Cell Mobilsoft Zrt. All rights reserved
 * Author: Péter Németh
 * This code is licensed under MIT license (see LICENSE.md for details)
 */
package hu.icell.graphql.converter;

import graphql.schema.*;

import java.util.*;

public abstract class AbstractGraphQLConverter<S> implements GraphQLConverter<S, Object> {

    private final String GraphQLInt = "Int";
    private final String GraphQLFloat = "Float";
    private final String GraphQLString = "String";
    private final String GraphQLBoolean = "Boolean";

    private final DataFetchingEnvironment dataFetchingEnvironment;

    protected abstract Set<String> getKeys(S sourceObject);
    protected abstract int getInteger(S sourceObject, String name);
    protected abstract String getString(S sourceObject,String name);
    protected abstract double getDouble(S sourceObject,String name);
    protected abstract boolean getBoolean(S sourceObject,String name);
    protected abstract Object getObject(S sourceObject,String name);
    protected abstract List<Object> getList(S sourceObject, String name, GraphQLType baseType);
    protected abstract boolean hasField(S sourceObject, String name);

    protected AbstractGraphQLConverter(DataFetchingEnvironment dataFetchingEnvironment){
        this.dataFetchingEnvironment = dataFetchingEnvironment;
    }

    protected DataFetchingEnvironment getDataFetchingEnvironment() {
        return dataFetchingEnvironment;
    }

    protected GraphQLType getCurrentFieldType(){
        return dataFetchingEnvironment.getFieldDefinition().getType();
    }

    protected GraphQLObjectType getObjectType(String name){
        return dataFetchingEnvironment.getGraphQLSchema().getObjectType(name);
    }

    @Override
    public Object ConvertToGraphQLResponse(S source) {
        return Convert(source);
    }

    private Map<String, Object> Convert(S source) {
        GraphQLObjectType objectType = getObjectType(getCurrentFieldType().getName());
        return ConvertToGraphQLObject(source, objectType);
    }

    protected Map<String, Object> ConvertToGraphQLObject(S sourceObject, GraphQLObjectType objectType) {
        Map<String, Object> propertyMap = new LinkedHashMap<>();
        List<GraphQLFieldDefinition> fieldDefinitions = objectType.getFieldDefinitions();
        for(GraphQLFieldDefinition definition : fieldDefinitions){
          if(hasField(sourceObject, definition.getName())) {
              Object o = ConvertField(sourceObject, definition.getType(), definition.getName());
              propertyMap.put(definition.getName(), o);
          }
        }
        return propertyMap;
    }

    private Object ConvertField(S sourceObject, GraphQLType type, String name){
        if(type instanceof GraphQLList) {
            return getList(sourceObject, name, ((GraphQLList) type).getWrappedType());
        }
        if(type instanceof  GraphQLNonNull) {
            type = ((GraphQLNonNull) type).getWrappedType();
        }
        switch (type.getName()) {
            case GraphQLInt:
                return getInteger(sourceObject, name);
            case GraphQLString:
                return getString(sourceObject, name);
            case GraphQLFloat:
                return getDouble(sourceObject, name);
            case GraphQLBoolean:
                return getBoolean(sourceObject, name);
            default:
                return sourceObject;
        }
    }
}