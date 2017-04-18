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


public class CustomerData extends Customer {
	
	/*
	 * This class encapsulates information about a customer for the GenApp Web Application.
	 * It is used to pass customer details into and on return from the Customer methods of
	 * the business logic primitives. 
	 */

	private int BirthYear;
	private int BirthMonth;
	private int BirthDay;
	
	private String HouseName;
	private int    HouseNumber;
	private String PostCode;
	
	private String HomePhone;
	private String MobilePhone;
	private String EmailAddress;
	
	/*
	 * Constructor requires values for all fields for simplicity, 
	 * not all fields are necessarily required for all instances, 
	 * so empty String or zero are used in such cases.
	 */
	public CustomerData() {
		super();
		BirthYear = 1900;
		BirthMonth = 1;
		BirthDay = 1;
		HouseName = "";
		HouseNumber = 0;
		PostCode = "";
		HomePhone = "";
		MobilePhone = "";
		EmailAddress = "";
	}
	
	


	public int getBirthYear() {
		return BirthYear;
	}

	public void setBirthYear(int birthYear) {
		BirthYear = birthYear;
	}

	public int getBirthMonth() {
		return BirthMonth;
	}

	public void setBirthMonth(int birthMonth) {
		BirthMonth = birthMonth;
	}

	public int getBirthDay() {
		return BirthDay;
	}

	public void setBirthDay(int birthDay) {
		BirthDay = birthDay;
	}

	public String getHouseName() {
		return HouseName;
	}

	public void setHouseName(String houseName) {
		HouseName = houseName;
	}

	public int getHouseNumber() {
		return HouseNumber;
	}

	public void setHouseNumber(int houseNumber) {
		HouseNumber = houseNumber;
	}

	public String getPostCode() {
		return PostCode;
	}

	public void setPostCode(String postCode) {
		PostCode = postCode;
	}
	
	public String getHomePhone() {
		return HomePhone;
	}

	public void setHomePhone(String homePhone) {
		HomePhone = homePhone;
	}

	public String getMobilePhone() {
		return MobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {
		MobilePhone = mobilePhone;
	}

	public String getEmailAddress() {
		return EmailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		EmailAddress = emailAddress;
	}

}
