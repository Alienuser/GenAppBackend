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
import com.ibm.cics.genapp.records.GenAppCommarea;
import com.ibm.cics.server.*;
import com.ibm.connector2.cics.ECIChannelRecord;
import com.ibm.connector2.cics.ECIInteractionSpec;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.resource.ResourceException;
import javax.resource.cci.Connection;
import javax.resource.cci.ConnectionFactory;
import javax.resource.cci.Interaction;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

/*
 * This class encapsulates the primitive business logic operations provided by the
 * base GenApp COBOL application (add/inquire Customer, Add/Update/Inquire/Delete
 * policy.
 * 
 * The GenApp commareas are constructed and interpreted using the generated record 
 * class LGACUS01.  JCA is used to invoke the appropriate CICS program.
 */

public class GenAppJCAImpl implements GenAppInterface {

    private static boolean debug = true;

    // Declare names of target CICS programs for maintainability.
    private static final String InsCustPgm = "LGACUS01";
    private static final String InsPolicyPgm = "LGAPOL01";
    private static final String InqCustPgm = "LGICUS01";
    private static final String InqPolicyPgm = "LGIPOL01";
    private static final String UpdateCustPgm = "LGUCUS01";
    private static final String UpdatePolicyPgm = "LGUPOL01";
    private static final String DeletePolicyPgm = "LGDPOL01";
    private static final String ListCustomersPgm = "LGLCVS02"; // New COBOL
    // program to
    // list
    // customers.

    // Request IDs for request types, for commarea
    private static final String InqCustReqID = "01ICUS";
    private static final String InqCarReqID = "01IMOT";
    private static final String InqHouseReqID = "01IHOU";
    private static final String AddCustReqID = "01ACUS";
    private static final String AddCarReqID = "01AMOT";
    private static final String AddHouseReqID = "01AHOU";
    private static final String UpdateCustReqID = "01UCUS";
    private static final String UpdateCarReqID = "01UMOT";
    private static final String UpdateHouseReqID = "01UHOU";
    private static final String DeleteCarReqID = "01DMOT";
    private static final String DeleteHouseReqID = "01DHOU";

    private static final String ccsid = "IBM037"; // Constant for conversion.

    // Known responses from target programs:
    /*
	 * Not used at present. private static final int rc_OK = 0; private static
	 * final int rc_notfound = 90; // Lookup failed private static final int
	 * rc_duplicate = 80; // Already exists
	 */

    private static final String ConnectionFactoryJNDINameProperty = "com.ibm.cics.genapp.jca.jndiname";

    private static Connection eciConn = null; // Must be created only once.

    private GenAppCommarea record; // New record per instance

    public GenAppJCAImpl() throws GenAppException {
        super();
        // Obtain a JCA connection for the life of this object.
        // This connection is threadsafe, can be used concurrently by many
        // requests.
        // Look up JCA connection factory in JNDI, location in system property.

        ConnectionFactory cf;
        try {
            Context initialContext = new InitialContext();

            java.lang.Object ejbBusIntf = initialContext.lookup(// "java:"+
                    System.getProperty(ConnectionFactoryJNDINameProperty,
                            "eis/defaultCICSConnectionFactory"));
            cf = (ConnectionFactory) javax.rmi.PortableRemoteObject.narrow(
                    ejbBusIntf, ConnectionFactory.class);
        } catch (NamingException e) { // Error getting the business interface
            throw new GenAppException(e);
        }

        synchronized (this.getClass()) { // Protect static variable from
            // concurrent updates.
            if (eciConn == null) { // Not initialized yet.
                try {
                    eciConn = cf.getConnection();
                } catch (ResourceException e) {
                    e.printStackTrace();
                    throw new GenAppException(e);
                }

            }

        }

        record = new GenAppCommarea(); // Create record for this object.
    }

    /***************************************************************************
     * Interface methods
     ***************************************************************************/

