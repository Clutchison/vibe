package com.hutchison.vibe.router;

import com.hutchison.vibe.model.bot.BotManager;
import com.hutchison.vibe.swan.jda.CommandMessage;
import com.hutchison.vibe.swan.jda.Route;
import com.hutchison.vibe.swan.jda.Router;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.beans.factory.annotation.Autowired;

import static com.hutchison.vibe.swan.jda.Command.DISCONNECT;

@Router(DISCONNECT)
public class DisconnectRouter extends VibeRouter {

    @Autowired
    public DisconnectRouter(BotManager botManager) {
        super(botManager);
    }

    @Route()
    public void disconnect(CommandMessage commandMessage, MessageReceivedEvent event) {
        getBot(event).disconnect(event);
    }

    @Override
    public String getInfoText() {
        return "~disconnect, ~d:\n\tDisconnect Vibe from your voice channel.";
    }
}
