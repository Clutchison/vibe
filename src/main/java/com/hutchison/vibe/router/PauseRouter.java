package com.hutchison.vibe.router;

import com.hutchison.vibe.model.bot.BotManager;
import com.hutchison.vibe.swan.jda.CommandMessage;
import com.hutchison.vibe.swan.jda.Route;
import com.hutchison.vibe.swan.jda.Router;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.beans.factory.annotation.Autowired;

import static com.hutchison.vibe.swan.jda.Command.PAUSE;

@Router(PAUSE)
public class PauseRouter extends VibeRouter {

    @Autowired
    public PauseRouter(BotManager botManager) {
        super(botManager);
    }

    @Route()
    public void togglePause(CommandMessage commandMessage, MessageReceivedEvent event) {
        getBot(event).pause();
    }

    @Override
    public String getInfoText() {
        return "~pause:\n\tPause the current track.";
    }
}
