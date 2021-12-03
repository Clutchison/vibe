package com.hutchison.vibe.router;

import com.hutchison.vibe.model.bot.BotState;
import com.hutchison.vibe.swan.jda.CommandMessage;
import com.hutchison.vibe.swan.jda.Route;
import com.hutchison.vibe.swan.jda.Router;
import com.hutchison.vibe.swan.jda.SwanRouter;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.beans.factory.annotation.Autowired;

import static com.hutchison.vibe.swan.jda.Command.LOOP;

@Router(LOOP)
public class LoopRouter extends SwanRouter {

    private final BotState botState;

    @Autowired
    public LoopRouter(BotState botState) {
        this.botState = botState;
    }

    @Route("off")
    public void off(CommandMessage commandMessage, MessageReceivedEvent event) {
        botState.setLoop(commandMessage, event);
    }

    @Route("track")
    public void track(CommandMessage commandMessage, MessageReceivedEvent event) {
        botState.setLoop(commandMessage, event);
    }

    @Route("queue")
    public void queue(CommandMessage commandMessage, MessageReceivedEvent event) {
        botState.setLoop(commandMessage, event);
    }

    @Override
    public String getInfoText() {
        return "~loop, ~l [option]:\n\tSet the loop behavior. {off, track, queue}";
    }
}
