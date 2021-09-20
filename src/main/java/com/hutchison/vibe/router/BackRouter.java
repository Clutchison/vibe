package com.hutchison.vibe.router;

import com.hutchison.vibe.model.BotState;
import com.hutchison.vibe.swan.jda.CommandMessage;
import com.hutchison.vibe.swan.jda.Route;
import com.hutchison.vibe.swan.jda.Router;
import com.hutchison.vibe.swan.jda.SwanRouter;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.beans.factory.annotation.Autowired;

import static com.hutchison.vibe.swan.jda.Command.BACK;

@Router(BACK)
public class BackRouter extends SwanRouter {

    private final BotState botState;

    @Autowired
    public BackRouter(BotState botState) {
        this.botState = botState;
    }

    @Route()
    public void back(CommandMessage commandMessage, MessageReceivedEvent event) {
        botState.back(event);
    }

    @Override
    protected void info(CommandMessage commandMessage, MessageReceivedEvent event) {
        event.getChannel().sendMessage("Use this command to begin playing the previous song in the queue.").queue();
    }
}
