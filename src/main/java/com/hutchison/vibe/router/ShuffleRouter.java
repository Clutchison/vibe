package com.hutchison.vibe.router;

import com.hutchison.vibe.model.bot.BotState;
import com.hutchison.vibe.swan.jda.CommandMessage;
import com.hutchison.vibe.swan.jda.Route;
import com.hutchison.vibe.swan.jda.Router;
import com.hutchison.vibe.swan.jda.SwanRouter;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.beans.factory.annotation.Autowired;

import static com.hutchison.vibe.swan.jda.Command.SHUFFLE;

@Router(SHUFFLE)
public class ShuffleRouter extends SwanRouter {

    private final BotState botState;

    @Autowired
    public ShuffleRouter(BotState botState) {
        this.botState = botState;
    }

    @Route()
    public void shuffle(CommandMessage commandMessage, MessageReceivedEvent event) {
        botState.shuffle(event);
    }

    @Route("off")
    public void shuffleOff(CommandMessage commandMessage, MessageReceivedEvent event) {
        botState.shuffleOff(event);
    }

    @Override
    public String getInfoText() {
        return "~shuffle:\n\tShuffle the current queue.";
    }
}
