/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.icell.mock;

import hu.icell.mock.entity.User;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLQuery;
import java.util.Collection;

/**
 *
 * @author peter.nemeth
 */
public class UserService {

    private final UserCatRepository repository;

    public UserService(UserCatRepository respository) {
        this.repository = respository;
    }

    @GraphQLQuery(name = "user")
    public User getById(@GraphQLArgument(name = "id") Integer id) {
        if (id == null) {
            return null;
        }
        return repository.findUserById(id);
    }

    @GraphQLQuery(name = "user")
    public User getByName(@GraphQLArgument(name = "name") String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        return repository.findUserByName(name);
    }

    @GraphQLQuery(name = "users")
    public Collection<User> getAll() {
        return repository.getUsers();
    }
}
