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


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.resource.cci.Record;
import javax.resource.cci.Streamable;

public class GenAppCCIRecord implements Record, Streamable {
	
	private static final long serialVersionUID = 1L;

	private String RecordName;
	private String RecordShortDescription;
	
	private byte[] bytes;
	
	public GenAppCCIRecord(byte[] record) {
		this.bytes = record;
	}

	/**
	* use the superclass clone method for this class
	*/
	public java.lang.Object clone() throws CloneNotSupportedException {
	return super.clone();
	}

	public String getRecordName() {
		return RecordName;
	}

	public void setRecordName(String recordName) {
		RecordName = recordName;
	}

	public String getRecordShortDescription() {
		return RecordShortDescription;
	}

	public void setRecordShortDescription(String recordShortDescription) {
		RecordShortDescription = recordShortDescription;
	}

	/*
	 * @see javax.resource.cci.Streamable#read(java.io.InputStream)
	 */
	public void read(InputStream is) throws IOException {
   		  if (is.available() > bytes.length) {
   			  throw new IOException("Response commarea from ECI call too large");
   		  }
	      is.read(bytes);
	}

	@Override
	public void write(OutputStream os) throws IOException {
		os.write(bytes);
	}

}
