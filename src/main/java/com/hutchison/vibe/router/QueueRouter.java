package com.hutchison.vibe.router;

import com.hutchison.vibe.model.BotState;
import com.hutchison.vibe.swan.jda.CommandMessage;
import com.hutchison.vibe.swan.jda.Route;
import com.hutchison.vibe.swan.jda.Router;
import com.hutchison.vibe.swan.jda.SwanRouter;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import static com.hutchison.vibe.swan.jda.Command.QUEUE;

@Router(QUEUE)
public class QueueRouter extends SwanRouter {

    private final BotState botState;

    public QueueRouter(BotState botState) {
        this.botState = botState;
    }

    @Route()
    public void queue(CommandMessage commandMessage, MessageReceivedEvent event) {
        botState.sendQueueInfo(commandMessage, event);
    }

    @Override
    protected void info(CommandMessage commandMessage, MessageReceivedEvent event) {
        event.getChannel().sendMessage(getInfoText()).queue();
    }

    @Override
    public String getInfoText() {
        return "~queue:\n\tUse this command to display the current queue.";
    }
}
