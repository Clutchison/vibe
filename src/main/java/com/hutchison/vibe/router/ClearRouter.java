package com.hutchison.vibe.router;

import com.hutchison.vibe.model.BotState;
import com.hutchison.vibe.swan.jda.CommandMessage;
import com.hutchison.vibe.swan.jda.Route;
import com.hutchison.vibe.swan.jda.Router;
import com.hutchison.vibe.swan.jda.SwanRouter;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import static com.hutchison.vibe.swan.jda.Command.CLEAR;

@Router(CLEAR)
public class ClearRouter extends SwanRouter {

    private final BotState botState;

    public ClearRouter(BotState botState) {
        this.botState = botState;
    }

    @Route()
    public void clear(CommandMessage commandMessage, MessageReceivedEvent event) {
        botState.clearQueue(event);
    }

    @Override
    public String getInfoText() {
        return "~clear:\n\tClear the contents of the current queue.";
    }
}
