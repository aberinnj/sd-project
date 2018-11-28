import java.util.ArrayList;
import java.util.List;

/*//////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////*/
public class Messenger {
    private ArrayList<String> messages;

    Messenger(){
        messages = new ArrayList<>();
    }

    public String getMessage(){
        String message = "";

        for(int i=0; i<messages.size(); i++){
            message += messages.get(i);
            message += "\n";
        }
        messages.clear();
        return message;
    }

    public void putMessage(String put){
        messages.add(put);
    }

    public void putMessage(List<String> listing){
        for(String item: listing)
            putMessage(item);
    }
}