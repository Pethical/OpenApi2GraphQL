/*
 * Copyright (c) 2019 i-Cell Mobilsoft Zrt. All rights reserved
 * Author: peter.nemeth
 * This code is licensed under MIT license (see LICENSE.md for details)
 */
package hu.icell.graphql.converter.json;

import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLType;
import hu.icell.graphql.converter.AbstractGraphQLConverter;

import javax.json.JsonArray;
import javax.json.JsonObject;
import java.util.*;

public class JsonObjectToGraphQLConverter extends AbstractGraphQLConverter<JsonObject> {

    public JsonObjectToGraphQLConverter(DataFetchingEnvironment dataFetchingEnvironment) {
        super(dataFetchingEnvironment);
    }

    @Override
    protected Set<String> getKeys(JsonObject sourceObject) {
        return sourceObject.keySet();
    }

    @Override
    protected int getInteger(JsonObject sourceObject, String name) {
        return sourceObject.getInt(name);
    }

    @Override
    protected String getString(JsonObject sourceObject, String name) {
        return sourceObject.getString(name);
    }

    @Override
    protected double getDouble(JsonObject sourceObject, String name) {
        return sourceObject.getJsonNumber(name).doubleValue();
    }

    @Override
    protected boolean getBoolean(JsonObject sourceObject, String name) {
        return sourceObject.getBoolean(name);
    }

    @Override
    protected Object getList(JsonObject sourceObject, String name, GraphQLType baseType) {
        JsonArray jsonArray = sourceObject.getJsonArray(name);
        List<Object> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            Map<String, Object> properties = new LinkedHashMap<>();
            for (String key : jsonArray.getJsonObject(i).keySet()) {
                properties.put(key, ConvertField(jsonArray.getJsonObject(i),
                        getObjectType(baseType.getName()).getFieldDefinition(key).getType(), key));
            }
            list.add(properties);
        }
        return list;
    }

    @Override
    protected boolean hasField(JsonObject sourceObject, String name) {
        return sourceObject.containsKey(name);
    }

    @Override
    public Object ConvertToGraphQLResponse(JsonObject source) {
        GraphQLObjectType objectType = getObjectType(getCurrentFieldType().getName());
        Map<String, Object> propertyMap = new LinkedHashMap<>();
        for (String key : source.keySet()) {
            if (objectType.getFieldDefinition(key) != null && objectType.getFieldDefinition(key).getType() != null) {
                GraphQLType type = objectType.getFieldDefinition(key).getType();
                Object o = ConvertField(source, type, key);
                propertyMap.put(key, o);
            }
        }
        return propertyMap;
    }
}
