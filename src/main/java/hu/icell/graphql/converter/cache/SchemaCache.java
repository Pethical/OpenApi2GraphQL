/*
 * Copyright (c) 2019 i-Cell Mobilsoft Zrt. All rights reserved
 * Author: Péter Németh
 * This code is licensed under MIT license (see LICENSE.md for details)
 */

package hu.icell.graphql.converter.cache;

public interface SchemaCache {
    /**
     * Searches for the item in the cache and returns it
     * @param cacheKey The key of the item in the cache
     * @return String The content of the cached item
     * @throws Exception Can throw an exception based on the implementation
     */
    String getItem(String cacheKey) throws Exception;

    /**
     * Put an item into the cache
     * @param cacheKey The key of the item in the cache
     * @param content The content to put into the cache
     * @throws Exception Can throw an exception based on the implementation
     */
    void setItem(String cacheKey, String content) throws Exception;
}