    @Override
    public int addCustomer(CustomerData cdata) throws GenAppException {

        if (debug) System.out.println("addCustomer(CustomerData) called");
        insertCustomer(cdata); // Copy All customer data except number into
        // record.

        record.setCaRequestId(AddCustReqID);
        record.setCaCustomerNum(cdata.getCustomerNumber());

        invokeTargetProgram(InsCustPgm);

        int rc = record.getCaReturnCode();
        if (rc == 0) { // Success, copy new customer number from record
            int cnum = (int) record.getCaCustomerNum();
            cdata.setCustomerNumber(cnum);
            if (debug) System.out.println("addCustomer " + cnum + " OK");
            return cnum;
        }
        // No other return code is expected...
        throw new GenAppException("Unexpected return code " + rc + " from "
                + InsCustPgm);

    }

    @Override
    public CustomerData getCustomer(int cnum) throws GenAppException {

        record.setCaRequestId(InqCustReqID);
        record.setCaCustomerNum(cnum);

        invokeTargetProgram(InqCustPgm);

        int rc = record.getCaReturnCode();
        if (rc != 0)
            throw new GenAppException("Unexpected return code " + rc + " from "
                    + InqCustPgm);

        CustomerData cdata = new CustomerData();

        cdata.setCustomerNumber(cnum);
        extractCustomer(cdata);
        if (debug) System.out.println("getCustomer " + cnum + " OK");
        return cdata;

    }

    // cdata contains customer data and number
    @Override
    public void updateCustomer(int cnum, CustomerData cdata)
            throws GenAppException {

        insertCustomer(cdata); // All customer data except number

        record.setCaRequestId(UpdateCustReqID);
        record.setCaCustomerNum(cnum);

        invokeTargetProgram(UpdateCustPgm);

        int rc = record.getCaReturnCode();
        if (rc != 0)
            throw new GenAppException("Unexpected return code " + rc + " from "
                    + UpdateCustPgm);
        if (debug) System.out.println("updateCustomer " + cnum + " OK");
    }

    public CustomerData[] listCustomers(int startCustomer, int maxCount)
            throws GenAppException {

        // Constant string declarations for channel & containers
        String CHANNEL = "GENAPP";
        String REQUEST_CONTAINER = "REQUEST";
        String RESPONSE_CONTAINER = "RESPONSE";
        String ERROR_CONTAINER = "ERROR";

        // Check maxCount < 100 which is a precondition.
        if (maxCount > 99)
            throw new GenAppException(
                    "Maximum number of customers to request is 99!");

        // Build String for request, 10 char customer id, 2 char count.
        StringBuffer request = new StringBuffer(12);
        String cstring = Integer.toString(startCustomer);
        String cmaxstr = Integer.toString(maxCount);
        // Pad numeric fields with '0' characters.
        for (int i = 0; i < 10 - cstring.length(); i++)
            request.append('0');
        request.append(cstring);
        if (maxCount < 10)
            request.append('0'); // Add single 0 if needed
        request.append(cmaxstr);

        // Create channel
        ECIChannelRecord channel;
        try {
            channel = new ECIChannelRecord(CHANNEL);
        } catch (ResourceException e) {
            e.printStackTrace();
            throw new GenAppException(e);
        }

        // Create request container
        channel.put(REQUEST_CONTAINER, request.toString());

        // Invoke target program passing channel.
        invokeChannelProgram(ListCustomersPgm, channel);

        if (channel.containsKey(ERROR_CONTAINER)) // Error string from COBOL
        // program.
        {
            throw new GenAppException((String) channel.get(ERROR_CONTAINER));
        }

        // No ERROR so response should contain list of customers.
        // Check response exists and extract String from container.
        if (!channel.containsKey(RESPONSE_CONTAINER)) // Error string from COBOL
            // program.
            throw new GenAppException(
                    "Channel did not contain RESPONSE container after call.");
        String response = (String) channel.get(RESPONSE_CONTAINER);
        // System.out.println("Response contains "+response.substring(0,2)+" customers");
        // System.out.println("Response length = "+response.length());
        // Get customer count, then extract all returned customers into
        // CustomerData[].
        int ccount = Integer.parseInt(response.substring(0, 2));

        // Pad response to correct length if necessary
        int expectlen = ccount * 30 + 2;
        if (response.length() < expectlen) {
            StringBuffer responsebuf = new StringBuffer(expectlen);
            responsebuf.append(response);
            for (int i = responsebuf.length(); i < expectlen; i++)
                // Pad to correct length
                responsebuf.append(' '); // Pad with blanks.
            response = responsebuf.toString();
        }

        int coffset = 2; // Start offset for customer data.

        CustomerData[] carray = new CustomerData[ccount];
        for (int cindex = 0; cindex < ccount; cindex++) {
            CustomerData cdata = new CustomerData();

            int cnumber = Integer.parseInt(response.substring(coffset,
                    coffset + 10));
            cdata.setCustomerNumber(cnumber);
            cdata.setFirstName(response.substring(coffset + 10, coffset + 20));
            cdata.setLastName(response.substring(coffset + 20, coffset + 30));

            carray[cindex] = cdata; // Store new CustomerData in array.
            coffset += 30; // Shift forward by length of each customer data.
        }
        if (debug)
            System.out.println("listCustomers(" + startCustomer + "," + maxCount + ") found " + carray.length + " customers.");
        return carray; // Return full CustomerData[] to caller.

    }

