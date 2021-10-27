package com.hutchison.vibe.lava.handlers;

import com.hutchison.vibe.lava.VibeAudioManager;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

@Log4j2
public class VibeAudioLoadResultHandler implements AudioLoadResultHandler {

    protected final VibeAudioManager manager;
    private final MessageReceivedEvent event;

    @Getter
    private boolean noMatches;

    public VibeAudioLoadResultHandler(VibeAudioManager manager, MessageReceivedEvent event) {
        this.manager = manager;
        this.event = event;
        this.noMatches = false;
    }

    @Override
    public void trackLoaded(AudioTrack audioTrack) {
        manager.getTrackScheduler().queue(audioTrack);
        event.getChannel().sendMessage("Successfully loaded " + audioTrack.getInfo().title).queue();
    }

    @Override
    public void playlistLoaded(AudioPlaylist audioPlaylist) {
        //TODO-- Handle multiple track loading
    }

    @Override
    public void noMatches() {
        log.info("No Matches found for query");
        noMatches = true;
    }

    @Override
    public void loadFailed(FriendlyException e) {
        log.error("Load Failed", e);
        event.getChannel().sendMessage("Failed to load track").queue();
    }
}
