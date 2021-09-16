package com.hutchison.vibe.jda;

import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum Command {
    PING("ping"),
    JOIN("join")
    ;

    @Getter
    private final List<String> aliases;
    private static final Map<String, Command> map;

    static {
        Map<String, Command> m = new HashMap<>();

        Arrays.stream(Command.values()).forEach(command ->
                command.getAliases().forEach(alias -> m.put(alias, command))
        );

        map = Collections.unmodifiableMap(m);
    }

    Command(String... aliases) {
        this.aliases = Arrays.asList(aliases);
    }

    public static Command fromAlias(String alias) {
        Command command = map.get(alias);
        if (command == null)
            throw new IllegalArgumentException("Invalid command: " + alias);
        return command;
    }

    @Override
    public String toString() {
        return aliases.get(0);
    }
}
