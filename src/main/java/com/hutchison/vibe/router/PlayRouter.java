package com.hutchison.vibe.router;

import com.hutchison.vibe.swan.jda.CommandMessage;
import com.hutchison.vibe.swan.jda.Route;
import com.hutchison.vibe.swan.jda.Router;
import com.hutchison.vibe.swan.jda.SwanRouter;
import com.hutchison.vibe.model.BotState;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.beans.factory.annotation.Autowired;

import static com.hutchison.vibe.swan.jda.Command.PLAY;

@Router(PLAY)
public class PlayRouter extends SwanRouter {

    private final BotState botState;

    @Autowired
    public PlayRouter(BotState botState) {
        this.botState = botState;
    }

    @Route()
    public void play(CommandMessage commandMessage, MessageReceivedEvent event) {
        botState.play(commandMessage, event);
    }

    @Override
    protected void info(CommandMessage commandMessage, MessageReceivedEvent event) {

    }
}
