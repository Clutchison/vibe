package com.hutchison.vibe.router;

import com.hutchison.vibe.jda.CommandMessage;
import com.hutchison.vibe.jda.Route;
import com.hutchison.vibe.jda.Router;
import com.hutchison.vibe.jda.VibeRouter;
import com.hutchison.vibe.model.BotState;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.beans.factory.annotation.Autowired;

import static com.hutchison.vibe.jda.Command.PLAY;

@Router(PLAY)
public class PlayRouter extends VibeRouter {

    private final BotState botState;

    @Autowired
    public PlayRouter(BotState botState) {
        this.botState = botState;
    }

    @Route("yt")
    public void play(CommandMessage commandMessage, MessageReceivedEvent event) {
        botState.play(commandMessage, event);
    }

    @Override
    protected void info(CommandMessage commandMessage, MessageReceivedEvent event) {
        event.getChannel().sendMessage("Use this command to begin playback of a track.").queue();
    }
}
