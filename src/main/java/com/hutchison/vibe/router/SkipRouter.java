package com.hutchison.vibe.router;

import com.hutchison.vibe.model.BotState;
import com.hutchison.vibe.swan.jda.CommandMessage;
import com.hutchison.vibe.swan.jda.Route;
import com.hutchison.vibe.swan.jda.Router;
import com.hutchison.vibe.swan.jda.SwanRouter;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.beans.factory.annotation.Autowired;

import static com.hutchison.vibe.swan.jda.Command.SKIP;

@Router(SKIP)
public class SkipRouter extends SwanRouter {

    private final BotState botState;

    @Autowired
    public SkipRouter(BotState botState) {
        this.botState = botState;
    }

    @Route()
    public void skip(CommandMessage commandMessage, MessageReceivedEvent event) {
        botState.skip(event);
    }

    @Override
    protected void info(CommandMessage commandMessage, MessageReceivedEvent event) {
        event.getChannel().sendMessage("Use this command to skip to the next track in the queue.").queue();
    }
}
