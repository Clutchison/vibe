package com.hutchison.vibe.swan.jda;

import lombok.Getter;

import java.util.*;

public enum Command {
    BACK("back", "b"),
    CLEAR("clear", "c"),
    DISCONNECT("disconnect", "d"),
    HELP("help", "h"),
    JOIN("join", "j"),
    JUMP("jump"),
    LOOP("loop", "l"),
    PAUSE("pause"),
    PING("ping"),
    PLAY("play", "p"),
    QUEUE("queue", "q"),
    REMOVE("remove"),
    RESUME("resume"),
    SAVED_QUEUE("saved-queue"),
    SEARCH("search"),
    SHUFFLE("shuffle"),
    SKIP("skip"),
    STOP("stop", "s")
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
