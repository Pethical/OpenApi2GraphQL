/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.icell.gqlpoc.entity;

import hu.icell.gqlpoc.UserCatRepository;
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
        for (int i = 0; i < users.size(); i++) {
            if (name.equals(users.get(i).getName())) {
                return users.get(i);
            }
        }
        return null;
    }

    @Override
    public Cat findCatByName(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
        for (int i = 0; i < cats.size(); i++) {
            if (name.equals(cats.get(i).getName())) {
                return cats.get(i);
            }
        }
        return null;
    }

    @Override
    public Collection<Cat> findCatByLive(boolean isLive) {
        List<Cat> result = new ArrayList<>();
        for (int i = 0; i < cats.size(); i++) {
            if (cats.get(i).isLive() == isLive) {
                result.add(cats.get(i));
            }
        }
        return result;
    }

}
