package com.hutchison.vibe.router;

import com.hutchison.vibe.model.BotState;
import com.hutchison.vibe.service.SavedQueueService;
import com.hutchison.vibe.swan.jda.CommandMessage;
import com.hutchison.vibe.swan.jda.Route;
import com.hutchison.vibe.swan.jda.Router;
import com.hutchison.vibe.swan.jda.SwanRouter;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.beans.factory.annotation.Autowired;

import static com.hutchison.vibe.swan.jda.Command.SAVED_QUEUE;

@Router(SAVED_QUEUE)
public class SavedQueueRouter extends SwanRouter {

    private final BotState botState;
    private final SavedQueueService savedQueueService;

    @Autowired
    public SavedQueueRouter(BotState botState, SavedQueueService savedQueueService) {
        this.botState = botState;
        this.savedQueueService = savedQueueService;
    }

    @Route("create")
    public void createSavedQueue(CommandMessage commandMessage, MessageReceivedEvent event) {
        botState.saveCurrentQueue(commandMessage.getArgs().get(0), event);
    }

    @Route("load")
    public void loadSavedQueue(CommandMessage commandMessage, MessageReceivedEvent event) {
        botState.loadQueue(commandMessage.getArgs().get(0), event);
    }

    @Route("update")
    public void updateSavedQueue(CommandMessage commandMessage, MessageReceivedEvent event) {
        String queueName = commandMessage.getArgs().get(0);
        String username = event.getMember().getUser().getName();
        if(savedQueueService.exists(queueName, username)) {
            botState.updateSavedQueue(queueName, username, event);
        }
        else {
            event.getChannel().sendMessage("You don't own a queue named " + queueName + ".").queue();
        }
    }

    @Route("delete")
    public void deleteSavedQueue(CommandMessage commandMessage, MessageReceivedEvent event) {
        String queueName = commandMessage.getArgs().get(0);
        String username = event.getMember().getUser().getName();
        if(savedQueueService.exists(queueName, username)) {
            savedQueueService.deleteSavedQueue(queueName, username);
            event.getChannel().sendMessage("Successfully deleted queue of name " + queueName + ".").queue();
        }
        else {
            event.getChannel().sendMessage("You don't own a queue named " + queueName + ".").queue();
        }
    }

    @Override
    protected void info(CommandMessage commandMessage, MessageReceivedEvent event) {
        event.getChannel().sendMessage(getInfoText()).queue();
    }

    @Override
    public String getInfoText() {
        return "~queue [option] [name]:\n\tUse this command to interact with a saved queue. {create, load, delete}.";
    }
}
