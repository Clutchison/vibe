package com.hutchison.vibe.router;

import com.hutchison.vibe.model.bot.BotManager;
import com.hutchison.vibe.swan.jda.CommandMessage;
import com.hutchison.vibe.swan.jda.Route;
import com.hutchison.vibe.swan.jda.Router;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import static com.hutchison.vibe.swan.jda.Command.PING;

@Router(PING)
public class PingRouter extends VibeRouter {

    protected PingRouter(BotManager botManager) {
        super(botManager);
    }

    @Route()
    public void help(CommandMessage commandMessage, MessageReceivedEvent event) {
        event.getChannel().sendMessage("Test").queue();
    }

    @Override
    public String getInfoText() {
        return "~ping, ~p:\n\tPing the bot!";
    }
}