    @Override
    public void deleteCustomer(int custNumber) throws GenAppException {

        // Customer may be deleted only when there are no associated policies.
        PolicyData[] plist = listPolicies(custNumber);
        if (plist.length == 0) { // Customer has no policies
            KSDS ksdscust = new KSDS();
            ksdscust.setName("KSDSCUST");
            // Pad with leading zeroes
            String cstring = padInteger(custNumber, 10);
            byte[] key = null;
            try {
                key = cstring.getBytes(ccsid);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            try {
                ksdscust.delete(key);
            } catch (RecordNotFoundException e) {
                throw new GenAppException("Customer " + custNumber + " does not exist.");
            } catch (CicsException e) {
                e.printStackTrace();
                throw new GenAppException(e);
            }
        } else { // Customer has active policies
            throw new GenAppException(
                    "Customer "
                            + custNumber
                            + " has "
                            + plist.length
                            + " active policies. Delete policies before customer can be deleted.");
        } // if()
        if (debug) System.out.println("deleteCustomer " + custNumber + " OK");
    }

    @Override
    public int addPolicy(int custNumber, PolicyData pdata)
            throws GenAppException {

        String requestID;

        switch (pdata.getPolicyType()) {
            case Car:
                insertCar((CarData) pdata);
                requestID = AddCarReqID;
                break;
            case House:
                insertHouse((HouseData) pdata);
                requestID = AddHouseReqID;
                break;
            default:
                throw new GenAppException("Invalid policy type."); // Should never
                // occur

        }
        record.setCaRequestId(requestID);
        record.setCaCustomerNum(custNumber);

        invokeTargetProgram(InsPolicyPgm);

        int rc = record.getCaReturnCode();
        if (rc == 0) { // Success, copy new customer number from record
            int pnum = (int) record.getCaPolicyNum();
            pdata.setPolicyNumber(pnum);
            if (debug) System.out.println("addPolicy for customer " + custNumber + " added policy " + pnum + " OK.");
            return pnum;
        }
        // No other return code is expected...
        throw new GenAppException("Unexpected return code " + rc + " from "
                + InsPolicyPgm);
    }

    @Override
    public PolicyData getPolicy(int custNumber, int policyNumber,
                                PolicyType ptype) throws GenAppException {

        String requestID;

        switch (ptype) {
            case Car:
                requestID = InqCarReqID;
                break;
            case House:
                requestID = InqHouseReqID;
                break;
            default:
                throw new GenAppException("Invalid policy type."); // Should never
                // occur

        }
        record.setCaRequestId(requestID);
        record.setCaPolicyNum(policyNumber);
        record.setCaCustomerNum(custNumber);

        invokeTargetProgram(InqPolicyPgm);

        int rc = record.getCaReturnCode();
        if (rc != 0)
            throw new GenAppException("Unexpected return code " + rc + " from "
                    + InqPolicyPgm);

        PolicyData pdata;
        switch (ptype) {
            case Car:
                pdata = new CarData();
                extractCar((CarData) pdata);
                break;
            case House:
                pdata = new HouseData();
                extractHouse((HouseData) pdata);
                break;
            default:
                throw new GenAppException("Invalid policy type."); // Should never
                // occur

        }
        pdata.setPolicyNumber(policyNumber); // Ensure policy number in pdata is
        // consistent.
        if (debug) System.out.println("getPolicy for customer " + custNumber + " policy " + policyNumber + " OK.");
        return pdata;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.cics.genapp.bli.GenAppInterface#updatePolicy(int,
     * com.ibm.cics.genapp.bli.PolicyData)
     */
    @Override
    public void updatePolicy(int custNumber, int policyNumber, PolicyData pdata)
            throws GenAppException {

        String requestID;

        switch (pdata.getPolicyType()) {
            case Car:
                insertCar((CarData) pdata);
                requestID = UpdateCarReqID;
                break;
            case House:
                insertHouse((HouseData) pdata);
                requestID = UpdateHouseReqID;
                break;
            default:
                throw new GenAppException("Invalid policy type."); // Should never
                // occur

        }
        System.out.println(requestID + custNumber + policyNumber);
        record.setCaRequestId(requestID);
        record.setCaCustomerNum(custNumber);
        record.setCaPolicyNum(policyNumber);

        invokeTargetProgram(UpdatePolicyPgm);

        int rc = record.getCaReturnCode();
        if (rc != 0)
            throw new GenAppException("Unexpected return code " + rc + " from "
                    + UpdatePolicyPgm);
        pdata.setPolicyNumber((int) record.getCaPolicyNum());
        // No other return code is expected...
        if (debug) System.out.println("updatePolicy for customer " + custNumber + " policy " + policyNumber + " OK.");
    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.cics.genapp.bli.GenAppInterface#deletePolicy(int, int)
     */
    @Override
    public void deletePolicy(int custNumber, int policyNumber, PolicyType ptype)
            throws GenAppException {
        String requestID;

        switch (ptype) {
            case Car:
                requestID = DeleteCarReqID;
                break;
            case House:
                requestID = DeleteHouseReqID;
                break;
            default:
                throw new GenAppException("Invalid policy type."); // Should never
                // occur

        }
        record.setCaRequestId(requestID);
        record.setCaPolicyNum(policyNumber);
        record.setCaCustomerNum(custNumber);

        System.out.println(requestID + custNumber + policyNumber);
        invokeTargetProgram(DeletePolicyPgm);

        int rc = record.getCaReturnCode();
        if (rc != 0)
            throw new GenAppException( // Unexpected
                    "Unexpected return code " + rc + " from " + DeletePolicyPgm);
        if (debug) System.out.println("deletePolicy for customer " + custNumber + " policy " + policyNumber + " OK.");

    }

    /*
     * (non-Javadoc)
     *
     * @see com.ibm.cics.genapp.bli.GenAppInterface#listPolicies(int) This
     * implementation will only work in a CICS Java environment where JCICS is
     * enabled.
     */
    public PolicyData[] listPolicies(int custNumber) throws GenAppException {

        KSDS ksdspoly = new KSDS();
        ksdspoly.setName("KSDSPOLY");
        byte[] key = new byte[21]; // Null value array, key length = 21.

        ArrayList<PolicyData> policyList = new ArrayList<PolicyData>();
        KeyedFileBrowse policyBrowse;
        try {
            policyBrowse = ksdspoly.startBrowse(key); // Start with low values.

            RecordHolder recordholder = new RecordHolder();
            KeyHolder keyholder = new KeyHolder(key);

            while (true) { // Browse until end of file
                try {
                    policyBrowse.next(recordholder, keyholder);

                    // No Exception so we found a new record.
                    // Construct PolicyData from key
                    key = keyholder.getValue();
                    String keystring;
                    try {
                        keystring = new String(key, ccsid);
                    } catch (UnsupportedEncodingException e) {
                        throw new GenAppException(e);
                    }

                    // If the key matches the requested customer number,
                    // process the key to extract policy number & type.
                    PolicyData pdata = null;
                    if (Integer.parseInt(keystring.substring(1, 11)) == custNumber) {

                        switch (keystring.charAt(0)) {
                            case 'M':
                                pdata = new PolicyData(PolicyType.Car);
                                break;
                            case 'H':
                                pdata = new PolicyData(PolicyType.House);
                                break;
                            default: // other
                                break; // Just ignore other policy types
                        } // switch()

                        if (pdata != null) { // Correct customer *and* accepted
                            // type.
                            pdata.setPolicyNumber(Integer.parseInt(keystring
                                    .substring(11)));
                            policyList.add(pdata);
                        }

                    }

                } catch (EndOfFileException endfile) {
                    break; // End browse loop normally - expected.
                }
            } // while()

            policyBrowse.end(); // End the file browse normally.

        } catch (CicsException e) {
            e.printStackTrace();
            throw new GenAppException(e);
        } // try/catch

        if (debug)
            System.out.println("listPolicies for customer " + custNumber + " found " + policyList.size() + " policies.");
        return policyList.toArray(new PolicyData[0]); // Return array of correct
        // type.

    } // listPolicies

    /***************************************************************************
     * Internal methods
     ***************************************************************************/

    private void insertCustomer(CustomerData cdata) {

        record.setCaFirstName(cdata.getFirstName());
        record.setCaLastName(cdata.getLastName());

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Calendar birthday = new GregorianCalendar(cdata.getBirthYear(),
                cdata.getBirthMonth() - 1,
                cdata.getBirthDay());
        record.setCaDob(df.format(birthday.getTime()));

        record.setCaHouseName(cdata.getHouseName());
        record.setCaHouseNum(Integer.toString(cdata.getHouseNumber()));
        record.setCaPostcode(cdata.getPostCode());

        record.setCaPhoneHome(cdata.getHomePhone());
        record.setCaPhoneMobile(cdata.getMobilePhone());
        record.setCaEmailAddress(cdata.getEmailAddress());

    }

    private void extractCustomer(CustomerData cdata) {

        // Copy data from record
        cdata.setFirstName(record.getCaFirstName());
        cdata.setLastName(record.getCaLastName());

        String[] birthday = record.getCaDob().split("-");

        // Avoid array index exceptions by checking first:
        if (birthday.length == 3) {
            cdata.setBirthYear(Integer.parseInt(birthday[0]));
            cdata.setBirthMonth(Integer.parseInt(birthday[1]));
            cdata.setBirthDay(Integer.parseInt(birthday[2]));
        }

        cdata.setHouseName(record.getCaHouseName());
        try {
            cdata.setHouseNumber(Integer.parseInt(record.getCaHouseNum()));
        } catch (NumberFormatException nfe) {
        } // Thrown if house number not stored.
        cdata.setPostCode(record.getCaPostcode());
        cdata.setHomePhone(record.getCaPhoneHome());
        cdata.setMobilePhone(record.getCaPhoneMobile());
        cdata.setEmailAddress(record.getCaEmailAddress());

    }

    private void insertHouse(HouseData housedata) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        record.setCaIssueDate(df.format(housedata.getIssueDate().getTime()));
        record.setCaExpiryDate(df.format(housedata.getExpiryDate().getTime()));

        record.setCaHPropertyType(housedata.getPropertyType());
        record.setCaHBedrooms(housedata.getBedrooms());
        record.setCaHValue(housedata.getHouseValue());
        record.setCaHouseName(housedata.getHouseName());
        record.setCaHHouseNumber(Integer.toString(housedata.getHouseNumber()));
        record.setCaHPostcode(housedata.getPostcode());
    }

    private void insertCar(CarData cardata) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        record.setCaIssueDate(df.format(cardata.getIssueDate().getTime()));
        record.setCaExpiryDate(df.format(cardata.getExpiryDate().getTime()));

        record.setCaMMake(cardata.getCarMake());
        record.setCaMModel(cardata.getCarModel());
        record.setCaMValue(cardata.getCarValue());
        record.setCaMRegnumber(cardata.getRegistration());
        record.setCaMManufactured("1999-07-19");
    }

