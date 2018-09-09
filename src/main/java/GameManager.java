import java.util.Scanner;

public class GameManager {

    private static int playerSize;
    private static Player[] playerList;
    public static String base = System.getProperty("user.dir");

    /**/
    public static void main(String[] args)
    {
        System.out.println("Game of Risk");
        System.out.println("------------");
        System.out.println("PlayerCount");
        Scanner setup = new Scanner(System.in);
        playerSize = setup.nextInt();

        setPlayerList(playerSize);

        BoardManager bm = new BoardManager(base + "\\src\\main\\java\\mapSource.json");

    }

    /**/
    private static void setPlayerList(int size)
    {
        playerList = new Player[size];
        int cavalry;

        switch(size){
            case 2:
                cavalry=40;
                break;
            case 3:
                cavalry=35;
                break;
            case 4:
                cavalry=30;
                break;
            case 5:
                cavalry=25;
                break;
            case 6:
                cavalry=20;
                break;
            default:
                cavalry=0;
                break;
        }

        for(int a=0; a<size; a++){
            playerList[a] = new Player(a, cavalry);
        }
    }

    /**/
}
