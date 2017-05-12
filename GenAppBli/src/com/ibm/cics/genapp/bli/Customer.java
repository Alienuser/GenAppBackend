package com.ibm.cics.genapp.bli;

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

public class Customer {

    /*
     * This class encapsulates information about a customer for the GenApp Web Application.
     * It is used to pass customer details into and on return from the Customer methods of
     * the business logic primitives.
     */
    private int CustomerNumber; // Must be included to allow list function.

    private String FirstName;
    private String LastName;


    public Customer() {
        super();
        CustomerNumber = 0; // Invalid customer number per initialization.
        FirstName = "";
        LastName = "";
    }

    public int getCustomerNumber() {
        return CustomerNumber;
    }

    public void setCustomerNumber(int customerNumber) {
        CustomerNumber = customerNumber;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

}
