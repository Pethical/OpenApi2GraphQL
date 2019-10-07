/*
 * Copyright (c) 2019 i-Cell Mobilsoft Zrt. All rights reserved
 * Author: Péter Németh (Pethical)
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
        if(sourceObject.getValueType() == JsonValue.ValueType.OBJECT){
            return ((JsonObject)sourceObject).keySet();
        }
        return new HashSet<>();
    }

    @Override
    protected int getInteger(JsonStructure sourceObject, String name) {
        return ((JsonObject)sourceObject).getInt(name);
    }

    @Override
    protected String getString(JsonStructure sourceObject, String name) {
        return ((JsonObject)sourceObject).getString(name);
    }

    @Override
    protected double getDouble(JsonStructure sourceObject, String name) {
        return ((JsonObject)sourceObject).getJsonNumber(name).doubleValue();
    }

    @Override
    protected boolean getBoolean(JsonStructure sourceObject, String name) {
        return ((JsonObject)sourceObject).getBoolean(name);
    }

    @Override
    protected Object getList(JsonStructure sourceObject, String name, GraphQLType baseType) {
        JsonArray jsonArray = ((JsonObject) sourceObject).getJsonArray(name);
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
    protected boolean hasField(JsonStructure sourceObject, String name) {
        if(sourceObject.getValueType() == JsonValue.ValueType.OBJECT){
            return ((JsonObject)sourceObject).containsKey(name);
        }
        return false;
    }

    /*
        private Object ConvertType(JsonObject jsonObject, GraphQLType type, String name) {
            if (type instanceof GraphQLList) {
                GraphQLType wrappedType = ((GraphQLList) type).getWrappedType();
                JsonArray jsonArray = jsonObject.getJsonArray(name);
                List<Object> list = new ArrayList<>();
                for (int i = 0; i < jsonArray.size(); i++) {
                    Map<String, Object> properties = new LinkedHashMap<>();
                    for (String key : jsonArray.getJsonObject(i).keySet()) {
                        properties.put(key, ConvertType(jsonArray.getJsonObject(i),
                                getObjectType(wrappedType.getName()).getFieldDefinition(key).getType(), key));
                    }
                    list.add(properties);
                }
                return list;
            }
            switch (type.getName()) {
                case "Int":
                    return jsonObject.getInt(name);
                case "String":
                    return jsonObject.getString(name);
                case "Float":
                    return jsonObject.getJsonNumber(name).doubleValue();
                case "Boolean":
                    return jsonObject.getBoolean(name);
                default:
                    return jsonObject;
            }
        }
    */
    private Map<String, Object> JsonToMap(JsonObject jsonObject, GraphQLObjectType objectType) {
        Map<String, Object> propertyMap = new LinkedHashMap<>();
        for (String key : jsonObject.keySet()) {
            if (objectType.getFieldDefinition(key) != null && objectType.getFieldDefinition(key).getType() != null) {
                GraphQLType type = objectType.getFieldDefinition(key).getType();
                Object o = ConvertField(jsonObject, type, key);
                propertyMap.put(key, o);
            }
        }
        return propertyMap;
    }

    private Map<String, Object> MapJson(JsonObject jsonObject) {
        GraphQLObjectType objectType = getObjectType(getCurrentFieldType().getName());
        return JsonToMap(jsonObject, objectType);
    }

    private Map<String, Object> MapList(JsonObject jsonObject) {
        GraphQLType graphQLType = getCurrentFieldType();
        if (!(graphQLType instanceof GraphQLList)) throw new IllegalArgumentException("Not a list");
        GraphQLType baseType = ((GraphQLList) graphQLType).getWrappedType();
        GraphQLObjectType objectType = getObjectType(baseType.getName());
        return JsonToMap(jsonObject, objectType);
    }

    /**
     *
     * @param jsonStructure Json response to convert
     * @return GraphQL object
     */
    public Object ConvertToGraphQLResponse(JsonStructure jsonStructure) {
        if (jsonStructure.getValueType() == JsonValue.ValueType.OBJECT) {
            return ConvertIt(jsonStructure);
            //return new JsonObjectToGraphQLConverter(getDataFetchingEnvironment()).ConvertToGraphQLResponse((JsonObject)jsonStructure);
//            JsonObject jsonObject = (JsonObject) jsonStructure;
//            return MapJson(jsonObject);
        } else if (jsonStructure.getValueType() == JsonValue.ValueType.ARRAY) {
            JsonArray array = (JsonArray) jsonStructure;
            List<Map<String, Object>> map = new ArrayList<>();
            for (int i = 0; i < array.size(); i++) {
                JsonObject jsonObject = array.getJsonObject(i);
                map.add(MapList(jsonObject));
            }
            return map;
        }
        return null;
    }
}
