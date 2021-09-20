package com.hutchison.vibe.swan.jda;

import lombok.Value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Value
public class CommandMessage {

    Command command;
    String subCommand;
    List<String> args;

    public CommandMessage(String message) {
        validateMessage(message);
        if (!message.contains(" ")) {
            command = Command.fromAlias(message.substring(1));
            subCommand = "";
            args = new ArrayList<>();
        } else {
            List<String> split = new ArrayList<>(Arrays.asList(message.split("\\s")));
            command = Command.fromAlias(split.remove(0).substring(1));
            subCommand = split.size() > 0 ?
                    split.remove(0) :
                    "";
            args = Collections.unmodifiableList(split);
        }
    }

    private void validateMessage(String message) {
        String prefix = "Cannot create command: ";
        if (message == null || message.length() == 0)
            throw new IllegalArgumentException(prefix + "message must not be empty.");
        if (message.charAt(0) != '~')
            throw new IllegalArgumentException(prefix + "message must start with !");
        if (message.length() == 1)
            throw new IllegalArgumentException(prefix + "message does not contain a command");
    }

    @Override
    public String toString() {
        return "!" + command + " " + subCommand + " " + String.join(" ", args);
    }
}
