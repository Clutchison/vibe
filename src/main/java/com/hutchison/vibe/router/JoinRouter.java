package com.hutchison.vibe.router;

import com.hutchison.vibe.model.BotState;
import com.hutchison.vibe.swan.jda.CommandMessage;
import com.hutchison.vibe.swan.jda.Route;
import com.hutchison.vibe.swan.jda.Router;
import com.hutchison.vibe.swan.jda.SwanRouter;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.beans.factory.annotation.Autowired;

import static com.hutchison.vibe.swan.jda.Command.JOIN;

@Router(JOIN)
@Log4j2
public class JoinRouter extends SwanRouter {

    private final BotState botState;

    @Autowired
    public JoinRouter(BotState botState) {
        this.botState = botState;
    }

    @Route()
    public void join(CommandMessage commandMessage, MessageReceivedEvent event) {
        botState.join(event);
    }

    @Override
    public String getInfoText() {
        return "~join, ~j:\n\tAsk the bot to join your voice channel.";
    }
}
