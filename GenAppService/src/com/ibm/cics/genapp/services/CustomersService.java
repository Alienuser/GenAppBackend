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

import com.ibm.cics.genapp.GenAppException;
import com.ibm.cics.genapp.bli.Customer;
import com.ibm.cics.genapp.bli.CustomerData;
import com.ibm.cics.genapp.bli.GenAppInterface;
import com.ibm.cics.genapp.bli.GenAppJCAImpl;
import com.ibm.cics.genapp.bli.PolicyData;
import com.ibm.cics.genapp.bli.PolicyType;

import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;

@javax.ws.rs.Path("/customers")
public class CustomersService {

	private boolean error = false;
	private static final String ERROR_MSG_LABEL = "errorMsg";
	private static final String ERROR_CODE_LABEL = "errorCode";
	private static String errorMsg = "";
	private static int errorCode = 0;
	private static final String ERROR_NOT_INTEGER = "Customer number must contain only numerical characters";
	private static final String ERROR_GENAPP_ERROR = "Error retrieving response from the GenApp application.";

	private static final int ERROR_NOT_INTEGER_CODE = 1;
	private static final int ERROR_GENAPP_ERROR_CODE = 2;

	private static final String CUSTOMER_NO_LABEL = "customerNumber";
	private static final String FIRST_NAME_LABEL = "firstName";
	private static final String LAST_NAME_LABEL = "lastName";
	private static final String BIRTH_YEAR_LABEL = "birthYear";
	private static final String BIRTH_MONTH_LABEL = "birthMonth";
	private static final String BIRTH_DAY_LABEL = "birthDay";
	private static final String HOUSE_NAME_LABEL = "houseName";
	private static final String HOUSE_NUMBER_LABEL = "houseNumber";
	private static final String POST_CODE_LABEL = "postCode";
	private static final String MOBILE_PHONE_LABEL = "mobilePhone";
	private static final String EMAIL_ADDRESS_LABEL = "emailAddress";

	private static final String POLICY_NO_LABEL = "policyNumber";
	private static final String POLICY_TYPE_LABEL = "policyType";
	private static final String POLICY_ISSUE_DATE = "issueDate";
	private static final String POLICY_EXPIRY_DATE = "expiryDate";

	private static final String DATA_LABEL = "customer";
	private static final String RECORD_COUNT_LABEL = "recordCount";

	@javax.ws.rs.GET
	@javax.ws.rs.Path("/{customerNumber : \\d+}")
	@javax.ws.rs.Produces("application/json")
	public JSONObject fetchCustomerById(
			@javax.ws.rs.PathParam("customerNumber") int customerNumber) {

		JSONObject cJson = new JSONObject();

		try {
			GenAppInterface genAppJcaImpl = new GenAppJCAImpl();
			CustomerData cdata = genAppJcaImpl.getCustomer(customerNumber);

			cJson.put(CUSTOMER_NO_LABEL, cdata.getCustomerNumber());
			cJson.put(FIRST_NAME_LABEL, cdata.getFirstName());
			cJson.put(LAST_NAME_LABEL, cdata.getLastName());

			if (cdata.getBirthDay() > 0) {
				cJson.put(BIRTH_DAY_LABEL, cdata.getBirthDay());
				cJson.put(BIRTH_MONTH_LABEL, cdata.getBirthMonth());
				cJson.put(BIRTH_YEAR_LABEL, cdata.getBirthYear());
			}
			cJson.put(HOUSE_NAME_LABEL, cdata.getHouseName());
			cJson.put(HOUSE_NUMBER_LABEL, cdata.getHouseNumber());
			cJson.put(POST_CODE_LABEL, cdata.getPostCode());
			cJson.put(MOBILE_PHONE_LABEL, cdata.getMobilePhone());
			cJson.put(EMAIL_ADDRESS_LABEL, cdata.getEmailAddress());

		} catch (NumberFormatException e) {
			errorMsg = ERROR_NOT_INTEGER;
			errorCode = ERROR_NOT_INTEGER_CODE;
			error = true;
		} catch (GenAppException e) {
			error = true;
			errorMsg = ERROR_GENAPP_ERROR;
			e.printStackTrace();
		}

		JSONObject jsonResponse = new JSONObject();

		if (!error) {
			jsonResponse.put(DATA_LABEL, cJson);
		} else {

			jsonResponse.put(ERROR_CODE_LABEL, errorCode);
			jsonResponse.put(ERROR_MSG_LABEL, errorMsg);
		}

		return jsonResponse;
	}

