package com.hutchison.vibe.lava;

import com.hutchison.vibe.lava.handlers.VibeAudioSendHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import lombok.Getter;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class VibeAudioManager extends DefaultAudioPlayerManager {

    private final AudioPlayer player;
    @Getter
    private final TrackScheduler trackScheduler;
    @Getter
    private final VibeAudioSendHandler vibeAudioSendHandler;

    public VibeAudioManager() {
        this.player = this.createPlayer();
        this.trackScheduler = new TrackScheduler(this.player);
        this.vibeAudioSendHandler = new VibeAudioSendHandler(this.player);
        this.player.addListener(trackScheduler);
    }

    @PostConstruct
    public void registerAndConfig() {
        AudioSourceManagers.registerRemoteSources(this);
    }
}
