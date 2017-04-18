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

public class CarData  extends PolicyData {

	private String CarMake;
	private String CarModel;
	private int CarValue;
	private String Registration;
	private Calendar Manufactured;
	
	public CarData() {
		super(PolicyType.Car);
		CarMake = "";
		CarModel = "";
		CarValue = 0;
		Registration = "";
		Manufactured = new GregorianCalendar();
	}

	public String getCarMake() {
		return CarMake;
	}

	public void setCarMake(String carMake) {
		CarMake = carMake;
	}

	public String getCarModel() {
		return CarModel;
	}

	public void setCarModel(String carModel) {
		CarModel = carModel;
	}

	public int getCarValue() {
		return CarValue;
	}

	public void setCarValue(int carValue) {
		CarValue = carValue;
	}

	public String getRegistration() {
		return Registration;
	}

	public void setRegistration(String registration) {
		Registration = registration;
	}

	/**
	 * @return the manufactured
	 */
	public Calendar getManufactured() {
		return Manufactured;
	}

	/**
	 * @param manufactured the manufactured to set
	 */
	public void setManufactured(Calendar manufactured) {
		Manufactured = manufactured;
	}
	
	
	
}