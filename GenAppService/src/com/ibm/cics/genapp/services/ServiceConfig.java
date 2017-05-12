package com.ibm.cics.genapp.services;

/*
 * 
 * Licensed Materials - Property of IBM 
 * 
 * (c) Copyright IBM Corp. 2015 All Rights Reserved.
 * 
 * US Government Users Restricted Rights - Use, duplication or 
 * disclosure restricted by GSA ADP Schedule Contract with
 * IBM Corp.
 * 
 */

import java.util.HashSet;
import java.util.Set;

public class ServiceConfig extends javax.ws.rs.core.Application {

    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<Class<?>>();
        classes.add(com.ibm.cics.genapp.services.CustomersService.class);
        return classes;
    }
}
