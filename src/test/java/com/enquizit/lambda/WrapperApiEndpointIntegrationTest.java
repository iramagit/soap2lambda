package com.enquizit.lambda;

import com.enquizit.utility.DataAccessClient;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import static org.assertj.core.api.Assertions.assertThat;

@Ignore
public class WrapperApiEndpointIntegrationTest {

    /**
     * Provide your AWS API endpoint here and remove the {@link Ignore}
     * https://kodth05jj8.execute-api.us-east-1.amazonaws.com/test1210
     */
    private static final String WRAPPER_API_ENDPOINT = "https://kodth05jj8.execute-api.us-east-1.amazonaws.com/test1210/legacy";
    private static final String BODY_SOAP_XML = "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
            "  <soap:Body>\n" +
            "    <NumberToWords xmlns=\"http://www.dataaccess.com/webservicesserver/\">\n" +
            "      <ubiNum>100</ubiNum>\n" +
            "    </NumberToWords>\n" +
            "  </soap:Body>\n" +
            "</soap:Envelope>";

    private SOAPConnectionFactory soapConnectionFactory;

    @Before
    public void setUp() throws Exception {
        soapConnectionFactory = SOAPConnectionFactory.newInstance();
    }

    @Test
    //https://kodth05jj8.execute-api.us-east-1.amazonaws.com/test1210   -- api gtw endpoint
    public void should_retrieve_soap_message_from_wrapper_endpoint() throws Exception {
        DataAccessClient dataAccessClient = new DataAccessClient();
        dataAccessClient.getXMLfromDataAccess(BODY_SOAP_XML,WRAPPER_API_ENDPOINT);

        /*
        SOAPMessage request = ExampleSoapMessage.create();

        SOAPMessage response = executeApiCallWith(request);

        assertThat(new SoapXmlMessage(response).toXml()).isEqualTo(EXPECTED_SOAP_XML);

         */
    }

    private SOAPMessage executeApiCallWith(SOAPMessage soapMessage) throws SOAPException {
        SOAPConnection connection = null;
        try {
            connection = soapConnectionFactory.createConnection();
            return connection.call(soapMessage, WRAPPER_API_ENDPOINT);
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

