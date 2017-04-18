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


import java.util.Calendar;
import java.util.GregorianCalendar;

public class PolicyData {

	private PolicyType policyType;
	private int policyNumber; // Required to support list function.
	private Calendar issueDate = new GregorianCalendar(); // Today - to satisfy SQL.
	private Calendar expiryDate = new GregorianCalendar(); // Today
	
	public PolicyData(PolicyType policyType) {
		super();
		policyNumber = 0; // Invalid value set by initialization
		this.policyType = policyType;
	}
	
	public PolicyType getPolicyType() {
		return policyType;
	}

	public int getPolicyNumber() {
		return policyNumber;
	}

	public void setPolicyNumber(int policyNumber) {
		this.policyNumber = policyNumber;
	}

	/**
	 * @return the issueDate
	 */
	public Calendar getIssueDate() {
		return issueDate;
	}

	/**
	 * @param issueDate the issueDate to set
	 */
	public void setIssueDate(Calendar issueDate) {
		this.issueDate = issueDate;
	}

	/**
	 * @return the expiryDate
	 */
	public Calendar getExpiryDate() {
		return expiryDate;
	}

	/**
	 * @param expiryDate the expiryDate to set
	 */
	public void setExpiryDate(Calendar expiryDate) {
		this.expiryDate = expiryDate;
	}
	
}