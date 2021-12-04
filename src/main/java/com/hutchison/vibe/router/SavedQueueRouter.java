package com.hutchison.vibe.router;

import com.hutchison.vibe.exception.UnauthorizedException;
import com.hutchison.vibe.model.bot.BotManager;
import com.hutchison.vibe.service.SavedQueueService;
import com.hutchison.vibe.swan.jda.CommandMessage;
import com.hutchison.vibe.swan.jda.Route;
import com.hutchison.vibe.swan.jda.Router;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

import static com.hutchison.vibe.swan.jda.Command.SAVED_QUEUE;

@Router(SAVED_QUEUE)
public class SavedQueueRouter extends VibeRouter {

    private final SavedQueueService savedQueueService;

    @Autowired
    public SavedQueueRouter(BotManager botManager, SavedQueueService savedQueueService) {
        super(botManager);
        this.savedQueueService = savedQueueService;
    }

    @Route("create")
    public void createSavedQueue(CommandMessage commandMessage, MessageReceivedEvent event) {
        String queueName = commandMessage.getArgs().get(0);
        if (savedQueueService.exists(queueName)) {
            getBot(event).saveCurrentQueue(queueName, event);
        } else {
            event.getChannel().sendMessage("A queue with this name already exists.").queue();
        }
    }

    @Route("load")
    public void loadSavedQueue(CommandMessage commandMessage, MessageReceivedEvent event) {
        String queueName = commandMessage.getArgs().get(0);
        if (savedQueueService.exists(queueName)) {
            getBot(event).loadQueue(queueName, event);
        } else {
            event.getChannel().sendMessage("You don't own a queue named " + queueName + ".").queue();
        }
    }

    @Route("update")
    public void updateSavedQueue(CommandMessage commandMessage, MessageReceivedEvent event) {
        String queueName = commandMessage.getArgs().get(0);
        Long userId = event.getMember().getUser().getIdLong();
        if (savedQueueService.exists(queueName)) {
            getBot(event).updateSavedQueue(queueName, userId, event);
        } else {
            event.getChannel().sendMessage("You don't own a queue named " + queueName + ".").queue();
        }
    }

    @Route("delete")
    public void deleteSavedQueue(CommandMessage commandMessage, MessageReceivedEvent event) {
        String queueName = commandMessage.getArgs().get(0);
        Long ownerId = event.getMember().getUser().getIdLong();
        try {
            if (savedQueueService.exists(queueName)) {
                savedQueueService.deleteSavedQueue(queueName, ownerId);
                event.getChannel().sendMessage("Successfully deleted queue of name " + queueName + ".").queue();
            } else {
                event.getChannel().sendMessage("You don't own a queue named " + queueName + ".").queue();
            }
        } catch (UnauthorizedException e) {
            event.getChannel().sendMessage("You do not have permission to delete \"" + queueName + "\".").queue();
        }
    }

    @Route("share")
    public void shareSavedQueue(CommandMessage commandMessage, MessageReceivedEvent event) {
        List<User> mentions = event.getMessage().getMentionedMembers()
                .stream()
                .map(Member::getUser)
                .collect(Collectors.toList());
        String queueName = commandMessage.getArgs().get(0);
        try {
            savedQueueService.shareQueue(queueName, event.getMember().getIdLong(), mentions);
            event.getChannel().sendMessage("Queue shared to " + mentions.stream().map(User::getName).collect(Collectors.joining(", "))).queue();
        } catch (UnauthorizedException e) {
            event.getChannel().sendMessage("You do not have permission to share \"" + queueName + "\".").queue();
        }
    }

    @Route("list-perms")
    public void listSavedQueuePerms(CommandMessage commandMessage, MessageReceivedEvent event) {
        String queueName = commandMessage.getArgs().get(0);
        try {
            String queuePerms = savedQueueService.getQueuePerms(queueName, event.getMember().getIdLong());
            event.getChannel().sendMessage(queuePerms).queue();
        } catch (UnauthorizedException e) {
            event.getChannel().sendMessage("You do not have permission to list the permissions of \"" + queueName + "\".").queue();
        }
    }

    @Route("update-perms")
    public void updateSavedQueuePerms(CommandMessage commandMessage, MessageReceivedEvent event) {
        String queueName = commandMessage.getArgs().get(0);
        List<User> mentions = event.getMessage().getMentionedMembers()
                .stream()
                .map(Member::getUser)
                .collect(Collectors.toList());
        String perms = commandMessage.getArgs().get(1);

        try {
            savedQueueService.updateQueuePerms(queueName, event.getMember().getIdLong(), mentions, perms);
            event.getChannel().sendMessage("Queue permissions updated.").queue();
        } catch (UnauthorizedException e) {
            event.getChannel().sendMessage("You do not have permission to update the permissions of \"" + queueName + "\".").queue();
        }
    }

    @Override
    public String getInfoText() {
        return "~saved-queue [option] [name]:\n" +
                "\tInteract with a saved queue. {create, load, delete, list-perms}.\n" +
                "~saved-queue share [name] [@mentions]:\n" +
                "\tGive read access to each mention. Creator only.\n" +
                "~saved-queue update-perms [name] [perms:r|u|d] [@mentions]:\n" +
                "\tSet access to queue for each mention. Creator only.";
    }
}
