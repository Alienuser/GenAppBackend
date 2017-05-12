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

import com.ibm.cics.genapp.GenAppException;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class GenAppLocal implements GenAppInterface {

    @Override
    public int addCustomer(CustomerData cdata) throws GenAppException {
        // TODO Auto-generated method stub
        System.out
                .println("##Customer " + cdata.getCustomerNumber() + " added");
        return 553;
    }

    @Override
    public CustomerData getCustomer(int custNumber) throws GenAppException {

        CustomerData cd = new CustomerData();
        if (custNumber != 553) {
            cd.setFirstName("firstname");
            cd.setLastName("lastname");
            cd.setBirthMonth(1);
            cd.setBirthDay(1);
            cd.setBirthYear(1999);
            cd.setPostCode("55555");
            cd.setEmailAddress("my.email@mail.com");
            cd.setCustomerNumber(custNumber);
        } else {
            cd.setCustomerNumber(custNumber);
        }

        return cd;
    }

    @Override
    public void updateCustomer(int custNumber, CustomerData cdata)
            throws GenAppException {
        System.out.println("birthday-Day:\t" + cdata.getBirthDay());
        System.out.println("birthday-Month:\t" + cdata.getBirthMonth());
        System.out.println("birthday-year:\t" + cdata.getBirthYear());
        System.out.println("customer number:\t" + cdata.getCustomerNumber());
        System.out.println("email:\t" + cdata.getEmailAddress());
        System.out.println("firstname:\t" + cdata.getFirstName());
        System.out.println("lastname:\t" + cdata.getLastName());
        System.out.println("housename:\t" + cdata.getHouseName());
        System.out.println("housenumber:\t" + cdata.getHouseNumber());
        System.out.println("mobileNumber:\t" + cdata.getMobilePhone());
        System.out.println("homephone:\t" + cdata.getHomePhone());
        System.out.println("postcode:\t" + cdata.getPostCode());
        System.out.println("##Customer " + custNumber + " updated");

    }

    @Override
    public void deleteCustomer(int custNumber) throws GenAppException {
        // TODO Auto-generated method stub
        System.out.println("##Customer " + custNumber + " deleted");

    }

    @Override
    public Customer[] listCustomers(int startCustomer, int maxCount)
            throws GenAppException {
        // only for test fix values
        CustomerData[] customers = new CustomerData[maxCount];
        CustomerData ctemp = null;
        for (int i = 0; i < customers.length; i++) {
            ctemp = new CustomerData();
            ctemp.setCustomerNumber(startCustomer + i);
            ctemp.setFirstName("first_name " + i);
            ctemp.setLastName("lastName " + i);
            customers[startCustomer + i] = ctemp;

        }
        return customers;
    }

    @Override
    public int addPolicy(int custNumber, PolicyData pdata)
            throws GenAppException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public PolicyData getPolicy(int custNumber, int policyNumber,
                                PolicyType ptype) throws GenAppException {
        if (ptype == (PolicyType.Car)) {
            CarData cd = new CarData();
            cd.setCarMake("audi");
            cd.setCarModel("a8");
            cd.setPolicyNumber(policyNumber);
            return cd;

        } else if (ptype == (PolicyType.House)) {
            HouseData hd = new HouseData();
            hd.setHouseName("MyHousename");
            hd.setPolicyNumber(policyNumber);
            return hd;

        }
        return null;
    }

    @Override
    public void updatePolicy(int custNumber, int policyNumber, PolicyData pdata)
            throws GenAppException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy MMM dd");
        if (pdata.getPolicyType() == PolicyType.Car) {
            CarData cd = (CarData) pdata;
            System.out.println("type:\t" + cd.getPolicyType().toString());
            System.out.println("Make:\t" + cd.getCarMake());
            System.out.println("Model:\t" + cd.getCarModel());
            System.out.println("Value:\t" + cd.getCarValue());
            System.out.println("PolNum:\t" + cd.getPolicyNumber());
            System.out.println("expireDate:\t"
                    + sdf.format(cd.getExpiryDate().getTime()));
            System.out.println("issueDate:\t"
                    + sdf.format(cd.getIssueDate().getTime()));
            System.out.println("Manufactured:\t"
                    + sdf.format(cd.getManufactured().getTime()));
            System.out.println("Registration:\t" + cd.getRegistration());

        } else if (pdata.getPolicyType() == PolicyType.House) {
            HouseData hd = (HouseData) pdata;
            System.out.println("type:\t" + hd.getPropertyType().toString());
            System.out.println("Bedrooms:\t" + hd.getBedrooms());
            System.out.println("Housename:\t" + hd.getHouseName());
            System.out.println("House-Value:\t" + hd.getHouseValue());
            System.out.println("HouseNumber:\t" + hd.getHouseNumber());
            System.out.println("expireDate:\t"
                    + sdf.format(hd.getExpiryDate().getTime()));
            System.out.println("issueDate:\t"
                    + sdf.format(hd.getIssueDate().getTime()));
            System.out.println("polNumber:\t" + hd.getPolicyNumber());
            System.out.println("postCode: \t" + hd.getPostcode());
            System.out.println("PropertyType:\t" + hd.getPropertyType());

        }
        System.out.println("policy: " + policyNumber + "updated");

    }

    @Override
    public void deletePolicy(int custNumber, int policyNumber, PolicyType ptype)
            throws GenAppException {
        System.out.println("policy " + policyNumber + "deleted");

    }

    @Override
    public PolicyData[] listPolicies(int custNumber) throws GenAppException {
        PolicyData[] policys = new PolicyData[5];
        PolicyData pd = null;
        Calendar calex = new GregorianCalendar(2015, 11, 11);
        Calendar calis = new GregorianCalendar(2014, 11, 11);

        for (int i = 0; i < 3; i++) {
            pd = new PolicyData(PolicyType.Car);
            pd.setExpiryDate(calex);
            pd.setIssueDate(calis);
            pd.setPolicyNumber(i * 100);
            policys[i] = pd;

        }

        for (int i = 3; i < policys.length; i++) {
            pd = new PolicyData(PolicyType.House);
            pd.setExpiryDate(calex);
            pd.setIssueDate(calis);
            pd.setPolicyNumber(i * 100);
            policys[i] = pd;
        }
        return policys;
    }

}
