package com.hutchison.vibe.router;

import com.hutchison.vibe.jda.CommandMessage;
import com.hutchison.vibe.jda.Route;
import com.hutchison.vibe.jda.Router;
import com.hutchison.vibe.jda.VibeRouter;
import com.hutchison.vibe.model.BotState;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.beans.factory.annotation.Autowired;

import static com.hutchison.vibe.jda.Command.DISCONNECT;

@Router(DISCONNECT)
public class DisconnectRouter extends VibeRouter {

    private final BotState botState;

    @Autowired
    public DisconnectRouter(BotState botState) {
        this.botState = botState;
    }

    @Route()
    public void disconnect(CommandMessage commandMessage, MessageReceivedEvent event) {
        botState.disconnect(event);

        //TODO -- Add Queue Clear
    }

    @Override
    protected void info(CommandMessage commandMessage, MessageReceivedEvent event) {
        event.getChannel().sendMessage("Use this command to disconnect Vibe from your voice channel.").queue();
    }
}