    private void extractHouse(HouseData housedata) {

        housedata.setPropertyType(record.getCaHPropertyType());
        housedata.setBedrooms(record.getCaHBedrooms());
        housedata.setHouseValue(record.getCaHValue());
        housedata.setHouseName(record.getCaHouseName());
        housedata.setHouseNumber(Integer.parseInt(record.getCaHHouseNumber()
                .trim()));
        housedata.setPostcode(record.getCaHPostcode());
    }

    private void extractCar(CarData cardata) {

        cardata.setCarMake(record.getCaMMake());
        cardata.setCarModel(record.getCaMModel());
        cardata.setCarValue(record.getCaMValue());
        cardata.setRegistration(record.getCaMRegnumber());
    }

    // Method concerned solely with invoking target program via JCA.
    private void invokeTargetProgram(String program) throws GenAppException {
        try {
            Interaction eciInt = eciConn.createInteraction();

            byte[] commarea = this.record.getByteBuffer();
            int recordlength = commarea.length;

            GenAppCCIRecord ccirecord = new GenAppCCIRecord(commarea);

            // Setup the interactionSpec.
            ECIInteractionSpec eSpec = new ECIInteractionSpec();
            eSpec.setCommareaLength(recordlength);
            eSpec.setReplyLength(recordlength);
            eSpec.setFunctionName(program);
            eSpec.setInteractionVerb(ECIInteractionSpec.SYNC_SEND_RECEIVE);

            // Execute the interaction (invoke target CICS program).
            eciInt.execute(eSpec, ccirecord, ccirecord); // Use same record for
            // input & output
            eciInt.close();

        } catch (Exception e) {
            e.printStackTrace();
            throw new GenAppException(e);
        }

    }

    /*
     * Low level utility methods
     */
    private String padInteger(int number, int length) {
        String numstring = Integer.toString(number);
        StringBuffer buf = new StringBuffer(length);
        // Pad numeric fields with '0' characters.
        int padlen = length - numstring.length();
        for (int i = 0; i < padlen; i++) buf.append('0');
        buf.append(numstring);
        return buf.toString();
    }


    /**
     * @param program String name of program to call
     * @param channel ECIChannelRecord to pass to channel enabled program
     * @throws GenAppException
     */
    private void invokeChannelProgram(String program, ECIChannelRecord channel)
            throws GenAppException {
        try {
            // Setup the interactionSpec.
            ECIInteractionSpec eSpec = new ECIInteractionSpec();
            eSpec.setFunctionName(program);
            eSpec.setInteractionVerb(ECIInteractionSpec.SYNC_SEND_RECEIVE);

            // Execute the interaction (invoke target CICS program).
            Interaction eciInt = eciConn.createInteraction();
            eciInt.execute(eSpec, channel, channel); // Use same record for
            // input & output
            eciInt.close();

        } catch (Exception e) {
            e.printStackTrace();
            throw new GenAppException(e);
        }

    }

}