	@javax.ws.rs.GET
	@javax.ws.rs.Produces("application/json")
	public JSONObject fetchAllCustomers(
			@javax.ws.rs.PathParam("customerNumber") String customerNumber) {

		JSONArray cJsonArray = new JSONArray();

		try {
			GenAppInterface genAppJcaImpl = new GenAppJCAImpl();
			Customer[] cArray = genAppJcaImpl.listCustomers(1, 99);

			for (int i = 0; i < cArray.length; i++) {
				Customer cdata = cArray[i];
				JSONObject cJson = new JSONObject();

				cJson.put(CUSTOMER_NO_LABEL, cdata.getCustomerNumber());
				cJson.put(FIRST_NAME_LABEL, cdata.getFirstName());
				cJson.put(LAST_NAME_LABEL, cdata.getLastName());

				cJsonArray.add(cJson);
			}

		} catch (NumberFormatException e) {
			errorMsg = ERROR_NOT_INTEGER;
			errorCode = ERROR_NOT_INTEGER_CODE;
			error = true;
		} catch (GenAppException e) {
			error = true;
			errorMsg = e.getMessage();
			e.printStackTrace();
		}

		JSONObject jsonResponse = new JSONObject();

		if (!error) {
			jsonResponse.put(DATA_LABEL, cJsonArray);
			jsonResponse.put(RECORD_COUNT_LABEL, cJsonArray.size());
		} else {

			jsonResponse.put(ERROR_CODE_LABEL, errorCode);
			jsonResponse.put(ERROR_MSG_LABEL, errorMsg);
		}

		return jsonResponse;
	}

	@javax.ws.rs.Produces("application/json")
	@javax.ws.rs.Consumes("application/json")
	@javax.ws.rs.POST
	public JSONObject createCustomer(JSONObject jsonInput) {
		int customerNumber = 0;
		JSONObject jsonResponse = new JSONObject();

		try {

			GenAppInterface genAppJcaImpl = new GenAppJCAImpl();
			CustomerData cdata = new CustomerData();

			// Check the minimum data was input
			if (!jsonInput.containsKey(FIRST_NAME_LABEL)
					|| !jsonInput.containsKey(LAST_NAME_LABEL)) {
				error = true;
				errorMsg = "Must provide firstName & lastName fields when creating a new customer";
			} else {
				cdata.setFirstName(jsonInput.get(FIRST_NAME_LABEL).toString());
				cdata.setLastName(jsonInput.get(LAST_NAME_LABEL).toString());
				customerNumber = genAppJcaImpl.addCustomer(cdata);
			}

		} catch (GenAppException e) {
			error = true;
			errorMsg = e.getMessage();
		}
		if (error) {
			jsonResponse.put(ERROR_CODE_LABEL, errorCode);
			jsonResponse.put(ERROR_MSG_LABEL, errorMsg);
			return jsonResponse;
		}

		return fetchCustomerById(customerNumber);

	}

	@javax.ws.rs.Path("/{customerNumber}/policy/car/{policyNumber}")
	@javax.ws.rs.GET
	@javax.ws.rs.Produces("application/json")
	public JSONObject getPolicy(
			@javax.ws.rs.PathParam("customerNumber") int customerNumber,
			@javax.ws.rs.PathParam("policyNumber") int policyNumber) {
		JSONObject pJson = new JSONObject();

		try {

			GenAppInterface genAppJclImpl = new GenAppJCAImpl();
			PolicyData pdata = genAppJclImpl.getPolicy(customerNumber,
					policyNumber, PolicyType.Car);

			pJson.put(POLICY_NO_LABEL, pdata.getPolicyNumber());
			pJson.put(POLICY_TYPE_LABEL, pdata.getPolicyType());
			pJson.put(POLICY_ISSUE_DATE, pdata.getIssueDate().toString());
			pJson.put(POLICY_EXPIRY_DATE, pdata.getExpiryDate().toString());

		} catch (GenAppException e) {
			error = true;
			errorMsg = e.getMessage();
		}

		JSONObject jsonResponse = new JSONObject();

		if (error) {
			jsonResponse.put(ERROR_CODE_LABEL, errorCode);
			jsonResponse.put(ERROR_MSG_LABEL, errorMsg);
		} else {
			jsonResponse.put(DATA_LABEL, pJson);
		}
		return jsonResponse;
	}

	@javax.ws.rs.GET
	@javax.ws.rs.Path("/insurance")
	@javax.ws.rs.Produces("application/json")
	public JSONObject fetchInsuranceData() {
		JSONObject cJson = new JSONObject();

		cJson.put("data", "Hallo das ist ein Test");

		JSONObject jsonResponse = new JSONObject();

		jsonResponse.put("insurance", cJson);

		return jsonResponse;
	}

}
