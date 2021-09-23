package com.hutchison.vibe.router;

import com.hutchison.vibe.model.BotState;
import com.hutchison.vibe.swan.jda.CommandMessage;
import com.hutchison.vibe.swan.jda.Route;
import com.hutchison.vibe.swan.jda.Router;
import com.hutchison.vibe.swan.jda.SwanRouter;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.beans.factory.annotation.Autowired;

import static com.hutchison.vibe.swan.jda.Command.RESUME;

@Router(RESUME)
public class ResumeRouter extends SwanRouter {

    private final BotState botState;

    @Autowired
    public ResumeRouter(BotState botState) {
        this.botState = botState;
    }

    @Route()
    public void togglePause(CommandMessage commandMessage, MessageReceivedEvent event) {
        botState.resume(commandMessage, event);
    }

    @Override
    protected void info(CommandMessage commandMessage, MessageReceivedEvent event) {
        event.getChannel().sendMessage(getInfoText()).queue();
    }

    @Override
    public String getInfoText() {
        return "~resume:\n\tUse this command to resume the current track.";
    }
}