package com.hutchison.vibe.lava.handlers;

import com.hutchison.vibe.lava.VibeAudioManager;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class VibeAudioLoadResultHandler implements AudioLoadResultHandler {

    private VibeAudioManager manager;

    public VibeAudioLoadResultHandler(VibeAudioManager manager) {
        this.manager = manager;
    }

    @Override
    public void trackLoaded(AudioTrack audioTrack) {
        manager.getTrackScheduler().play(audioTrack);
    }

    @Override
    public void playlistLoaded(AudioPlaylist audioPlaylist) {
        //TODO-- Handle multiple track loading
    }

    @Override
    public void noMatches() {
        log.info("No Matches found for query");
    }

    @Override
    public void loadFailed(FriendlyException e) {
        log.error("Load Failed", e);
    }
}
