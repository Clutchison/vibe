package com.hutchison.vibe.router;

import com.hutchison.vibe.model.bot.BotManager;
import com.hutchison.vibe.swan.jda.CommandMessage;
import com.hutchison.vibe.swan.jda.Route;
import com.hutchison.vibe.swan.jda.Router;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.beans.factory.annotation.Autowired;

import static com.hutchison.vibe.swan.jda.Command.PLAY;

@Router(PLAY)
public class PlayRouter extends VibeRouter {

    @Autowired
    public PlayRouter(BotManager botManager) {
        super(botManager);
    }

    @Route()
    public void start(CommandMessage commandMessage, MessageReceivedEvent event) {
        if (commandMessage.getArgs().isEmpty())
            getBot(event).start(event);
        else
            getBot(event).play(commandMessage, event);
    }

    @Override
    public String getInfoText() {
        return "~play, ~p [identifier]:\n\tLoad a track to the queue. If nothing is in the queue, it will begin playback.";
    }
}
