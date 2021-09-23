package com.hutchison.vibe.router;

import com.hutchison.vibe.model.BotState;
import com.hutchison.vibe.swan.jda.CommandMessage;
import com.hutchison.vibe.swan.jda.Route;
import com.hutchison.vibe.swan.jda.Router;
import com.hutchison.vibe.swan.jda.SwanRouter;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.beans.factory.annotation.Autowired;

import static com.hutchison.vibe.swan.jda.Command.PAUSE;

@Router(PAUSE)
public class PauseRouter extends SwanRouter {

    private final BotState botState;

    @Autowired
    public PauseRouter(BotState botState) {
        this.botState = botState;
    }

    @Route()
    public void togglePause(CommandMessage commandMessage, MessageReceivedEvent event) {
        botState.pause(commandMessage, event);
    }

    @Override
    public String getInfoText() {
        return "~pause:\n\tPause the current track.";
    }
}
