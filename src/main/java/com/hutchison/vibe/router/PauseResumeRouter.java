package com.hutchison.vibe.router;

import com.hutchison.vibe.jda.CommandMessage;
import com.hutchison.vibe.jda.Route;
import com.hutchison.vibe.jda.Router;
import com.hutchison.vibe.jda.VibeRouter;
import com.hutchison.vibe.model.BotState;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.beans.factory.annotation.Autowired;

import static com.hutchison.vibe.jda.Command.PAUSE_RESUME;

@Router(PAUSE_RESUME)
public class PauseResumeRouter extends VibeRouter {

    private final BotState botState;

    @Autowired
    public PauseResumeRouter(BotState botState) {
        this.botState = botState;
    }

    @Route()
    public void togglePause(CommandMessage commandMessage, MessageReceivedEvent event) {
        botState.togglePause(commandMessage, event);
    }


    @Override
    protected void info(CommandMessage commandMessage, MessageReceivedEvent event) {
        event.getChannel().sendMessage("Use this command to pause/resume the current track.").queue();
    }
}
