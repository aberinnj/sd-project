import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.amazonaws.services.s3.AmazonS3;
import java.io.IOException;

public class MoveManager {
    private ArrayList<Move> moveList;
    private String clientRegion = "us-east-1";
    private String bucketName = "motherfuckinrisk";
    private String stringObjKeyName = "test_json";
    private String fileObjKeyName = "RiskGSON";
    private String fileName;
    private String key = "RiskGSON";

    MoveManager(String base){
        moveList = new ArrayList<Move>();
        fileName = base + "/RiskGSON.json";
    }

    public void upload(String json) throws JsonProcessingException {

        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(clientRegion)
                .withCredentials(new ProfileCredentialsProvider())
                .build();

        try {

            if (!s3Client.doesBucketExistV2(bucketName)) {
                // Because the CreateBucketRequest object doesn't specify a region, the
                // bucket is created in the region specified in the client.
                s3Client.createBucket(new CreateBucketRequest(bucketName));

                // Verify that the bucket was created by retrieving it and checking its location.
                String bucketLocation = s3Client.getBucketLocation(new GetBucketLocationRequest(bucketName));
                System.out.println("Bucket location: " + bucketLocation);
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

        ObjectMapper objectMapper = new ObjectMapper();
        byte[] bytesToWrite = objectMapper.writeValueAsBytes(json);

        try {

            // Upload a text string as a new object.
            s3Client.putObject(bucketName, stringObjKeyName, "Uploaded String Object");

            // Upload a file as a new object with ContentType and title specified.
            PutObjectRequest request = new PutObjectRequest(bucketName, fileObjKeyName, new File(fileName));
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType("plain/text");
            metadata.addUserMetadata("x-amz-meta-title", "someTitle");
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

    public void download() throws IOException {

        S3Object fullObject = null, objectPortion = null, headerOverrideObject = null;
        try {
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withRegion(clientRegion)
                    .withCredentials(new ProfileCredentialsProvider())
                    .build();

            // Get an object and print its contents.
            System.out.println("Downloading an object");
            fullObject = s3Client.getObject(new GetObjectRequest(bucketName, key));
            System.out.println("Content-Type: " + fullObject.getObjectMetadata().getContentType());
            System.out.println("Content: ");
            displayTextInputStream(fullObject.getObjectContent());

            // Get a range of bytes from an object and print the bytes.
            GetObjectRequest rangeObjectRequest = new GetObjectRequest(bucketName, key)
                    .withRange(0,9);
            objectPortion = s3Client.getObject(rangeObjectRequest);
            System.out.println("Printing bytes retrieved.");
            displayTextInputStream(objectPortion.getObjectContent());

            // Get an entire object, overriding the specified response headers, and print the object's content.
            ResponseHeaderOverrides headerOverrides = new ResponseHeaderOverrides()
                    .withCacheControl("No-cache")
                    .withContentDisposition("attachment; filename=example.txt");
            GetObjectRequest getObjectRequestHeaderOverride = new GetObjectRequest(bucketName, key)
                    .withResponseHeaders(headerOverrides);
            headerOverrideObject = s3Client.getObject(getObjectRequestHeaderOverride);
            displayTextInputStream(headerOverrideObject.getObjectContent());
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

    private void displayTextInputStream(InputStream input) throws IOException {
        // Read the text input stream one line at a time and display each line.
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        String line = null;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
        System.out.println();
    }

    public void addMove(Move i){
        moveList.add(i);
    }

    // Users can undo their actions
    public Move getLastMove(){
        return moveList.get(moveList.size()-1);
    }

    // Users can replay games
    public Move returnToMove(int i)
    {
        return moveList.get(i);
    }

    public Move addToMoveManager(BoardManager bm, MoveManager MM, Player[] list, int size, int playerID){
        HashMap<String, Territory> moveMap = new HashMap<String, Territory>();
        HashMap<String, Territory> boardMap = bm.getBoardMap();
        HashMap<Integer, List<String>> playerTerritories = new HashMap<Integer, List<String>>();
        for(String key: boardMap.keySet())
        {
            List<String> neighbors = boardMap.get(key).getNeighbors();
            int occ = boardMap.get(key).getOccupantID();
            boolean isOcc = boardMap.get(key).isOccupied();
            int count = boardMap.get(key).getArmy().getInfantryCount();
            moveMap.put(key, new Territory(isOcc, occ, new Army(count), neighbors));

        }
        for(int i=0; i<size; i++)
        {
            List<String> territoryList = new ArrayList<String>(list[i].getTerritories());
            playerTerritories.put(i, territoryList);
        }
        return new Move(playerID, moveMap, playerTerritories);

    }
}
