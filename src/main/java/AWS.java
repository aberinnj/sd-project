import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.google.gson.JsonObject;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AWS {

    static String base;
    AmazonS3 s3Client;
    private String fileName;
    private String bucketName = "risk-game-team-one";

    AWS(){

        base = System.getProperty("user.dir");
        this.fileName = base + "/src/files/Risk.json";

        // s3Client = AmazonS3ClientBuilder.defaultClient();
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(_GameMaster.props.getAws_access_key_id(), _GameMaster.props.getAws_secret_access_key());

        s3Client = AmazonS3ClientBuilder.standard()
                .withRegion("us-east-1")
                //.withCredentials(new AWSStaticCredentialsProvider(sessionCredentials))
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .build();


    }

    // list available game objects;
    public ArrayList<String> listObjects(){
        ArrayList<String> k = new ArrayList<>();
        ListObjectsV2Result result = s3Client.listObjectsV2(bucketName);
        List<S3ObjectSummary> objects = result.getObjectSummaries();
        for (S3ObjectSummary os: objects) {
            k.add(os.getKey());
        }
        return k;
    }

    //Function to upload saved game file to S3 bucket, fileObjKeyName = GameID
    public void upload(String fileObjKeyName) {

        try {

            if (!s3Client.doesBucketExistV2(bucketName)) {
                // Because the CreateBucketRequest object doesn't specify a region, the
                // bucket is created in the region specified in the client.
                s3Client.createBucket(new CreateBucketRequest(bucketName));

                // Verify that the bucket was created by retrieving it and checking its location.
                //String bucketLocation = s3Client.getBucketLocation(new GetBucketLocationRequest(bucketName));
                //System.out.println("Bucket location: " + bucketLocation);
            }
        }
        catch(AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it and returned an error response.
            e.printStackTrace();
        }
        catch(SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            e.printStackTrace();
        }

        try {

            // Upload a text string as a new object.
            //s3Client.putObject(bucketName, stringObjKeyName, "Uploaded String Object");

            // Upload a file as a new object with ContentType and title specified.
            PutObjectRequest request = new PutObjectRequest(bucketName, fileObjKeyName, new File(fileName));
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType("plain/text");
            metadata.addUserMetadata("Risk_Json_SavedGames", "RiskJSON");
            request.setMetadata(metadata);
            s3Client.putObject(request);
        }
        catch(AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it, so it returned an error response.
            e.printStackTrace();
        }
        catch(SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            e.printStackTrace();
        }
    }

    //Function to download saved game file from S3 bucket
    public void download(String gameID) throws IOException {
        String fileObjKeyName = gameID; // Game ID is the name of the file containing a games info stored in the AWS bucket
        S3Object fullObject = null, objectPortion = null, headerOverrideObject = null;
        try {
            s3Client.getObject(
                    new GetObjectRequest(bucketName, fileObjKeyName),
                    new File(fileName)
            );

            // Get an object from the bucket
            fullObject = s3Client.getObject(new GetObjectRequest(bucketName, fileObjKeyName));
            // stream the object content
            S3ObjectInputStream stream = fullObject.getObjectContent();
            // open saved game file
            File targetFile = new File(fileName);
            // save stream to file
            FileUtils.copyInputStreamToFile(stream, targetFile);
        }
        catch(AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it, so it returned an error response.
            e.printStackTrace();
        }
        catch(SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            e.printStackTrace();
        }
        finally {
            // To ensure that the network connection doesn't remain open, close any open input streams.
            if(fullObject != null) {
                fullObject.close();
            }
            if(objectPortion != null) {
                objectPortion.close();
            }
            if(headerOverrideObject != null) {
                headerOverrideObject.close();
            }
        }
    }
}
