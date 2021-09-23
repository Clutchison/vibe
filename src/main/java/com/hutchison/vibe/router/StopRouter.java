package com.hutchison.vibe.router;

import com.hutchison.vibe.model.BotState;
import com.hutchison.vibe.swan.jda.CommandMessage;
import com.hutchison.vibe.swan.jda.Route;
import com.hutchison.vibe.swan.jda.Router;
import com.hutchison.vibe.swan.jda.SwanRouter;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.beans.factory.annotation.Autowired;

import static com.hutchison.vibe.swan.jda.Command.STOP;

@Router(STOP)
public class StopRouter extends SwanRouter {

    private final BotState botState;

    @Autowired
    public StopRouter(BotState botState) {
        this.botState = botState;
    }

    @Route()
    public void stop(CommandMessage commandMessage, MessageReceivedEvent event) {
        botState.stop(commandMessage, event);
    }

    @Override
    public String getInfoText() {
        return "~stop:\n\tStop the current playing track.";
    }
}