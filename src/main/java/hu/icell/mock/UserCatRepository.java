/*
 * Copyright (c) 2019 i-Cell Mobilsoft Zrt. All rights reserved
 * Author: Péter Németh
 * This code is licensed under MIT license (see LICENSE.md for details)
 */

package hu.icell.mock;

import hu.icell.mock.entity.Cat;
import hu.icell.mock.entity.User;

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
