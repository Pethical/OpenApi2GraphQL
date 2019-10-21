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

    private final UserCatRepository repository;

    public CatService(UserCatRepository repository) {
        this.repository = repository;
    }

    @GraphQLQuery(name = "cat")
    public Cat getById(@GraphQLArgument(name = "id") Integer id) {
        if (id == null) {
            return null;
        }
        return repository.findCatById(id);
    }

    @GraphQLQuery(name = "cat")
    public Cat getByName(@GraphQLArgument(name = "name") String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        return repository.findCatByName(name);
    }

    @GraphQLQuery(name = "cats")
    public Collection<Cat> getAll() {
        return repository.getCats();
    }

    @GraphQLQuery(name = "cats")
    public Collection<Cat> getByLive(@GraphQLArgument(name = "isLive") Boolean isLive) {
        if (isLive == null) {
            return null;
        }
        return repository.findCatByLive(isLive);
    }

}
