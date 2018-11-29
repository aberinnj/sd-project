import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import twitter4j.TwitterException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;


class UpdateHandler {

    private UpdateHandler(){}

    public static String commandReader(ChatInput in, int id, long chat_id, String username) {

        String response = "";

            Game game = CommandUtils.getGame(id);


            switch (in.getCommand()) {
                case "/start": {
                    response += (Responses.onStart());
                    break;
                }
                case "/listAllGames": {
                    response += (Responses.onListAllGames());
                    break;
                }
                case "/skipReinforce": {
                    // THESE ARE FOR TESTING, REMOVE IF NEED BE
                    response += (Responses.onSkipReinforce(CommandUtils.getGame(id)));
                    break;

                }

                case "/skipClaim": {
                    // THESE ARE FOR TESTING, REMOVE IF NEED BE
                    response += (Responses.onSkipClaim(CommandUtils.getGame(id)));
                    break;
                }

                case "/load": {
                    AWS aws = new AWS();
                    response += (Responses.onLoad(game, aws, in));
                    break;
                }

                case "/save": {
                    AWS aws = new AWS();
                    aws.upload(game.gameID);
                    response += ("your game has been saved");
                    break;
                }

                case "/undo": {
                    AWS aws = new AWS();
                    response += (Responses.onUndo(game, aws));
                    break;
                }
                case "/join": {
                    response += (Responses.onJoin(in, id, username, chat_id));
                    break;
                }

                case "/listMyGames": {
                    response += (Responses.onListMyGames(id));
                    break;
                }

                case "/create": {
                    response += (Responses.onCreate(
                            id,
                            "risk-game-" + UUID.randomUUID().toString(),
                            username,
                            chat_id)
                    );
                    break;
                }

                case "/help": {
                    response = (Responses.onHelp());
                    break;
                }

                // function to pick territory, there may be a way to message a player directly and in turn order given a player's user_id and turn number
                case "/pick": {
                    response += (Responses.onPick(
                            in,
                            id,
                            CommandUtils.getGame(id)));
                    break;
                }

                case "/reinforce": {
                    response += (Responses.onReinforce(
                            in,
                            id,
                            CommandUtils.getGame(id)));

                    break;
                }

                // message should be formatted /attack (attack territory) (defend territory) (Number of armies to attack with) (number of armies to defend with)
                case "/attack": {
                    response += (Responses.onAttack(game, in));
                    break;
                }

                case "/attackWith": {
                    response += (Responses.onAttackWith(game, in));
                    break;
                }

                case "/defendWith": {
                    response += (Responses.onDefendWith(game, in));
                    break;
                }

                // message should be formatted /fortify (move from) (move to) (Num armies)
                case "/fortify": {
                    response += (Responses.onFortify(game, in));
                    break;
                }

                // assumes it is your turn, checks your hand for three matching cards, pops them from your hand and gives you the armies
                case "/trade": {
                    Player player = CommandUtils.getPlayer(game);
                    Turn turn = game.currentTurn;
                    response += "You don't trade.";
                    break;
                }

                // message format -> /buycredit (credit amount)
                case "/buycredit": {
                    response += (Responses.onBuyCredit(game, in));
                    break;
                }

                // format -> /buystuff (# undos to buy) (# cards to buy)
                case "/buystuff": {
                    response += (Responses.onBuyStuff(game, in));
                    break;
                }

                case "/endturn": {
                    Twitter tw = new Twitter();
                    response += (Responses.onEndTurn(game, tw));
                    break;
                }

                case "/beginTurn": {
                    //int turnNo = game.turn % game.playerDirectory.size();
                    response += (Responses.onBeginTurn(game));
                    break;
                }
                default:
                    response += ("Command " + in.getCommand() + " not found.\n\n" + Responses.onHelp());
                    break;
            }
        return response;
    }


    public static String commandsFollowUp(ChatInput in, int id, long chat_id, String username){


        if (in.getCommand().equals("/join") && _GameMaster.gamesListing.containsKey(in.args.get(0)) && _GameMaster.gamesListing.get(in.args.get(0)).playerDirectory.size() == 2 && in.args.size() > 0) {
            return(Responses.onFollowUpJoin(in.args.get(0)));
        }
        else if((in.getCommand().equals("/pick") || (in.getCommand().equals("/skipClaim")))
                && _GameMaster.gamesListing.get(CommandUtils.getGame(id).gameID).BM.getFreeTerritories().size() == 0)
        {
            return(Responses.onFollowUpInitPick(CommandUtils.getGame(id)));

        }
        else if (in.getCommand().equals("/reinforce") || in.getCommand().equals("/skipReinforce"))
        {
            return(Responses.onFollowUpReinforce(CommandUtils.getGame(id)));
        }
        else if (CommandUtils.getGame(id).state == GameState.ATTACKING
                && (CommandUtils.getGame(id)).context != null
                && (CommandUtils.getGame(id)).context.count2 != 0)
        {
            return(Responses.onFollowUpAttack(CommandUtils.getGame(id)));
        } else if (CommandUtils.getGame(id).state == GameState.RESULT)
        {
            // means game.context has all the values needed
            return(Responses.onFollowUpResult(CommandUtils.getGame(id)));
        }
        else {
            return("Follow-up Message: none");
        }
    }
}

/*////////////////////////////////////////////////////////////////////////////////
Where updates (messages) from telegram are received
*///////////////////////////////////////////////////////////////////////////////*/
public class CommandsHandler extends TelegramLongPollingBot {

    @Override
    public void onUpdateReceived (Update update){
            // INITIAL MESSAGE
        if (update.hasMessage() && update.getMessage().hasText()) {

            // parse
            ChatInput in = new ChatInput(CommandUtils.getInput(update.getMessage().getText()));
            // get necessary data
            int user_id = update.getMessage().getFrom().getId();
            long chat_id = update.getMessage().getChatId();
            String username = update.getMessage().getFrom().getUserName();


            SendMessage message = new SendMessage();
            message.setText(UpdateHandler.commandReader(in, user_id, chat_id, username));
            message.setChatId(update.getMessage().getChatId());
            try{
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }

            // FOLLOW UP MESSAGE
            SendMessage announcement = new SendMessage();
            announcement.setText(UpdateHandler.commandsFollowUp(in, user_id, chat_id, username));
            announcement.setChatId(update.getMessage().getChatId());
            try {
                execute(announcement);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getBotUsername(){
        return(_GameMaster.props.getBot_name());

    }

    @Override
    public String getBotToken(){
        return(_GameMaster.props.getBot_apiToken());

    }

}