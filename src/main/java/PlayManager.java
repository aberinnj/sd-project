import java.io.File;
import java.util.UUID;
import java.io.IOException;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.GetBucketLocationRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.google.gson.JsonArray;


public class PlayManager {
    private final AmazonS3 s3instance;
    private String bucketForRisk;

    PlayManager(){

        bucketForRisk = "risk-project-team-one-" + UUID.randomUUID();
        s3instance = AmazonS3ClientBuilder.defaultClient();

        try {
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
        }

    }

    public void createFileForMove(Move i){
        JsonArray arr = new JsonArray();

        /*NEEDS WORK*/

    }

    public void UploadMove(File k, String key)
    {
        s3instance.putObject(new PutObjectRequest(bucketForRisk, key, k));
    }

}

/* Data model for Moves. Read and Write
* {
    "moves": [{
        "playerID": 0,
        "territories": [{
                "name": "SIAM",
                "territory-status": {
                    "occupied": true,
                    "occupiedID": 0,
                    "armycount": 2,
                    "neighbors": ["SIAM-neighbor1", "SIAM-neighbor2"]
                }
            },
            {
                "name": "INDIA",
                "territory-status": {
                    "occupied": true,
                    "occupiedID": 0,
                    "armycount": 2,
                    "neighbors": ["INDIA-neighbor1", "INDIA-neighbor2"]
                }
            }
        ],
        "playerTerritories": [{
                "0": ["SIAM", "INDIA"]
            },
            {
                "1": ["CHINA", "JAPAN", "MONGOLIA"]
            }
        ]
    }]
}
*
* */