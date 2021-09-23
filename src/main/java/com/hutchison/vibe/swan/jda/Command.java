package com.hutchison.vibe.swan.jda;

import lombok.Getter;

import java.util.*;

public enum Command {
    PING("ping"),
    JOIN("join"),
    DISCONNECT("disconnect"),
    PLAY("play"),
    PAUSE("pause"),
    RESUME("resume"),
    STOP("stop"),
    QUEUE("queue"),
    CLEAR("clear"),
    BACK("back"),
    SKIP("skip"),
    LOOP("loop"),
    SAVED_QUEUE("saved-queue"),
    HELP("help")
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
