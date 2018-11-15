/*//////////////////////////////////////////////////////////////////////////////////
Main function
//////////////////////////////////////////////////////////////////////////////////*/
public class CommandUtils {

    public static ChatInput getInput(String message){

        String[] listing = message.split(" ");
        ChatInput parsedInput = new ChatInput();
        parsedInput.command = listing[0];

        for(int i=1; i<listing.length; i++)
        {
            parsedInput.args.add(listing[i]);
        }
        return parsedInput;
    }

    public static boolean validateArgumentCount(ChatInput input, int expectedArguments){
        return input.args.size() == expectedArguments;
    }
}
