package com.hutchison.vibe.router;

import com.hutchison.vibe.model.bot.BotState;
import com.hutchison.vibe.swan.jda.CommandMessage;
import com.hutchison.vibe.swan.jda.Route;
import com.hutchison.vibe.swan.jda.Router;
import com.hutchison.vibe.swan.jda.SwanRouter;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.beans.factory.annotation.Autowired;

import static com.hutchison.vibe.swan.jda.Command.SEARCH;

@Router(SEARCH)
public class SearchRouter extends SwanRouter {

    private final BotState botState;

    @Autowired
    public SearchRouter(BotState botState) {
        this.botState = botState;
    }

    @Route()
    public void search(CommandMessage commandMessage, MessageReceivedEvent event) {
        event.getChannel().sendMessage("Not yet implemented.").queue();
//            botState.search(commandMessage, event);
    }

    @Override
    public String getInfoText() {
        return "~search [identifier]:\n\tSearch YouTube for a song, then choose the number from the returned list to play.";
    }
}
