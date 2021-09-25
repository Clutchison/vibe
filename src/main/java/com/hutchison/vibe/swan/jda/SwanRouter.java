package com.hutchison.vibe.swan.jda;

import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

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

    protected abstract void info(CommandMessage commandMessage, MessageReceivedEvent event);
}
