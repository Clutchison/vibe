package com.hutchison.vibe.router;

import com.hutchison.vibe.swan.jda.CommandMessage;
import com.hutchison.vibe.swan.jda.Route;
import com.hutchison.vibe.swan.jda.Router;
import com.hutchison.vibe.swan.jda.SwanRouter;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import static com.hutchison.vibe.swan.jda.Command.PING;

@Router(PING)
public class PingRouter extends SwanRouter {

    @Route()
    public void help(CommandMessage commandMessage, MessageReceivedEvent event) {
        event.getChannel().sendMessage("Test").queue();
    }

    @Override
    protected void info(CommandMessage commandMessage, MessageReceivedEvent event) {
        event.getChannel().sendMessage(getInfoText()).queue();
    }

    @Override
    public String getInfoText() {
        return "~ping:\n\tUse this command to ping the bot!";
    }
}
