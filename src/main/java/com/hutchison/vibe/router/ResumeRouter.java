package com.hutchison.vibe.router;

import com.hutchison.vibe.model.bot.BotManager;
import com.hutchison.vibe.swan.jda.CommandMessage;
import com.hutchison.vibe.swan.jda.Route;
import com.hutchison.vibe.swan.jda.Router;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.beans.factory.annotation.Autowired;

import static com.hutchison.vibe.swan.jda.Command.RESUME;

@Router(RESUME)
public class ResumeRouter extends VibeRouter {

    @Autowired
    public ResumeRouter(BotManager botManager) {
        super(botManager);
    }

    @Route()
    public void togglePause(CommandMessage commandMessage, MessageReceivedEvent event) {
        getBot(event).resume();
    }

    @Override
    public String getInfoText() {
        return "~resume:\n\tResume the current track.";
    }
}