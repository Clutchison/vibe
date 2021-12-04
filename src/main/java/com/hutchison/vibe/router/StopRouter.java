package com.hutchison.vibe.router;

import com.hutchison.vibe.model.bot.BotManager;
import com.hutchison.vibe.swan.jda.CommandMessage;
import com.hutchison.vibe.swan.jda.Route;
import com.hutchison.vibe.swan.jda.Router;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.beans.factory.annotation.Autowired;

import static com.hutchison.vibe.swan.jda.Command.STOP;

@Router(STOP)
public class StopRouter extends VibeRouter {

    @Autowired
    public StopRouter(BotManager botManager) {
        super(botManager);
    }

    @Route()
    public void stop(CommandMessage commandMessage, MessageReceivedEvent event) {
        getBot(event).stop(event);
    }

    @Override
    public String getInfoText() {
        return "~stop, ~s:\n\tStop the current playing track.";
    }
}