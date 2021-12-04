package com.hutchison.vibe.router;

import com.hutchison.vibe.model.bot.BotManager;
import com.hutchison.vibe.swan.jda.CommandMessage;
import com.hutchison.vibe.swan.jda.Route;
import com.hutchison.vibe.swan.jda.Router;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.beans.factory.annotation.Autowired;

import static com.hutchison.vibe.swan.jda.Command.LOOP;

@Router(LOOP)
public class LoopRouter extends VibeRouter {

    @Autowired
    public LoopRouter(BotManager botManager) {
        super(botManager);
    }

    @Route("off")
    public void off(CommandMessage commandMessage, MessageReceivedEvent event) {
        getBot(event).setLoop(commandMessage, event);
    }

    @Route("track")
    public void track(CommandMessage commandMessage, MessageReceivedEvent event) {
        getBot(event).setLoop(commandMessage, event);
    }

    @Route("queue")
    public void queue(CommandMessage commandMessage, MessageReceivedEvent event) {
        getBot(event).setLoop(commandMessage, event);
    }

    @Override
    public String getInfoText() {
        return "~loop, ~l [option]:\n\tSet the loop behavior. {off, track, queue}";
    }
}
