/*
 * Copyright (c) 2019 i-Cell Mobilsoft Zrt. All rights reserved
 * Author: Péter Németh (Pethical)
 * This code is licensed under MIT license (see LICENSE.md for details)
 */
package hu.icell.graphql.logging;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SLF4JProducer {

    @Produces
    public Logger producer(InjectionPoint injectionPoint){
        return LoggerFactory.getLogger(
                injectionPoint.getMember().getDeclaringClass().getName());
    }
}
