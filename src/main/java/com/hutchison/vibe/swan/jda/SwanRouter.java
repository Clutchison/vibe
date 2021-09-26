package com.hutchison.vibe.swan.jda;

import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import static com.hutchison.vibe.model.Constants.USE_COMMAND_TO;

@Log4j2
public abstract class SwanRouter {

    public void route(CommandMessage commandMessage, MessageReceivedEvent event) {
        Arrays.stream(this.getClass().getMethods())
                .filter(method -> method.getAnnotation(Route.class) != null)
                .filter(method -> method.getAnnotation(Route.class).value().equals(commandMessage.getSubCommand()))
                .findFirst().ifPresentOrElse(m -> {
                            try {
                                m.invoke(this, commandMessage, event);
                            } catch (IllegalAccessException | InvocationTargetException e) {
                                e.printStackTrace();
                                throw new RuntimeException(e);
                            }
                        },
                        () -> {
                            if(commandMessage.getSubCommand() == null) {
                                info(commandMessage, event);
                            } else {
                                route(commandMessage.reparseAllArgs(), event);
                            }
                        });
    }

    protected void info(CommandMessage commandMessage, MessageReceivedEvent event) {
        String[] infoTxtSplit = getInfoText().split("\t");
        String infoText = infoTxtSplit[0] + "\t" + USE_COMMAND_TO + " " + StringUtils.uncapitalize(infoTxtSplit[1]);
        event.getChannel().sendMessage(infoText).queue();
    };

    public abstract String getInfoText();
}
