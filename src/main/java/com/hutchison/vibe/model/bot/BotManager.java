package com.hutchison.vibe.model.bot;

import com.hutchison.vibe.client.youtube.VibeYouTube;
import com.hutchison.vibe.lava.VibeAudioManager;
import com.hutchison.vibe.service.SavedQueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class BotManager {

    private final SavedQueueService savedQueueService;
    private final VibeYouTube vibeYouTube;
    private final Map<Long, BotState> bots;

    @Autowired
    public BotManager(SavedQueueService savedQueueService, VibeYouTube vibeYouTube) {
        this.savedQueueService = savedQueueService;
        this.vibeYouTube = vibeYouTube;
        bots = new HashMap<>();
    }

    public BotState getBot(Long guildId) {
        BotState savedBot = bots.get(guildId);
        if (savedBot != null) return savedBot;

        BotState newBot = newBot();
        bots.put(guildId, newBot);
        return newBot;
    }

    private BotState newBot() {
        return new BotState(new VibeAudioManager(), savedQueueService, vibeYouTube);
    }
}
