/*
 * Copyright (c) 2019 i-Cell Mobilsoft Zrt. All rights reserved
 * Author: Péter Németh
 * This code is licensed under MIT license (see LICENSE.md for details)
 */

package hu.icell.mock.entity;

import io.leangen.graphql.annotations.GraphQLId;
import io.leangen.graphql.annotations.GraphQLQuery;

/**
 *
 * @author peter.nemeth
 */
public class Cat {

    private final int id;
    private final String name;
    private final boolean isLive;

    public Cat(int id, String name, boolean isLive) {
        this.id = id;
        this.name = name;
        this.isLive = isLive;
    }

    @GraphQLQuery(name = "isLive")
    public boolean isLive() {
        return isLive;
    }

    @GraphQLQuery
    public String getName() {
        return name;
    }

    @GraphQLQuery
    @GraphQLId
    public int getId() {
        return id;
    }

}
