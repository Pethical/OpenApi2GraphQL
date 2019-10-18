/*
 * Copyright (c) 2019 i-Cell Mobilsoft Zrt. All rights reserved
 * Author: Péter Németh
 * This code is licensed under MIT license (see LICENSE.md for details)
 */
package hu.icell.graphql.converter.json;

import graphql.schema.*;
import hu.icell.graphql.converter.AbstractGraphQLConverter;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonStructure;
import javax.json.JsonValue;
import java.util.*;

/**
 * @author peter.nemeth
 * OpenApi response json to graphql response converter
 */
public class JsonToGraphQLConverter extends AbstractGraphQLConverter<JsonStructure> {

    public JsonToGraphQLConverter(DataFetchingEnvironment dataFetchingEnvironment) {
        super(dataFetchingEnvironment);
    }

    @Override
    protected Set<String> getKeys(JsonStructure sourceObject) {
        if(sourceObject.getValueType() == JsonValue.ValueType.OBJECT) {
            return ((JsonObject)sourceObject).keySet();
        }
        return new HashSet<>();
    }

    @Override
    protected int getInteger(JsonStructure sourceObject, String name) {
        assert sourceObject instanceof JsonObject;
        return ((JsonObject) sourceObject).getInt(name);
    }

    @Override
    protected String getString(JsonStructure sourceObject, String name) {
        assert sourceObject instanceof JsonObject;
        return ((JsonObject)sourceObject).get(name).toString();
    }

    @Override
    protected double getDouble(JsonStructure sourceObject, String name) {
        assert sourceObject instanceof JsonObject;
        return ((JsonObject)sourceObject).getJsonNumber(name).doubleValue();
    }

    @Override
    protected boolean getBoolean(JsonStructure sourceObject, String name) {
        assert sourceObject instanceof JsonObject;
        return ((JsonObject)sourceObject).getBoolean(name);
    }

    @Override
    protected Object getObject(JsonStructure sourceObject, String name) {
        assert sourceObject instanceof JsonObject;
        return ((JsonObject)sourceObject).get(name);
    }

    @Override
    protected List<Object> getList(JsonStructure sourceObject, String name, GraphQLType baseType) {
        assert sourceObject instanceof JsonObject;
        JsonArray jsonArray = ((JsonObject) sourceObject).getJsonArray(name);
        List<Object> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            if(jsonArray.get(i).getValueType() == JsonValue.ValueType.OBJECT) {
                list.add(ConvertToGraphQLObject(jsonArray.getJsonObject(i), getObjectType(baseType.getName())));
            }
        }
        return list;
    }

    @Override
    protected boolean hasField(JsonStructure sourceObject, String name) {
        if(sourceObject.getValueType() == JsonValue.ValueType.OBJECT){
            return ((JsonObject)sourceObject).containsKey(name);
        }
        return false;
    }

    private Map<String, Object> MapJsonArray(JsonObject jsonObject) {
        GraphQLType graphQLType = getCurrentFieldType();
        assert graphQLType instanceof GraphQLList;
        GraphQLType baseType = ((GraphQLList) graphQLType).getWrappedType();
        GraphQLObjectType objectType = getObjectType(baseType.getName());
        return ConvertToGraphQLObject(jsonObject, objectType);
    }

    /**
     *
     * @param jsonStructure Json response to convert
     * @return GraphQL object
     */
    @Override
    public Object ConvertToGraphQLResponse(JsonStructure jsonStructure) {
        if (jsonStructure.getValueType() == JsonValue.ValueType.OBJECT)
        {
            return super.ConvertToGraphQLResponse(jsonStructure);
        }
        if (jsonStructure.getValueType() == JsonValue.ValueType.ARRAY) {
            JsonArray array = (JsonArray) jsonStructure;
            List<Map<String, Object>> map = new ArrayList<>();
            for (int i = 0; i < array.size(); i++) {
                JsonObject jsonObject = array.getJsonObject(i);
                map.add(MapJsonArray(jsonObject));
            }
            return map;
        }
        return null;
    }
}
