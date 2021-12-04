package com.hutchison.vibe.router;

import com.hutchison.vibe.model.bot.BotManager;
import com.hutchison.vibe.swan.jda.CommandMessage;
import com.hutchison.vibe.swan.jda.Route;
import com.hutchison.vibe.swan.jda.Router;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.beans.factory.annotation.Autowired;

import static com.hutchison.vibe.swan.jda.Command.SHUFFLE;

@Router(SHUFFLE)
public class ShuffleRouter extends VibeRouter {

    @Autowired
    public ShuffleRouter(BotManager botManager) {
        super(botManager);
    }

    @Route()
    public void shuffle(CommandMessage commandMessage, MessageReceivedEvent event) {
        getBot(event).shuffle(event);
    }

    @Route("off")
    public void shuffleOff(CommandMessage commandMessage, MessageReceivedEvent event) {
        getBot(event).shuffleOff(event);
    }

    @Override
    public String getInfoText() {
        return "~shuffle:\n\tShuffle the current queue.";
    }
}
