package com.hutchison.vibe.router;

import com.hutchison.vibe.model.bot.BotManager;
import com.hutchison.vibe.model.bot.BotState;
import com.hutchison.vibe.swan.jda.SwanRouter;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@Log4j2
public abstract class VibeRouter extends SwanRouter {

    private final BotManager botManager;

    protected VibeRouter(BotManager botManager) {
        this.botManager = botManager;
    }

    public BotState getBot(MessageReceivedEvent event) {
        return botManager.getBot(event.getGuild().getIdLong());
    }
}
