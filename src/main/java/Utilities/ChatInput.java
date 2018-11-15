package Utilities;
import java.util.ArrayList;

/*//////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////*/
public class ChatInput {
    String command;
    ArrayList<String> args;

    public ChatInput(){
        command = "";
        args = new ArrayList<>();
    }

    public ChatInput(final ChatInput other){
        command = other.command;
        args = new ArrayList<String>();

        if(other.args.size() > 0){
            args.addAll(other.args);
        }
    }

    public String getCommand(){
        return command;
    }

    public ArrayList<String> getArgs(){
        return args;
    }
}