package com.hutchison.vibe.router;

import com.hutchison.vibe.model.BotState;
import com.hutchison.vibe.swan.jda.CommandMessage;
import com.hutchison.vibe.swan.jda.Route;
import com.hutchison.vibe.swan.jda.Router;
import com.hutchison.vibe.swan.jda.SwanRouter;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.beans.factory.annotation.Autowired;

import static com.hutchison.vibe.swan.jda.Command.JUMP;

@Router(JUMP)
public class JumpRouter extends SwanRouter {

    private final BotState botState;

    @Autowired
    public JumpRouter(BotState botState) {
        this.botState = botState;
    }

    @Route()
    public void jump(CommandMessage commandMessage, MessageReceivedEvent event) {
        String trackIndex = commandMessage.getArgs().get(0);
        try {
            int parsedIndex = Integer.parseInt(trackIndex);
            if(parsedIndex > 0) {
                botState.jump(parsedIndex - 1, event);
            }
        } catch (NumberFormatException e) {
            event.getChannel().sendMessage("NaN").queue();
        }
    }

    @Override
    protected void info(CommandMessage commandMessage, MessageReceivedEvent event) {
        event.getChannel().sendMessage("Use this command to jump to a specific track in the queue.").queue();
    }
}
