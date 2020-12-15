package com.enquizit.utility;

import javax.xml.soap.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class DataAccessClient {
   // private static final String WRAPPER_API_ENDPOINT = "https://www.dataaccess.com/webservicesserver/NumberConversion.wso";
    /*private static final String BODY_SOAP_XML = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
            "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
            "  <soap:Body>\n" +
            "    <NumberToWords xmlns=\"http://www.dataaccess.com/webservicesserver/\">\n" +
            "      <ubiNum>100</ubiNum>\n" +
            "    </NumberToWords>\n" +
            "  </soap:Body>\n" +
            "</soap:Envelope>";

     */

    private static SOAPConnectionFactory soapConnectionFactory;

    public  SOAPMessage getXMLfromDataAccess(String inputxml, String endpoint) throws SOAPException, IOException {
        soapConnectionFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection connection = null;
        try {
            connection = soapConnectionFactory.createConnection();
         //   InputStream is = new ByteArrayInputStream(BODY_SOAP_XML.getBytes());
            InputStream is = new ByteArrayInputStream(inputxml.getBytes());
            SOAPMessage request = MessageFactory.newInstance().createMessage(null, is);
            SOAPMessage response = connection.call(request, endpoint);


           //ByteArrayOutputStream out = new ByteArrayOutputStream();
            //response.writeTo(out);
           //String strMsg = new String(out.toByteArray());


          // System.out.println("-------"+ strMsg);
            return response;
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }


}
