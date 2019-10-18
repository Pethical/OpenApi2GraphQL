/*
 * Copyright (c) 2019 i-Cell Mobilsoft Zrt. All rights reserved
 * Author: Péter Németh
 * This code is licensed under MIT license (see LICENSE.md for details)
 */
package hu.icell.graphql.rest;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/poc")
public class RestApplication extends Application {

    public RestApplication(){
        super();
    }
}
