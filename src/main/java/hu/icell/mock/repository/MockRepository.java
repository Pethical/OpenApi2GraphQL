/*
 * Copyright (c) 2019 i-Cell Mobilsoft Zrt. All rights reserved
 * Author: Péter Németh (Pethical)
 * This code is licensed under MIT license (see LICENSE.md for details)
 */
package hu.icell.mock.repository;

import hu.icell.mock.entity.User;
import hu.icell.mock.UserCatRepository;
import hu.icell.mock.entity.Cat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author peter.nemeth
 */
public class MockRepository implements UserCatRepository {

    private final List<Cat> cats = new ArrayList<>();
    private final List<User> users = new ArrayList<>();

    @Override
    public List<Cat> getCats() {
        return cats;
    }

    @Override
    public List<User> getUsers() {
        return users;
    }

    public MockRepository() {
        for (int i = 0; i < 100; i++) {
            cats.add(new Cat(i, String.format("Macsek %d", i), i % 2 == 0));
        }
        for (int i = 0; i < 10; i++) {
            User user = new User(String.format("Béla %d", i), i);
            for (int j = 0; j < 10; j++) {
                user.getCats().add(cats.get(j));
            }
            users.add(user);
        }
    }

    @Override
    public Cat findCatById(int id) {
        if (id < 0 || cats.size() <= id) {
            return null;
        }
        return cats.get(id);
    }

    @Override
    public User findUserById(int id) {
        if (id < 0 || users.size() <= id) {
            return null;
        }
        return users.get(id);
    }

    @Override
    public User findUserByName(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        for (User user : users) {
            if (name.equals(user.getName())) {
                return user;
            }
        }
        return null;
    }

    @Override
    public Cat findCatByName(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        for (Cat cat : cats) {
            if (name.equals(cat.getName())) {
                return cat;
            }
        }
        return null;
    }

    @Override
    public Collection<Cat> findCatByLive(boolean isLive) {
        List<Cat> result = new ArrayList<>();
        for (Cat cat : cats) {
            if (cat.isLive() == isLive) {
                result.add(cat);
            }
        }
        return result;
    }

}
