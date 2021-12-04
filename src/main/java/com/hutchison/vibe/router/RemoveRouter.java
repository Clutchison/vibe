package com.hutchison.vibe.router;

import com.hutchison.vibe.model.bot.BotManager;
import com.hutchison.vibe.swan.jda.CommandMessage;
import com.hutchison.vibe.swan.jda.Route;
import com.hutchison.vibe.swan.jda.Router;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.beans.factory.annotation.Autowired;

import static com.hutchison.vibe.swan.jda.Command.REMOVE;

@Router(REMOVE)
public class RemoveRouter extends VibeRouter {

    @Autowired
    public RemoveRouter(BotManager botManager) {
        super(botManager);
    }

    @Route()
    public void remove(CommandMessage commandMessage, MessageReceivedEvent event) {
        String trackIndex = commandMessage.getArgs().get(0);
        try {
            int parsedIndex = Integer.parseInt(trackIndex);
            if (parsedIndex > 0) {
                getBot(event).remove(parsedIndex - 1, event);
            }
        } catch (NumberFormatException e) {
            event.getChannel().sendMessage("NaN").queue();
        }
    }

    @Override
    public String getInfoText() {
        return "~remove [trackNumber]:\n\tRemove a specific track from the queue.";
    }
}
