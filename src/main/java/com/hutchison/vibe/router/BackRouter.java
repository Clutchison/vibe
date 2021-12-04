package com.hutchison.vibe.router;

import com.hutchison.vibe.model.bot.BotManager;
import com.hutchison.vibe.swan.jda.CommandMessage;
import com.hutchison.vibe.swan.jda.Route;
import com.hutchison.vibe.swan.jda.Router;
import com.hutchison.vibe.swan.jda.SwanRouter;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.beans.factory.annotation.Autowired;

import static com.hutchison.vibe.swan.jda.Command.BACK;

@Router(BACK)
public class BackRouter extends VibeRouter {

    @Autowired
    public BackRouter(BotManager botManager) {
        super(botManager);
    }

    @Route()
    public void back(CommandMessage commandMessage, MessageReceivedEvent event) {
        getBot(event).back(event);
    }

    @Override
    public String getInfoText() {
        return "~back, ~b:\n\tBegin playing the previous song in the queue.";
    }
}
