/*
 * Copyright (c) 2019 i-Cell Mobilsoft Zrt. All rights reserved
 * Author: Péter Németh
 * This code is licensed under MIT license (see LICENSE.md for details)
 */

package hu.icell.mock;

import hu.icell.mock.entity.Cat;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLQuery;
import java.util.Collection;

/**
 *
 * @author peter.nemeth
 */
public class CatService {

    private final UserCatRepository respository;

    public CatService(UserCatRepository respository) {
        this.respository = respository;
    }

    @GraphQLQuery(name = "cat")
    public Cat getById(@GraphQLArgument(name = "id") Integer id) {
        if (id == null) {
            return null;
        }
        return respository.findCatById(id);
    }

    @GraphQLQuery(name = "cat")
    public Cat getByName(@GraphQLArgument(name = "name") String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        return respository.findCatByName(name);
    }

    @GraphQLQuery(name = "cats")
    public Collection<Cat> getAll() {
        return respository.getCats();
    }

    @GraphQLQuery(name = "cats")
    public Collection<Cat> getByLive(@GraphQLArgument(name = "isLive") Boolean isLive) {
        if (isLive == null) {
            return null;
        }
        return respository.findCatByLive(isLive);
    }

}
