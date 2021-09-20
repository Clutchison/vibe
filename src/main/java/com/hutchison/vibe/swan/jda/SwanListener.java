package com.hutchison.vibe.swan.jda;

import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class SwanListener extends ListenerAdapter implements EventListener {

    private final CommandRouter commandRouter;

    @Autowired
    public SwanListener(CommandRouter commandRouter) {
        this.commandRouter = commandRouter;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        String message = event.getMessage().getContentRaw();
        if (message.length() == 0 || message.charAt(0) != '~') return;
        log.info("Received message: " + message);
        commandRouter.route(new CommandMessage(message), event);
    }
}
