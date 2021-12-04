package com.hutchison.vibe.router;

import com.hutchison.vibe.model.bot.BotManager;
import com.hutchison.vibe.swan.jda.CommandMessage;
import com.hutchison.vibe.swan.jda.Route;
import com.hutchison.vibe.swan.jda.Router;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import static com.hutchison.vibe.swan.jda.Command.CLEAR;

@Router(CLEAR)
public class ClearRouter extends VibeRouter {

    public ClearRouter(BotManager botManager) {
        super(botManager);
    }

    @Route()
    public void clear(CommandMessage commandMessage, MessageReceivedEvent event) {
        getBot(event).clearQueue(event);
    }

    @Override
    public String getInfoText() {
        return "~clear, ~c:\n\tClear the contents of the current queue.";
    }
}
