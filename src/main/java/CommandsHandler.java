import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import twitter4j.TwitterException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

/*////////////////////////////////////////////////////////////////////////////////
Bot is a proxy for games and players, it forwards output and input to respective
entities

*///////////////////////////////////////////////////////////////////////////////*/
public class CommandsHandler extends TelegramLongPollingBot {

    @Override
    public void onUpdateReceived (Update update){

        if (update.hasMessage() && update.getMessage().hasText()){

            SendMessage message = new SendMessage();

            message.setChatId(update.getMessage().getChatId());

            ChatInput in = new ChatInput(CommandUtils.getInput(update.getMessage().getText()));

            Game game = CommandUtils.getGame(update.getMessage().getFrom().getId());


            switch(in.getCommand())
            {
                case "/start": {
                    message.setText(Responses.onStart());
                    break;
                }
                case "/listAllGames": {
                    message.setText(Responses.onListAllGames());
                    break;
                }
                case "/skipReinforce": {
                    // THESE ARE FOR TESTING, REMOVE IF NEED BE
                    message.setText(Responses.onSkipReinforce(CommandUtils.getGame(update.getMessage().getFrom().getId())));
                    break;

                }

                case "/skipClaim": {
                    // THESE ARE FOR TESTING, REMOVE IF NEED BE
                    message.setText(Responses.onSkipClaim(CommandUtils.getGame(update.getMessage().getFrom().getId())));
                    break;
                }

                case "/load": {
                    AWS aws = new AWS();
                    message.setText(Responses.onLoad(game, aws, in));
                    break;
                }

                case "/save": {
                    AWS aws = new AWS();
                    aws.upload(game.gameID);
                    message.setText("your game has been saved");
                    break;
                }

                case "/undo": {
                    AWS aws = new AWS();
                    message.setText(Responses.onUndo(game, aws));
                    break;
                }
                case "/join": {
                    message.setText(Responses.onJoin(in, update.getMessage().getFrom().getId(), update.getMessage().getFrom().getUserName(), update.getMessage().getChatId()));
                    break;
                }

                case "/listMyGames": {
                    message.setText(Responses.onListMyGames(update.getMessage().getFrom().getId()));
                    break;
                }

                case "/create": {
                    message.setText(Responses.onCreate(
                            update.getMessage().getFrom().getId(),
                            "risk-game-" + UUID.randomUUID().toString(),
                            update.getMessage().getFrom().getUserName(),
                            update.getMessage().getChatId())
                    );
                    break;
                }

                case "/help": {
                    message.setText(Responses.onHelp());
                    break;
                }

                // function to pick territory, there may be a way to message a player directly and in turn order given a player's user_id and turn number
                case "/pick": {
                    message.setText(Responses.onPick(
                            in,
                            update.getMessage().getFrom().getId(),
                            CommandUtils.getGame(update.getMessage().getFrom().getId())));
                    break;
                }

                case "/reinforce": {
                    message.setText(Responses.onReinforce(
                            in,
                            update.getMessage().getFrom().getId(),
                            CommandUtils.getGame(update.getMessage().getFrom().getId())));

                    break;
                }

                // message should be formatted /attack (attack territory) (defend territory) (Number of armies to attack with) (number of armies to defend with)
                case "/attack": {
                    message.setText(Responses.onAttack(game, in));
                    break;
                }

                case "/atttackWith": {
                    message.setText(Responses.onAttackWith(game, in));
                    break;
                }

                case "/defendWith": {
                    message.setText(Responses.onDefendWith(game, in));
                    break;
                }

                // message should be formatted /fortify (move from) (move to) (Num armies)
                case "/fortify": {
                    message.setText(Responses.onFortify(game, in));
                    break;
                }

                // assumes it is your turn, checks your hand for three matching cards, pops them from your hand and gives you the armies
                case "/trade": {
                    Player player = CommandUtils.getPlayer(game);
                    Turn turn = game.currentTurn;
                    break;
                }

                // message format -> /buycredit (credit amount)
                case "/buycredit": {
                    message.setText(Responses.onBuyCredit(game, in));
                    break;
                }

                // format -> /buystuff (# undos to buy) (# cards to buy)
                case "/buystuff": {
                    message.setText(Responses.onBuyStuff(game, in));
                    break;
                }

                case "/endturn": {
                    Twitter tw = new Twitter();
                    message.setText(Responses.onEndTurn(game, tw));
                }

                case "/beginTurn": {
                    //int turnNo = game.turn % game.playerDirectory.size();
                    message.setText(Responses.onBeginTurn(game));
                    break;
                }
                default:
                    message.setText("Command " + in.getCommand() + " not found.\n\n" + Responses.onHelp());
                    break;
            }

            try{
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }


            // follow up messages
            SendMessage announcement = new SendMessage();
            if (in.getCommand().equals("/join") && _GameMaster.gamesListing.containsKey(in.args.get(0)) && _GameMaster.gamesListing.get(in.args.get(0)).playerDirectory.size() == 2 && in.args.size() > 0) {
                announcement.setText(Responses.onFollowUpJoin(in.args.get(0)));
            }
            else if((in.getCommand().equals("/pick") || (in.getCommand().equals("/skipClaim"))) && _GameMaster.gamesListing.get(CommandUtils.getGame(update.getMessage().getFrom().getId()).gameID).BM.getFreeTerritories().size() == 0)
            {
                announcement.setText(Responses.onFollowUpInitPick(CommandUtils.getGame(update.getMessage().getFrom().getId())));

            }
            else if (in.getCommand().equals("/reinforce") || in.getCommand().equals("/skipReinforce"))
            {
                announcement.setText(Responses.onFollowUpReinforce(CommandUtils.getGame(update.getMessage().getFrom().getId())));
            }
            else if (CommandUtils.getGame(update.getMessage().getFrom().getId()).state == GameState.ATTACKING
                    && (CommandUtils.getGame(update.getMessage().getFrom().getId())).context != null
                    && (CommandUtils.getGame(update.getMessage().getFrom().getId())).context.count2 != 0)
            {
                announcement.setText(Responses.onFollowUpAttack(CommandUtils.getGame(update.getMessage().getFrom().getId())));
            } else if (CommandUtils.getGame(update.getMessage().getFrom().getId()).state == GameState.RESULT)
            {
                // means game.context has all the values needed
                announcement.setText(Responses.onFollowUpResult(CommandUtils.getGame(update.getMessage().getFrom().getId())));
            }
            else {
                announcement.setText("Follow-up Message: none");
            }
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
        return _GameMaster.props.getBot_name();

    }

    @Override
    public String getBotToken(){
        return _GameMaster.props.getBot_apiToken();

    }

}