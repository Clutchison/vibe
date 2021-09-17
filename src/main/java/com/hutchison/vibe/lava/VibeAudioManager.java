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

    @Getter
    private TrackScheduler trackScheduler;

    private AudioPlayer player;
    @Getter
    private VibeAudioSendHandler vibeAudioSendHandler;

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
