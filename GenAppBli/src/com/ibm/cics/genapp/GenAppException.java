package com.ibm.cics.genapp;

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

public class GenAppException extends Exception {

	/*
     * This subclass of Exception is used for GenApp specific Exceptions.
	 * It is further extended to provide more granular Exceptions for different
	 * types of Exception which may occur (System, Application, Business). 
	 */

    private static final long serialVersionUID = 1L;

    public GenAppException(String arg0) {
        super(arg0);
    }

    public GenAppException(Throwable arg0) {
        super(arg0);
    }

}
