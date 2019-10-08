/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hu.icell.mock.entity;

import io.leangen.graphql.annotations.GraphQLId;
import io.leangen.graphql.annotations.GraphQLQuery;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author peter.nemeth
 */
public class User {

    private final String name;
    private final int id;
    private final Date registrationDate;
    private final List<Cat> cats = new ArrayList<>();

    public User(String name, int id) {
        this.id = id;
        this.name = name;
        this.registrationDate = new Date();
    }

    @GraphQLQuery(name = "name", description = "A person's name")
    public String getName() {
        return name;
    }

    @GraphQLQuery
    @GraphQLId
    public int getId() {
        return id;
    }

    @GraphQLQuery(name = "regDate")
    public Date getRegistrationDate() {
        return registrationDate;
    }

    public List<Cat> getCats() {
        return cats;
    }
}
