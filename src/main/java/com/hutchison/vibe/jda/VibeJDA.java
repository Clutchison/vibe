package com.hutchison.vibe.jda;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.security.auth.login.LoginException;
import java.util.Set;

import static net.dv8tion.jda.api.requests.GatewayIntent.*;
import static net.dv8tion.jda.api.utils.cache.CacheFlag.EMOTE;
import static net.dv8tion.jda.api.utils.cache.CacheFlag.VOICE_STATE;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VibeJDA {
    static final Set<GatewayIntent> INTENTS = Set.of(
            GUILD_MESSAGES,
            DIRECT_MESSAGES,
            GUILD_VOICE_STATES
    );
    static final Set<CacheFlag> DISABLED_CACHES = Set.of(
            VOICE_STATE,
            EMOTE
    );
    static final boolean jdaEnabled = true;

    final VibeListener listener;
    @Getter
    JDA jda;

    @Value("${discord.token}")
    String botToken;

    @Autowired
    public VibeJDA(VibeListener vibeListener) {
        this.listener = vibeListener;
    }

    @PostConstruct
    private void startJDA() {
        try {
            if (jdaEnabled) {
                jda = JDABuilder.createDefault(botToken)
                        .setEnabledIntents(INTENTS)
                        .disableCache(DISABLED_CACHES)
                        .addEventListeners(listener)
                        .build();
            }
        } catch (LoginException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public User getUser(long id) {
        return jda.getUserById(id);
    }
}
