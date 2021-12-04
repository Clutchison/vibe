package com.hutchison.vibe.router;

import com.hutchison.vibe.model.bot.BotManager;
import com.hutchison.vibe.swan.jda.CommandMessage;
import com.hutchison.vibe.swan.jda.Route;
import com.hutchison.vibe.swan.jda.Router;
import com.hutchison.vibe.swan.jda.SwanRouter;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.stream.Collectors;

import static com.hutchison.vibe.swan.jda.Command.HELP;

@Router(HELP)
public class HelpRouter extends VibeRouter implements ApplicationContextAware {

    private ApplicationContext applicationContext;
    private String helpText;

    protected HelpRouter(BotManager botManager) {
        super(botManager);
    }

    @Route()
    public void help(CommandMessage commandMessage, MessageReceivedEvent event) {
        event.getChannel().sendMessage(new MessageBuilder().appendCodeBlock(helpText, "").build()).queue();
    }

    @Override
    public String getInfoText() {
        return "~help, ~h:\n\tDisplay available commands and their usage.";
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    private void getHelpText() {
        helpText = Arrays.stream(applicationContext.getBeanNamesForAnnotation(Router.class))
                .map(name -> (SwanRouter) applicationContext.getBean(name))
                .map(SwanRouter::getInfoText).collect(Collectors.joining("\n"));
    }
}
