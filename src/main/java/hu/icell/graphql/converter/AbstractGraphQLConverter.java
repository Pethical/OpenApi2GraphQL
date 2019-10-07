/*
 * Copyright (c) 2019 i-Cell Mobilsoft Zrt. All rights reserved
 * Author: peter.nemeth
 * This code is licensed under MIT license (see LICENSE.md for details)
 */
package hu.icell.graphql.converter;

import graphql.schema.*;
import org.omg.CORBA.ObjectHelper;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class AbstractGraphQLConverter<S> implements GraphQLConverter<S, Object> {

    private final DataFetchingEnvironment dataFetchingEnvironment;

    public AbstractGraphQLConverter(DataFetchingEnvironment dataFetchingEnvironment){
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


    protected abstract Set<String> getKeys(S sourceObject);
    protected abstract int getInteger(S sourceObject, String name);
    protected abstract String getString(S sourceObject,String name);
    protected abstract double getDouble(S sourceObject,String name);
    protected abstract boolean getBoolean(S sourceObject,String name);
    protected abstract Object getList(S sourceObject, String name, GraphQLType baseType);
    protected abstract boolean hasField(S sourceObject, String name);
    @Override
    public abstract Object ConvertToGraphQLResponse(S source);

    public Object ConvertIt(S source){
        GraphQLObjectType objectType = getObjectType(getCurrentFieldType().getName());
        return Convert(source, objectType);
    }

    public Object Convert(S sourceObject, GraphQLObjectType objectType) {
        Map<String, Object> propertyMap = new LinkedHashMap<>();
        List<GraphQLFieldDefinition> fieldDefinitions = objectType.getFieldDefinitions();
        for(GraphQLFieldDefinition definition : fieldDefinitions){
          if( hasField(sourceObject, definition.getName())) {
              Object o = ConvertField(sourceObject, definition.getType(), definition.getName());
              propertyMap.put(definition.getName(), o);
          }
        }
        return propertyMap;
    }

    protected Object ConvertField(S sourceObject, GraphQLType type, String name){
        if(type instanceof GraphQLList){
            return getList(sourceObject, name, ((GraphQLList) type).getWrappedType());
        }
        switch (type.getName()) {
            case "Int":
                return getInteger(sourceObject, name);
            case "String":
                return getString(sourceObject, name);
            case "Float":
                return getDouble(sourceObject, name);
            case "Boolean":
                return getBoolean(sourceObject, name);
            default:
                return sourceObject;
        }

    }



}
