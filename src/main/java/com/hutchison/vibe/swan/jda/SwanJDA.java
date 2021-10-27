package com.hutchison.vibe.swan.jda;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.security.auth.login.LoginException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static net.dv8tion.jda.api.requests.GatewayIntent.*;
import static net.dv8tion.jda.api.utils.cache.CacheFlag.EMOTE;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SwanJDA {
    static final Set<GatewayIntent> INTENTS = Set.of(
            GUILD_MESSAGES,
            DIRECT_MESSAGES,
            GUILD_VOICE_STATES
    );
    static final Set<CacheFlag> DISABLED_CACHES = Set.of(
            EMOTE
    );
    static final boolean jdaEnabled = true;

    final SwanListener listener;
    @Getter
    JDA jda;

    @Value("${discord.token}")
    String botToken;

    @Autowired
    public SwanJDA(SwanListener swanListener) {
        this.listener = swanListener;
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

    public Optional<VoiceChannel> findBotChannel() {
        long botId = jda.getSelfUser().getIdLong();
        return jda.getSelfUser().getMutualGuilds()
                .stream()
                .map(Guild::getIdLong)
                .map(guildId -> {
                    Member bot = jda.getGuildById(guildId).getMemberById(botId);
                    if(bot != null && bot.getVoiceState() != null && bot.getVoiceState().inVoiceChannel()) {
                        return bot.getVoiceState().getChannel();
                    }
                    else {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .findFirst();
    }

    public Optional<TextChannel> getTextChannel() {
        return jda.getMutualGuilds().get(0).getTextChannels()
                .stream()
                .filter(c -> "general".equalsIgnoreCase(c.getName()))
                .findFirst();
    }
}
