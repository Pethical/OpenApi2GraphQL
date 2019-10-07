/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.icell.graphql.mock;

import hu.icell.graphql.mock.entity.Cat;
import hu.icell.graphql.mock.entity.User;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author peter.nemeth
 */
public interface UserCatRepository {

    Cat findCatById(int id);

    Collection<Cat> findCatByLive(boolean isLive);

    Cat findCatByName(String name);

    User findUserById(int id);

    User findUserByName(String name);

    List<Cat> getCats();

    List<User> getUsers();

}
