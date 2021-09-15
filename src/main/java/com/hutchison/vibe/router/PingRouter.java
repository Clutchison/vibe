package com.hutchison.vibe.router;

import com.hutchison.vibe.jda.CommandMessage;
import com.hutchison.vibe.jda.Route;
import com.hutchison.vibe.jda.Router;
import com.hutchison.vibe.jda.VibeRouter;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import static com.hutchison.vibe.jda.Command.PING;

@Router(PING)
public class PingRouter extends VibeRouter {

    @Route()
    public void help(CommandMessage commandMessage, MessageReceivedEvent event) {
        event.getChannel().sendMessage("Test").queue();
    }

    @Override
    protected void info(CommandMessage commandMessage, MessageReceivedEvent event) {
        event.getChannel().sendMessage("PingRouter gives you info!").queue();
    }
}
