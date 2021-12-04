package com.hutchison.vibe.router;

import com.hutchison.vibe.model.bot.BotManager;
import com.hutchison.vibe.swan.jda.CommandMessage;
import com.hutchison.vibe.swan.jda.Route;
import com.hutchison.vibe.swan.jda.Router;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.beans.factory.annotation.Autowired;

import static com.hutchison.vibe.swan.jda.Command.SKIP;

@Router(SKIP)
public class SkipRouter extends VibeRouter {

    @Autowired
    public SkipRouter(BotManager botManager) {
        super(botManager);
    }

    @Route()
    public void skip(CommandMessage commandMessage, MessageReceivedEvent event) {
        getBot(event).skip(event);
    }

    @Override
    public String getInfoText() {
        return "~skip:\n\tSkip to the next track in the queue.";
    }
}
