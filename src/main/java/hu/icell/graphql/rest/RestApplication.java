package hu.icell.graphql.rest;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/poc")
public class RestApplication extends Application {

    public RestApplication(){
        super();
    }
}
