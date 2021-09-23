package com.hutchison.vibe.router;

import com.hutchison.vibe.model.BotState;
import com.hutchison.vibe.swan.jda.CommandMessage;
import com.hutchison.vibe.swan.jda.Route;
import com.hutchison.vibe.swan.jda.Router;
import com.hutchison.vibe.swan.jda.SwanRouter;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.beans.factory.annotation.Autowired;

import static com.hutchison.vibe.swan.jda.Command.DISCONNECT;

@Router(DISCONNECT)
public class DisconnectRouter extends SwanRouter {

    private final BotState botState;

    @Autowired
    public DisconnectRouter(BotState botState) {
        this.botState = botState;
    }

    @Route()
    public void disconnect(CommandMessage commandMessage, MessageReceivedEvent event) {
        botState.disconnect(event);
    }

    @Override
    public String getInfoText() {
        return "~disconnect:\n\tDisconnect Vibe from your voice channel.";
    }
}
