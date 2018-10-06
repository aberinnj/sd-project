import java.util.ArrayList;
import java.util.Scanner;
import java.util.UUID;

import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.AmazonS3;

class Utilities {
    private Scanner scan;

    Utilities() {
        scan = new Scanner(System.in);
    }

    private String getInput() {
        return scan.nextLine();
    }

    public String getInputUnique(String prompt, ArrayList<String> taken, String err) throws InputRiskException {
        System.out.print(prompt);
        String uniqueInput = getInput();
        if(taken.contains(uniqueInput))
            throw new InputRiskException(err);
        return uniqueInput;
    }

    public int getInputNumber(String prompt, String err) throws NumberFormatException {
        System.out.print(prompt);
        return Integer.parseInt(getInput());

    }

    public int getInputNumberButLimit(String prompt, int min, int max, String err) throws InputRiskException, NumberFormatException {
        System.out.print(prompt);
        int value = Integer.parseInt(getInput());
        if (value < min || value > max)
            throw new InputRiskException(err);
        return value;
    }
}

public class MainManager {
    private static AmazonS3 s3instance;
    private static String bucketForRisk;
    private static String base;
    private static Dice dice1;
    private static Dice dice2;
    private static Dice dice3;

    public static void initManager() {
        base = System.getProperty("user.dir");
        bucketForRisk = "risk-project-team-one-" + UUID.randomUUID();
        s3instance = AmazonS3ClientBuilder.defaultClient();
        dice1 = new Dice();
        dice2 = new Dice();
        dice3 = new Dice();
    }

    //
    public static int[] getTurnPattern(int size, int highest) {
        int[] array = new int[size];

        for (int i = 0; i < size; i++) {
            array[i] = (highest + i) % size;
        }
        return array;
    }

    public static int getPlayerSize(Utilities utils){
        int size;
        while(true) {
            try {
                size = utils.getInputNumberButLimit("Player Setup -- Number of Players: ", 2, 6, "(!) Error: This game mode only supports 2-6 players.");
                break;
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        return size;
    }

    public static int[] setGameManager(int size)
    {
        int rollForFirstPlayer = dice1.getIndexOfHighestRollIn(size);
        return getTurnPattern(size, rollForFirstPlayer);
    }

    public static void main(String[] args) {
        Utilities utils = new Utilities();
        System.out.println("Game of Risk");
        initManager();

        // Set Player Size
        int size = getPlayerSize(utils);
        GameManager GM = new GameManager(size, base, setGameManager(size));

        GM.initPlayers(GM.getDefaultArmySize(size));
        GM.initTerritories(utils);
        //GM.completeDeploy(utils);
    }

}



/*
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
        }*/

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