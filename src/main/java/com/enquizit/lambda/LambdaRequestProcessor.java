package com.enquizit.lambda;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.google.inject.Inject;
import com.enquizit.soap.ExampleResponse;
import com.enquizit.soap.SoapMessageParser;
import com.enquizit.soap.SoapWrapper;
import com.enquizit.soap.SoapXmlMessage;
import com.enquizit.utility.DataAccessClient;
import org.apache.log4j.Logger;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import java.io.*;
import java.util.List;

class LambdaRequestProcessor {

    private static final Logger LOGGER = Logger.getLogger(LambdaRequestProcessor.class);

    private final SoapMessageParser soapMessageParser;
    private static final AWSCredentials credentials;
    private static String bucketName;
    private String serviceUrl="https://www.dataaccess.com/webservicesserver/NumberConversion.wso";

    static {
        //put your accesskey and secretkey here --- works with my exam2020@iyappan ---
        /* accesskey and secret key for the sandbocx

         */
        credentials = new BasicAWSCredentials(
                "AKIATC22ZGBIK4EYROLT",
                "Gd0m+J8JGaD2t2Kt1Gzh6y3+Pa/uz2yXsbQ/PFu4"
        );
    }


    @Inject
    LambdaRequestProcessor(SoapMessageParser soapMessageParser) {
        this.soapMessageParser = soapMessageParser;
    }

    SoapWrapper process(SoapWrapper request) {
        LOGGER.info("Received api request*********  [" + request + "]");
        try {
            SOAPMessage soapMessage = soapMessageParser.parseFrom(request.getBody());
            SOAPMessage wsresponse = calltheWebservice(soapMessage);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            wsresponse.writeTo(out);
            String strMsg = new String(out.toByteArray());


            LOGGER.info("Upload this response to S3 as weresponse:::  "+wrap(wsresponse));

            LOGGER.info("Upload this response to S3 as strg:::  "+strMsg);
            uploadToS3(strMsg);
            return wrap(wsresponse);
        } catch (Exception e) {
            LOGGER.error("Failed to handle API request: [" + request + "]", e);
        }
        return null;
    }


    private SOAPMessage calltheWebservice(SOAPMessage soapMessage) throws SOAPException, IOException {
        LOGGER.info("calling the wb incoming ---------> "+soapMessage);
        DataAccessClient dataAccessClient=new DataAccessClient();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        soapMessage.writeTo(out);

        return dataAccessClient.getXMLfromDataAccess(new String(out.toByteArray()), serviceUrl);

       // return soapMessage;
    }

    private void uploadToS3(String strxml) {
        LOGGER.info("upload to s3...............:"+strxml);
        //set-up the client
        AmazonS3 s3client = AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.US_EAST_2)
                .build();

        AWSS3Service awsService = new AWSS3Service(s3client);


        //listing objects
        LOGGER.info("List the S3 buckets...............");
        List<Bucket> buckets = s3client.listBuckets();
        for(Bucket bucket : buckets) {
            System.out.println("------------- S3 buckets --------- "+bucket.getName());
        }
        InputStream is = new ByteArrayInputStream(strxml.getBytes());
        System.out.println("------------- InputStream of strxml--------- "+is.toString());
        bucketName = "xmldata1";
        String dstKey = "ex2.xml" ;

        // Set Content-Length and Content-Type
        ObjectMetadata meta = new ObjectMetadata();
        //meta.setContentLength(os.size());

            meta.setContentType("text/xml");


        // Uploading to S3 destination bucket
        System.out.println("Writing to for the demo..... : " + bucketName + "/" + dstKey);
        try {
            s3client.putObject(bucketName, dstKey, is, meta);
        }
        catch(AmazonServiceException e)
        {
            System.err.println(e.getErrorMessage());
            System.exit(1);
        }
        System.out.println("Successfully uploaded to " + bucketName + "/" + dstKey);
       // return "Ok";


      //  String stringObjKeyName = "test2.xml";
     //   new File(String.valueOf(is));


        // Upload a text string as a new object.
      //  s3client.putObject(bucketName, stringObjKeyName, "Uploaded String Object");

// Upload a file as a new object with ContentType and title specified.
        /*
        PutObjectRequest request = new PutObjectRequest(bucketName, "fileObjKeyName",  new File(String.valueOf(is)));
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("text/xml");
        metadata.addUserMetadata("title", "someTitle");
        request.setMetadata(metadata);
        s3client.putObject(request);

         */


/*
        //creating a bucket



         // Upload a text string as a new object.
            s3Client.putObject(bucketName, stringObjKeyName, "Uploaded String Object");
        if(awsService.doesBucketExist(bucketName)) {
            System.out.println(bucketName+":: Bucket name is not available."
                    + " Try again with a different Bucket name.");
            return;
        }
        awsService.createBucket(bucketName);/

 */





    }



    private SOAPMessage handle(SOAPMessage soapMessage) {
        try {
            // TODO actually handle the message. For demonstration purposes return example response.
            LOGGER.info(("creates and returns a dummy message"));
            return ExampleResponse.create();
        } catch (SOAPException e) {
            throw new RuntimeException("Failed to create SOAPMessage", e);
        }
    }

    private SoapWrapper wrap(SOAPMessage soapMessage) {
        String messageAsXml = new SoapXmlMessage(soapMessage).toXml();
        return new SoapWrapper(messageAsXml);
    }
}
