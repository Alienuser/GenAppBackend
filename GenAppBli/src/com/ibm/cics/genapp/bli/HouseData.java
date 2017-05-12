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

public class HouseData extends PolicyData {

    private String PropertyType;
    private int Bedrooms;
    private int HouseValue;
    private String HouseName;
    private int HouseNumber;
    private String Postcode;

    public HouseData() {
        super(PolicyType.House);
        PropertyType = "";
        Bedrooms = 0;
        HouseValue = 0;
        HouseName = "";
        HouseNumber = 0;
        Postcode = "";
    }

    public String getPropertyType() {
        return PropertyType;
    }

    public void setPropertyType(String propertyType) {
        PropertyType = propertyType;
    }

    public int getBedrooms() {
        return Bedrooms;
    }

    public void setBedrooms(int bedrooms) {
        Bedrooms = bedrooms;
    }

    public int getHouseValue() {
        return HouseValue;
    }

    public void setHouseValue(int houseValue) {
        HouseValue = houseValue;
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

    public String getPostcode() {
        return Postcode;
    }

    public void setPostcode(String postcode) {
        Postcode = postcode;
    }

}
