import java.io.File;
import java.io.FileWriter;
import java.util.UUID;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.GetBucketLocationRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;


public class PlayManager {

    public String createBucket() {
        String bucketForRisk = "risk-project-team-one-" + UUID.randomUUID();
        return bucketForRisk;
    }

    public AmazonS3 createInstance() {
        AmazonS3 s3instance = AmazonS3ClientBuilder.defaultClient();
        return s3instance;
    }

    PlayManager(){
        String bucket = createBucket();
        AmazonS3 s3 = createInstance();


/*        try {
            if (!s3instance.doesBucketExistV2(bucketForRisk)) {
                try {
                    s3instance.createBucket(bucketForRisk);

                    // Verify
                    String bucketLocation = s3instance.getBucketLocation(new GetBucketLocationRequest(bucketForRisk));
                    System.out.println("Bucket location: " + bucketLocation);
                } catch (AmazonS3Exception e) {
                    System.out.println("(!) Error: Something went wrong with S3 Bucket Creation. ");
                }
            }
        } catch (AmazonClientException networkErr) {
            System.out.println("Caught an AmazonClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with S3, "
                    + "such as not being able to access the network.");
            System.out.println("-----------------------------------------------------------");
            System.out.println("Error Message: " + networkErr.getMessage());
            System.out.println("-----------------------------------------------------------");
        }*/

    }

/*    public void createFileForMove(Move i, String base){
        JsonObject jobject = new JsonObject();
        JsonArray arr = new JsonArray();
        *//*NEEDS WORK*//*

    }*/

/*
    public void UploadMove(File k, String key)
    {
        s3instance.putObject(new PutObjectRequest(bucketForRisk, key, k));
    }
*/

}