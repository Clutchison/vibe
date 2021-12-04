package com.hutchison.vibe.router;

import com.hutchison.vibe.model.bot.BotManager;
import com.hutchison.vibe.swan.jda.CommandMessage;
import com.hutchison.vibe.swan.jda.Route;
import com.hutchison.vibe.swan.jda.Router;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import static com.hutchison.vibe.swan.jda.Command.QUEUE;

@Router(QUEUE)
public class QueueRouter extends VibeRouter {

    public QueueRouter(BotManager botManager) {
        super(botManager);
    }

    @Route()
    public void queue(CommandMessage commandMessage, MessageReceivedEvent event) {
        getBot(event).sendQueueInfo(event);
    }

    @Override
    public String getInfoText() {
        return "~queue, ~q:\n\tDisplay the current queue.";
    }
}
